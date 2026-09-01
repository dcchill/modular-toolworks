package com.toolsmithsworkshop.tool;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.Map;

/** One table is the only place a built-in material's base numbers are defined. */
public final class ToolMaterials {
    private static final Map<ResourceLocation, ToolMaterial> MATERIALS = new LinkedHashMap<>();

    public static final ToolMaterial WOOD = add("wood", "Wood", 59, 2.0f, 0, 0.65f, 2.0f, 0.15f, Items.OAK_PLANKS, "familiar");
    public static final ToolMaterial STONE = add("stone", "Stone", 131, 4.0f, 1, 1.25f, 3.0f, -0.05f, Items.COBBLESTONE, "crude");
    public static final ToolMaterial COPPER = add("copper", "Copper", 210, 5.2f, 1, 1.65f, 3.5f, 0.05f, Items.COPPER_INGOT, "conductive");
    public static final ToolMaterial IRON = add("iron", "Iron", 250, 6.0f, 2, 1.55f, 4.0f, 0.0f, Items.IRON_INGOT, "reliable");
    public static final ToolMaterial GOLD = add("gold", "Gold", 32, 12.0f, 0, 1.20f, 2.5f, 0.35f, Items.GOLD_INGOT, "refined");
    public static final ToolMaterial DIAMOND = add("diamond", "Diamond", 1561, 8.0f, 3, 1.45f, 5.0f, 0.10f, Items.DIAMOND, "hardened");
    public static final ToolMaterial NETHERITE = add("netherite", "Netherite", 2031, 9.0f, 4, 1.70f, 6.0f, 0.12f, Items.NETHERITE_INGOT, "hardened");

    private ToolMaterials() {}

    private static ToolMaterial add(String name, String displayName, int durability, float speed, int level,
                                    float weight, float damage, float handling, net.minecraft.world.item.Item repair,
                                    String trait) {
        ResourceLocation id = id(name);
        ToolMaterial material = new ToolMaterial(id, displayName, durability, speed, level, weight, damage,
                handling, repair, id(trait));
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
