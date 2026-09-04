package com.toolsmithsworkshop.block.entity;

import com.toolsmithsworkshop.registry.ModBlockEntities;
import com.toolsmithsworkshop.block.CrucibleBlock;
import com.toolsmithsworkshop.registry.ModItems;
import com.toolsmithsworkshop.registry.ModDataComponents;
import com.toolsmithsworkshop.tool.ComponentRole;
import com.toolsmithsworkshop.tool.ToolComponentData;
import com.toolsmithsworkshop.tool.ToolMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public final class CrucibleBlockEntity extends BlockEntity implements Container {
    public static final int COOK_TIME_TOTAL = 200;
    private static final int LAVA_PER_BUCKET = 1_000;
    private static final int LAVA_BURN_TIME = 20_000;
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private int burnTime, burnDuration, cookTime;
    private final FluidTank lava = new FluidTank(4_000, fluid -> fluid.getFluid() == net.minecraft.world.level.material.Fluids.LAVA);
    public final ContainerData data = new ContainerData() {
        @Override public int get(int index) { return switch (index) { case 0 -> burnTime; case 1 -> burnDuration; case 2 -> cookTime; case 3 -> COOK_TIME_TOTAL; default -> 0; }; }
        @Override public void set(int index, int value) { switch (index) { case 0 -> burnTime = value; case 1 -> burnDuration = value; case 2 -> cookTime = value; } }
        @Override public int getCount() { return 4; }
    };

    public CrucibleBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.CRUCIBLE.get(), pos, state); }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        boolean changed = false;
        if (crucible.burnTime > 0) { crucible.burnTime--; changed = true; }
        crucible.loadLavaBucket();
        if (crucible.canSmelt()) {
            if (crucible.burnTime == 0 && crucible.lava.getFluidAmount() >= LAVA_PER_BUCKET) {
                crucible.lava.drain(LAVA_PER_BUCKET, IFluidHandler.FluidAction.EXECUTE);
                crucible.burnDuration = crucible.burnTime = LAVA_BURN_TIME;
                changed = true;
            }
            if (crucible.burnTime > 0 && ++crucible.cookTime >= COOK_TIME_TOTAL) {
                crucible.items.get(0).shrink(1);
                crucible.items.get(1).shrink(1);
                ItemStack output = crucible.items.get(3);
                if (output.isEmpty()) crucible.items.set(3, crucible.smeltingResult()); else output.grow(1);
                crucible.cookTime = 0;
                changed = true;
            }
        } else if (crucible.cookTime != 0) { crucible.cookTime = 0; changed = true; }
        boolean lit = crucible.burnTime > 0 && crucible.canSmelt();
        if (state.getValue(CrucibleBlock.LIT) != lit) level.setBlock(pos, state.setValue(CrucibleBlock.LIT, lit), 3);
        if (changed) crucible.setChanged();
    }

    private boolean canSmelt() {
        ItemStack result = smeltingResult();
        if (!result.isEmpty()) {
            ItemStack output = items.get(3);
            return output.isEmpty() || ItemStack.isSameItemSameComponents(output, result) && output.getCount() < output.getMaxStackSize();
        }
        return false;
    }

    private ItemStack smeltingResult() {
        // Check for component recycling (slot 0 has a tool component, slot 1 is empty)
        if (items.get(1).isEmpty() && !items.get(0).isEmpty()) {
            ItemStack component = items.get(0);
            ToolComponentData data = component.getItem() instanceof com.toolsmithsworkshop.item.ToolComponentItem
                    ? component.get(ModDataComponents.TOOL_COMPONENT) : null;
            if (data != null) {
                var material = ToolMaterials.get(data.material());
                return new ItemStack(material.repairItem().get(), 1);
            }
        }
        // Original alloy smelting recipes
        if (matches(Items.GOLD_INGOT, Items.NETHERITE_SCRAP)) return new ItemStack(Items.NETHERITE_INGOT);
        if (matches(Items.COPPER_INGOT, Items.GOLD_INGOT)) return new ItemStack(ModItems.ROSE_GOLD_INGOT.get());
        if (matches(Items.IRON_INGOT, Items.COAL_BLOCK)) return new ItemStack(ModItems.STEEL_INGOT.get());
        if (matches(ModItems.STEEL_INGOT.get(), Items.BLAZE_POWDER)) return new ItemStack(ModItems.BLAZE_STEEL_INGOT.get());
        if (matches(Items.NETHERITE_INGOT, Items.ECHO_SHARD)) return new ItemStack(ModItems.SCULKITE_INGOT.get());
        if (matches(ModItems.STEEL_INGOT.get(), Items.SOUL_SAND) || matches(ModItems.STEEL_INGOT.get(), Items.SOUL_SOIL)) return new ItemStack(ModItems.SOUL_STEEL_INGOT.get());
        return ItemStack.EMPTY;
    }

    private boolean matches(net.minecraft.world.item.Item first, net.minecraft.world.item.Item second) {
        return items.get(0).is(first) && items.get(1).is(second) || items.get(0).is(second) && items.get(1).is(first);
    }

    private void loadLavaBucket() {
        if (items.get(2).is(Items.LAVA_BUCKET) && lava.fill(new FluidStack(net.minecraft.world.level.material.Fluids.LAVA, LAVA_PER_BUCKET), IFluidHandler.FluidAction.SIMULATE) == LAVA_PER_BUCKET) {
            lava.fill(new FluidStack(net.minecraft.world.level.material.Fluids.LAVA, LAVA_PER_BUCKET), IFluidHandler.FluidAction.EXECUTE);
            items.set(2, new ItemStack(Items.BUCKET));
            setChanged();
        }
    }

    public IFluidHandler fluidHandler() { return lava; }

    @Override public int getContainerSize() { return items.size(); }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack stack = ContainerHelper.removeItem(items, slot, amount); if (!stack.isEmpty()) setChanged(); return stack; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); stack.limitSize(getMaxStackSize(stack)); setChanged(); }
    @Override public boolean stillValid(Player player) { return level != null && player.distanceToSqr(worldPosition.getX() + .5, worldPosition.getY() + .5, worldPosition.getZ() + .5) <= 64; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot != 3 && (slot != 2 || stack.is(Items.LAVA_BUCKET)); }
    @Override public void clearContent() { items.clear(); setChanged(); }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("BurnDuration", burnDuration);
        tag.putInt("CookTime", cookTime);
        CompoundTag lavaTag = new CompoundTag();
        lava.writeToNBT(registries, lavaTag);
        tag.put("Lava", lavaTag);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) { super.loadAdditional(tag, registries); ContainerHelper.loadAllItems(tag, items, registries); burnTime = tag.getInt("BurnTime"); burnDuration = tag.getInt("BurnDuration"); cookTime = tag.getInt("CookTime"); lava.readFromNBT(registries, tag.getCompound("Lava")); }
}
