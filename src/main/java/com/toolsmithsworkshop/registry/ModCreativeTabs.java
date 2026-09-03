package com.toolsmithsworkshop.registry;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.item.ModularToolItem;
import com.toolsmithsworkshop.tool.ComponentRole;
import com.toolsmithsworkshop.tool.ToolBuildData;
import com.toolsmithsworkshop.tool.ToolMaterial;
import com.toolsmithsworkshop.tool.ToolMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ToolsmithsWorkshop.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = REGISTER.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.toolsmiths_workshop"))
            .icon(() -> new ItemStack(ModBlocks.BASIC_WORKSHOP.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModBlocks.BASIC_WORKSHOP.get());
                output.accept(ModBlocks.FIREBRICKS.get());
                output.accept(ModItems.FIRE_BRICK_CLAY_BALL.get());
                output.accept(ModItems.FIRE_BRICK.get());
                output.accept(ModItems.ROSE_GOLD_INGOT.get());
                output.accept(ModItems.STEEL_INGOT.get());
                output.accept(ModItems.BLAZE_STEEL_INGOT.get());
                output.accept(ModItems.SCULKITE_INGOT.get());
                output.accept(ModItems.GARNET.get());
                output.accept(ModBlocks.GARNET_ORE.get());
                output.accept(ModBlocks.TIER_2_WORKSHOP.get());
                output.accept(ModBlocks.TIER_3_WORKSHOP.get());
                output.accept(ModBlocks.TIER_4_WORKSHOP.get());
                output.accept(ModBlocks.TOOLSMITHING_WORKBENCH.get());
                output.accept(ModBlocks.GEMSETTING_BENCH.get());
                output.accept(ModBlocks.CRUCIBLE.get());
                for (ComponentRole role : ComponentRole.values()) ModItems.components(role).forEach(output::accept);
                ToolBuildData ironWood = new ToolBuildData(ToolMaterials.IRON.id(), ToolMaterials.IRON.id(), ToolMaterials.WOOD.id());
                output.accept(ModularToolItem.create(ModItems.MODULAR_PICKAXE.get(), ironWood));
                output.accept(ModularToolItem.create(ModItems.MODULAR_AXE.get(), ironWood));
                output.accept(ModularToolItem.create(ModItems.MODULAR_BATTLE_AXE.get(), ironWood));
                output.accept(ModularToolItem.create(ModItems.MODULAR_SHOVEL.get(), ironWood));
                output.accept(ModularToolItem.create(ModItems.MODULAR_SWORD.get(), ironWood));
                for (ToolMaterial material : ToolMaterials.values()) {
                    var hammer = ModItems.forgingHammer(material.id());
                    if (hammer != null) output.accept(hammer.get());
                }
            }).build());

    private ModCreativeTabs() {}
}
