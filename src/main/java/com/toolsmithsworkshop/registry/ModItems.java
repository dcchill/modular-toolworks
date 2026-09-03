package com.toolsmithsworkshop.registry;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.item.ModularToolItem;
import com.toolsmithsworkshop.item.ToolComponentItem;
import com.toolsmithsworkshop.item.ForgingHammerItem;
import com.toolsmithsworkshop.item.GuidebookItem;
import com.toolsmithsworkshop.item.ToolVisualItem;
import com.toolsmithsworkshop.tool.ComponentRole;
import com.toolsmithsworkshop.tool.ToolArchetype;
import com.toolsmithsworkshop.tool.ToolComponentData;
import com.toolsmithsworkshop.tool.ToolMaterial;
import com.toolsmithsworkshop.tool.ToolMaterials;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ToolsmithsWorkshop.MOD_ID);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> FIREBRICKS = REGISTER.registerSimpleBlockItem(ModBlocks.FIREBRICKS);
    public static final DeferredItem<Item> FIRE_BRICK_CLAY_BALL = REGISTER.registerSimpleItem("fire_brick_clay_ball");
    public static final DeferredItem<Item> FIRE_BRICK = REGISTER.registerSimpleItem("fire_brick");
    public static final DeferredItem<Item> ROSE_GOLD_INGOT = REGISTER.registerSimpleItem("rose_gold_ingot");
    public static final DeferredItem<Item> STEEL_INGOT = REGISTER.registerSimpleItem("steel_ingot");
    public static final DeferredItem<Item> BLAZE_STEEL_INGOT = REGISTER.registerSimpleItem("blaze_steel_ingot");
    public static final DeferredItem<Item> SCULKITE_INGOT = REGISTER.registerSimpleItem("sculkite_ingot");
    public static final DeferredItem<Item> SOUL_STEEL_INGOT = REGISTER.registerSimpleItem("soul_steel_ingot");
    public static final DeferredItem<Item> GARNET = REGISTER.registerSimpleItem("garnet");
    public static final DeferredItem<GuidebookItem> GUIDEBOOK = REGISTER.register("guidebook", () -> new GuidebookItem(new Item.Properties().stacksTo(1)));
    private static final Map<ComponentRole, Map<net.minecraft.resources.ResourceLocation, DeferredItem<ToolComponentItem>>> COMPONENTS = new EnumMap<>(ComponentRole.class);
    private static final Map<ToolArchetype, DeferredItem<ModularToolItem>> TOOLS = new EnumMap<>(ToolArchetype.class);
    private static final Map<ToolArchetype, Map<ComponentRole, DeferredItem<ToolVisualItem>>> VISUALS = new EnumMap<>(ToolArchetype.class);
    private static final Map<ToolArchetype, DeferredItem<ToolVisualItem>> SLIME_BINDING_VISUALS = new EnumMap<>(ToolArchetype.class);
    private static final Map<ToolArchetype, DeferredItem<ToolVisualItem>> PHANTOM_BINDING_VISUALS = new EnumMap<>(ToolArchetype.class);
    private static final Map<ToolArchetype, DeferredItem<ToolVisualItem>> SCULK_BINDING_VISUALS = new EnumMap<>(ToolArchetype.class);
    private static final Map<ToolArchetype, DeferredItem<ToolVisualItem>> BONE_GRIP_VISUALS = new EnumMap<>(ToolArchetype.class);
    private static final Map<ToolArchetype, DeferredItem<ToolVisualItem>> CACTUS_GRIP_VISUALS = new EnumMap<>(ToolArchetype.class);
    private static final Map<ToolArchetype, DeferredItem<ToolVisualItem>> BLAZE_STEEL_BINDING_VISUALS = new EnumMap<>(ToolArchetype.class);
    private static final Map<ToolArchetype, DeferredItem<ToolVisualItem>> SCULKITE_BINDING_VISUALS = new EnumMap<>(ToolArchetype.class);
    private static final Map<ToolArchetype, DeferredItem<ToolVisualItem>> SCULKITE_GRIP_VISUALS = new EnumMap<>(ToolArchetype.class);
    private static final Map<ToolArchetype, DeferredItem<ToolVisualItem>> GEM_VISUALS = new EnumMap<>(ToolArchetype.class);

    public static final DeferredItem<net.minecraft.world.item.BlockItem> BASIC_WORKSHOP = REGISTER.registerSimpleBlockItem(ModBlocks.BASIC_WORKSHOP);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> TIER_2_WORKSHOP = REGISTER.registerSimpleBlockItem(ModBlocks.TIER_2_WORKSHOP);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> TIER_3_WORKSHOP = REGISTER.registerSimpleBlockItem(ModBlocks.TIER_3_WORKSHOP);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> TIER_4_WORKSHOP = REGISTER.registerSimpleBlockItem(ModBlocks.TIER_4_WORKSHOP);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> TOOLSMITHING_WORKBENCH = REGISTER.registerSimpleBlockItem(ModBlocks.TOOLSMITHING_WORKBENCH);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> GEMSETTING_BENCH = REGISTER.registerSimpleBlockItem(ModBlocks.GEMSETTING_BENCH);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> CRUCIBLE = REGISTER.registerSimpleBlockItem(ModBlocks.CRUCIBLE);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> GARNET_ORE = REGISTER.registerSimpleBlockItem(ModBlocks.GARNET_ORE);
    public static final DeferredItem<ModularToolItem> MODULAR_PICKAXE = registerTool(ToolArchetype.PICKAXE);
    public static final DeferredItem<ModularToolItem> MODULAR_AXE = registerTool(ToolArchetype.AXE);
    public static final DeferredItem<ModularToolItem> MODULAR_BATTLE_AXE = registerTool(ToolArchetype.BATTLE_AXE);
    public static final DeferredItem<ModularToolItem> MODULAR_SHOVEL = registerTool(ToolArchetype.SHOVEL);
    public static final DeferredItem<ModularToolItem> MODULAR_SWORD = registerTool(ToolArchetype.SWORD);
    public static final DeferredItem<ToolComponentItem> WOODEN_GRIP = REGISTER.register("wooden_grip", () -> new ToolComponentItem(new Item.Properties()
            .component(ModDataComponents.TOOL_COMPONENT.value(), new ToolComponentData(ComponentRole.GRIP, ToolMaterials.WOOD.id()))));
    private static final Map<net.minecraft.resources.ResourceLocation, DeferredItem<ForgingHammerItem>> FORGING_HAMMERS = new LinkedHashMap<>();

    static {
        for (ComponentRole role : ComponentRole.values()) {
            Map<net.minecraft.resources.ResourceLocation, DeferredItem<ToolComponentItem>> byMaterial = new LinkedHashMap<>();
            for (ToolMaterial material : ToolMaterials.values()) {
                if (role == ComponentRole.GRIP && material == ToolMaterials.WOOD) continue;
                if (material == ToolMaterials.SLIME && role != ComponentRole.BINDING) continue;
                if (material == ToolMaterials.PHANTOM && role != ComponentRole.BINDING) continue;
                if (material == ToolMaterials.SCULK && role != ComponentRole.BINDING) continue;
                if (material == ToolMaterials.BONE && role != ComponentRole.GRIP) continue;
                if (material == ToolMaterials.CACTUS && role != ComponentRole.GRIP) continue;
                String name = material.id().getPath() + "_" + role.serializedName();
                byMaterial.put(material.id(), REGISTER.register(name, () -> new ToolComponentItem(new Item.Properties()
                        .component(ModDataComponents.TOOL_COMPONENT.value(), new ToolComponentData(role, material.id())))));
            }
            COMPONENTS.put(role, byMaterial);
        }
        for (ToolArchetype archetype : ToolArchetype.values()) {
            Map<ComponentRole, DeferredItem<ToolVisualItem>> byRole = new EnumMap<>(ComponentRole.class);
            for (ComponentRole role : new ComponentRole[]{archetype.headRole(), ComponentRole.BINDING, ComponentRole.GRIP}) {
                byRole.put(role, REGISTER.register(archetype.serializedName() + "_visual_" + role.serializedName(),
                        () -> new ToolVisualItem(new Item.Properties())));
            }
            VISUALS.put(archetype, byRole);
            SLIME_BINDING_VISUALS.put(archetype, REGISTER.register(archetype.serializedName() + "_slime_binding_visual",
                    () -> new ToolVisualItem(new Item.Properties())));
            PHANTOM_BINDING_VISUALS.put(archetype, REGISTER.register(archetype.serializedName() + "_phantom_binding_visual",
                    () -> new ToolVisualItem(new Item.Properties())));
            SCULK_BINDING_VISUALS.put(archetype, REGISTER.register(archetype.serializedName() + "_sculk_binding_visual",
                    () -> new ToolVisualItem(new Item.Properties())));
            BONE_GRIP_VISUALS.put(archetype, REGISTER.register(archetype.serializedName() + "_bone_grip_visual",
                    () -> new ToolVisualItem(new Item.Properties())));
            CACTUS_GRIP_VISUALS.put(archetype, REGISTER.register(archetype.serializedName() + "_cactus_grip_visual",
                    () -> new ToolVisualItem(new Item.Properties())));
            BLAZE_STEEL_BINDING_VISUALS.put(archetype,
                    REGISTER.register(archetype.serializedName() + "_blaze_steel_binding_visual", () -> new ToolVisualItem(new Item.Properties())));
            SCULKITE_BINDING_VISUALS.put(archetype,
                    REGISTER.register(archetype.serializedName() + "_sculkite_binding_visual", () -> new ToolVisualItem(new Item.Properties())));
            SCULKITE_GRIP_VISUALS.put(archetype,
                    REGISTER.register(archetype.serializedName() + "_sculkite_grip_visual", () -> new ToolVisualItem(new Item.Properties())));
            GEM_VISUALS.put(archetype, REGISTER.register(archetype.serializedName() + "_gem_visual",
                    () -> new ToolVisualItem(new Item.Properties())));
        }
        for (ToolMaterial material : ToolMaterials.values()) {
            if (material == ToolMaterials.WOOD || material == ToolMaterials.SLIME || material == ToolMaterials.PHANTOM || material == ToolMaterials.SCULK || material == ToolMaterials.BONE || material == ToolMaterials.CACTUS || material == ToolMaterials.BLAZE_STEEL || material == ToolMaterials.SCULKITE || material == ToolMaterials.SOUL_STEEL) continue;
            FORGING_HAMMERS.put(material.id(), REGISTER.register(material.id().getPath() + "_forging_hammer",
                    () -> new ForgingHammerItem(material, new Item.Properties().durability(material.durability()))));
        }
    }

    private ModItems() {}

    private static DeferredItem<ModularToolItem> registerTool(ToolArchetype archetype) {
        DeferredItem<ModularToolItem> item = REGISTER.register(archetype.serializedName(),
                () -> new ModularToolItem(archetype, new Item.Properties()));
        TOOLS.put(archetype, item);
        return item;
    }

    public static DeferredItem<ModularToolItem> tool(ToolArchetype archetype) {
        return TOOLS.get(archetype);
    }

    public static DeferredItem<ToolComponentItem> component(ComponentRole role, net.minecraft.resources.ResourceLocation material) {
        return COMPONENTS.get(role).get(material);
    }

    public static Iterable<DeferredItem<ToolComponentItem>> components(ComponentRole role) {
        return COMPONENTS.get(role).values();
    }

    public static DeferredItem<ToolVisualItem> visual(ToolArchetype archetype, ComponentRole role) {
        return VISUALS.get(archetype).get(role);
    }

    public static DeferredItem<ToolVisualItem> visual(ToolArchetype archetype, ComponentRole role, net.minecraft.resources.ResourceLocation material) {
        if (role == ComponentRole.BINDING && material.equals(ToolMaterials.SLIME.id())) return SLIME_BINDING_VISUALS.get(archetype);
        if (role == ComponentRole.BINDING && material.equals(ToolMaterials.PHANTOM.id())) return PHANTOM_BINDING_VISUALS.get(archetype);
        if (role == ComponentRole.BINDING && material.equals(ToolMaterials.SCULK.id())) return SCULK_BINDING_VISUALS.get(archetype);
        if (role == ComponentRole.GRIP && material.equals(ToolMaterials.BONE.id())) return BONE_GRIP_VISUALS.get(archetype);
        if (role == ComponentRole.GRIP && material.equals(ToolMaterials.CACTUS.id())) return CACTUS_GRIP_VISUALS.get(archetype);
        if (role == ComponentRole.BINDING && material.equals(ToolMaterials.BLAZE_STEEL.id())) return BLAZE_STEEL_BINDING_VISUALS.getOrDefault(archetype, visual(archetype, role));
        if (role == ComponentRole.BINDING && material.equals(ToolMaterials.SCULKITE.id())) return SCULKITE_BINDING_VISUALS.get(archetype);
        if (role == ComponentRole.GRIP && material.equals(ToolMaterials.SCULKITE.id())) return SCULKITE_GRIP_VISUALS.get(archetype);
        return visual(archetype, role);
    }

    public static DeferredItem<ToolVisualItem> gemVisual(ToolArchetype archetype) {
        return GEM_VISUALS.get(archetype);
    }

    public static DeferredItem<ForgingHammerItem> forgingHammer(net.minecraft.resources.ResourceLocation material) {
        return FORGING_HAMMERS.get(material);
    }
}
