package com.opryshok.item;

import com.opryshok.block.food.VeganPizza;
import com.opryshok.utils.BorukvaFoodUtil;
import com.opryshok.utils.ModProperties;
import com.opryshok.utils.ModTags;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class KnifeTool extends MiningToolItem implements PolymerItem {

    public KnifeTool(Item.Settings settings) {
        super(ToolMaterial.IRON, ModTags.Blocks.KNIFE_MINEABLE, 6.0F, -3.1F, settings);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable PacketContext context) {
        return Items.IRON_SWORD;
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();

        if (state.getBlock() instanceof VeganPizza block && player != null) {
            int i = state.get(ModProperties.SLICES);

            ItemScatterer.spawn(world, player.getX(), player.getY(), player.getZ(), new ItemStack(block.getSlice(), 1));
            player.getStackInHand(Hand.MAIN_HAND).damage(1, player, Hand.MAIN_HAND.getEquipmentSlot());
            if (i == 7){
                BorukvaFoodUtil.ledgerMixinInvoke();
                world.removeBlock(pos, false);
                world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            }
            else {
                world.setBlockState(pos, state.with(ModProperties.SLICES, i + 1), 3);
                BorukvaFoodUtil.ledgerMixinInvoke();
            }

            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
    }
}

