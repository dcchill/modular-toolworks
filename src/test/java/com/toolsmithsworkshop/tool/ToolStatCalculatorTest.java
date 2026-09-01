package com.toolsmithsworkshop.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToolStatCalculatorTest {
    @Test
    void componentPositionChangesStats() {
        ToolStats copperHead = ToolStatCalculator.calculate(ToolArchetype.PICKAXE,
                new ToolBuildData(ToolMaterials.COPPER.id(), ToolMaterials.IRON.id(), ToolMaterials.WOOD.id()));
        ToolStats ironHead = ToolStatCalculator.calculate(ToolArchetype.PICKAXE,
                new ToolBuildData(ToolMaterials.IRON.id(), ToolMaterials.COPPER.id(), ToolMaterials.WOOD.id()));

        assertNotEquals(copperHead.miningSpeed(), ironHead.miningSpeed());
        assertNotEquals(copperHead.durability(), ironHead.durability());
    }

    @Test
    void weightProducesTradeoffs() {
        ToolStats light = ToolStatCalculator.calculate(ToolArchetype.PICKAXE,
                new ToolBuildData(ToolMaterials.WOOD.id(), ToolMaterials.WOOD.id(), ToolMaterials.WOOD.id()));
        ToolStats heavy = ToolStatCalculator.calculate(ToolArchetype.PICKAXE,
                new ToolBuildData(ToolMaterials.COPPER.id(), ToolMaterials.COPPER.id(), ToolMaterials.STONE.id()));

        assertTrue(light.attackSpeed() > heavy.attackSpeed());
        assertTrue(heavy.attackDamage() > light.attackDamage());
    }
}
