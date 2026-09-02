package com.toolsmithsworkshop.tool;

import com.mojang.serialization.Codec;

public enum ToolArchetype {
    PICKAXE("pickaxe", "Pickaxe", ComponentRole.PICKAXE_HEAD, 1.20f, 1.0f,
            ToolVisualTransform.HEAD, ToolVisualTransform.BINDING, ToolVisualTransform.DEFAULT),
    AXE("axe", "Axe", ComponentRole.AXE_HEAD, 1.00f, 1.0f,
            ToolVisualTransform.HEAD, ToolVisualTransform.BINDING, ToolVisualTransform.DEFAULT),
    BATTLE_AXE("battle_axe", "Battle Axe", ComponentRole.BATTLE_AXE_HEAD, 1.00f, 1.0f,
            ToolVisualTransform.HEAD, ToolVisualTransform.BINDING, ToolVisualTransform.DEFAULT),
    SHOVEL("shovel", "Shovel", ComponentRole.SHOVEL_HEAD, 1.00f, 1.0f,
            ToolVisualTransform.HEAD, ToolVisualTransform.BINDING, ToolVisualTransform.DEFAULT),
    SWORD("sword", "Sword", ComponentRole.SWORD_BLADE, 1.60f, 1.0f,
            ToolVisualTransform.HEAD, ToolVisualTransform.BINDING, ToolVisualTransform.SWORD_GRIP);

    public static final Codec<ToolArchetype> CODEC = Codec.STRING.xmap(ToolArchetype::byName, ToolArchetype::serializedName);

    private final String serializedName;
    private final String displayName;
    private final ComponentRole headRole;
    private final float baseAttackSpeed;
    private final float weightSensitivity;
    private final ToolVisualTransform headTransform;
    private final ToolVisualTransform bindingTransform;
    private final ToolVisualTransform gripTransform;

    ToolArchetype(String serializedName, String displayName, ComponentRole headRole, float baseAttackSpeed, float weightSensitivity,
                  ToolVisualTransform headTransform, ToolVisualTransform bindingTransform, ToolVisualTransform gripTransform) {
        this.serializedName = serializedName;
        this.displayName = displayName;
        this.headRole = headRole;
        this.baseAttackSpeed = baseAttackSpeed;
        this.weightSensitivity = weightSensitivity;
        this.headTransform = headTransform;
        this.bindingTransform = bindingTransform;
        this.gripTransform = gripTransform;
    }

    public String serializedName() { return serializedName; }
    public String displayName() { return displayName; }
    public ComponentRole headRole() { return headRole; }
    public float baseAttackSpeed() { return baseAttackSpeed; }
    public float weightSensitivity() { return weightSensitivity; }

    public ToolVisualTransform visualTransform(ComponentRole role) {
        return role == headRole ? headTransform : role == ComponentRole.BINDING ? bindingTransform : gripTransform;
    }

    private static ToolArchetype byName(String name) {
        for (ToolArchetype archetype : values()) {
            if (archetype.serializedName.equals(name)) return archetype;
        }
        throw new IllegalArgumentException("Unknown tool archetype: " + name);
    }
}
