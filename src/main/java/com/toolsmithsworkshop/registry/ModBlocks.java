package com.toolsmithsworkshop.registry;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.block.BasicWorkshopBlock;
import com.toolsmithsworkshop.block.ToolsmithingWorkbenchBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(ToolsmithsWorkshop.MOD_ID);
    public static final DeferredBlock<BasicWorkshopBlock> BASIC_WORKSHOP = REGISTER.registerBlock(
            "basic_workshop", BasicWorkshopBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE));
    public static final DeferredBlock<ToolsmithingWorkbenchBlock> TOOLSMITHING_WORKBENCH = REGISTER.registerBlock(
            "toolsmithing_workbench", ToolsmithingWorkbenchBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE));

    private ModBlocks() {}
}
