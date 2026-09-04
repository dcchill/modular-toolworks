package com.toolsmithsworkshop;

import com.toolsmithsworkshop.registry.ModBlocks;
import com.toolsmithsworkshop.registry.ModBlockEntities;
import com.toolsmithsworkshop.registry.ModCreativeTabs;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.registry.ModEnchantments;
import com.toolsmithsworkshop.registry.ModItems;
import com.toolsmithsworkshop.registry.ModMenus;
import com.toolsmithsworkshop.registry.ModBlockEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(ToolsmithsWorkshop.MOD_ID)
public final class ToolsmithsWorkshop {
    public static final String MOD_ID = "toolsmiths_workshop";

    public ToolsmithsWorkshop(IEventBus modBus) {
        ModDataComponents.REGISTER.register(modBus);
        ModBlocks.REGISTER.register(modBus);
        ModBlockEntities.REGISTER.register(modBus);
        ModItems.REGISTER.register(modBus);
        ModMenus.REGISTER.register(modBus);
        ModCreativeTabs.REGISTER.register(modBus);
        ModEnchantments.register(modBus);
        modBus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(Capabilities.FluidHandler.BLOCK,
                (level, pos, state, crucible, side) -> ((com.toolsmithsworkshop.block.entity.CrucibleBlockEntity) crucible).fluidHandler(), ModBlocks.CRUCIBLE.get());
    }
}
