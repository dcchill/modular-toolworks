package com.toolsmithsworkshop.menu;

import com.toolsmithsworkshop.registry.ModItems;
import com.toolsmithsworkshop.tool.ComponentRole;
import com.toolsmithsworkshop.tool.ToolMaterials;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BasicWorkshopMenuTest {
    @Test
    void partCostsAndStickGripAreIntentional() {
        assertEquals(3, BasicWorkshopMenu.requiredCount(ComponentRole.PICKAXE_HEAD));
        assertEquals(2, BasicWorkshopMenu.requiredCount(ComponentRole.BINDING));
        assertEquals(1, BasicWorkshopMenu.requiredCount(ComponentRole.GRIP));
        assertNull(ModItems.component(ComponentRole.GRIP, ToolMaterials.WOOD.id()));
    }
}
