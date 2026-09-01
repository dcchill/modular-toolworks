package com.toolsmithsworkshop.menu;

import com.toolsmithsworkshop.item.ModularToolItem;
import com.toolsmithsworkshop.item.ForgingHammerItem;
import com.toolsmithsworkshop.registry.ModBlocks;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.registry.ModItems;
import com.toolsmithsworkshop.registry.ModMenus;
import com.toolsmithsworkshop.tool.ComponentRole;
import com.toolsmithsworkshop.tool.ToolArchetype;
import com.toolsmithsworkshop.tool.ToolBuildData;
import com.toolsmithsworkshop.tool.ToolComponentData;
import com.toolsmithsworkshop.tool.ToolMaterials;
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
import net.minecraft.world.item.Items;

public final class ToolsmithingMenu extends AbstractContainerMenu {
    public static final int HEAD = 0;
    public static final int BINDING = 1;
    public static final int GRIP = 2;
    public static final int FORGING_HAMMER = 3;
    public static final int REPAIR = 4;
    public static final int RESULT = 5;

    private final Container input = new SimpleContainer(5) {
        @Override
        public void setChanged() {
            super.setChanged();
            ToolsmithingMenu.this.slotsChanged(this);
        }
    };
    private final ResultContainer result = new ResultContainer();
    private final ContainerLevelAccess access;
    private final DataSlot selected = DataSlot.standalone();

    public ToolsmithingMenu(int id, Inventory inventory) {
        this(id, inventory, ContainerLevelAccess.NULL);
    }

    public ToolsmithingMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenus.TOOLSMITHING.get(), id);
        this.access = access;
        addDataSlot(selected);
        addSlot(componentSlot(input, HEAD, 26, 27, true));
        addSlot(componentSlot(input, BINDING, 52, 27, false));
        addSlot(componentSlot(input, GRIP, 78, 27, false));
        addSlot(new Slot(input, FORGING_HAMMER, 104, 53) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof ForgingHammerItem; }
        });
        addSlot(new Slot(input, REPAIR, 52, 53));
        addSlot(new Slot(result, 0, 130, 34) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
            @Override public void onTake(Player player, ItemStack stack) {
                consumeInputs();
                super.onTake(player, stack);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
        }
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 8 + column * 18, 142));
    }

    private Slot componentSlot(Container container, int index, int x, int y, boolean allowTool) {
        return new Slot(container, index, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (allowTool && stack.getItem() instanceof ModularToolItem) return true;
                ToolComponentData component = componentData(stack);
                if (component == null) return false;
                return switch (index) {
                    case HEAD -> component.role() == archetype().headRole();
                    case BINDING -> component.role() == ComponentRole.BINDING;
                    case GRIP -> component.role() == ComponentRole.GRIP;
                    default -> false;
                };
            }
        };
    }

    public ToolArchetype archetype() {
        return ToolArchetype.values()[Math.max(0, Math.min(selected.get(), ToolArchetype.values().length - 1))];
    }

    public ItemStack preview() {
        return result.getItem(0);
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        result.setItem(0, makeResult());
        broadcastChanges();
    }

    private ItemStack makeResult() {
        ItemStack headStack = input.getItem(HEAD);
        if (headStack.getItem() instanceof ModularToolItem tool && !input.getItem(REPAIR).isEmpty()
                && !input.getItem(FORGING_HAMMER).isEmpty()) {
            ItemStack repaired = headStack.copy();
            if (tool.isValidRepairItem(repaired, input.getItem(REPAIR)) && repaired.isDamaged()) {
                repaired.setDamageValue(Math.max(0, repaired.getDamageValue() - Math.max(1, repaired.getMaxDamage() / 4)));
                repaired.setCount(1);
                return repaired;
            }
            return ItemStack.EMPTY;
        }

        ToolComponentData head = componentData(headStack);
        ToolComponentData binding = componentData(input.getItem(BINDING));
        ToolComponentData grip = componentData(input.getItem(GRIP));
        if (head == null || binding == null || grip == null || head.role() != archetype().headRole()
                || binding.role() != ComponentRole.BINDING || grip.role() != ComponentRole.GRIP) return ItemStack.EMPTY;

        ModularToolItem item = switch (archetype()) {
            case PICKAXE -> ModItems.MODULAR_PICKAXE.get();
            case AXE -> ModItems.MODULAR_AXE.get();
        };
        if (input.getItem(FORGING_HAMMER).isEmpty()) return ItemStack.EMPTY;
        return ModularToolItem.create(item, new ToolBuildData(head.material(), binding.material(), grip.material()));
    }

    private static ToolComponentData componentData(ItemStack stack) {
        ToolComponentData component = stack.get(ModDataComponents.TOOL_COMPONENT);
        return component == null && stack.is(Items.STICK)
                ? new ToolComponentData(ComponentRole.GRIP, ToolMaterials.WOOD.id()) : component;
    }

    private boolean consumeInputs() {
        if (input.getItem(HEAD).getItem() instanceof ModularToolItem) {
            input.removeItem(HEAD, 1);
            input.removeItem(REPAIR, 1);
        } else {
            input.removeItem(HEAD, 1);
            input.removeItem(BINDING, 1);
            input.removeItem(GRIP, 1);
        }
        damageForgingHammer();
        slotsChanged(input);
        return true;
    }

    private void damageForgingHammer() {
        ItemStack hammer = input.getItem(FORGING_HAMMER);
        if (hammer.getDamageValue() + 1 >= hammer.getMaxDamage()) input.removeItem(FORGING_HAMMER, 1);
        else hammer.setDamageValue(hammer.getDamageValue() + 1);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < ToolArchetype.values().length) {
            selected.set(id);
            slotsChanged(input);
            return true;
        }
        if (id == 3 && !preview().isEmpty()) {
            ItemStack output = preview().copy();
            if (player.getInventory().add(output)) return consumeInputs();
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack moved = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return moved;
        ItemStack source = slot.getItem();
        moved = source.copy();
        if (index == RESULT) {
            if (!moveItemStackTo(source, 5, 41, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(source, moved);
        } else if (index < 5) {
            if (!moveItemStackTo(source, 6, 42, false)) return ItemStack.EMPTY;
        } else {
            ToolComponentData component = componentData(source);
            int target = component == null ? -1 : component.role().isHead() ? HEAD : component.role() == ComponentRole.BINDING ? BINDING : GRIP;
            if (source.getItem() instanceof ModularToolItem) target = HEAD;
            if (source.getItem() instanceof ForgingHammerItem) target = FORGING_HAMMER;
            if (target < 0 || !moveItemStackTo(source, target, target + 1, false)) return ItemStack.EMPTY;
        }
        if (source.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        if (source.getCount() == moved.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, source);
        return moved;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.TOOLSMITHING_WORKBENCH.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        clearContainer(player, input);
    }
}
