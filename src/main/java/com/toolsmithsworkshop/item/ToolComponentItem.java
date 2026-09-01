package com.toolsmithsworkshop.item;

import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.tool.ToolComponentData;
import com.toolsmithsworkshop.tool.ToolMaterials;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
}
