package com.toolsmithsworkshop.registry;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.item.ModularToolItem;
import com.toolsmithsworkshop.item.ToolComponentItem;
import com.toolsmithsworkshop.item.ForgingHammerItem;
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
    private static final Map<ComponentRole, Map<net.minecraft.resources.ResourceLocation, DeferredItem<ToolComponentItem>>> COMPONENTS = new EnumMap<>(ComponentRole.class);

    public static final DeferredItem<net.minecraft.world.item.BlockItem> BASIC_WORKSHOP = REGISTER.registerSimpleBlockItem(ModBlocks.BASIC_WORKSHOP);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> TOOLSMITHING_WORKBENCH = REGISTER.registerSimpleBlockItem(ModBlocks.TOOLSMITHING_WORKBENCH);
    public static final DeferredItem<ModularToolItem> MODULAR_PICKAXE = tool(ToolArchetype.PICKAXE);
    public static final DeferredItem<ModularToolItem> MODULAR_AXE = tool(ToolArchetype.AXE);
    public static final DeferredItem<Item> WOODEN_GRIP = REGISTER.registerSimpleItem("wooden_grip");
    private static final Map<net.minecraft.resources.ResourceLocation, DeferredItem<ForgingHammerItem>> FORGING_HAMMERS = new LinkedHashMap<>();

    static {
        for (ComponentRole role : ComponentRole.values()) {
            Map<net.minecraft.resources.ResourceLocation, DeferredItem<ToolComponentItem>> byMaterial = new LinkedHashMap<>();
            for (ToolMaterial material : ToolMaterials.values()) {
                if (role == ComponentRole.GRIP && material == ToolMaterials.WOOD) continue;
                String name = material.id().getPath() + "_" + role.serializedName();
                byMaterial.put(material.id(), REGISTER.register(name, () -> new ToolComponentItem(new Item.Properties()
                        .component(ModDataComponents.TOOL_COMPONENT.value(), new ToolComponentData(role, material.id())))));
            }
            COMPONENTS.put(role, byMaterial);
        }
        for (ToolMaterial material : ToolMaterials.values()) {
            if (material == ToolMaterials.WOOD) continue;
            FORGING_HAMMERS.put(material.id(), REGISTER.register(material.id().getPath() + "_forging_hammer",
                    () -> new ForgingHammerItem(material, new Item.Properties().durability(material.durability()))));
        }
    }

    private ModItems() {}

    private static DeferredItem<ModularToolItem> tool(ToolArchetype archetype) {
        return REGISTER.register(archetype.serializedName(), () -> new ModularToolItem(archetype, new Item.Properties()));
    }

    public static DeferredItem<ToolComponentItem> component(ComponentRole role, net.minecraft.resources.ResourceLocation material) {
        return COMPONENTS.get(role).get(material);
    }

    public static Iterable<DeferredItem<ToolComponentItem>> components(ComponentRole role) {
        return COMPONENTS.get(role).values();
    }

    public static DeferredItem<ForgingHammerItem> forgingHammer(net.minecraft.resources.ResourceLocation material) {
        return FORGING_HAMMERS.get(material);
    }
}
