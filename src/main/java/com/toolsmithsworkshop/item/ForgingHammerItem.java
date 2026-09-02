package com.toolsmithsworkshop.item;

import com.toolsmithsworkshop.tool.ToolMaterial;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ForgingHammerItem extends Item {
    private final ToolMaterial material;

    public ForgingHammerItem(ToolMaterial material, Properties properties) {
        super(properties.stacksTo(1));
        this.material = material;
    }

    public int workshopTier() {
        return material.workshopTier();
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(material.displayName() + " Forging Hammer");
    }
}
