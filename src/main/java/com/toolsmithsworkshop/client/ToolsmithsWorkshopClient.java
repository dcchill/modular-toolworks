package com.toolsmithsworkshop.client;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ToolsmithsWorkshop.MOD_ID, value = Dist.CLIENT)
public final class ToolsmithsWorkshopClient {
    private ToolsmithsWorkshopClient() {}

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.BASIC_WORKSHOP.get(), BasicWorkshopScreen::new);
        event.register(ModMenus.TOOLSMITHING.get(), ToolsmithingScreen::new);
    }
}
