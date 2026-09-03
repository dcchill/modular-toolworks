package com.toolsmithsworkshop.menu;

import com.toolsmithsworkshop.block.entity.CrucibleBlockEntity;
import com.toolsmithsworkshop.registry.ModBlocks;
import com.toolsmithsworkshop.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class CrucibleMenu extends AbstractContainerMenu {
    public static final int INPUT_A = 0, INPUT_B = 1, FUEL = 2, RESULT = 3;
    private final Container input;
    private final ContainerLevelAccess access;
    private final DataSlot burnTime, burnDuration, cookTime;

    public CrucibleMenu(int id, Inventory inventory) { this(id, inventory, new net.minecraft.world.SimpleContainer(4), ContainerLevelAccess.NULL, new net.minecraft.world.inventory.SimpleContainerData(4)); }
    public CrucibleMenu(int id, Inventory inventory, CrucibleBlockEntity crucible) { this(id, inventory, crucible, ContainerLevelAccess.create(crucible.getLevel(), crucible.getBlockPos()), crucible.data); }
    private CrucibleMenu(int id, Inventory inventory, Container input, ContainerLevelAccess access, net.minecraft.world.inventory.ContainerData data) {
        super(ModMenus.CRUCIBLE.get(), id);
        this.input = input;
        this.access = access;
        burnTime = tracked(data, 0);
        burnDuration = tracked(data, 1);
        cookTime = tracked(data, 2);
        addSlot(new Slot(input, INPUT_A, 34, 10));
        addSlot(new Slot(input, INPUT_B, 54, 10));
        addSlot(new Slot(input, FUEL, 44, 48) { @Override public boolean mayPlace(ItemStack stack) { return stack.is(Items.LAVA_BUCKET); } });
        addSlot(new Slot(input, RESULT, 116, 29) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 8 + column * 18, 142));
    }

    private DataSlot tracked(net.minecraft.world.inventory.ContainerData data, int index) {
        DataSlot slot = new DataSlot() { @Override public int get() { return data.get(index); } @Override public void set(int value) { data.set(index, value); } };
        addDataSlot(slot);
        return slot;
    }
    public boolean isLit() { return burnTime.get() > 0; }
    public int litProgress() { int total = burnDuration.get(); return total == 0 ? 0 : burnTime.get() * 13 / total; }
    public int cookProgress() { return cookTime.get() * 24 / CrucibleBlockEntity.COOK_TIME_TOTAL; }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack source = slot.getItem(), moved = source.copy();
        if (index == RESULT) {
            if (!moveItemStackTo(source, 4, 40, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(source, moved);
        } else if (index < RESULT) {
            if (!moveItemStackTo(source, 4, 40, false)) return ItemStack.EMPTY;
        } else if (source.is(Items.LAVA_BUCKET)) {
            if (!moveItemStackTo(source, FUEL, FUEL + 1, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(source, INPUT_A, INPUT_B + 1, false)) return ItemStack.EMPTY;
        if (source.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        if (source.getCount() == moved.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, source);
        return moved;
    }

    @Override public boolean stillValid(Player player) {
        return access.evaluate((level, pos) -> level.getBlockState(pos).is(ModBlocks.CRUCIBLE.get())
                && player.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) <= 64.0, true);
    }
    @Override public void removed(Player player) { super.removed(player); }
}
