package com.toolsmithsworkshop.block.entity;

import com.toolsmithsworkshop.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class ToolsmithingWorkbenchBlockEntity extends BlockEntity implements Container {
    private final NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);

    public ToolsmithingWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TOOLSMITHING_WORKBENCH.get(), pos, state);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public AABB renderBounds() {
        return switch (getBlockState().getValue(com.toolsmithsworkshop.block.ToolsmithingWorkbenchBlock.FACING)) {
            case NORTH -> new AABB(worldPosition).expandTowards(1.0D, 0.0D, 0.0D);
            case SOUTH -> new AABB(worldPosition).expandTowards(-1.0D, 0.0D, 0.0D);
            case EAST -> new AABB(worldPosition).expandTowards(0.0D, 0.0D, 1.0D);
            case WEST -> new AABB(worldPosition).expandTowards(0.0D, 0.0D, -1.0D);
            default -> new AABB(worldPosition).expandTowards(1.0D, 0.0D, 0.0D);
        };
    }

    @Override public int getContainerSize() { return items.size(); }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(items, slot, amount);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        stack.limitSize(getMaxStackSize(stack));
        setChanged();
    }
    @Override public boolean stillValid(Player player) {
        return level != null && player.distanceToSqr(worldPosition.getX() + .5, worldPosition.getY() + .5,
                worldPosition.getZ() + .5) <= 64;
    }
    @Override public void clearContent() { items.clear(); setChanged(); }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, items, registries);
    }
}
