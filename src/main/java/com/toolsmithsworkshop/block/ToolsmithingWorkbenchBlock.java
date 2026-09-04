package com.toolsmithsworkshop.block;

import com.mojang.serialization.MapCodec;
import com.toolsmithsworkshop.menu.ToolsmithingMenu;
import com.toolsmithsworkshop.block.entity.ToolsmithingWorkbenchBlockEntity;
import com.toolsmithsworkshop.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public final class ToolsmithingWorkbenchBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<ToolsmithingWorkbenchBlock> CODEC = simpleCodec(ToolsmithingWorkbenchBlock::new);

    public ToolsmithingWorkbenchBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ToolsmithingWorkbenchBlockEntity(pos, state);
    }

    @Override
    public net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos,
                                                                 net.minecraft.world.phys.shapes.CollisionContext context) {
        return workbenchFootprint(state);
    }

    @Override
    public net.minecraft.world.phys.shapes.VoxelShape getCollisionShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos,
                                                                         net.minecraft.world.phys.shapes.CollisionContext context) {
        return workbenchFootprint(state);
    }

    private static net.minecraft.world.phys.shapes.VoxelShape workbenchFootprint(BlockState state) {
        // The model's local X axis spans from 0 to 32 pixels. The blockstate
        // rotation turns that extension around the placed block.
        return switch (state.getValue(FACING)) {
            case NORTH -> net.minecraft.world.level.block.Block.box(0, 0, 0, 32, 16, 16);
            case SOUTH -> net.minecraft.world.level.block.Block.box(-16, 0, 0, 16, 16, 16);
            case EAST -> net.minecraft.world.level.block.Block.box(0, 0, 0, 16, 16, 32);
            case WEST -> net.minecraft.world.level.block.Block.box(0, 0, -16, 16, 16, 16);
            default -> net.minecraft.world.level.block.Block.box(0, 0, 0, 32, 16, 16);
        };
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moved) {
        if (state.getBlock() != replacement.getBlock() && level.getBlockEntity(pos) instanceof ToolsmithingWorkbenchBlockEntity workbench) {
            net.minecraft.world.Containers.dropContents(level, pos, workbench);
        }
        super.onRemove(state, level, pos, replacement, moved);
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.setValue(FACING, mirror.mirror(state.getValue(FACING))); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) { builder.add(FACING); }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(menuProvider(level, pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static MenuProvider menuProvider(Level level, BlockPos pos) {
        return new SimpleMenuProvider((id, inventory, player) ->
                new ToolsmithingMenu(id, inventory, (ToolsmithingWorkbenchBlockEntity) level.getBlockEntity(pos)),
                Component.translatable("container.toolsmiths_workshop.toolsmithing_workbench"));
    }
}
