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

        float weight = weightedWeight(head, 1.0f) + weightedWeight(binding, 0.75f) + weightedWeight(grip, 0.40f);
        if (isVanillaEquivalent(build)) {
            return new ToolStats(head.durability(), head.miningSpeed(), head.miningLevel(),
                    defaultAttackDamage(archetype, head), defaultAttackSpeed(archetype, head), weight, 0.0f);
        }

        float durability = head.durability() * 0.35f + binding.durability() * 0.50f + grip.durability() * 0.15f;
        float miningSpeed = head.miningSpeed() * 0.82f + binding.miningSpeed() * 0.10f + grip.miningSpeed() * 0.08f;
        float damage = head.attackDamage() * 0.78f + binding.attackDamage() * 0.14f + grip.attackDamage() * 0.08f;
        float handling = grip.handling() * 0.70f + binding.handling() * 0.20f + head.handling() * 0.10f;

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
        if (archetype == ToolArchetype.SWORD) damage += 2.0f;

        return new ToolStats(Math.max(1, Math.round(durability)), miningSpeed, miningLevel, damage,
                Math.max(0.2f, attackSpeed), weight, knockback);
    }

    private static float weightedWeight(ToolMaterial material, float roleFactor) {
        return material.weight() * roleFactor * ToolTraits.get(material.trait()).weightMultiplier();
    }

    public static boolean isVanillaEquivalent(ToolBuildData build) {
        return build.binding().equals(ToolMaterials.WOOD.id()) && build.grip().equals(ToolMaterials.WOOD.id());
    }

    private static float defaultAttackDamage(ToolArchetype archetype, ToolMaterial head) {
        if (archetype == ToolArchetype.PICKAXE || archetype == ToolArchetype.SHOVEL) return head.attackDamage();
        if (archetype == ToolArchetype.SWORD) {
            return head == ToolMaterials.GOLD ? 4.0f : head.attackDamage() + 2.0f;
        }
        if (head == ToolMaterials.NETHERITE) return 10.0f;
        if (head == ToolMaterials.STONE || head == ToolMaterials.IRON || head == ToolMaterials.DIAMOND) return 9.0f;
        if (head == ToolMaterials.WOOD || head == ToolMaterials.GOLD) return 7.0f;
        return head.attackDamage() + 1.0f;
    }

    private static float defaultAttackSpeed(ToolArchetype archetype, ToolMaterial head) {
        if (archetype == ToolArchetype.PICKAXE) return 1.2f;
        if (archetype == ToolArchetype.SHOVEL) return 1.0f;
        if (archetype == ToolArchetype.SWORD) return 1.6f;
        if (head == ToolMaterials.WOOD || head == ToolMaterials.STONE) return 0.8f;
        if (head == ToolMaterials.IRON) return 0.9f;
        return 1.0f;
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
