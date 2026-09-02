package com.toolsmithsworkshop.item;

import net.minecraft.world.item.Item;

/** Render-only item used to select an archetype-specific component shape. */
public final class ToolVisualItem extends Item {
    public ToolVisualItem(Properties properties) {
        super(properties.stacksTo(1));
    }
}
