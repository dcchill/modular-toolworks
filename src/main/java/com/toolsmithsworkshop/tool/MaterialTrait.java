package com.toolsmithsworkshop.tool;

public record MaterialTrait(String displayName, float durabilityMultiplier, float miningSpeedMultiplier,
                            float weightMultiplier, float handlingBonus, float miningLevelBonus,
                            float futureEnergyUseMultiplier) {
}
