package com.toolsmithsworkshop.tool;

import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

/** All component-position, trait, archetype, and weight math routes through here. */
public final class ToolStatCalculator {
    private ToolStatCalculator() {}

    public static ToolStats calculate(ToolArchetype archetype, ToolBuildData build) {
        ToolMaterial head = ToolMaterials.get(build.head());
        ToolMaterial binding = ToolMaterials.get(build.binding());
        ToolMaterial grip = ToolMaterials.get(build.grip());

        float durability = head.durability() * 0.35f + binding.durability() * 0.50f + grip.durability() * 0.15f;
        float miningSpeed = head.miningSpeed() * 0.82f + binding.miningSpeed() * 0.10f + grip.miningSpeed() * 0.08f;
        float damage = head.attackDamage() * 0.78f + binding.attackDamage() * 0.14f + grip.attackDamage() * 0.08f;
        float handling = grip.handling() * 0.70f + binding.handling() * 0.20f + head.handling() * 0.10f;

        float weight = weightedWeight(head, 1.0f) + weightedWeight(binding, 0.75f) + weightedWeight(grip, 0.40f);
        int miningLevel = head.miningLevel();
        for (ToolMaterial material : new ToolMaterial[]{head, binding, grip}) {
            MaterialTrait trait = ToolTraits.get(material.trait());
            durability *= trait.durabilityMultiplier();
            miningSpeed *= trait.miningSpeedMultiplier();
            handling += trait.handlingBonus();
        }
        miningLevel = Math.min(4, miningLevel + Math.round(ToolTraits.get(head.trait()).miningLevelBonus()));

        float normalizedWeight = weight * archetype.weightSensitivity();
        float attackSpeed = archetype.baseAttackSpeed() + handling;
        float knockback = 0;
        if (normalizedWeight < 2.2f) {
            miningSpeed *= 1.05f;
            attackSpeed += 0.12f;
            damage *= 0.95f;
        } else if (normalizedWeight > 3.6f) {
            miningSpeed *= 0.94f;
            attackSpeed -= 0.16f;
            damage *= 1.08f;
            knockback = Math.min(0.75f, (normalizedWeight - 3.6f) * 0.22f);
        }
        if (archetype == ToolArchetype.AXE) damage += 1.0f;

        return new ToolStats(Math.max(1, Math.round(durability)), miningSpeed, miningLevel, damage,
                Math.max(0.2f, attackSpeed), weight, knockback);
    }

    private static float weightedWeight(ToolMaterial material, float roleFactor) {
        return material.weight() * roleFactor * ToolTraits.get(material.trait()).weightMultiplier();
    }

    public static Map<ComponentRole, ToolMaterial> materials(ToolArchetype archetype, ToolBuildData build) {
        Map<ComponentRole, ToolMaterial> materials = new LinkedHashMap<>();
        materials.put(archetype.headRole(), ToolMaterials.get(build.head()));
        materials.put(ComponentRole.BINDING, ToolMaterials.get(build.binding()));
        materials.put(ComponentRole.GRIP, ToolMaterials.get(build.grip()));
        return materials;
    }

    public static Map<ResourceLocation, MaterialTrait> activeTraits(ToolBuildData build) {
        Map<ResourceLocation, MaterialTrait> traits = new LinkedHashMap<>();
        for (ResourceLocation id : new ResourceLocation[]{build.head(), build.binding(), build.grip()}) {
            ToolMaterial material = ToolMaterials.get(id);
            traits.putIfAbsent(material.trait(), ToolTraits.get(material.trait()));
        }
        return traits;
    }
}
