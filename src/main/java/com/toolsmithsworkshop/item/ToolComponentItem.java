package com.toolsmithsworkshop.item;

import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.tool.ToolComponentData;
import com.toolsmithsworkshop.tool.ToolMaterial;
import com.toolsmithsworkshop.tool.ToolMaterials;
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
        tooltip.add(Component.literal("Tier " + material.workshopTier()).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("Durability: " + material.durability()).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("Mining Speed: " + String.format(Locale.ROOT, "%.2f", material.miningSpeed())).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("Attack Damage: " + String.format(Locale.ROOT, "%.2f", material.attackDamage())).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("Weight: " + String.format(Locale.ROOT, "%.2f", material.weight())).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("Handling: " + String.format(Locale.ROOT, "%.2f", material.handling())).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("Property: " + ToolTraits.get(material.trait()).displayName()).withStyle(ChatFormatting.GOLD));
        if (material == ToolMaterials.SCULK) tooltip.add(Component.literal("Mends 4 durability per XP").withStyle(ChatFormatting.DARK_AQUA));
        if (material == ToolMaterials.CACTUS) tooltip.add(Component.literal("25% self-thorns; weapons have 50% double damage").withStyle(ChatFormatting.GREEN));
    }
}
