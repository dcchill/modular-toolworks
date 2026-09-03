package com.toolsmithsworkshop.integration.jei;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.registry.ModBlocks;
import com.toolsmithsworkshop.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

@JeiPlugin
public final class ToolsmithsWorkshopJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(ToolsmithsWorkshop.MOD_ID, "jei_plugin");
    @Override public ResourceLocation getPluginUid() { return UID; }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) { registration.addRecipeCategories(new CrucibleJeiCategory(registration.getJeiHelpers().getGuiHelper())); }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CrucibleJeiCategory.TYPE, List.of(
                new CrucibleJeiRecipe(new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.NETHERITE_SCRAP), new ItemStack(Items.NETHERITE_INGOT)),
                new CrucibleJeiRecipe(new ItemStack(Items.COPPER_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(ModItems.ROSE_GOLD_INGOT.get())),
                new CrucibleJeiRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(Items.COAL_BLOCK), new ItemStack(ModItems.STEEL_INGOT.get())),
                new CrucibleJeiRecipe(new ItemStack(ModItems.STEEL_INGOT.get()), new ItemStack(Items.BLAZE_POWDER), new ItemStack(ModItems.BLAZE_STEEL_INGOT.get())),
                new CrucibleJeiRecipe(new ItemStack(Items.NETHERITE_INGOT), new ItemStack(Items.ECHO_SHARD), new ItemStack(ModItems.SCULKITE_INGOT.get()))));
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) { registration.addRecipeCatalysts(CrucibleJeiCategory.TYPE, ModBlocks.CRUCIBLE.get()); }
}
