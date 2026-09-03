package com.toolsmithsworkshop.item;

import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.tool.ComponentRole;
import com.toolsmithsworkshop.tool.ToolArchetype;
import com.toolsmithsworkshop.tool.ToolBuildData;
import com.toolsmithsworkshop.tool.ToolComponentData;
import com.toolsmithsworkshop.tool.ToolMaterial;
import com.toolsmithsworkshop.tool.ToolMaterials;
import com.toolsmithsworkshop.tool.ToolStatCalculator;
import com.toolsmithsworkshop.tool.ToolStats;
import com.toolsmithsworkshop.tool.ToolTraits;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Locale;

public final class ToolComponentItem extends Item {
    public ToolComponentItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        ToolComponentData data = stack.get(ModDataComponents.TOOL_COMPONENT);
        if (data == null) return super.getName(stack);
        return Component.literal(ToolMaterials.get(data.material()).displayName() + " " + data.role().displayName());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ToolComponentData data = stack.get(ModDataComponents.TOOL_COMPONENT);
        if (data == null) return;
        ToolMaterial material = ToolMaterials.get(data.material());
        if (!flag.hasShiftDown()) {
            tooltip.add(Component.literal("[Shift for Info]").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        ToolArchetype weaponArchetype = switch (data.role()) {
            case SWORD_BLADE -> ToolArchetype.SWORD;
            case BATTLE_AXE_HEAD -> ToolArchetype.BATTLE_AXE;
            default -> null;
        };
        ToolStats weaponStats = weaponArchetype == null ? null : ToolStatCalculator.calculate(
                weaponArchetype,
                new ToolBuildData(material.id(), ToolMaterials.WOOD.id(), ToolMaterials.WOOD.id())
        );
        tooltip.add(Component.literal("Tier " + material.workshopTier()).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("Durability: " + material.durability()).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("Mining Speed: " + String.format(Locale.ROOT, "%.2f", material.miningSpeed())).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("Attack Damage: " + String.format(Locale.ROOT, "%.2f",
                weaponStats == null ? material.attackDamage() : weaponStats.attackDamage())).withStyle(ChatFormatting.BLUE));
        if (weaponStats != null) {
            tooltip.add(Component.literal("Attack Speed: " + String.format(Locale.ROOT, "%.2f", weaponStats.attackSpeed()))
                    .withStyle(ChatFormatting.BLUE));
        }
        tooltip.add(Component.literal("Weight: " + String.format(Locale.ROOT, "%.2f", material.weight())).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("Handling: " + String.format(Locale.ROOT, "%.2f", material.handling())).withStyle(ChatFormatting.BLUE));
        if (data.role() == ComponentRole.SWORD_BLADE
                || data.role() == ComponentRole.BATTLE_AXE_HEAD
                || data.role() == ComponentRole.BINDING
                || data.role() == ComponentRole.GRIP) {
            tooltip.add(Component.literal(String.format(Locale.ROOT, "Weapon Crit Affinity: +%.1f%% rate, +%.1f%% damage",
                    ToolStatCalculator.critRateContribution(material, data.role()),
                    ToolStatCalculator.critDamageContribution(material, data.role()))).withStyle(ChatFormatting.BLUE));
        }
        tooltip.add(Component.literal("Property: " + ToolTraits.get(material.trait()).displayName()).withStyle(ChatFormatting.GOLD));
        if (material == ToolMaterials.SCULK) tooltip.add(Component.literal("Mends 1 durability per XP").withStyle(ChatFormatting.DARK_AQUA));
        if (material == ToolMaterials.CACTUS && data.role() == ComponentRole.GRIP)
            tooltip.add(Component.literal("25% self-thorns; weapons gain +7% critical rate").withStyle(ChatFormatting.GREEN));
        if (material == ToolMaterials.BONE && data.role() == ComponentRole.GRIP)
            tooltip.add(Component.literal("Swords gain +15% critical damage").withStyle(ChatFormatting.GREEN));
        if (material == ToolMaterials.SLIME && data.role() == ComponentRole.BINDING)
            tooltip.add(Component.literal("Sword custom criticals slow the target").withStyle(ChatFormatting.GREEN));
    }
}
