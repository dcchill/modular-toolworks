package com.toolsmithsworkshop.tool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Socketed item identities and their shared tool effects. */
public final class ToolGems {
    public static final ResourceLocation DIAMOND = ResourceLocation.withDefaultNamespace("diamond");
    public static final ResourceLocation EMERALD = ResourceLocation.withDefaultNamespace("emerald");

    private ToolGems() {}

    public static boolean isGem(ItemStack stack) {
        return stack.is(Items.DIAMOND) || stack.is(Items.EMERALD);
    }

    public static ResourceLocation id(ItemStack stack) {
        return stack.is(Items.DIAMOND) ? DIAMOND : EMERALD;
    }

    public static int count(ToolBuildData build, ResourceLocation gem) {
        return (int) build.modules().stream().filter(gem::equals).count();
    }

    public static int socketCount(ToolBuildData build) {
        return ToolMaterials.get(build.head()).workshopTier();
    }
}
