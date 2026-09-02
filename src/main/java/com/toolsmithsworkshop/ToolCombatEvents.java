package com.toolsmithsworkshop;

import com.toolsmithsworkshop.item.ModularToolItem;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.tool.ToolArchetype;
import com.toolsmithsworkshop.tool.ToolMaterials;
import com.toolsmithsworkshop.tool.ToolGems;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = ToolsmithsWorkshop.MOD_ID)
public final class ToolCombatEvents {
    private ToolCombatEvents() {}

    @SubscribeEvent
    public static void applyCriticalTraits(CriticalHitEvent event) {
        var stack = event.getEntity().getMainHandItem();
        var build = stack.get(ModDataComponents.TOOL_BUILD);
        if (!event.isVanillaCritical() || !(stack.getItem() instanceof ModularToolItem tool)
                || tool.archetype() != ToolArchetype.SWORD || build == null) return;
        if (build.grip().equals(ToolMaterials.BONE.id())) event.setDamageMultiplier(event.getDamageMultiplier() * 1.3f);
        if (build.binding().equals(ToolMaterials.SLIME.id()) && event.getTarget() instanceof LivingEntity target) {
            MobEffectInstance existing = target.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10,
                    Math.min(4, existing == null ? 0 : existing.getAmplifier() + 1)));
        }
    }

    @SubscribeEvent
    public static void applyEmeraldGemEnchantments(GetEnchantmentLevelEvent event) {
        var build = event.getStack().get(ModDataComponents.TOOL_BUILD);
        if (!(event.getStack().getItem() instanceof ModularToolItem) || build == null) return;
        int level = ToolGems.count(build, ToolGems.EMERALD);
        if (level == 0) return;
        event.getHolder(Enchantments.FORTUNE).ifPresent(fortune -> {
            if (event.isTargetting(fortune)) event.getEnchantments().upgrade(fortune, event.getEnchantments().getLevel(fortune) + level);
        });
        event.getHolder(Enchantments.LOOTING).ifPresent(looting -> {
            if (event.isTargetting(looting)) event.getEnchantments().upgrade(looting, event.getEnchantments().getLevel(looting) + level);
        });
    }

    @SubscribeEvent
    public static void teleportEnderPearlGemDrops(BlockDropsEvent event) {
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
    public static void mendSculkBinding(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        var stack = player.getMainHandItem();
        var build = stack.get(ModDataComponents.TOOL_BUILD);
        if (!(stack.getItem() instanceof ModularToolItem) || build == null || !build.binding().equals(ToolMaterials.SCULK.id())
                || !stack.isDamaged() || event.getOrb().value <= 0) return;
        int repaired = Math.min(stack.getDamageValue(), event.getOrb().value * 4);
        stack.setDamageValue(stack.getDamageValue() - repaired);
        event.getOrb().value -= (repaired + 3) / 4;
    }

    @SubscribeEvent
    public static void applyCactusGripDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide || !(event.getSource().getEntity() instanceof Player player)) return;
        var stack = player.getMainHandItem();
        var build = stack.get(ModDataComponents.TOOL_BUILD);
        if (!(stack.getItem() instanceof ModularToolItem tool) || build == null || !build.grip().equals(ToolMaterials.CACTUS.id())
                || (tool.archetype() != ToolArchetype.SWORD && tool.archetype() != ToolArchetype.BATTLE_AXE)) return;
        if (player.getRandom().nextFloat() < 0.50f) event.setNewDamage(event.getNewDamage() * 2.0f);
    }
}
