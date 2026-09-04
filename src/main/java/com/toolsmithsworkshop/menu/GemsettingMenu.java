package com.toolsmithsworkshop.menu;

import com.toolsmithsworkshop.item.ModularToolItem;
import com.toolsmithsworkshop.registry.ModBlocks;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.registry.ModMenus;
import com.toolsmithsworkshop.tool.ToolBuildData;
import com.toolsmithsworkshop.tool.ToolGems;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public final class GemsettingMenu extends AbstractContainerMenu {
    public static final int TOOL = 0;
    public static final int GEM = 1;
    public static final int RESULT = 2;

    private final Container input = new SimpleContainer(2) {
        @Override public void setChanged() { super.setChanged(); GemsettingMenu.this.slotsChanged(this); }
    };
    private final ResultContainer result = new ResultContainer();
    private final ContainerLevelAccess access;

    public GemsettingMenu(int id, Inventory inventory) {
        this(id, inventory, ContainerLevelAccess.NULL);
    }

    public GemsettingMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenus.GEMSETTING.get(), id);
        this.access = access;
        addSlot(new Slot(input, TOOL, 42, 29) { @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof ModularToolItem; } });
        addSlot(new Slot(input, GEM, 80, 29) { @Override public boolean mayPlace(ItemStack stack) { return ToolGems.isGem(stack); } });
        addSlot(new Slot(result, 0, 118, 29) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
            @Override public void onTake(Player player, ItemStack stack) {
                input.removeItem(TOOL, 1);
                input.removeItem(GEM, 1);
                super.onTake(player, stack);
            }
        });
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 8 + column * 18, 142));
    }

    @Override public void slotsChanged(Container container) {
        super.slotsChanged(container);
        result.setItem(0, makeResult());
        broadcastChanges();
    }

    private ItemStack makeResult() {
        ItemStack tool = input.getItem(TOOL);
        ToolBuildData build = tool.get(ModDataComponents.TOOL_BUILD);
        if (!(tool.getItem() instanceof ModularToolItem item) || build == null || !ToolGems.canSocket(build, input.getItem(GEM))) return ItemStack.EMPTY;
        var modules = new ArrayList<>(build.modules());
        modules.add(ToolGems.id(input.getItem(GEM)));
        ItemStack socketed = tool.copy();
        socketed.set(ModDataComponents.TOOL_BUILD,
                new ToolBuildData(build.head(), build.binding(), build.grip(), modules, build.partStats()));
        ModularToolItem.refreshStats(socketed, item);
        return socketed;
    }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack source = slot.getItem();
        ItemStack moved = source.copy();
        if (index == RESULT) {
            if (!moveItemStackTo(source, 3, 39, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(source, moved);
        } else if (index < RESULT) {
            if (!moveItemStackTo(source, 3, 39, false)) return ItemStack.EMPTY;
        } else if (source.getItem() instanceof ModularToolItem) {
            if (!moveItemStackTo(source, TOOL, TOOL + 1, false)) return ItemStack.EMPTY;
        } else if (ToolGems.isGem(source)) {
            if (!moveItemStackTo(source, GEM, GEM + 1, false)) return ItemStack.EMPTY;
        } else return ItemStack.EMPTY;
        if (source.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        if (source.getCount() == moved.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, source);
        return moved;
    }

    @Override public boolean stillValid(Player player) { return stillValid(access, player, ModBlocks.GEMSETTING_BENCH.get()); }
    @Override public void removed(Player player) { super.removed(player); clearContainer(player, input); }
}
