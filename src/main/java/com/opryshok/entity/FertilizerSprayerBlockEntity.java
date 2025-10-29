package com.opryshok.entity;

import com.opryshok.block.ModBlocks;
import com.opryshok.item.ModItems;
import com.opryshok.sounds.SoundRegistry;
import com.opryshok.ui.GuiTextures;
import com.opryshok.ui.LedgerSimpleGui;
import com.opryshok.ui.LedgerSlot;
import com.opryshok.utils.MinimalSidedInventory;
import com.opryshok.utils.ModProperties;
import eu.pb4.factorytools.api.block.entity.LockableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class FertilizerSprayerBlockEntity extends LockableBlockEntity implements MinimalSidedInventory, SidedInventory {
    private static final int[] SLOTS =  new int[]{0, 1};
    public static final int AREA_SIZE = 9;
    private int ticks = 20 * 60;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);
    public FertilizerSprayerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModEntities.FERTILIZER_SPRAYER, blockPos, blockState);
    }

    @Override
    public DefaultedList<ItemStack> getStacks() {
        return items;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return SLOTS;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData(view, this.items);
        view.putInt("ticks", this.ticks);
    }

    @Override
    public void readData(ReadView view) {
        super.readData(view);
        Inventories.readData(view, this.items);
        this.ticks = view.getInt("ticks", 20 * 60);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if(slot == 0 && stack.isOf(ModItems.FERTILIZER)){
            return true;
        } else return slot == 1 && stack.isOf(ModItems.COMPOST);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    protected void createGui(ServerPlayerEntity playerEntity) {
        new Gui(playerEntity);
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T t){
        if (t instanceof FertilizerSprayerBlockEntity self) {
            if (state.get(Properties.POWERED)) {
                if (self.ticks == 20 * 60) {
                    self.ticks = 0;
                    BlockPos startPos = pos.add(-AREA_SIZE / 2, -AREA_SIZE / 2, -AREA_SIZE / 2);
                    BlockPos endPos = pos.add(AREA_SIZE / 2, AREA_SIZE / 2, AREA_SIZE / 2);
                    AtomicBoolean play = new AtomicBoolean(false);
                    BlockPos.stream(startPos, endPos).forEach(blockPos -> {
                        BlockState blockState = world.getBlockState(blockPos);
                        if (blockState.isOf(ModBlocks.BETTER_FARMLAND)) {
                            for (ItemStack item : self.items) {
                                if (item.isOf(ModItems.FERTILIZER)) {
                                    BlockState farmlandState = world.getBlockState(blockPos);
                                    int fertility = farmlandState.get(ModProperties.FERTILITY);
                                    int acidity = farmlandState.get(ModProperties.ACIDITY);
                                    if ((fertility <= 8 && acidity > 3) || acidity > 7) {
                                        world.setBlockState(blockPos, farmlandState.with(ModProperties.FERTILITY, Math.min(fertility + 2, 10)).with(ModProperties.ACIDITY, Math.max(acidity - 1, 0)));
                                        item.decrement(1);
                                        play.set(true);
                                    }
                                } else if (item.isOf(ModItems.COMPOST)) {
                                    BlockState farmlandState = world.getBlockState(blockPos);
                                    int fertility = farmlandState.get(ModProperties.FERTILITY);
                                    int acidity = farmlandState.get(ModProperties.ACIDITY);
                                    if ((fertility <= 8 && acidity < 7) || acidity < 3) {
                                        world.setBlockState(blockPos, farmlandState.with(ModProperties.FERTILITY, Math.min(fertility + 2, 10)).with(ModProperties.ACIDITY, Math.min(acidity + 1, 10)));
                                        item.decrement(1);
                                        play.set(true);
                                    }
                                }
                            }
                        }
                    });
                    if (play.get()){
                        world.playSound(null, pos, SoundRegistry.SPRAYING, SoundCategory.BLOCKS, 0.5f, 1f);
                    }
                }else {
                    self.ticks++;
                }
            }else{
                self.ticks = 20 * 60;
            }
        }
    }
    public class Gui extends LedgerSimpleGui {
        public Gui(ServerPlayerEntity player) {
            super(ScreenHandlerType.GENERIC_9X2, player, false);
            this.setTitle(GuiTextures.FERTILIZER_SPRAYER.apply(Text.translatable("block.borukva-food.fertilizer_sprayer")));

            this.setSlotRedirect(9 + 3, new LedgerSlot(pos, player, FertilizerSprayerBlockEntity.this, 0, 0, 0){
                @Override
                public boolean canInsert(ItemStack stack) {
                    return stack.isOf(ModItems.FERTILIZER);
                }
            });
            this.setSlotRedirect(9 + 5, new LedgerSlot(pos, player, FertilizerSprayerBlockEntity.this,  1, 1, 0){
                @Override
                public boolean canInsert(ItemStack stack) {
                    return stack.isOf(ModItems.COMPOST);
                }
            });

            this.open();
        }
        @Override
        public void onTick() {
            if (player.getEntityPos().squaredDistanceTo(Vec3d.ofCenter(FertilizerSprayerBlockEntity.this.pos)) > (18 * 18)) {
                this.close();
            }
            super.onTick();
        }
    }
}
