package com.toolsmithsworkshop.client;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.registry.ModMenus;
import com.toolsmithsworkshop.registry.ModItems;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.tool.ToolArchetype;
import com.toolsmithsworkshop.tool.ToolMaterials;
import net.minecraft.client.color.item.ItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = ToolsmithsWorkshop.MOD_ID, value = Dist.CLIENT)
public final class ToolsmithsWorkshopClient {
    private ToolsmithsWorkshopClient() {}

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.BASIC_WORKSHOP.get(), BasicWorkshopScreen::new);
        event.register(ModMenus.TOOLSMITHING.get(), ToolsmithingScreen::new);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        ItemColor color = (stack, tintIndex) -> {
            var data = stack.get(ModDataComponents.TOOL_COMPONENT);
            return data == null || data.material().equals(ToolMaterials.SLIME.id()) ? 0xFFFFFFFF : materialColor(data.material().getPath());
        };
        for (ToolArchetype archetype : ToolArchetype.values()) {
            event.register(color, ModItems.visual(archetype, archetype.headRole()).get(),
                    ModItems.visual(archetype, com.toolsmithsworkshop.tool.ComponentRole.BINDING).get(),
                    ModItems.visual(archetype, com.toolsmithsworkshop.tool.ComponentRole.GRIP).get(),
                    ModItems.visual(archetype, com.toolsmithsworkshop.tool.ComponentRole.BINDING, ToolMaterials.SLIME.id()).get(),
                    ModItems.visual(archetype, com.toolsmithsworkshop.tool.ComponentRole.GRIP, ToolMaterials.BONE.id()).get());
        }
        for (var role : com.toolsmithsworkshop.tool.ComponentRole.values()) {
            for (var component : ModItems.components(role)) event.register(color, component.get());
        }
        event.register(color, ModItems.WOODEN_GRIP.get());
    }

    private static int materialColor(String material) {
        return switch (material) {
            case "wood" -> 0xFF6B511F;
            case "stone" -> 0xFF8A8A8A;
            case "flint" -> 0xFF565656;
            case "copper" -> 0xFFE77C56;
            case "iron" -> 0xFFFFFFFF;
            case "gold" -> 0xFFFFD83D;
            case "quartz" -> 0xFFC7B9A7;
            case "diamond" -> 0xFF55E8D1;
            case "obsidian" -> 0xFF271E3D;
            case "netherite" -> 0xFF4F3C3E;
            default -> 0xFFFFFFFF;
        };
    }
}
