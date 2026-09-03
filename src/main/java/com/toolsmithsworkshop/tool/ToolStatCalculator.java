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
        if (binding == ToolMaterials.PHANTOM) weight *= 0.5f;
        if (isVanillaEquivalent(build)) {
            float diamondSpeed = 1.0f + ToolGems.count(build, ToolGems.DIAMOND) * 0.25f * ToolGems.effectMultiplier(build);
            return applyPartStats(new ToolStats(head.durability(), head.miningSpeed() * diamondSpeed, head.miningLevel(),
                    defaultAttackDamage(archetype, head), defaultAttackSpeed(archetype, head) * diamondSpeed, weight, 0.0f,
                    0.0f, 0.0f),
                    archetype, build);
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
        if (archetype == ToolArchetype.BATTLE_AXE) damage += 4.0f;
        if (archetype == ToolArchetype.SWORD) damage += 2.0f;
        float diamondSpeed = 1.0f + ToolGems.count(build, ToolGems.DIAMOND) * 0.25f * ToolGems.effectMultiplier(build);
        miningSpeed *= diamondSpeed;
        attackSpeed *= diamondSpeed;

        return applyPartStats(new ToolStats(Math.max(1, Math.round(durability)), miningSpeed, miningLevel, damage,
                Math.max(0.2f, attackSpeed), weight, knockback, 0.0f, 0.0f), archetype, build);
    }

    private static ToolStats applyPartStats(ToolStats stats, ToolArchetype archetype, ToolBuildData build) {
        float attack = archetype.isWeapon() ? 1.0f + build.percent(PartStat.Type.ATTACK) / 100.0f : 1.0f;
        float miningSpeed = archetype.isWeapon() ? 1.0f : 1.0f + build.percent(PartStat.Type.MINING_SPEED) / 100.0f;
        float durability = 1.0f - build.percent(PartStat.Type.FRAGILE) / 100.0f;
        float critRate = 0.0f;
        float critDamage = 0.0f;
        if (archetype.isWeapon()) {
            ToolMaterial head = ToolMaterials.get(build.head());
            ToolMaterial binding = ToolMaterials.get(build.binding());
            ToolMaterial grip = ToolMaterials.get(build.grip());
            critRate = archetype.baseCritRate() + critRateContribution(head, archetype.headRole())
                    + critRateContribution(binding, ComponentRole.BINDING) + critRateContribution(grip, ComponentRole.GRIP)
                    + build.percent(PartStat.Type.CRIT_RATE) + ToolGems.count(build, ToolGems.GARNET) * 10.0f;
            critDamage = archetype.baseCritDamage() + critDamageContribution(head, archetype.headRole())
                    + critDamageContribution(binding, ComponentRole.BINDING) + critDamageContribution(grip, ComponentRole.GRIP)
                    + build.percent(PartStat.Type.CRIT_DAMAGE);
            if (build.grip().equals(ToolMaterials.CACTUS.id())) critRate += 7.0f;
            if (archetype == ToolArchetype.SWORD && build.grip().equals(ToolMaterials.BONE.id())) critDamage += 15.0f;
        }
        return new ToolStats(Math.max(1, Math.round(stats.durability() * durability)), stats.miningSpeed() * miningSpeed,
                stats.miningLevel(), stats.attackDamage() * attack, stats.attackSpeed(), stats.weight(), stats.knockback(),
                critRate, critDamage);
    }

    public static float critRateContribution(ToolMaterial material, ComponentRole role) {
        return material.critRateAffinity() * (role.isHead() ? 0.20f : role == ComponentRole.BINDING ? 0.30f : 0.50f);
    }

    public static float critDamageContribution(ToolMaterial material, ComponentRole role) {
        return material.critDamageAffinity() * (role.isHead() ? 0.60f : role == ComponentRole.BINDING ? 0.25f : 0.15f);
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
        if (archetype == ToolArchetype.BATTLE_AXE) return head.attackDamage() + 4.0f;
        if (head == ToolMaterials.NETHERITE) return 10.0f;
        if (head == ToolMaterials.STONE || head == ToolMaterials.IRON || head == ToolMaterials.DIAMOND) return 9.0f;
        if (head == ToolMaterials.WOOD || head == ToolMaterials.GOLD) return 7.0f;
        return head.attackDamage() + 1.0f;
    }

    private static float defaultAttackSpeed(ToolArchetype archetype, ToolMaterial head) {
        if (archetype == ToolArchetype.PICKAXE) return 1.2f;
        if (archetype == ToolArchetype.SHOVEL || archetype == ToolArchetype.BATTLE_AXE) return 1.0f;
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
