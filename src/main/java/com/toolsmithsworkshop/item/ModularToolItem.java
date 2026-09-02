package com.toolsmithsworkshop.item;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.tool.MaterialTrait;
import com.toolsmithsworkshop.tool.ToolArchetype;
import com.toolsmithsworkshop.tool.ToolBuildData;
import com.toolsmithsworkshop.tool.ToolMaterial;
import com.toolsmithsworkshop.tool.ToolMomentum;
import com.toolsmithsworkshop.tool.ToolGems;
import com.toolsmithsworkshop.tool.ToolMaterials;
import com.toolsmithsworkshop.tool.ToolStatCalculator;
import com.toolsmithsworkshop.tool.ToolStats;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public final class ModularToolItem extends Item {
    private static final int DIGGING_MOMENTUM_WINDOW_TICKS = 25;
    private static final int MAX_MOMENTUM_STACKS = 16;
    private static final ResourceLocation DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(ToolsmithsWorkshop.MOD_ID, "tool_damage");
    private static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath(ToolsmithsWorkshop.MOD_ID, "tool_speed");
    private static final ResourceLocation KNOCKBACK_ID = ResourceLocation.fromNamespaceAndPath(ToolsmithsWorkshop.MOD_ID, "tool_knockback");
    private final ToolArchetype archetype;

    public ModularToolItem(ToolArchetype archetype, Properties properties) {
        super(properties.stacksTo(1));
        this.archetype = archetype;
    }

    public ToolArchetype archetype() {
        return archetype;
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                var minecraft = net.minecraft.client.Minecraft.getInstance();
                return new com.toolsmithsworkshop.client.ModularToolRenderer(
                        minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
            }
        });
    }

    public static ItemStack create(ModularToolItem item, ToolBuildData build) {
        ItemStack stack = new ItemStack(item);
        stack.set(ModDataComponents.TOOL_BUILD, build);
        applyVanillaStats(stack, ToolStatCalculator.calculate(item.archetype, build));
        return stack;
    }

    public static void refreshStats(ItemStack stack, ModularToolItem item) {
        ToolBuildData build = stack.get(ModDataComponents.TOOL_BUILD);
        if (build != null) {
            int damage = stack.getDamageValue();
            applyVanillaStats(stack, ToolStatCalculator.calculate(item.archetype, build));
            stack.set(DataComponents.DAMAGE, Math.min(damage, stack.getMaxDamage()));
        }
    }

    private static void applyVanillaStats(ItemStack stack, ToolStats stats) {
        stack.set(DataComponents.MAX_DAMAGE, stats.durability());
        stack.set(DataComponents.DAMAGE, 0);
        ItemAttributeModifiers.Builder attributes = ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(DAMAGE_ID, stats.attackDamage() - 1.0,
                        AttributeModifier.Operation.ADD_VALUE), net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(SPEED_ID, stats.attackSpeed() - 4.0,
                        AttributeModifier.Operation.ADD_VALUE), net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND);
        if (stats.knockback() > 0) {
            attributes.add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(KNOCKBACK_ID, stats.knockback(),
                    AttributeModifier.Operation.ADD_VALUE), net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND);
        }
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, attributes.build());
    }

    @Override
    public Component getName(ItemStack stack) {
        ToolBuildData build = stack.get(ModDataComponents.TOOL_BUILD);
        if (build == null) return Component.translatable(getDescriptionId(stack));
        return Component.literal(ToolMaterials.get(build.head()).displayName() + " " + archetype.displayName());
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        ToolBuildData build = stack.get(ModDataComponents.TOOL_BUILD);
        if (build == null || !canMine(state)) return 1.0f;
        float speed = ToolStatCalculator.calculate(archetype, build).miningSpeed();
        if (!ToolStatCalculator.isVanillaEquivalent(build) && hasMaterial(build, ToolMaterials.STONE.id())
                && state.is(BlockTags.MINEABLE_WITH_PICKAXE)) speed *= 1.05f;
        if (archetype == ToolArchetype.PICKAXE && isSticky(build)) speed *= momentumMultiplier(stack);
        if (archetype == ToolArchetype.PICKAXE && isBrittle(build)) speed *= 1.5f;
        return speed;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        ToolBuildData build = stack.get(ModDataComponents.TOOL_BUILD);
        return build != null && canMine(state) && meetsMiningLevel(state, ToolStatCalculator.calculate(archetype, build).miningLevel());
    }

    private boolean canMine(BlockState state) {
        return switch (archetype) {
            case AXE, BATTLE_AXE -> state.is(BlockTags.MINEABLE_WITH_AXE);
            case SHOVEL -> state.is(BlockTags.MINEABLE_WITH_SHOVEL);
            case PICKAXE -> state.is(BlockTags.MINEABLE_WITH_PICKAXE);
            case SWORD -> false;
        };
    }

    private static boolean meetsMiningLevel(BlockState state, int level) {
        return switch (level) {
            case 0 -> !state.is(BlockTags.INCORRECT_FOR_WOODEN_TOOL);
            case 1 -> !state.is(BlockTags.INCORRECT_FOR_STONE_TOOL);
            case 2 -> !state.is(BlockTags.INCORRECT_FOR_IRON_TOOL);
            case 3 -> !state.is(BlockTags.INCORRECT_FOR_DIAMOND_TOOL);
            default -> true;
        };
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0) {
            ToolBuildData build = stack.get(ModDataComponents.TOOL_BUILD);
            if (archetype == ToolArchetype.PICKAXE && isSticky(build)) addMomentum(stack, level.getGameTime());
            damage(stack, archetype == ToolArchetype.PICKAXE && isBrittle(build) && level.random.nextBoolean() ? 3 : 1, miner);
        }
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        ToolBuildData build = stack.get(ModDataComponents.TOOL_BUILD);
        damage(stack, archetype == ToolArchetype.SWORD && isBrittle(build) ? 4 : 2, attacker);
        return true;
    }

    private static void damage(ItemStack stack, int amount, LivingEntity entity) {
        ToolBuildData build = stack.get(ModDataComponents.TOOL_BUILD);
        if (build != null && hasMaterial(build, ToolMaterials.IRON.id()) && entity.getRandom().nextFloat() < 0.10f) return;
        stack.hurtAndBreak(amount, entity, EquipmentSlot.MAINHAND);
    }

    private static boolean hasMaterial(ToolBuildData build, ResourceLocation material) {
        return build.head().equals(material) || build.binding().equals(material) || build.grip().equals(material);
    }

    private static boolean isSticky(ToolBuildData build) {
        return build != null && build.binding().equals(ToolMaterials.SLIME.id());
    }

    private static boolean isBrittle(ToolBuildData build) {
        return build != null && build.grip().equals(ToolMaterials.BONE.id());
    }

    private static float momentumMultiplier(ItemStack stack) {
        ToolMomentum momentum = stack.get(ModDataComponents.MOMENTUM);
        return momentum == null ? 1.0f : 1.0f + momentum.stacks() / (float) MAX_MOMENTUM_STACKS;
    }

    private static void addMomentum(ItemStack stack, long gameTime) {
        ToolMomentum momentum = stack.get(ModDataComponents.MOMENTUM);
        int stacks = momentum != null && gameTime - momentum.lastMineTick() <= DIGGING_MOMENTUM_WINDOW_TICKS
                ? Math.min(MAX_MOMENTUM_STACKS, momentum.stacks() + 1) : 1;
        stack.set(ModDataComponents.MOMENTUM, new ToolMomentum(gameTime, stacks));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        ToolMomentum momentum = stack.get(ModDataComponents.MOMENTUM);
        if (!level.isClientSide && momentum != null && level.getGameTime() - momentum.lastMineTick() > DIGGING_MOMENTUM_WINDOW_TICKS) {
            stack.remove(ModDataComponents.MOMENTUM);
        }
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        return switch (archetype) {
            case PICKAXE -> ability == ItemAbilities.PICKAXE_DIG;
            case AXE, BATTLE_AXE -> ability == ItemAbilities.AXE_DIG || ability == ItemAbilities.AXE_STRIP ||
                    ability == ItemAbilities.AXE_SCRAPE || ability == ItemAbilities.AXE_WAX_OFF;
            case SHOVEL -> ability == ItemAbilities.SHOVEL_DIG;
            case SWORD -> ability == ItemAbilities.SWORD_DIG || ability == ItemAbilities.SWORD_SWEEP;
        };
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        ToolBuildData build = stack.get(ModDataComponents.TOOL_BUILD);
        return build != null && ingredient.is(ToolMaterials.get(build.head()).repairItem());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ToolBuildData build = stack.get(ModDataComponents.TOOL_BUILD);
        if (build == null) return;
        ToolStats stats = ToolStatCalculator.calculate(archetype, build);
        tooltip.add(Component.literal(ToolMaterials.get(build.head()).displayName() + " " + archetype.headRole().displayName()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal(ToolMaterials.get(build.binding()).displayName() + " Tool Binding").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal(ToolMaterials.get(build.grip()).displayName() + " Tool Grip").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        boolean weapon = archetype == ToolArchetype.SWORD || archetype == ToolArchetype.BATTLE_AXE;
        tooltip.add(weapon ? stat("Damage", stats.attackDamage()) : stat("Mining Speed", stats.miningSpeed()));
        if (!weapon) tooltip.add(Component.literal("Mining Level: " + miningLevelName(stats.miningLevel())).withStyle(ChatFormatting.BLUE));
        if (weapon) tooltip.add(stat("Attack Speed", stats.attackSpeed()));
        tooltip.add(Component.literal("Durability: " + (stack.getMaxDamage() - stack.getDamageValue()) + " / " + stack.getMaxDamage()).withStyle(ChatFormatting.BLUE));
        tooltip.add(stat("Weight", stats.weight()));
        int diamonds = ToolGems.count(build, ToolGems.DIAMOND);
        int emeralds = ToolGems.count(build, ToolGems.EMERALD);
        if (diamonds > 0) tooltip.add(Component.literal("Diamond Gems: +" + (diamonds * 25) + "% speed").withStyle(ChatFormatting.AQUA));
        if (emeralds > 0) tooltip.add(Component.literal("Emerald Gems: +" + emeralds + " Fortune / Looting").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("Traits: " + String.join(", ", ToolStatCalculator.activeTraits(build).values().stream().map(MaterialTrait::displayName).toList())).withStyle(ChatFormatting.GOLD));
        if (flag.hasShiftDown()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Head drives mining level, speed, and damage.").withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.literal("Binding drives durability and weight.").withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.literal("Grip drives handling and attack speed.").withStyle(ChatFormatting.DARK_GRAY));
            if (!build.modules().isEmpty()) tooltip.add(Component.literal("Modules: " + build.modules().size()).withStyle(ChatFormatting.DARK_AQUA));
        } else {
            tooltip.add(Component.literal("Hold Shift for component details").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static Component stat(String label, float value) {
        return Component.literal(label + ": " + String.format(Locale.ROOT, "%.2f", value)).withStyle(ChatFormatting.BLUE);
    }

    public static String miningLevelName(int level) {
        return switch (level) {
            case 0 -> "Wood";
            case 1 -> "Stone";
            case 2 -> "Iron";
            case 3 -> "Diamond";
            default -> "Hardened";
        };
    }
}
