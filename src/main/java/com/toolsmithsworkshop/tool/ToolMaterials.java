package com.toolsmithsworkshop.tool;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.Map;

/** One table is the only place a built-in material's base numbers are defined. */
public final class ToolMaterials {
    private static final Map<ResourceLocation, ToolMaterial> MATERIALS = new LinkedHashMap<>();

    public static final ToolMaterial WOOD = add("wood", "Wood", 1, 59, 2.0f, 0, 0.65f, 2.0f, 0.15f, 2, 10, Items.OAK_PLANKS, "familiar");
    public static final ToolMaterial STONE = add("stone", "Stone", 1, 131, 4.0f, 1, 1.25f, 3.0f, -0.05f, 0, 24, Items.COBBLESTONE, "crude");
    public static final ToolMaterial FLINT = add("flint", "Flint", 1, 160, 5.2f, 1, 0.85f, 3.5f, 0.08f, 3, 24, Items.FLINT, "crude");
    public static final ToolMaterial COPPER = add("copper", "Copper", 1, 210, 5.2f, 1, 1.65f, 3.5f, 0.05f, 2, 16, Items.COPPER_INGOT, "conductive");
    public static final ToolMaterial SLIME = add("slime", "Slime", 2, 225, 7.0f, 0, 0.45f, 2.5f, 0.20f, 5, 8, Items.SLIME_BALL, "sticky");
    public static final ToolMaterial BONE = add("bone", "Bone", 2, 90, 3.0f, 0, 0.60f, 3.0f, 0.05f, 3, 28, Items.BONE, "brittle");
    public static final ToolMaterial CACTUS = add("cactus", "Cactus", 1, 80, 20.0f, 0, 0.45f, 2.5f, 0.10f, 4, 12, Items.CACTUS, "spiky");
    public static final ToolMaterial PHANTOM = add("phantom", "Phantom", 2, 100, 4.0f, 0, 0.25f, 2.0f, 0.15f, 7, 4, Items.PHANTOM_MEMBRANE, "winged");
    public static final ToolMaterial IRON = add("iron", "Iron", 2, 250, 6.0f, 2, 1.55f, 4.0f, 0.0f, 1, 20, Items.IRON_INGOT, "reliable");
    public static final ToolMaterial SCULK = add("sculk", "Sculk", 4, 200, 5.5f, 2, 0.30f, 3.0f, 0.15f, 4, 16, Items.SCULK, "resonant");
    public static final ToolMaterial QUARTZ = add("quartz", "Quartz", 2, 190, 6.0f, 2, 1.40f, 4.0f, 0.04f, 3, 24, Items.QUARTZ, "reliable");
    public static final ToolMaterial GOLD = add("gold", "Gold", 2, 32, 12.0f, 0, 1.20f, 2.5f, 0.35f, 8, 8, Items.GOLD_INGOT, "refined");
    public static final ToolMaterial DIAMOND = add("diamond", "Diamond", 3, 1561, 8.0f, 3, 1.45f, 5.0f, 0.10f, 2, 32, Items.DIAMOND, "hardened");
    public static final ToolMaterial ROSE_GOLD = add("rose_gold", "Rose Gold", 3, 1000, 8.0f, 3, 1.20f, 4.5f, 0.18f, 8, 24, ModItems.ROSE_GOLD_INGOT, "rose_gold");
    public static final ToolMaterial STEEL = add("steel", "Steel", 3, 1700, 7.8f, 3, 1.85f, 5.5f, 0.02f, 1, 40, ModItems.STEEL_INGOT, "adaptable");
    public static final ToolMaterial BLAZE_STEEL = add("blaze_steel", "Blaze Steel", 3, 1850, 8.2f, 3, 1.80f, 6.0f, 0.05f, 2, 45, ModItems.BLAZE_STEEL_INGOT, "overheated");
    public static final ToolMaterial OBSIDIAN = add("obsidian", "Obsidian", 3, 1800, 7.5f, 3, 2.00f, 5.5f, -0.08f, 0, 40, Items.OBSIDIAN, "hardened");
    public static final ToolMaterial NETHERITE = add("netherite", "Netherite", 4, 2031, 9.0f, 4, 1.70f, 6.0f, 0.12f, 1, 36, Items.NETHERITE_INGOT, "hardened");
    public static final ToolMaterial SCULKITE = add("sculkite", "Sculkite", 4, 2400, 9.5f, 4, 1.60f, 6.5f, 0.15f, 2, 42, ModItems.SCULKITE_INGOT, "resonant");
    public static final ToolMaterial SOUL_STEEL = add("soul_steel", "Soul Steel", 3, 1500, 6.0f, 3, 1.55f, 2.0f, 0.05f, 1, 20, ModItems.SOUL_STEEL_INGOT, "soul_surge");

    private ToolMaterials() {}

    private static ToolMaterial add(String name, String displayName, int workshopTier, int durability, float speed, int level,
                                    float weight, float damage, float handling, float critRate, float critDamage,
                                    net.minecraft.world.item.Item repair,
                                    String trait) {
        return add(name, displayName, workshopTier, durability, speed, level, weight, damage, handling, critRate, critDamage, () -> repair, trait);
    }

    private static ToolMaterial add(String name, String displayName, int workshopTier, int durability, float speed, int level,
                                    float weight, float damage, float handling, float critRate, float critDamage,
                                    java.util.function.Supplier<? extends net.minecraft.world.item.Item> repair,
                                    String trait) {
        ResourceLocation id = id(name);
        ToolMaterial material = new ToolMaterial(id, displayName, workshopTier, durability, speed, level, weight, damage,
                handling, critRate, critDamage, repair, id(trait));
        MATERIALS.put(id, material);
        return material;
    }

    public static ToolMaterial get(ResourceLocation id) {
        ToolMaterial material = MATERIALS.get(id);
        if (material == null) throw new IllegalArgumentException("Unknown tool material: " + id);
        return material;
    }

    public static Iterable<ToolMaterial> values() {
        return MATERIALS.values();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ToolsmithsWorkshop.MOD_ID, path);
    }
}
