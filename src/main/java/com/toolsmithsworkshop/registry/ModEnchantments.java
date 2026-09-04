package com.toolsmithsworkshop.registry;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEnchantments {
    public static final DeferredRegister<Enchantment> REGISTER = DeferredRegister.create(Registries.ENCHANTMENT, ToolsmithsWorkshop.MOD_ID);

    // Vanilla Density, Breach, and Wind Burst are data-driven and use the
    // minecraft:enchantable/mace item tag. No replacement enchantments are needed.

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
