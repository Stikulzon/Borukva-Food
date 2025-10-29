package com.opryshok.entity;

import com.opryshok.BorukvaFood;
import com.opryshok.block.cooking.CuttingBoard;
import com.opryshok.item.ModItems;
import com.opryshok.recipe.ModRecipeTypes;
import com.opryshok.recipe.cuttingBoard.CuttingBoardInput;
import com.opryshok.recipe.cuttingBoard.CuttingBoardRecipe;
import com.opryshok.utils.BorukvaFoodUtil;
import com.opryshok.utils.MinimalSidedInventory;
import eu.pb4.factorytools.api.block.BlockEntityExtraListener;
import eu.pb4.factorytools.api.block.entity.LockableBlockEntity;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CuttingBoardBlockEntity extends LockableBlockEntity implements MinimalSidedInventory, SidedInventory, BlockEntityExtraListener{
    private static final int[] SLOTS = new int[]{0};
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private CuttingBoard.Model model;
    @Nullable
    protected RecipeEntry<CuttingBoardRecipe> currentRecipe = null;
    public CuttingBoardBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModEntities.CUTTING_BOARD, blockPos, blockState);
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
            this.model.updateItem(this.getItemStack());
        }
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.model != null) {
            this.model.updateItem(getItemStack());
        }
    }
    @Override
    public DefaultedList<ItemStack> getStacks() {
        return items;
    }

    public ItemStack getItemStack(){
        return getStack(0);
    }
    public boolean addItem(ItemStack itemStack) {
        if (isEmpty() && !itemStack.isEmpty()) {
            setStack(0, itemStack.split(1));
            markDirty();
            return true;
        }

        return false;
    }
    public void removeItem() {
        if (!isEmpty()) {

            setStack(0, ItemStack.EMPTY);
            markDirty();
        }
    }
    public boolean processItemUsingTool(World world, ItemStack tool, PlayerEntity player) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return false;
        }
        if (tool.isOf(ModItems.KNIFE)){
            var input = new CuttingBoardInput(getItemStack(), world);
            if(currentRecipe == null && !getItemStack().isEmpty()){
                currentRecipe = serverWorld.getRecipeManager().getFirstMatch(ModRecipeTypes.CUTTING_BOARD, input, world).orElse(null);
            }
            if(currentRecipe != null){
                tool.damage(1, player, player.getActiveHand().getEquipmentSlot());

                BorukvaFoodUtil.ledgerMixinInvoke();

                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), currentRecipe.value().craft(input, world.getRegistryManager()).copy());
                currentRecipe.value().applyRecipeUse(this, world);
                currentRecipe = null;
                markDirty();
            }
        }
        return true;
    }
    @Override
    public int[] getAvailableSlots(Direction side) {
        return SLOTS;
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
    public void onListenerUpdate(WorldChunk chunk) {
        try {
            this.model = (CuttingBoard.Model) Objects.requireNonNull(BlockAwareAttachment.get(chunk, this.getPos())).holder();
            this.model.updateItem(getItemStack());
        } catch (Throwable e) {
            BorukvaFood.LOGGER.debug("error: ", e);
        }
    }
}
