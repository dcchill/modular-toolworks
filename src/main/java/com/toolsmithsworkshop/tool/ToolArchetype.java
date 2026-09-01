package com.toolsmithsworkshop.tool;

import com.mojang.serialization.Codec;

public enum ToolArchetype {
    PICKAXE("pickaxe", "Pickaxe", ComponentRole.PICKAXE_HEAD, 1.20f, 1.0f),
    AXE("axe", "Axe", ComponentRole.AXE_HEAD, 1.00f, 1.0f);

    public static final Codec<ToolArchetype> CODEC = Codec.STRING.xmap(ToolArchetype::byName, ToolArchetype::serializedName);

    private final String serializedName;
    private final String displayName;
    private final ComponentRole headRole;
    private final float baseAttackSpeed;
    private final float weightSensitivity;

    ToolArchetype(String serializedName, String displayName, ComponentRole headRole, float baseAttackSpeed, float weightSensitivity) {
        this.serializedName = serializedName;
        this.displayName = displayName;
        this.headRole = headRole;
        this.baseAttackSpeed = baseAttackSpeed;
        this.weightSensitivity = weightSensitivity;
    }

    public String serializedName() { return serializedName; }
    public String displayName() { return displayName; }
    public ComponentRole headRole() { return headRole; }
    public float baseAttackSpeed() { return baseAttackSpeed; }
    public float weightSensitivity() { return weightSensitivity; }

    private static ToolArchetype byName(String name) {
        for (ToolArchetype archetype : values()) {
            if (archetype.serializedName.equals(name)) return archetype;
        }
        throw new IllegalArgumentException("Unknown tool archetype: " + name);
    }
}
