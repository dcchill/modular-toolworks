package com.toolsmithsworkshop.tool;

import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

/** Trait definitions stay independent from items and tool archetypes. */
public final class ToolTraits {
    private static final Map<ResourceLocation, MaterialTrait> TRAITS = new LinkedHashMap<>();

    static {
        add("familiar", "Familiar", 1.00f, 1.00f, 0.85f, 0.03f, 0, 1.00f);
        add("crude", "Crude", 0.90f, 1.00f, 1.00f, 0.00f, 0, 1.00f);
        // The unused energy hook is deliberate: powered tools can consume this trait without changing saved tool data.
        add("conductive", "Conductive", 1.00f, 1.05f, 1.00f, 0.00f, 0, 0.95f);
        add("reliable", "Reliable", 1.00f, 1.00f, 1.00f, 0.00f, 0, 1.00f);
        add("refined", "Refined", 0.85f, 1.08f, 1.00f, 0.12f, 0, 1.00f);
        add("rose_gold", "Rose Gold", 1.00f, 1.13f, 1.00f, 0.12f, 0, 0.95f);
        add("adaptable", "Adaptable", 1.00f, 1.00f, 1.00f, 0.00f, 0, 1.00f);
        add("overheated", "Overheated", 1.00f, 1.00f, 1.00f, 0.00f, 0, 1.00f);
        add("hardened", "Hardened", 1.10f, 1.00f, 1.00f, 0.00f, 1, 1.00f);
        add("sticky", "Sticky", 1.00f, 1.00f, 1.00f, 0.00f, 0, 1.00f);
        add("brittle", "Brittle", 1.00f, 1.00f, 1.00f, 0.00f, 0, 1.00f);
        add("spiky", "Spiky", 1.00f, 1.00f, 1.00f, 0.00f, 0, 1.00f);
        add("winged", "Winged", 1.00f, 1.00f, 1.00f, 0.00f, 0, 1.00f);
        add("resonant", "Resonant", 1.00f, 1.00f, 1.00f, 0.00f, 0, 1.00f);
    }

    private ToolTraits() {}

    private static void add(String id, String name, float durability, float speed, float weight, float handling,
                            float level, float energyUse) {
        TRAITS.put(ToolMaterials.id(id), new MaterialTrait(name, durability, speed, weight, handling, level, energyUse));
    }

    public static MaterialTrait get(ResourceLocation id) {
        return TRAITS.get(id);
    }
}
