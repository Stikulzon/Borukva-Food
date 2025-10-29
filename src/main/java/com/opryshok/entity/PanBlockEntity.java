package com.opryshok.entity;

import com.opryshok.BorukvaFood;
import com.opryshok.block.cooking.Pan;
import com.opryshok.polydex.PolydexCompat;
import com.opryshok.recipe.ModRecipeTypes;
import com.opryshok.recipe.pan.PanInput;
import com.opryshok.recipe.pan.PanRecipe;
import com.opryshok.sounds.SoundRegistry;
import com.opryshok.ui.GuiTextures;
import com.opryshok.ui.LedgerSimpleGui;
import com.opryshok.ui.LedgerSlot;
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
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandlerType;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;


public class PanBlockEntity extends LockableBlockEntity implements MinimalSidedInventory, BlockEntityExtraListener, SidedInventory{
    private static final int[] SLOTS =  new int[]{0, 1, 2};
    public boolean active = false;
    private int soundTicks = 20;
    private static final int SOUND_PLAY_INTERVAL = 15;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private final HashMap<Integer, Integer> slotTick = new HashMap<>();
    {
        slotTick.put(0, 0);
        slotTick.put(1, 0);
        slotTick.put(2, 0);
    }
    @SuppressWarnings("unchecked")
    protected RecipeEntry<PanRecipe>[] currentRecipes = (RecipeEntry<PanRecipe>[]) new RecipeEntry[3];
    private Pan.Model model;
    public PanBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModEntities.PAN, blockPos, blockState);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData(view, this.items);
    }

    @Override
    public void readData(ReadView view) {
        super.readData(view);
        Inventories.readData(view, this.items);
        if (this.model != null) {
            this.model.updateItems(this.getStacks());
        }
    }
    @Override
    public void markDirty() {
        super.markDirty();
        if (this.model != null) {
            this.model.updateItems(this.getStacks());
        }
    }
    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T t) {
        if (t instanceof PanBlockEntity self && world instanceof ServerWorld serverWorld) {
            self.active = state.get(Properties.LIT);
            boolean hasItems = self.items.stream().anyMatch(stack -> !stack.isEmpty() && !stack.isOf(Items.AIR));
            if (self.active && hasItems) {
                for (int i = 0; i < self.items.size(); i++) {
                    ItemStack stack = self.items.get(i);
                    RecipeEntry<PanRecipe> currentRecipe = self.currentRecipes[i];

                    if (currentRecipe == null || !currentRecipe.value().matches(new PanInput(stack, world), world)) {
                        self.currentRecipes[i] = serverWorld.getRecipeManager()
                                .getFirstMatch(ModRecipeTypes.PAN, new PanInput(stack, world), world)
                                .orElse(null);
                        self.slotTick.put(i, 0);
                    }

                    if (currentRecipe != null) {
                        int tickCount = self.slotTick.getOrDefault(i, 0);
                        double recipeTime = currentRecipe.value().time();

                        if (tickCount >= recipeTime * stack.getCount()) {
                            ItemStack result = currentRecipe.value().craft(new PanInput(stack, world), world.getRegistryManager()).copyWithCount(stack.getCount());
                            self.items.set(i, result);
                            self.slotTick.put(i, 0);
                            self.currentRecipes[i] = null;
                            self.model.updateItems(self.items);
                        } else {
                            self.slotTick.put(i, tickCount + 1);
                        }
                    } else {
                        self.slotTick.put(i, 0);
                    }
                }

                if (self.active) {
                    self.soundTicks++;

                    if (self.soundTicks >= SOUND_PLAY_INTERVAL) {
                        float pitch = 0.95f + world.getRandom().nextFloat() * 0.1f;
                        float volume = 0.9f + world.getRandom().nextFloat() * 0.1f;

                        world.playSound(
                                null,
                                pos,
                                SoundRegistry.FRYING,
                                SoundCategory.BLOCKS,
                                volume,
                                pitch
                        );
                        self.soundTicks = 0;
                    }
                } else {
                    if (self.soundTicks > 0) {
                        self.soundTicks = 0;
                    }
                }
            }
        }
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
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }
    @Override
    protected void createGui(ServerPlayerEntity playerEntity) {
        new PanBlockEntity.Gui(playerEntity);
    }

    @Override
    public void onListenerUpdate(WorldChunk chunk) {
        try {
            this.model = (Pan.Model) Objects.requireNonNull(BlockAwareAttachment.get(chunk, this.getPos())).holder();
            this.model.updateItems(this.items);
        } catch (Throwable e) {
            BorukvaFood.LOGGER.debug("error: ", e);
        }

    }

    public class Gui extends LedgerSimpleGui {
        public Gui(ServerPlayerEntity player) {
            super(ScreenHandlerType.GENERIC_9X2, player, false);
            this.setTitle(GuiTextures.PAN.apply(Text.translatable("block.borukva-food.pan")));
            this.setSlot(0, PolydexCompat.getButton(ModRecipeTypes.PAN));
            this.setSlotRedirect(3, new LedgerSlot(pos, player, PanBlockEntity.this, 0, 0, 0));
            this.setSlotRedirect(4, new LedgerSlot(pos, player, PanBlockEntity.this,  1, 1, 0));
            this.setSlotRedirect(5, new LedgerSlot(pos, player, PanBlockEntity.this, 2, 2, 0));

            this.setSlot(13, GuiTextures.FLAME.get(PanBlockEntity.this.active ? 1 : 0));

            this.open();
        }
        @Override
        public void onTick() {
            if (player.getEntityPos().squaredDistanceTo(Vec3d.ofCenter(PanBlockEntity.this.pos)) > (18 * 18)) {
                this.close();
            }
            this.setSlot(13, GuiTextures.FLAME.get(PanBlockEntity.this.active ? 1 : 0));
            super.onTick();
        }
    }
}
