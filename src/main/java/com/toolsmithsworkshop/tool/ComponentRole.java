package com.toolsmithsworkshop.tool;

import com.mojang.serialization.Codec;

public enum ComponentRole {
    PICKAXE_HEAD("pickaxe_head", "Pickaxe Head"),
    AXE_HEAD("axe_head", "Axe Head"),
    SHOVEL_HEAD("shovel_head", "Shovel Head"),
    SWORD_BLADE("sword_blade", "Sword Blade"),
    BINDING("tool_binding", "Tool Binding"),
    GRIP("tool_grip", "Tool Grip");

    public static final Codec<ComponentRole> CODEC = Codec.STRING.xmap(ComponentRole::byName, ComponentRole::serializedName);

    private final String serializedName;
    private final String displayName;

    ComponentRole(String serializedName, String displayName) {
        this.serializedName = serializedName;
        this.displayName = displayName;
    }

    public String serializedName() {
        return serializedName;
    }

    public String displayName() {
        return displayName;
    }

    public boolean isHead() {
        return this == PICKAXE_HEAD || this == AXE_HEAD || this == SHOVEL_HEAD || this == SWORD_BLADE;
    }

    private static ComponentRole byName(String name) {
        for (ComponentRole role : values()) {
            if (role.serializedName.equals(name)) return role;
        }
        throw new IllegalArgumentException("Unknown component role: " + name);
    }
}
