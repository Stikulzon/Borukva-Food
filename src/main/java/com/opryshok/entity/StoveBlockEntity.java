package com.opryshok.entity;

import com.opryshok.BorukvaFood;
import com.opryshok.block.ModBlocks;
import com.opryshok.block.cooking.Stove;
import com.opryshok.ui.FuelSlot;
import com.opryshok.ui.GuiTextures;
import com.opryshok.ui.LedgerSimpleGui;
import com.opryshok.utils.BorukvaFoodUtil;
import com.opryshok.utils.MinimalSidedInventory;
import eu.pb4.factorytools.api.advancement.TriggerCriterion;
import eu.pb4.factorytools.api.block.entity.LockableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StoveBlockEntity extends LockableBlockEntity implements MinimalSidedInventory, SidedInventory{
    private static final int[] SLOTS = new int[]{0};
    public float state = 0;
    public int fuelTicks = 0;
    public int fuelInitial = 1;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public StoveBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModEntities.STOVE, blockPos, blockState);
    }

    @Override
    public DefaultedList<ItemStack> getStacks() {
        return items;
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T t) {
        var self = (StoveBlockEntity) t;

        if (self.fuelTicks > 0) {
            self.fuelTicks -= MathHelper.lerp(self.state, 40, 16);
            self.state = (float) Math.min(self.state + 0.005, 1);

            if (!state.get(Stove.LIT)) {
                ModBlocks.STOVE.onStateReplaced(state, (ServerWorld) world,  pos, state.with(Stove.LIT, true), false);
                world.setBlockState(pos, state.with(Stove.LIT, true));
            }
            self.markDirty();

        } else {
            for (int i = 0; i < 1; i++) {
                var stack = self.getStack(i);

                if (!stack.isEmpty()) {
                    var value = world.getFuelRegistry().getFuelTicks(stack);
                    if (value != 0) {
                        var remainder = stack.getRecipeRemainder();
                        stack.decrement(1);
                        self.fuelTicks = value * 10;
                        self.fuelInitial = self.fuelTicks;
                        if (stack.isEmpty()) {
                            self.setStack(i, ItemStack.EMPTY);
                        }

                        if (!remainder.isEmpty()) {
                            BorukvaFoodUtil.tryInsertingRegular(self, remainder);
                            if (!remainder.isEmpty()) {
                                ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, remainder);
                            }
                        }

                        self.markDirty();
                        return;
                    }
                }
            }

            if (self.state != 0) {
                self.state = (float) Math.max(self.state - 0.02, 0);
                self.markDirty();
            }
            if (state.get(Stove.LIT)) {
                ModBlocks.STOVE.onStateReplaced(state, (ServerWorld) world,  pos, state.with(Stove.LIT, false), false);
                world.setBlockState(pos, state.with(Stove.LIT, false));
            }
        }

    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return world.getFuelRegistry().isFuel(stack);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return MinimalSidedInventory.super.removeStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return MinimalSidedInventory.super.removeStack(slot, amount);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData(view, this.items);
        view.putInt("FuelTicks", this.fuelTicks);
        view.putInt("FuelInitial", this.fuelInitial);
        view.putFloat("State", this.state);
    }

    @Override
    public void readData(ReadView view) {
        super.readData(view);
        Inventories.readData(view, items);
        this.fuelInitial = view.getInt("FuelInitial", 0);
        this.fuelTicks = view.getInt("FuelTicks", 0);
        this.state = view.getFloat("State", 0f);
    }

    @Override
    protected void createGui(ServerPlayerEntity playerEntity) {
        new Gui(playerEntity);
    }

    private class Gui extends LedgerSimpleGui {
        private boolean active;

        public Gui(ServerPlayerEntity player) {
            super(ScreenHandlerType.GENERIC_9X2, player, false);
            this.setTitle(GuiTextures.STOVE.apply(Text.translatable("block.borukva-food.stove")));

            this.setSlotRedirect(13, new FuelSlot(pos, player, StoveBlockEntity.this, 0, 0, 0));
            this.setSlot(4, GuiTextures.FLAME.get(progress()));
            this.active = StoveBlockEntity.this.fuelTicks > 0;
            this.open();
        }

        private float progress() {
            return StoveBlockEntity.this.fuelInitial > 0
                    ? MathHelper.clamp(StoveBlockEntity.this.fuelTicks / (float) StoveBlockEntity.this.fuelInitial, 0, 1)
                    : 0;
        }

        @Override
        public void onTick() {
            if (player.getEntityPos().squaredDistanceTo(Vec3d.ofCenter(StoveBlockEntity.this.pos)) > (18 * 18)) {
                this.close();
            }

            var active = StoveBlockEntity.this.fuelTicks > 0;
            if (!this.active && active) {
                this.active = true;
                TriggerCriterion.trigger(player, Identifier.of(BorukvaFood.MOD_ID, "fuel_stove"));
            }
            this.setSlot(4, GuiTextures.FLAME.get(progress()));
            super.onTick();
        }
    }
}
