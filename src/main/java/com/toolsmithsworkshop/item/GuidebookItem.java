package com.toolsmithsworkshop.item;

import com.toolsmithsworkshop.menu.GuidebookMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class GuidebookItem extends Item {
    public GuidebookItem(Properties properties) { super(properties); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) serverPlayer.openMenu(
                new SimpleMenuProvider((id, inventory, ignored) -> new GuidebookMenu(id, inventory), Component.literal("Toolsmith's Guidebook")));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
