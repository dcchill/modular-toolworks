package com.toolsmithsworkshop;

import com.toolsmithsworkshop.registry.ModBlocks;
import com.toolsmithsworkshop.registry.ModCreativeTabs;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.registry.ModItems;
import com.toolsmithsworkshop.registry.ModMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ToolsmithsWorkshop.MOD_ID)
public final class ToolsmithsWorkshop {
    public static final String MOD_ID = "toolsmiths_workshop";

    public ToolsmithsWorkshop(IEventBus modBus) {
        ModDataComponents.REGISTER.register(modBus);
        ModBlocks.REGISTER.register(modBus);
        ModItems.REGISTER.register(modBus);
        ModMenus.REGISTER.register(modBus);
        ModCreativeTabs.REGISTER.register(modBus);
    }
}
