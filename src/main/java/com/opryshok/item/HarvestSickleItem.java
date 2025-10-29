package com.opryshok.item;

import com.github.quiltservertools.ledger.callbacks.BlockBreakCallback;
import com.github.quiltservertools.ledger.callbacks.BlockPlaceCallback;
import com.opryshok.utils.ModTags;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.Consumer;

public class HarvestSickleItem extends MiningToolItem implements PolymerItem {

    public HarvestSickleItem(Settings settings) {
        super(ToolMaterial.IRON, ModTags.Blocks.SICKLE_MINEABLE, -2.0F, -1.0F, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos placeAt = context.getBlockPos().offset(context.getSide());
        PlayerEntity player = context.getPlayer();
//        ItemStack sickleStack = context.getStack();

        if (!world.isClient() && player != null) {
        ItemStack offHandStack = player.getOffHandStack();
            boolean actionPerformed = false;

            // Check if the offhand item can be planted at the specified position.
            if (offHandStack.getItem() instanceof BlockItem seedItem
                && offHandStack.isIn(ModTags.Items.CONVENTIONAL_SEEDS)
                && seedItem.getBlock() instanceof CropBlock cropBlock
                && cropBlock.getDefaultState().canPlaceAt(world, placeAt)
            ) {
                // Plant seeds in a 3x3 area
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos pos = placeAt.add(dx, 0, dz);
                        BlockState stateAtPos = world.getBlockState(pos);

                        if (stateAtPos.isAir() && cropBlock.getDefaultState().canPlaceAt(world, pos) && offHandStack.getCount() > 0) {
                            BlockState seedBlockState = seedItem.getBlock().getDefaultState();
                            world.setBlockState(pos, seedBlockState);
                            offHandStack.decrement(1);
                            actionPerformed = true;
                            if(FabricLoader.getInstance().isModLoaded("ledger")) {
                                BlockPlaceCallback.EVENT.invoker().place(world, pos, seedBlockState, world.getBlockEntity(pos), player);
                            }
                        }
                    }
                }

                if (actionPerformed) {
                    // Play planting sound
                    world.playSound(null, placeAt, SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    public static void registerAttackAction() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!world.isClient() && hand == Hand.MAIN_HAND) {
                ItemStack sickleStack = player.getMainHandStack();
                if (sickleStack.getItem() instanceof HarvestSickleItem) {
                    boolean brokePlant = false;
                    int currentDamage = sickleStack.getDamage();

                    // Iterate over a 3x3 area centered on the targeted block
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            BlockPos targetPos = pos.add(dx, 0, dz);
                            BlockState state = world.getBlockState(targetPos);

                            if (state.getBlock() instanceof PlantBlock && sickleStack.getItem().canMine(sickleStack, state, world, targetPos, player)) {
                                world.breakBlock(targetPos, true, player);
                                sickleStack.getItem().postMine(sickleStack, world, state, targetPos, player);
                                if(FabricLoader.getInstance().isModLoaded("ledger")) {
                                    BlockBreakCallback.EVENT.invoker().breakBlock(world, targetPos, state, world.getBlockEntity(targetPos), player);
                                }
                                brokePlant = true;
                            }
                        }
                    }
                    sickleStack.setDamage(currentDamage);

                    if (brokePlant) {
                        if (player instanceof ServerPlayerEntity serverPlayerEntity && world instanceof ServerWorld serverWorld && !(player).getAbilities().creativeMode) {
                            sickleStack.damage(1, serverWorld, serverPlayerEntity, item -> {});
                        }
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.PASS;
        });
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
    //public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, tooltip, type);
        tooltip.accept(Text.translatable("tooltip.borukva-food.harvest_sickle").formatted(Formatting.GRAY));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable PacketContext context) {
        return Items.IRON_SWORD;
    }
}
