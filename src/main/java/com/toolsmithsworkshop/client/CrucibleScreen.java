package com.toolsmithsworkshop.client;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.menu.CrucibleMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class CrucibleScreen extends AbstractContainerScreen<CrucibleMenu> {
    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(ToolsmithsWorkshop.MOD_ID, "textures/gui/crucible_workshop_gui.png");
    private static final ResourceLocation LIT_PROGRESS = ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");
    private static final ResourceLocation BURN_PROGRESS = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");
    public CrucibleScreen(CrucibleMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth = 176; imageHeight = 166; }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(GUI, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        if (menu.isLit()) graphics.blitSprite(LIT_PROGRESS, 14, 14, 0, 14 - menu.litProgress(), leftPos + 44, topPos + 32 + 14 - menu.litProgress(), 14, menu.litProgress());
        graphics.blitSprite(BURN_PROGRESS, 24, 16, 0, 0, leftPos + 79, topPos + 31, menu.cookProgress(), 16);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { graphics.drawString(font, title, 8, 6, 0x404040, false); graphics.drawString(font, playerInventoryTitle, 8, 73, 0x404040, false); }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
}
