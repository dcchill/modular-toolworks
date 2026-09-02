package com.toolsmithsworkshop.menu;

import com.toolsmithsworkshop.registry.ModItems;
import com.toolsmithsworkshop.item.ForgingHammerItem;
import com.toolsmithsworkshop.tool.ComponentRole;
import com.toolsmithsworkshop.tool.ToolMaterials;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BasicWorkshopMenuTest {
    @Test
    void partCostsAndStickGripAreIntentional() {
        assertEquals(3, BasicWorkshopMenu.requiredCount(ComponentRole.PICKAXE_HEAD));
        assertEquals(3, BasicWorkshopMenu.requiredCount(ComponentRole.SWORD_BLADE));
        assertEquals("Sword Blade", ComponentRole.SWORD_BLADE.displayName());
        assertEquals(2, BasicWorkshopMenu.requiredCount(ComponentRole.BINDING));
        assertEquals(1, BasicWorkshopMenu.requiredCount(ComponentRole.GRIP));
        assertNull(ModItems.component(ComponentRole.GRIP, ToolMaterials.WOOD.id()));
        assertEquals(1, ToolMaterials.COPPER.workshopTier());
        assertEquals(2, ToolMaterials.IRON.workshopTier());
        assertEquals(2, ToolMaterials.GOLD.workshopTier());
        assertEquals(2, ToolMaterials.QUARTZ.workshopTier());
        assertEquals(3, ToolMaterials.DIAMOND.workshopTier());
        assertEquals(3, ToolMaterials.OBSIDIAN.workshopTier());
        assertEquals(4, ToolMaterials.NETHERITE.workshopTier());
        assertEquals(1, new ForgingHammerItem(ToolMaterials.FLINT, new net.minecraft.world.item.Item.Properties()).workshopTier());
        assertEquals(2, new ForgingHammerItem(ToolMaterials.QUARTZ, new net.minecraft.world.item.Item.Properties()).workshopTier());
        assertEquals(3, new ForgingHammerItem(ToolMaterials.OBSIDIAN, new net.minecraft.world.item.Item.Properties()).workshopTier());
        assertEquals(true, BasicWorkshopMenu.canForge(ToolMaterials.WOOD, 5, 5));
        assertEquals(true, BasicWorkshopMenu.canForge(ToolMaterials.NETHERITE, 5, 5));
        assertEquals(true, BasicWorkshopMenu.canForge(ToolMaterials.IRON, 4, 2));
        assertEquals(false, BasicWorkshopMenu.canForge(ToolMaterials.NETHERITE, 3, 4));
    }
}
