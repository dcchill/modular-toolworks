package com.toolsmithsworkshop.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

class ToolStatCalculatorTest {
    @Test
    void woodenShaftUsesHeadDefaults() {
        for (ToolMaterial head : ToolMaterials.values()) {
            ToolStats stats = ToolStatCalculator.calculate(ToolArchetype.PICKAXE,
                    new ToolBuildData(head.id(), ToolMaterials.WOOD.id(), ToolMaterials.WOOD.id()));

            assertEquals(head.durability(), stats.durability());
            assertEquals(head.miningSpeed(), stats.miningSpeed());
            assertEquals(head.miningLevel(), stats.miningLevel());
            assertEquals(head.attackDamage(), stats.attackDamage());
            assertEquals(ToolArchetype.PICKAXE.baseAttackSpeed(), stats.attackSpeed());
            assertEquals(0.0f, stats.knockback());
        }

        assertAxeDefaults(ToolMaterials.WOOD, 7.0f, 0.8f);
        assertAxeDefaults(ToolMaterials.STONE, 9.0f, 0.8f);
        assertAxeDefaults(ToolMaterials.IRON, 9.0f, 0.9f);
        assertAxeDefaults(ToolMaterials.GOLD, 7.0f, 1.0f);
        assertAxeDefaults(ToolMaterials.DIAMOND, 9.0f, 1.0f);
        assertAxeDefaults(ToolMaterials.NETHERITE, 10.0f, 1.0f);

        assertSwordDefaults(ToolMaterials.WOOD, 4.0f);
        assertSwordDefaults(ToolMaterials.STONE, 5.0f);
        assertSwordDefaults(ToolMaterials.IRON, 6.0f);
        assertSwordDefaults(ToolMaterials.GOLD, 4.0f);
        assertSwordDefaults(ToolMaterials.DIAMOND, 7.0f);
        assertSwordDefaults(ToolMaterials.NETHERITE, 8.0f);
    }

    private static void assertAxeDefaults(ToolMaterial head, float damage, float speed) {
        ToolStats stats = ToolStatCalculator.calculate(ToolArchetype.AXE,
                new ToolBuildData(head.id(), ToolMaterials.WOOD.id(), ToolMaterials.WOOD.id()));
        assertEquals(damage, stats.attackDamage());
        assertEquals(speed, stats.attackSpeed());
    }

    private static void assertSwordDefaults(ToolMaterial head, float damage) {
        ToolStats stats = ToolStatCalculator.calculate(ToolArchetype.SWORD,
                new ToolBuildData(head.id(), ToolMaterials.WOOD.id(), ToolMaterials.WOOD.id()));
        assertEquals(damage, stats.attackDamage());
        assertEquals(1.6f, stats.attackSpeed());
    }

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

    @Test
    void diamondGemsIncreaseMiningAndAttackSpeed() {
        ToolBuildData plain = new ToolBuildData(ToolMaterials.IRON.id(), ToolMaterials.WOOD.id(), ToolMaterials.WOOD.id());
        ToolBuildData socketed = new ToolBuildData(ToolMaterials.IRON.id(), ToolMaterials.WOOD.id(), ToolMaterials.WOOD.id(),
                List.of(ToolGems.DIAMOND));

        ToolStats base = ToolStatCalculator.calculate(ToolArchetype.PICKAXE, plain);
        ToolStats gemmed = ToolStatCalculator.calculate(ToolArchetype.PICKAXE, socketed);
        assertEquals(base.miningSpeed() * 1.25f, gemmed.miningSpeed());
        assertEquals(base.attackSpeed() * 1.25f, gemmed.attackSpeed());
    }

    @Test
    void battleAxeHitsHarderAndSlowerThanSword() {
        ToolBuildData build = new ToolBuildData(ToolMaterials.IRON.id(), ToolMaterials.WOOD.id(), ToolMaterials.WOOD.id());
        ToolStats battleAxe = ToolStatCalculator.calculate(ToolArchetype.BATTLE_AXE, build);
        ToolStats sword = ToolStatCalculator.calculate(ToolArchetype.SWORD, build);

        assertTrue(battleAxe.attackDamage() > sword.attackDamage());
        assertTrue(battleAxe.attackSpeed() < sword.attackSpeed());
    }

    @Test
    void partAffixesStackIntoDerivedAttackAndDurability() {
        ToolBuildData plain = new ToolBuildData(ToolMaterials.IRON.id(), ToolMaterials.WOOD.id(), ToolMaterials.WOOD.id());
        ToolBuildData affixed = new ToolBuildData(ToolMaterials.IRON.id(), ToolMaterials.WOOD.id(), ToolMaterials.WOOD.id(),
                List.of(), List.of(new PartStat(PartStat.Type.ATTACK, 10), new PartStat(PartStat.Type.ATTACK, 15),
                        new PartStat(PartStat.Type.FRAGILE, 20)));

        ToolStats base = ToolStatCalculator.calculate(ToolArchetype.SWORD, plain);
        ToolStats modified = ToolStatCalculator.calculate(ToolArchetype.SWORD, affixed);
        assertEquals(base.attackDamage() * 1.25f, modified.attackDamage());
        assertEquals(Math.round(base.durability() * 0.80f), modified.durability());
    }

    @Test
    void weaponAndMiningBonusesStayOnTheirArchetypes() {
        List<PartStat> affixes = List.of(new PartStat(PartStat.Type.ATTACK, 25),
                new PartStat(PartStat.Type.MINING_SPEED, 15));
        ToolBuildData plain = new ToolBuildData(ToolMaterials.IRON.id(), ToolMaterials.WOOD.id(), ToolMaterials.WOOD.id());
        ToolBuildData affixed = new ToolBuildData(ToolMaterials.IRON.id(), ToolMaterials.WOOD.id(), ToolMaterials.WOOD.id(),
                List.of(), affixes);

        ToolStats plainSword = ToolStatCalculator.calculate(ToolArchetype.SWORD, plain);
        ToolStats affixedSword = ToolStatCalculator.calculate(ToolArchetype.SWORD, affixed);
        assertEquals(plainSword.attackDamage() * 1.25f, affixedSword.attackDamage());
        assertEquals(plainSword.miningSpeed(), affixedSword.miningSpeed());

        ToolStats plainPickaxe = ToolStatCalculator.calculate(ToolArchetype.PICKAXE, plain);
        ToolStats affixedPickaxe = ToolStatCalculator.calculate(ToolArchetype.PICKAXE, affixed);
        assertEquals(plainPickaxe.attackDamage(), affixedPickaxe.attackDamage());
        assertEquals(plainPickaxe.miningSpeed() * 1.15f, affixedPickaxe.miningSpeed());
    }

    @Test
    void weaponCriticalStatsUseArchetypeAndPositionWeightedMaterials() {
        ToolBuildData build = new ToolBuildData(ToolMaterials.DIAMOND.id(), ToolMaterials.GOLD.id(), ToolMaterials.PHANTOM.id());

        ToolStats sword = ToolStatCalculator.calculate(ToolArchetype.SWORD, build);
        assertEquals(11.3f, sword.critRate(), 0.001f);
        assertEquals(71.8f, sword.critDamage(), 0.001f);

        ToolStats battleAxe = ToolStatCalculator.calculate(ToolArchetype.BATTLE_AXE, build);
        assertEquals(9.3f, battleAxe.critRate(), 0.001f);
        assertEquals(100.0f, battleAxe.critDamage(), 0.001f);

        ToolStats pickaxe = ToolStatCalculator.calculate(ToolArchetype.PICKAXE, build);
        assertEquals(0.0f, pickaxe.critRate());
        assertEquals(0.0f, pickaxe.critDamage());
    }

    @Test
    void criticalAffixesAddAndFinalValuesAreCapped() {
        ToolBuildData rateCapped = new ToolBuildData(ToolMaterials.GOLD.id(), ToolMaterials.GOLD.id(), ToolMaterials.GOLD.id(),
                List.of(), List.of(new PartStat(PartStat.Type.CRIT_RATE, 15), new PartStat(PartStat.Type.CRIT_RATE, 15),
                        new PartStat(PartStat.Type.CRIT_RATE, 15)));
        ToolBuildData damageCapped = new ToolBuildData(ToolMaterials.GOLD.id(), ToolMaterials.GOLD.id(), ToolMaterials.GOLD.id(),
                List.of(), List.of(new PartStat(PartStat.Type.CRIT_DAMAGE, 30), new PartStat(PartStat.Type.CRIT_DAMAGE, 30),
                        new PartStat(PartStat.Type.CRIT_DAMAGE, 30)));

        assertEquals(50.0f, ToolStatCalculator.calculate(ToolArchetype.SWORD, rateCapped).critRate());
        assertEquals(100.0f, ToolStatCalculator.calculate(ToolArchetype.SWORD, damageCapped).critDamage());
    }

    @Test
    void cactusAndBoneCriticalTraitsAreAdditive() {
        ToolBuildData cactus = new ToolBuildData(ToolMaterials.IRON.id(), ToolMaterials.WOOD.id(), ToolMaterials.CACTUS.id());
        ToolBuildData bone = new ToolBuildData(ToolMaterials.IRON.id(), ToolMaterials.WOOD.id(), ToolMaterials.BONE.id());

        ToolStats cactusSword = ToolStatCalculator.calculate(ToolArchetype.SWORD, cactus);
        assertEquals(14.8f, cactusSword.critRate(), 0.001f);
        assertEquals(66.3f, cactusSword.critDamage(), 0.001f);

        ToolStats boneSword = ToolStatCalculator.calculate(ToolArchetype.SWORD, bone);
        assertEquals(7.3f, boneSword.critRate(), 0.001f);
        assertEquals(83.7f, boneSword.critDamage(), 0.001f);
    }
}
