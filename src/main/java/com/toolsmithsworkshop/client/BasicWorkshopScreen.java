package com.toolsmithsworkshop.client;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.menu.BasicWorkshopMenu;
import com.toolsmithsworkshop.registry.ModItems;
import com.toolsmithsworkshop.tool.ComponentRole;
import com.toolsmithsworkshop.tool.ToolMaterials;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class BasicWorkshopScreen extends AbstractContainerScreen<BasicWorkshopMenu> {
    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(
            ToolsmithsWorkshop.MOD_ID, "textures/gui/basic_workbench_gui.png");
    private static final ItemStack[] PART_ICONS = {
            icon(ComponentRole.PICKAXE_HEAD), icon(ComponentRole.AXE_HEAD), icon(ComponentRole.SHOVEL_HEAD),
            icon(ComponentRole.SWORD_BLADE), icon(ComponentRole.BATTLE_AXE_HEAD),
            icon(ComponentRole.BINDING), icon(ComponentRole.GRIP)
    };
    private final List<Button> partButtons = new ArrayList<>();

    public BasicWorkshopScreen(BasicWorkshopMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        partButtons.clear();
        for (int i = 0; i < BasicWorkshopMenu.PARTS.length; i++) {
            int id = i;
            Button button = Button.builder(Component.empty(), ignored -> select(id))
                    .bounds(leftPos - 92 + (i % 3) * 28, topPos + 24 + (i / 3) * 28, 26, 26)
                    .tooltip(net.minecraft.client.gui.components.Tooltip.create(
                            Component.literal(BasicWorkshopMenu.PARTS[i].displayName())))
                    .build();
            partButtons.add(button);
            addRenderableWidget(button);
        }
    }

    private void select(int id) {
        if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        renderPanel(graphics, leftPos - 98, topPos + 4, 96, 108);
        renderPanel(graphics, leftPos + imageWidth + 2, topPos + 4, 108, 108);
        graphics.blit(GUI, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, 8, 73, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        for (int i = 0; i < partButtons.size(); i++) {
            partButtons.get(i).active = menu.selectedPart() != BasicWorkshopMenu.PARTS[i];
        }
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, "Parts", leftPos - 50, topPos + 11, 0xFFFFFF);
        graphics.drawString(font, "Tier " + menu.workshopTier(), leftPos + imageWidth + 10, topPos + 12, 0xFFFFFF, false);
        for (int i = 0; i < partButtons.size(); i++) {
            Button button = partButtons.get(i);
            graphics.renderItem(PART_ICONS[i], button.getX() + 5, button.getY() + 5);
        }

        int infoX = leftPos + imageWidth + 10;
        graphics.drawString(font, menu.selectedPart().displayName(), infoX, topPos + 24, 0xFFFFFF, false);
        graphics.drawString(font, "Cost", infoX, topPos + 42, 0xA0A0A0, false);
        graphics.drawString(font, BasicWorkshopMenu.requiredCount(menu.selectedPart()) + " material", infoX, topPos + 54,
                0xFFFFFF, false);
        ItemStack input = menu.getSlot(0).getItem();
        if (!input.isEmpty()) {
            graphics.drawString(font, "Using", infoX, topPos + 71, 0xA0A0A0, false);
            graphics.drawString(font, font.plainSubstrByWidth(input.getHoverName().getString(), 92), infoX, topPos + 83,
                    0xFFFFFF, false);
            for (var candidate : ToolMaterials.values()) {
                if (input.is(candidate.repairItem()) || input.is(net.minecraft.tags.ItemTags.PLANKS) && candidate == ToolMaterials.WOOD) {
                    graphics.drawString(font, "Requires Tier " + candidate.workshopTier(), infoX, topPos + 95, 0xA0A0A0, false);
                    break;
                }
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

    private static ItemStack icon(ComponentRole role) {
        return new ItemStack(ModItems.component(role, ToolMaterials.IRON.id()).get());
    }
}
