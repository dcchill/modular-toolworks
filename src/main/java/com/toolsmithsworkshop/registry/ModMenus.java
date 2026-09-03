package com.toolsmithsworkshop.registry;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.menu.BasicWorkshopMenu;
import com.toolsmithsworkshop.menu.CrucibleMenu;
import com.toolsmithsworkshop.menu.GemsettingMenu;
import com.toolsmithsworkshop.menu.GuidebookMenu;
import com.toolsmithsworkshop.menu.ToolsmithingMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, ToolsmithsWorkshop.MOD_ID);
    public static final DeferredHolder<MenuType<?>, MenuType<BasicWorkshopMenu>> BASIC_WORKSHOP = REGISTER.register(
            "basic_workshop", () -> new MenuType<>(BasicWorkshopMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<ToolsmithingMenu>> TOOLSMITHING = REGISTER.register(
            "toolsmithing", () -> new MenuType<>(ToolsmithingMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<GemsettingMenu>> GEMSETTING = REGISTER.register(
            "gemsetting", () -> new MenuType<>(GemsettingMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<CrucibleMenu>> CRUCIBLE = REGISTER.register(
            "crucible", () -> new MenuType<>(CrucibleMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<GuidebookMenu>> GUIDEBOOK = REGISTER.register(
            "guidebook", () -> new MenuType<>(GuidebookMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private ModMenus() {}
}
