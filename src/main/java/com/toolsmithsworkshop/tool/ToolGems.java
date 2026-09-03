package com.toolsmithsworkshop.tool;

import com.toolsmithsworkshop.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Socketed item identities and their shared tool effects. */
public final class ToolGems {
    public static final ResourceLocation DIAMOND = ResourceLocation.withDefaultNamespace("diamond");
    public static final ResourceLocation EMERALD = ResourceLocation.withDefaultNamespace("emerald");
    public static final ResourceLocation ENDER_PEARL = ResourceLocation.withDefaultNamespace("ender_pearl");
    public static final ResourceLocation GARNET = ToolMaterials.id("garnet");

    private ToolGems() {}

    public static boolean isGem(ItemStack stack) {
        return stack.is(Items.DIAMOND) || stack.is(Items.EMERALD) || stack.is(Items.ENDER_PEARL) || stack.is(ModItems.GARNET.get());
    }

    public static ResourceLocation id(ItemStack stack) {
        if (stack.is(Items.DIAMOND)) return DIAMOND;
        if (stack.is(Items.EMERALD)) return EMERALD;
        if (stack.is(ModItems.GARNET.get())) return GARNET;
        return ENDER_PEARL;
    }

    public static int count(ToolBuildData build, ResourceLocation gem) {
        return (int) build.modules().stream().filter(gem::equals).count();
    }

    public static float effectMultiplier(ToolBuildData build) {
        return build.head().equals(ToolMaterials.STEEL.id()) || build.binding().equals(ToolMaterials.STEEL.id()) || build.grip().equals(ToolMaterials.STEEL.id()) ? 1.25f : 1.0f;
    }

    public static int socketCount(ToolBuildData build) {
        return ToolMaterials.get(build.head()).workshopTier();
    }
}
