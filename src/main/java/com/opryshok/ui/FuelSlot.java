package com.opryshok.ui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class FuelSlot extends LedgerSlot {
    public FuelSlot(BlockPos pos, ServerPlayerEntity player, Inventory inventory, int index, int x, int y) {
        super(pos, player, inventory, index, x, y);
    }

    public boolean canInsert(ItemStack stack) {
        return player.getEntityWorld().getFuelRegistry().isFuel(stack) || isBucket(stack);
    }

    public int getMaxItemCount(ItemStack stack) {
        return isBucket(stack) ? 1 : super.getMaxItemCount(stack);
    }

    public static boolean isBucket(ItemStack stack) {
        return stack.isOf(Items.BUCKET);
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        super.onTakeItem(player, stack);
    }
}
