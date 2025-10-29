package com.opryshok.entity;

import com.opryshok.BorukvaFood;
import com.opryshok.block.ModBlocks;
import com.opryshok.block.cooking.Pot;
import com.opryshok.polydex.PolydexCompat;
import com.opryshok.recipe.ModRecipeTypes;
import com.opryshok.recipe.pot.PotRecipe;
import com.opryshok.recipe.pot.PotInput;
import com.opryshok.sounds.SoundRegistry;
import com.opryshok.ui.GuiTextures;
import com.opryshok.ui.LedgerSimpleGui;
import com.opryshok.ui.LedgerSlot;
import com.opryshok.utils.InventoryList;
import com.opryshok.utils.MinimalSidedInventory;
import eu.pb4.factorytools.api.block.BlockEntityExtraListener;
import eu.pb4.factorytools.api.block.entity.LockableBlockEntity;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class PotBlockEntity extends LockableBlockEntity implements MinimalSidedInventory, SidedInventory, BlockEntityExtraListener {
    private final int INPUT_FIRST = 0;
    private static final int[] INPUT_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6};
    private static final int OUTPUT_SLOT = 7;
    protected double process = 0;
    private boolean active;
    private Pot.Model model;
    private ItemStack delayedOutput;
    private boolean inventoryChanged = true;
    private int soundTicks = 20;
    @Nullable
    protected RecipeEntry<PotRecipe> currentRecipe = null;
    private final List<ItemStack> stacks = new InventoryList(this, INPUT_FIRST, 6);
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(8, ItemStack.EMPTY);
    public PotBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModEntities.POT, blockPos, blockState);
    }
    @Override
    public DefaultedList<ItemStack> getStacks() {
        return items;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData(view, this.items);
        view.putDouble("Progress", this.process);
    }

    @Override
    public void readData(ReadView view) {
        super.readData(view);
        Inventories.readData(view, this.items);
        this.process = view.getDouble("Progress", 0d);
        if (this.model != null) {
            this.model.tick();
        }
    }

    private boolean isInputEmpty() {
        for (int i = 0; i < 6; i++) {
            if (!this.getStack(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    public static <T extends BlockEntity> void ticker(World world, BlockPos pos, BlockState state, T t) {
        var self = (PotBlockEntity) t;
        self.active = state.get(Properties.LIT);
        if (self.isInputEmpty()) {
            self.process = 0;
            self.model.tick();
            return;
        }

        if (self.currentRecipe == null && !self.inventoryChanged) {
            self.process = 0;
            self.model.tick();
            self.inventoryChanged = false;
            return;
        }

        var input = new PotInput(self.stacks, self.getStack(6), world);

        if (world instanceof ServerWorld serverWorld && self.inventoryChanged && (self.currentRecipe == null || !self.currentRecipe.value().matches(input, world))) {
            self.process = 0;
            self.currentRecipe = serverWorld.getRecipeManager().getFirstMatch(ModRecipeTypes.POT, input, world).orElse(null);

            if (self.currentRecipe == null) {
                self.model.tick();
                self.inventoryChanged = false;
                return;
            }
        }
        self.inventoryChanged = false;
        if (!self.getCachedState().get(Properties.LIT)) {
            self.model.tick();
            return;
        }

        assert self.currentRecipe != null;

        if (self.process >= self.currentRecipe.value().time()) {
            ItemStack nextOut;
            if (self.delayedOutput != null) {
                nextOut = self.delayedOutput;
                self.delayedOutput = null;
            } else {
                assert self.world != null;
                nextOut = self.currentRecipe.value().craft(input, self.world.getRegistryManager());
            }
            var currentOut =  self.getStack(OUTPUT_SLOT);

            boolean success = false;

            if (currentOut.isEmpty()) {
                success = true;
                self.setStack(OUTPUT_SLOT, nextOut);
            } else {
                assert nextOut != null;
                if (ItemStack.areItemsAndComponentsEqual(currentOut, nextOut) && (currentOut.getCount() + nextOut.getCount()) <= 64) {
                    success = true;
                    currentOut.increment(nextOut.getCount());
                }
            }

            if (success) {
                self.process = 0;
                self.currentRecipe.value().applyRecipeUse(self, world);
            } else {
                self.delayedOutput = nextOut;
            }
        } else {
            self.process++;
            if (world instanceof ServerWorld serverWorld) {
                if (serverWorld.getTime() % 40 == 0) {
                    if (serverWorld.getBlockState(pos).isOf(ModBlocks.POT)) {
                        if (serverWorld.getBlockState(pos).get(Properties.LIT)) {
                            for (int i = 0; i < 5; i++) {
                                serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                        pos.getX() + 0.5,
                                        pos.getY() + 1.25,
                                        pos.getZ() + 0.5,
                                        1, 0.15, 0.4, 0.15, 0);
                            }
                        }
                    }
                }
            }
            if (self.soundTicks >= 20) {
                world.playSound(null, pos, SoundRegistry.BOILING, SoundCategory.BLOCKS, 1f, 1f);
                self.soundTicks = 0;
            } else {
                self.soundTicks++;
            }
        }
    }
    @Override
    public int[] getAvailableSlots(Direction side) {
        return INPUT_SLOTS;
    }
    @Override
    public void markDirty() {
        super.markDirty();
        this.inventoryChanged = true;
    }
    private float progress() {
        return PotBlockEntity.this.currentRecipe != null
                ? (float) MathHelper.clamp(PotBlockEntity.this.process / PotBlockEntity.this.currentRecipe.value().time(), 0, 1)
                : 0;
    }
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    protected void createGui(ServerPlayerEntity playerEntity) {
        new Gui(playerEntity);
    }

    @Override
    public void onListenerUpdate(WorldChunk chunk) {
        try {
            this.model = (Pot.Model) Objects.requireNonNull(BlockAwareAttachment.get(chunk, this.getPos())).holder();
        } catch (Throwable e) {
            BorukvaFood.LOGGER.debug("error: ", e);
        }
    }

    public class Gui extends LedgerSimpleGui {
        public Gui(ServerPlayerEntity player) {
            super(ScreenHandlerType.GENERIC_9X3, player, false);
            this.setTitle(GuiTextures.POT.apply(Text.translatable("block.borukva-food.pot")));
            this.setSlot(17, PolydexCompat.getButton(ModRecipeTypes.POT));
            this.setSlotRedirect(0, new LedgerSlot(pos, player, PotBlockEntity.this, 0, 0, 0));
            this.setSlotRedirect(1, new LedgerSlot(pos, player, PotBlockEntity.this, 1, 1, 0));
            this.setSlotRedirect(2, new LedgerSlot(pos, player, PotBlockEntity.this, 2, 2, 0));
            this.setSlotRedirect(9, new LedgerSlot(pos, player, PotBlockEntity.this, 3, 3, 0));
            this.setSlotRedirect(10, new LedgerSlot(pos, player, PotBlockEntity.this, 4, 4, 0));
            this.setSlotRedirect(11, new LedgerSlot(pos, player, PotBlockEntity.this, 5, 5, 0));
            this.setSlot(13, GuiTextures.PROGRESS_HORIZONTAL_OFFSET_RIGHT.get(progress()));
            this.setSlot(19, GuiTextures.FLAME.get(PotBlockEntity.this.active ? 1 : 0));
            this.setSlotRedirect(7, new LedgerSlot(pos, player, PotBlockEntity.this, 6, 6, 0){
                @Override
                public boolean canInsert(ItemStack stack) {
                    return stack.isOf(Items.BOWL);
                }
            });

            this.setSlotRedirect(25, new FurnaceOutputSlot(player, PotBlockEntity.this, 7, 3, 0));
            this.open();
        }
        @Override
        public void onTick() {
            if (player.getEntityPos().squaredDistanceTo(Vec3d.ofCenter(PotBlockEntity.this.pos)) > (18 * 18)) {
                this.close();
            }
            this.setSlot(13, GuiTextures.PROGRESS_HORIZONTAL_OFFSET_RIGHT.get(progress()));
            this.setSlot(19, GuiTextures.FLAME.get(PotBlockEntity.this.active ? 1 : 0));
            super.onTick();

            super.onTick();
        }

    }
}
