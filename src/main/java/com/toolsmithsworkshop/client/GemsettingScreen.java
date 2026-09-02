package com.toolsmithsworkshop.client;

import com.toolsmithsworkshop.menu.GemsettingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class GemsettingScreen extends AbstractContainerScreen<GemsettingMenu> {
    public GemsettingScreen(GemsettingMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth = 176; imageHeight = 166; }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFC6C6C6);
        graphics.fill(leftPos + 4, topPos + 4, leftPos + imageWidth - 4, topPos + 80, 0xFF36544B);
        for (int[] xy : new int[][]{{44, 29}, {80, 29}, {116, 29}}) graphics.fill(leftPos + xy[0] - 1, topPos + xy[1] - 1, leftPos + xy[0] + 17, topPos + xy[1] + 17, 0xFF202020);
    }

    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0xFFFFFF, false);
        graphics.drawString(font, "Tool", 40, 17, 0xFFFFFF, false);
        graphics.drawString(font, "Gem", 78, 17, 0xFFFFFF, false);
        graphics.drawString(font, "Result", 110, 17, 0xFFFFFF, false);
        graphics.drawString(font, "Sockets follow the head tier", 25, 61, 0xFFFFFF, false);
        graphics.drawString(font, playerInventoryTitle, 8, 73, 0x404040, false);
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); }
}
