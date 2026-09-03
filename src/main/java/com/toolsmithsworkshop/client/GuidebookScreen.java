package com.toolsmithsworkshop.client;

import com.toolsmithsworkshop.menu.GuidebookMenu;
import com.toolsmithsworkshop.tool.ToolArchetype;
import com.toolsmithsworkshop.tool.ToolMaterial;
import com.toolsmithsworkshop.tool.ToolMaterials;
import com.toolsmithsworkshop.tool.ToolTraits;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public final class GuidebookScreen extends AbstractContainerScreen<GuidebookMenu> {
    private Section section = Section.MATERIALS;
    private int page;

    public GuidebookScreen(GuidebookMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 320;
        imageHeight = 230;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.literal("Materials"), button -> select(Section.MATERIALS)).bounds(leftPos + 12, topPos + 28, 88, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Gems"), button -> select(Section.GEMS)).bounds(leftPos + 116, topPos + 28, 88, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Tools"), button -> select(Section.TOOLS)).bounds(leftPos + 220, topPos + 28, 88, 20).build());
        addRenderableWidget(Button.builder(Component.literal("<"), button -> { if (page > 0) page--; }).bounds(leftPos + 12, topPos + 198, 22, 20).build());
        addRenderableWidget(Button.builder(Component.literal(">"), button -> { if (page < pages() - 1) page++; }).bounds(leftPos + 286, topPos + 198, 22, 20).build());
    }

    private void select(Section next) { section = next; page = 0; }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF20170F);
        graphics.fill(leftPos + 3, topPos + 3, leftPos + imageWidth - 3, topPos + imageHeight - 3, 0xFFE8D5A8);
        graphics.fill(leftPos + 6, topPos + 6, leftPos + imageWidth - 6, topPos + imageHeight - 6, 0xFFF5E8C6);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 12, 10, 0xFF38200D, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        List<Component> lines = lines();
        int y = topPos + 56;
        for (Component line : lines) {
            for (var wrapped : font.split(line, imageWidth - 28)) {
                graphics.drawString(font, wrapped, leftPos + 14, y, 0xFF38200D, false);
                y += 10;
            }
            y += 3;
        }
        graphics.drawCenteredString(font, "Page " + (page + 1) + " / " + pages(), leftPos + imageWidth / 2, topPos + 204, 0xFF38200D);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private int pages() {
        if (section == Section.MATERIALS) return Math.max(1, (int) Math.ceil(materials().size() / 2.0));
        return section == Section.TOOLS ? 3 : 1;
    }

    private List<Component> lines() {
        List<Component> lines = new ArrayList<>();
        if (section == Section.MATERIALS) {
            List<ToolMaterial> materials = materials();
            int start = page * 2;
            for (int i = start; i < Math.min(start + 2, materials.size()); i++) {
                ToolMaterial material = materials.get(i);
                lines.add(Component.literal(material.displayName() + " — Tier " + material.workshopTier()).withStyle(ChatFormatting.DARK_AQUA));
                lines.add(Component.literal(String.format("Durability %d | Speed %.1f | Mining %d | Weight %.2f", material.durability(), material.miningSpeed(), material.miningLevel(), material.weight())));
                lines.add(Component.literal("Trait: " + ToolTraits.get(material.trait()).displayName() + " — " + ability(material)).withStyle(ChatFormatting.DARK_GRAY));
            }
        } else if (section == Section.GEMS) {
            lines.add(Component.literal("Socketed Gems").withStyle(ChatFormatting.DARK_AQUA));
            lines.add(Component.literal("Diamond: +25% mining and attack speed per gem."));
            lines.add(Component.literal("Emerald: +1 Fortune and Looting per gem."));
            lines.add(Component.literal("Ender Pearl: mined drops teleport to you."));
            lines.add(Component.literal("Garnet: weapons gain +10% critical rate; mining tools gain bonus ore XP."));
            lines.add(Component.literal("Sockets equal the head material's workshop tier. Steel's Adaptable trait improves socket effects by 25%.").withStyle(ChatFormatting.DARK_GRAY));
        } else if (page == 0) {
            lines.add(Component.literal("Building Tools").withStyle(ChatFormatting.DARK_AQUA));
            lines.add(Component.literal("Head controls mining level, most mining speed, and most damage. Binding controls most durability and weight. Grip controls most handling and many special effects.").withStyle(ChatFormatting.DARK_GRAY));
            for (ToolArchetype archetype : ToolArchetype.values()) lines.add(Component.literal(archetype.displayName() + ": " + toolDescription(archetype)));
            lines.add(Component.literal("Weapons can critically strike. Mining tools must meet their head's mining level to obtain correct block drops.").withStyle(ChatFormatting.DARK_GRAY));
        } else if (page == 1) {
            lines.add(Component.literal("Understanding Tool Stats").withStyle(ChatFormatting.DARK_AQUA));
            lines.add(Component.literal("Durability is the number of uses before the tool breaks. The binding contributes 50% of the final durability, the head 35%, and the grip 15%."));
            lines.add(Component.literal("Mining Speed controls how quickly suitable blocks break. The head supplies 82% of it; lighter tools receive +5% speed, while heavy tools lose 6%."));
            lines.add(Component.literal("Mining Level comes from the head. It determines which blocks can drop their normal loot."));
        } else {
            lines.add(Component.literal("Understanding Tool Stats").withStyle(ChatFormatting.DARK_AQUA));
            lines.add(Component.literal("Damage is mainly from the head. Heavy tools gain 8% damage and knockback, but lose attack and mining speed."));
            lines.add(Component.literal("Weight combines all three components. Low weight improves speed and attack speed; high weight makes tools slower but stronger."));
            lines.add(Component.literal("Handling primarily comes from the grip and changes attack speed. Critical rate and damage come from weapon components, gems, and special materials.").withStyle(ChatFormatting.DARK_GRAY));
        }
        return lines;
    }

    private static List<ToolMaterial> materials() {
        List<ToolMaterial> materials = new ArrayList<>();
        ToolMaterials.values().forEach(materials::add);
        return materials;
    }

    private static String ability(ToolMaterial material) {
        if (material == ToolMaterials.WOOD) return "Lower weight and a small handling bonus.";
        if (material == ToolMaterials.STONE || material == ToolMaterials.FLINT) return "10% less durability. Flint is the faster, lighter option.";
        if (material == ToolMaterials.COPPER) return "5% more mining speed.";
        if (material == ToolMaterials.SLIME) return "Binding only. Pickaxes build mining momentum to +100% speed; sword criticals slow targets.";
        if (material == ToolMaterials.BONE) return "Grip only. Pickaxes gain 50% speed; swords gain +15% critical damage, but uses may cost extra durability.";
        if (material == ToolMaterials.CACTUS) return "Grip only. Very fast; weapons gain +7% crit rate, but each use has a 25% chance to hurt the holder.";
        if (material == ToolMaterials.PHANTOM) return "Binding only. Halves total tool weight, making it excellent for fast, light tools.";
        if (material == ToolMaterials.IRON || material == ToolMaterials.QUARTZ) return "Balanced, no special downside.";
        if (material == ToolMaterials.GOLD) return "15% less durability, 8% more speed, and +0.12 handling.";
        if (material == ToolMaterials.DIAMOND || material == ToolMaterials.OBSIDIAN || material == ToolMaterials.NETHERITE) return "10% more durability and +1 mining level from the head.";
        if (material == ToolMaterials.STEEL) return "Socketed gem effects are 25% stronger.";
        if (material == ToolMaterials.BLAZE_STEEL) return "Hits ignite mobs; mined blocks autosmelt.";
        if (material == ToolMaterials.SCULKITE) return "Weapons chain attacks; mining tools veinmine connected ore.";
        if (material == ToolMaterials.SCULK) return "Sculk bindings repair from collected XP.";
        if (material == ToolMaterials.SOUL_STEEL) return "Soul Surge: weapons build up to 9 x1.25 damage stacks; mining tools have a 33% chance to build up to 5 x1.25 speed stacks.";
        if (material == ToolMaterials.ROSE_GOLD) return "13% more speed, +0.12 handling, and high critical affinities.";
        return "See the tool tooltip for active effects.";
    }

    private static String toolDescription(ToolArchetype archetype) {
        return switch (archetype) {
            case PICKAXE -> "mines stone and ores.";
            case AXE -> "cuts wood and serves as a heavy melee tool.";
            case BATTLE_AXE -> "slow, high-damage weapon with critical potential.";
            case SHOVEL -> "digs soil, sand, and gravel.";
            case SWORD -> "fast weapon with critical potential.";
        };
    }

    private enum Section { MATERIALS, GEMS, TOOLS }
}
