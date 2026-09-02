package com.toolsmithsworkshop.client;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.item.ModularToolItem;
import com.toolsmithsworkshop.menu.ToolsmithingMenu;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.tool.MaterialTrait;
import com.toolsmithsworkshop.tool.ToolBuildData;
import com.toolsmithsworkshop.tool.ToolStatCalculator;
import com.toolsmithsworkshop.tool.ToolStats;
import com.toolsmithsworkshop.tool.ToolArchetype;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public final class ToolsmithingScreen extends AbstractContainerScreen<ToolsmithingMenu> {
    private static final int GUI_WIDTH = 176;
    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(
            ToolsmithsWorkshop.MOD_ID, "textures/gui/toolsmith_workshop_gui.png");
    public ToolsmithingScreen(ToolsmithingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 286;
        imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.literal("Assemble / Repair"), button -> click(0))
                .bounds(leftPos + 92, topPos + 54, 78, 20).build());
    }

    private void click(int id) {
        if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        renderPanel(graphics, leftPos + GUI_WIDTH + 2, topPos + 4, 108, 108);
        graphics.blit(GUI, leftPos, topPos, 0, 0, GUI_WIDTH, imageHeight, GUI_WIDTH, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0xFFFFFF, false);
        graphics.drawString(font, playerInventoryTitle, 8, 73, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        ItemStack preview = menu.preview();
        ToolBuildData build = preview.get(ModDataComponents.TOOL_BUILD);
        int x = leftPos + GUI_WIDTH + 10;
        graphics.drawString(font, "Stats", x, topPos + 12, 0xFFFFFF, false);
        if (build != null && preview.getItem() instanceof ModularToolItem tool) {
            ToolStats stats = ToolStatCalculator.calculate(tool.archetype(), build);
            graphics.drawString(font, "Dur " + stats.durability(), x, topPos + 26, 0xFFFFFF, false);
            if (tool.archetype() == ToolArchetype.SWORD || tool.archetype() == ToolArchetype.BATTLE_AXE) {
                graphics.drawString(font, String.format(Locale.ROOT, "Damage %.1f", stats.attackDamage()), x, topPos + 40, 0xFFFFFF, false);
                graphics.drawString(font, String.format(Locale.ROOT, "Atk %.2f", stats.attackSpeed()), x, topPos + 52, 0xFFFFFF, false);
                graphics.drawString(font, String.format(Locale.ROOT, "Weight %.2f", stats.weight()), x, topPos + 64, 0xFFFFFF, false);
            } else {
                graphics.drawString(font, String.format(Locale.ROOT, "Speed %.1f", stats.miningSpeed()), x, topPos + 40, 0xFFFFFF, false);
                graphics.drawString(font, "Level " + ModularToolItem.miningLevelName(stats.miningLevel()), x, topPos + 52, 0xFFFFFF, false);
                graphics.drawString(font, String.format(Locale.ROOT, "Damage %.1f", stats.attackDamage()), x, topPos + 64, 0xFFFFFF, false);
                graphics.drawString(font, String.format(Locale.ROOT, "Atk %.2f", stats.attackSpeed()), x, topPos + 76, 0xFFFFFF, false);
                graphics.drawString(font, String.format(Locale.ROOT, "Weight %.2f", stats.weight()), x, topPos + 88, 0xFFFFFF, false);
            }
        }
        renderTooltip(graphics, mouseX, mouseY);
    }

    private static void renderPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, 0xFF1A1A1A);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFFD8D8D8);
        graphics.fill(x + 3, y + 3, x + width - 3, y + height - 3, 0xFF555555);
        graphics.fill(x + 5, y + 5, x + width - 5, y + height - 5, 0xFF202020);
    }
}
