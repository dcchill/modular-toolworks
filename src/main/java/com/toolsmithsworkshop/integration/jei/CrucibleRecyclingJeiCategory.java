package com.toolsmithsworkshop.integration.jei;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class CrucibleRecyclingJeiCategory implements IRecipeCategory<CrucibleRecyclingJeiRecipe> {
    public static final RecipeType<CrucibleRecyclingJeiRecipe> TYPE = RecipeType.create(ToolsmithsWorkshop.MOD_ID, "crucible_recycling", CrucibleRecyclingJeiRecipe.class);
    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(ToolsmithsWorkshop.MOD_ID, "textures/gui/crucible_workshop_jei.png");
    private final IDrawableStatic background;
    private final IDrawable icon;

    public CrucibleRecyclingJeiCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(GUI, 0, 0, 176, 82);
        icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.CRUCIBLE.get()));
    }
    @Override public RecipeType<CrucibleRecyclingJeiRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("jei.toolsmiths_workshop.crucible_recycling"); }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, CrucibleRecyclingJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 74, 29).addItemStack(recipe.component());
        builder.addSlot(RecipeIngredientRole.INPUT, 44, 48).addItemStack(new ItemStack(net.minecraft.world.item.Items.LAVA_BUCKET));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 29).addItemStack(recipe.output());
    }
}
