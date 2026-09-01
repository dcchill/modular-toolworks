package com.toolsmithsworkshop.client;

import com.toolsmithsworkshop.item.ModularToolItem;
import com.toolsmithsworkshop.menu.ToolsmithingMenu;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.tool.MaterialTrait;
import com.toolsmithsworkshop.tool.ToolBuildData;
import com.toolsmithsworkshop.tool.ToolStatCalculator;
import com.toolsmithsworkshop.tool.ToolStats;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public final class ToolsmithingScreen extends AbstractContainerScreen<ToolsmithingMenu> {
    public ToolsmithingScreen(ToolsmithingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        String[] names = {"Pickaxe", "Axe"};
        for (int i = 0; i < names.length; i++) {
            int id = i;
            addRenderableWidget(Button.builder(Component.literal(names[i]), button -> click(id))
                    .bounds(leftPos + 6 + i * 56, topPos - 22, 53, 20).build());
        }
        addRenderableWidget(Button.builder(Component.literal("Assemble / Repair"), button -> click(3))
                .bounds(leftPos + 103, topPos + 61, 68, 20).build());
    }

    private void click(int id) {
        if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFC6C6C6);
        graphics.fill(leftPos + 4, topPos + 4, leftPos + imageWidth - 4, topPos + 80, 0xFF8B6B45);
        for (int[] xy : new int[][]{{26, 27}, {52, 27}, {78, 27}, {104, 53}, {52, 53}, {130, 34}}) {
            graphics.fill(leftPos + xy[0] - 1, topPos + xy[1] - 1, leftPos + xy[0] + 17, topPos + xy[1] + 17, 0xFF202020);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0xFFFFFF, false);
        graphics.drawString(font, "Head", 22, 17, 0xFFFFFF, false);
        graphics.drawString(font, "Binding", 48, 17, 0xFFFFFF, false);
        graphics.drawString(font, "Grip", 76, 17, 0xFFFFFF, false);
        graphics.drawString(font, "Hammer", 99, 70, 0xFFFFFF, false);
        graphics.drawString(font, "Repair", 48, 70, 0xFFFFFF, false);
        graphics.drawString(font, playerInventoryTitle, 8, 73, 0x404040, false);

        ItemStack preview = menu.preview();
        ToolBuildData build = preview.get(ModDataComponents.TOOL_BUILD);
        if (build != null && preview.getItem() instanceof ModularToolItem tool) {
            ToolStats stats = ToolStatCalculator.calculate(tool.archetype(), build);
            int x = 99;
            graphics.drawString(font, String.format(Locale.ROOT, "Dur %d", stats.durability()), x, 8, 0xFFFFFF, false);
            graphics.drawString(font, String.format(Locale.ROOT, "Speed %.1f", stats.miningSpeed()), x, 18, 0xFFFFFF, false);
            graphics.drawString(font, "Level " + ModularToolItem.miningLevelName(stats.miningLevel()), x, 28, 0xFFFFFF, false);
            graphics.drawString(font, String.format(Locale.ROOT, "Damage %.1f", stats.attackDamage()), x, 48, 0xFFFFFF, false);
            graphics.drawString(font, String.format(Locale.ROOT, "Atk %.2f", stats.attackSpeed()), x, 58, 0xFFFFFF, false);
            graphics.drawString(font, String.format(Locale.ROOT, "Weight %.2f", stats.weight()), x, 68, 0xFFFFFF, false);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
