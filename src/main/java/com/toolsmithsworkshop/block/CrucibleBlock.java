package com.toolsmithsworkshop.block;

import com.mojang.serialization.MapCodec;
import com.toolsmithsworkshop.menu.CrucibleMenu;
import com.toolsmithsworkshop.block.entity.CrucibleBlockEntity;
import com.toolsmithsworkshop.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class CrucibleBlock extends Block implements EntityBlock {
    public static final MapCodec<CrucibleBlock> CODEC = simpleCodec(CrucibleBlock::new);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public CrucibleBlock(BlockBehaviour.Properties properties) { super(properties); registerDefaultState(stateDefinition.any().setValue(LIT, false)); }
    @Override protected MapCodec<? extends Block> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(LIT); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new CrucibleBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide || type != ModBlockEntities.CRUCIBLE.get() ? null :
                (tickLevel, tickPos, tickState, entity) -> CrucibleBlockEntity.serverTick(tickLevel, tickPos, tickState, (CrucibleBlockEntity) entity);
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moved) {
        if (state.getBlock() != replacement.getBlock() && level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible) {
            Containers.dropContents(level, pos, crucible);
        }
        super.onRemove(state, level, pos, replacement, moved);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(menuProvider(level, pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static MenuProvider menuProvider(Level level, BlockPos pos) {
        return new SimpleMenuProvider((id, inventory, player) ->
                new CrucibleMenu(id, inventory, (CrucibleBlockEntity) level.getBlockEntity(pos)),
                Component.translatable("container.toolsmiths_workshop.crucible"));
    }
}
