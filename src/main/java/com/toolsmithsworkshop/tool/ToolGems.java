package com.toolsmithsworkshop.tool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Socketed item identities and their shared tool effects. */
public final class ToolGems {
    public static final ResourceLocation DIAMOND = ResourceLocation.withDefaultNamespace("diamond");
    public static final ResourceLocation EMERALD = ResourceLocation.withDefaultNamespace("emerald");
    public static final ResourceLocation ENDER_PEARL = ResourceLocation.withDefaultNamespace("ender_pearl");
    public static final ResourceLocation ECHO_SHARD = ResourceLocation.withDefaultNamespace("echo_shard");

    private ToolGems() {}

    public static boolean isGem(ItemStack stack) {
        return stack.is(Items.DIAMOND) || stack.is(Items.EMERALD) || stack.is(Items.ENDER_PEARL) || stack.is(Items.ECHO_SHARD);
    }

    public static ResourceLocation id(ItemStack stack) {
        if (stack.is(Items.DIAMOND)) return DIAMOND;
        if (stack.is(Items.EMERALD)) return EMERALD;
        return stack.is(Items.ENDER_PEARL) ? ENDER_PEARL : ECHO_SHARD;
    }

    public static int count(ToolBuildData build, ResourceLocation gem) {
        return (int) build.modules().stream().filter(gem::equals).count();
    }

    public static int socketCount(ToolBuildData build) {
        return ToolMaterials.get(build.head()).workshopTier();
    }
}
