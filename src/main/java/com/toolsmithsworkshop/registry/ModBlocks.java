package com.toolsmithsworkshop.registry;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.block.BasicWorkshopBlock;
import com.toolsmithsworkshop.block.CrucibleBlock;
import com.toolsmithsworkshop.block.GemsettingBenchBlock;
import com.toolsmithsworkshop.block.ToolsmithingWorkbenchBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(ToolsmithsWorkshop.MOD_ID);
    public static final DeferredBlock<net.minecraft.world.level.block.Block> FIREBRICKS = REGISTER.registerBlock(
            "firebricks", net.minecraft.world.level.block.Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).strength(1.5F));
    public static final DeferredBlock<BasicWorkshopBlock> BASIC_WORKSHOP = REGISTER.registerBlock(
            "basic_workshop", BasicWorkshopBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE));
    public static final DeferredBlock<BasicWorkshopBlock> TIER_2_WORKSHOP = REGISTER.registerBlock(
            "tier_2_workshop", properties -> new BasicWorkshopBlock(2, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE));
    public static final DeferredBlock<BasicWorkshopBlock> TIER_3_WORKSHOP = REGISTER.registerBlock(
            "tier_3_workshop", properties -> new BasicWorkshopBlock(3, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE));
    public static final DeferredBlock<BasicWorkshopBlock> TIER_4_WORKSHOP = REGISTER.registerBlock(
            "tier_4_workshop", properties -> new BasicWorkshopBlock(4, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE));
    public static final DeferredBlock<ToolsmithingWorkbenchBlock> TOOLSMITHING_WORKBENCH = REGISTER.registerBlock(
            "toolsmithing_workbench", ToolsmithingWorkbenchBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE));
    public static final DeferredBlock<GemsettingBenchBlock> GEMSETTING_BENCH = REGISTER.registerBlock(
            "gemsetting_bench", GemsettingBenchBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SMITHING_TABLE));
    public static final DeferredBlock<CrucibleBlock> CRUCIBLE = REGISTER.registerBlock(
            "crucible", CrucibleBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.BLAST_FURNACE).lightLevel(state -> 0));
    public static final DeferredBlock<net.minecraft.world.level.block.Block> GARNET_ORE = REGISTER.registerBlock(
            "garnet_ore", net.minecraft.world.level.block.Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE).strength(1.75F).requiresCorrectToolForDrops());

    private ModBlocks() {}
}
