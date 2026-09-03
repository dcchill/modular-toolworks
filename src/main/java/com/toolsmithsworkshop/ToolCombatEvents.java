package com.toolsmithsworkshop;

import com.toolsmithsworkshop.item.ModularToolItem;
import com.toolsmithsworkshop.registry.ModItems;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.tool.ToolArchetype;
import com.toolsmithsworkshop.tool.PartStat;
import com.toolsmithsworkshop.tool.ToolMaterials;
import com.toolsmithsworkshop.tool.ToolGems;
import com.toolsmithsworkshop.tool.ToolStatCalculator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = ToolsmithsWorkshop.MOD_ID)
public final class ToolCombatEvents {
    private static final long FRACTURE_ROLL_COOLDOWN_TICKS = 40;
    private static final ResourceLocation TARGET_DUMMY = ResourceLocation.fromNamespaceAndPath("dummmmmmy", "target_dummy");
    private static final Map<UUID, FractureRoll> FRACTURE_ROLLS = new HashMap<>();

    private ToolCombatEvents() {}

    @SubscribeEvent
    public static void applyToolEnchantments(GetEnchantmentLevelEvent event) {
        var build = event.getStack().get(ModDataComponents.TOOL_BUILD);
        if (!(event.getStack().getItem() instanceof ModularToolItem) || build == null) return;
        int level = ToolGems.count(build, ToolGems.EMERALD);
        boolean prospecting = Boolean.TRUE.equals(event.getStack().get(ModDataComponents.PROSPECTING_ACTIVE));
        if (level == 0 && !prospecting) return;
        event.getHolder(Enchantments.FORTUNE).ifPresent(fortune -> {
            if (event.isTargetting(fortune)) event.getEnchantments().upgrade(fortune,
                    event.getEnchantments().getLevel(fortune) + level + (prospecting ? 1 : 0));
        });
        event.getHolder(Enchantments.LOOTING).ifPresent(looting -> {
            if (event.isTargetting(looting)) event.getEnchantments().upgrade(looting, event.getEnchantments().getLevel(looting) + level);
        });
    }

    @SubscribeEvent
    public static void teleportEnderPearlGemDrops(BlockDropsEvent event) {
        event.getTool().remove(ModDataComponents.PROSPECTING_ACTIVE);
        if (event.getBreaker() instanceof Player breaker) {
            breaker.getMainHandItem().remove(ModDataComponents.PROSPECTING_ACTIVE);
            FractureRoll roll = FRACTURE_ROLLS.get(breaker.getUUID());
            if (roll != null && event.getPos().equals(roll.pos())) {
                FRACTURE_ROLLS.put(breaker.getUUID(), new FractureRoll(roll.pos(), roll.nextRollTick(), false, true));
            }
        }
        if (!(event.getBreaker() instanceof Player player)) return;
        var build = event.getTool().get(ModDataComponents.TOOL_BUILD);
        if (!(event.getTool().getItem() instanceof ModularToolItem) || build == null
                || ToolGems.count(build, ToolGems.ENDER_PEARL) == 0) return;
        for (var drop : event.getDrops()) {
            drop.setPos(player.getX(), player.getY() + 0.5, player.getZ());
            drop.setPickUpDelay(0);
        }
    }

    @SubscribeEvent
    public static void autoSmeltBlazeSteelDrops(BlockDropsEvent event) {
        var build = event.getTool().get(ModDataComponents.TOOL_BUILD);
        if (!(event.getTool().getItem() instanceof ModularToolItem) || build == null
                || !hasMaterial(build, ToolMaterials.BLAZE_STEEL.id())) return;
        for (var drop : event.getDrops()) {
            var input = drop.getItem();
            event.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), event.getLevel())
                    .ifPresent(recipe -> {
                        var result = recipe.value().assemble(new SingleRecipeInput(input), event.getLevel().registryAccess());
                        if (!result.isEmpty()) drop.setItem(result.copyWithCount(result.getCount() * input.getCount()));
                    });
        }
    }

    @SubscribeEvent
    public static void mendSculkBinding(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        var stack = player.getMainHandItem();
        var build = stack.get(ModDataComponents.TOOL_BUILD);
        if (!(stack.getItem() instanceof ModularToolItem) || build == null || !build.binding().equals(ToolMaterials.SCULK.id())
                || !stack.isDamaged() || event.getOrb().value <= 0) return;
        int repaired = Math.min(stack.getDamageValue(), event.getOrb().value * 1);
        stack.setDamageValue(stack.getDamageValue() - repaired);
        event.getOrb().value -= (repaired + 3) / 4;
    }

    @SubscribeEvent
    public static void applyToolDamageModifiers(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide) return;

        if (event.getEntity() instanceof Player defender) {
            var held = defender.getMainHandItem();
            var defenseBuild = held.get(ModDataComponents.TOOL_BUILD);
            if (held.getItem() instanceof ModularToolItem && defenseBuild != null) {
                float reduction = Math.min(0.80f, defenseBuild.percent(PartStat.Type.DEFENSE) / 100.0f);
                event.setNewDamage(event.getNewDamage() * (1.0f - reduction));
            }
        }

        if (!(event.getSource().getEntity() instanceof Player player) || event.getSource().getDirectEntity() != player) return;
        var stack = player.getMainHandItem();
        var build = stack.get(ModDataComponents.TOOL_BUILD);
        if (!(stack.getItem() instanceof ModularToolItem tool) || build == null) return;

        float damage = event.getNewDamage();
        if (player.getRandom().nextFloat() < build.percent(PartStat.Type.FLIMSY) / 100.0f) damage *= 0.5f;

        if (!tool.archetype().isWeapon()) {
            event.setNewDamage(damage);
            return;
        }

        var stats = ToolStatCalculator.calculate(tool.archetype(), build);
        if (player.getRandom().nextFloat() < stats.critRate() / 100.0f) {
            float multiplier = 1.0f + stats.critDamage() / 100.0f;
            damage *= multiplier;
            markTargetDummyCritical(event.getEntity(), player, multiplier);
            player.crit(event.getEntity());
            if (tool.archetype() == ToolArchetype.SWORD && build.binding().equals(ToolMaterials.SLIME.id())) {
                LivingEntity target = event.getEntity();
                MobEffectInstance existing = target.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10,
                        Math.min(4, existing == null ? 0 : existing.getAmplifier() + 1)));
            }
        }
        event.setNewDamage(damage);
    }

    private static void markTargetDummyCritical(LivingEntity target, Player attacker, float multiplier) {
        if (!BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).equals(TARGET_DUMMY)) return;
        try {
            target.getClass().getMethod("moist", Entity.class, float.class).invoke(target, attacker, multiplier);
        } catch (ReflectiveOperationException ignored) {
            // MmmMmmMmm is optional and has no published compatibility API.
        }
    }

    @SubscribeEvent
    public static void rollProspecting(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide() || event.isCanceled()) return;
        var stack = event.getPlayer().getMainHandItem();
        stack.remove(ModDataComponents.PROSPECTING_ACTIVE);
        var build = stack.get(ModDataComponents.TOOL_BUILD);
        if (!(stack.getItem() instanceof ModularToolItem tool) || tool.archetype().isWeapon() || build == null
                || !tool.isCorrectToolForDrops(stack, event.getState())) return;
        if (event.getPlayer().getRandom().nextFloat() < build.percent(PartStat.Type.PROSPECTING) / 100.0f) {
            stack.set(ModDataComponents.PROSPECTING_ACTIVE, true);
        }
    }

    @SubscribeEvent
    public static void boostGarnetOreXp(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide() || event.isCanceled() || !isOre(event.getState())) return;
        var stack = event.getPlayer().getMainHandItem();
        var build = stack.get(ModDataComponents.TOOL_BUILD);
        if (!(stack.getItem() instanceof ModularToolItem tool) || tool.archetype().isWeapon() || build == null) return;
        int garnets = ToolGems.count(build, ToolGems.GARNET);
        if (garnets > 0) event.getPlayer().giveExperiencePoints(Math.round(garnets * 2.0f * ToolGems.effectMultiplier(build)));
    }

    @SubscribeEvent
    public static void addGarnetChestLoot(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();
        if (!name.equals(ResourceLocation.withDefaultNamespace("chests/simple_dungeon"))
                && !name.equals(ResourceLocation.withDefaultNamespace("chests/abandoned_mineshaft"))) return;
        event.getTable().addPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(ModItems.GARNET.get()).setWeight(4))
                .when(LootItemRandomChanceCondition.randomChance(0.25f)).build());
    }

    @SubscribeEvent
    public static void applyFracture(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        var stack = player.getMainHandItem();
        var build = stack.get(ModDataComponents.TOOL_BUILD);
        if (!(stack.getItem() instanceof ModularToolItem tool) || tool.archetype().isWeapon() || build == null
                || event.getOriginalSpeed() <= 0 || !tool.isCorrectToolForDrops(stack, event.getState())) return;
        BlockPos pos = event.getPosition().orElse(null);
        if (pos == null) return;

        long now = player.level().getGameTime();
        FractureRoll previous = FRACTURE_ROLLS.get(player.getUUID());
        if (previous != null && !previous.consumed() && pos.equals(previous.pos())) {
            if (previous.fractured()) event.setNewSpeed(Float.MAX_VALUE);
            return;
        }
        if (previous != null && now < previous.nextRollTick()) return;

        boolean fractured = player.getRandom().nextFloat() < build.percent(PartStat.Type.FRACTURE) / 100.0f;
        FRACTURE_ROLLS.put(player.getUUID(),
                new FractureRoll(pos.immutable(), now + FRACTURE_ROLL_COOLDOWN_TICKS, fractured, false));
        if (fractured) event.setNewSpeed(Float.MAX_VALUE);
    }

    private static boolean hasMaterial(com.toolsmithsworkshop.tool.ToolBuildData build, ResourceLocation material) {
        return build.head().equals(material) || build.binding().equals(material) || build.grip().equals(material);
    }

    private static boolean isOre(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath().contains("ore");
    }

    private record FractureRoll(BlockPos pos, long nextRollTick, boolean fractured, boolean consumed) {}
}
