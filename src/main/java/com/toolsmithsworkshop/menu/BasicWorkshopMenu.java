package com.toolsmithsworkshop.menu;

import com.toolsmithsworkshop.registry.ModItems;
import com.toolsmithsworkshop.registry.ModMenus;
import com.toolsmithsworkshop.block.BasicWorkshopBlock;
import com.toolsmithsworkshop.item.ForgingHammerItem;
import com.toolsmithsworkshop.tool.ComponentRole;
import com.toolsmithsworkshop.tool.ToolMaterial;
import com.toolsmithsworkshop.tool.ToolMaterials;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

public final class BasicWorkshopMenu extends AbstractContainerMenu {
    public static final int MATERIAL = 0;
    public static final int FORGING_HAMMER = 1;
    public static final int RESULT = 2;
    public static final ComponentRole[] PARTS = {
            ComponentRole.PICKAXE_HEAD, ComponentRole.AXE_HEAD, ComponentRole.SHOVEL_HEAD,
            ComponentRole.SWORD_BLADE,
            ComponentRole.BINDING, ComponentRole.GRIP
    };

    private final Container input = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            BasicWorkshopMenu.this.slotsChanged(this);
        }
    };
    private final ResultContainer result = new ResultContainer();
    private final ContainerLevelAccess access;
    private final DataSlot selected = DataSlot.standalone();
    private final DataSlot workshopTier = DataSlot.standalone();

    public BasicWorkshopMenu(int id, Inventory inventory) {
        this(id, inventory, ContainerLevelAccess.NULL, 1);
    }

    public BasicWorkshopMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        this(id, inventory, access, 1);
    }

    public BasicWorkshopMenu(int id, Inventory inventory, ContainerLevelAccess access, int tier) {
        super(ModMenus.BASIC_WORKSHOP.get(), id);
        this.access = access;
        addDataSlot(selected);
        workshopTier.set(tier);
        addDataSlot(workshopTier);
        addSlot(new Slot(input, MATERIAL, 42, 29));
        addSlot(new Slot(input, FORGING_HAMMER, 80, 29) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof ForgingHammerItem; }
        });
        addSlot(new Slot(result, 0, 118, 29) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
            @Override public void onTake(Player player, ItemStack stack) {
                input.removeItem(MATERIAL, requiredCount(selectedPart()));
                damageForgingHammer();
                super.onTake(player, stack);
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
        }
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 8 + column * 18, 142));
    }

    public ComponentRole selectedPart() {
        return PARTS[Math.max(0, Math.min(selected.get(), PARTS.length - 1))];
    }

    public int workshopTier() {
        return workshopTier.get();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < 0 || id >= PARTS.length) return false;
        selected.set(id);
        slotsChanged(input);
        return true;
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        result.setItem(0, makeResult());
        broadcastChanges();
    }

    private ItemStack makeResult() {
        ToolMaterial material = materialFor(input.getItem(MATERIAL));
        ComponentRole part = selectedPart();
        ForgingHammerItem hammer = input.getItem(FORGING_HAMMER).getItem() instanceof ForgingHammerItem item ? item : null;
        if (material == null || hammer == null || !canForge(material, workshopTier(), hammer.workshopTier())
                || input.getItem(MATERIAL).getCount() < requiredCount(part)) return ItemStack.EMPTY;
        DeferredItem<?> component = ModItems.component(part, material.id());
        return component == null ? ItemStack.EMPTY : new ItemStack(component.get());
    }

    private void damageForgingHammer() {
        ItemStack hammer = input.getItem(FORGING_HAMMER);
        if (hammer.getDamageValue() + 1 >= hammer.getMaxDamage()) input.removeItem(FORGING_HAMMER, 1);
        else hammer.setDamageValue(hammer.getDamageValue() + 1);
    }

    public static int requiredCount(ComponentRole role) {
        if (role.isHead()) return 3;
        return role == ComponentRole.BINDING ? 2 : 1;
    }

    static boolean canForge(ToolMaterial material, int workshopTier, int hammerTier) {
        return material.workshopTier() <= workshopTier && material.workshopTier() <= hammerTier;
    }

    private static ToolMaterial materialFor(ItemStack stack) {
        if (stack.is(ItemTags.PLANKS)) return ToolMaterials.WOOD;
        for (ToolMaterial material : ToolMaterials.values()) {
            if (stack.is(material.repairItem())) return material;
        }
        return null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack source = slot.getItem();
        ItemStack moved = source.copy();
        if (index == RESULT) {
            if (!moveItemStackTo(source, 3, 39, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(source, moved);
        } else if (index < 2) {
            if (!moveItemStackTo(source, 3, 39, false)) return ItemStack.EMPTY;
        } else if (source.getItem() instanceof ForgingHammerItem) {
            if (!moveItemStackTo(source, FORGING_HAMMER, FORGING_HAMMER + 1, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(source, MATERIAL, MATERIAL + 1, false)) {
            return ItemStack.EMPTY;
        }
        if (source.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        if (source.getCount() == moved.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, source);
        return moved;
    }

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate((level, pos) -> level.getBlockState(pos).getBlock() instanceof BasicWorkshopBlock
                && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0, true);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        clearContainer(player, input);
    }
}
