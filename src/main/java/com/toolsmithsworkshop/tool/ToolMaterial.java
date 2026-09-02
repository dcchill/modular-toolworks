package com.toolsmithsworkshop.tool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record ToolMaterial(ResourceLocation id, String displayName, int workshopTier, int durability, float miningSpeed,
                           int miningLevel, float weight, float attackDamage, float handling,
                           Item repairItem, ResourceLocation trait) {
}
