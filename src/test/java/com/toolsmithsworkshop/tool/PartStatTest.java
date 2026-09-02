package com.toolsmithsworkshop.tool;

import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PartStatTest {
    @Test
    void rollsFavorPositiveAndLowerValues() {
        RandomSource random = RandomSource.create(42L);
        int positive = 0;
        int lowerHalf = 0;
        int upperHalf = 0;

        for (int i = 0; i < 20_000; i++) {
            PartStat stat = PartStat.roll(random, i % 2 == 0);
            assertTrue(stat.percent() >= stat.type().minimum());
            assertTrue(stat.percent() <= stat.type().maximum());
            if (stat.type().positive() && i % 2 == 0) {
                assertTrue(stat.type() == PartStat.Type.CRIT_RATE || stat.type() == PartStat.Type.CRIT_DAMAGE
                        || stat.type() == PartStat.Type.ATTACK || stat.type() == PartStat.Type.DEFENSE);
            }
            if (stat.type().positive() && i % 2 != 0) {
                assertTrue(stat.type() == PartStat.Type.MINING_SPEED || stat.type() == PartStat.Type.PROSPECTING
                        || stat.type() == PartStat.Type.PRECISION || stat.type() == PartStat.Type.RECOVERY
                        || stat.type() == PartStat.Type.FRACTURE || stat.type() == PartStat.Type.DEFENSE);
            }
            if (stat.type().positive()) positive++;
            if (stat.percent() <= (stat.type().minimum() + stat.type().maximum()) / 2) lowerHalf++;
            else upperHalf++;
        }

        assertTrue(positive > 13_000, "positive affixes should roll about 70% of the time");
        assertTrue(lowerHalf > upperHalf * 2, "high percentage rolls should be substantially rarer");
    }
}
