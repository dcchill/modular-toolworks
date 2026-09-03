package com.toolsmithsworkshop.registry;

import com.toolsmithsworkshop.ToolsmithsWorkshop;
import com.toolsmithsworkshop.block.entity.CrucibleBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ToolsmithsWorkshop.MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleBlockEntity>> CRUCIBLE = REGISTER.register("crucible",
            () -> BlockEntityType.Builder.of(CrucibleBlockEntity::new, ModBlocks.CRUCIBLE.get()).build(null));
    private ModBlockEntities() {}
}
