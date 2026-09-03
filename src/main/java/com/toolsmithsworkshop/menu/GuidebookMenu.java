package com.toolsmithsworkshop.menu;

import com.toolsmithsworkshop.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class GuidebookMenu extends AbstractContainerMenu {
    public GuidebookMenu(int id, Inventory inventory) { super(ModMenus.GUIDEBOOK.get(), id); }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
    @Override public boolean stillValid(Player player) { return true; }
}
