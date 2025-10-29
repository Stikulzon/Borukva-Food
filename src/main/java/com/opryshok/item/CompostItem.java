package com.opryshok.item;

import com.opryshok.block.BetterFarmlandBlock;
import com.opryshok.utils.ModProperties;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

public class CompostItem extends SimplePolymerItem {
    public static final DispenserBehavior COMPOST_BEHAVIOR = new ItemDispenserBehavior(){
        @Override
        protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            ServerWorld world = pointer.world();
            Direction direction = pointer.state().get(net.minecraft.block.DispenserBlock.FACING);
            BlockPos pos = pointer.pos().offset(direction);
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() instanceof SaplingBlock|| state.getBlock() instanceof MossBlock) {
                Fertilizable block = (Fertilizable) state.getBlock();
                if (block.isFertilizable(world, pos, state)) {
                    if (!world.isClient()) {
                        if (block.canGrow(world, world.random, pos, state)) {
                            block.grow(world, world.random, pos, state);
                        }
                        stack.decrement(1);
                        return stack;
                    }
                }
            }

            return stack;
        }
    };
    public CompostItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof BetterFarmlandBlock block) {
            int newFertility = Math.min(block.getFertility(state) + 2, BetterFarmlandBlock.MAX_FERTILITY);
            int newAcidity = Math.min(block.getAcidity(state) + 1, BetterFarmlandBlock.MAX_ACIDITY);

            world.setBlockState(pos, state
                    .with(ModProperties.FERTILITY, newFertility)
                    .with(ModProperties.ACIDITY, newAcidity)
            );
            world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_FILL, SoundCategory.BLOCKS, 1f, 1f);

            context.getStack().decrement(1);

            return ActionResult.SUCCESS;
        }

        if (state.getBlock() instanceof SaplingBlock|| state.getBlock() instanceof MossBlock){
            Fertilizable block = (Fertilizable) state.getBlock();
            if (block.isFertilizable(world, pos, state)){
                if (!world.isClient()){
                    if (block.canGrow(world, world.random, pos, state)) {
                        block.grow((ServerWorld)world, world.random, pos, state);
                    }
                    context.getStack().decrement(1);
                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Text.translatable("tooltip.borukva-food.compost").formatted(Formatting.GRAY));
    }
}
