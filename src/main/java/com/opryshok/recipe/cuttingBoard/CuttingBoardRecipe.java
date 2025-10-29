package com.opryshok.recipe.cuttingBoard;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.opryshok.BorukvaFood;
import com.opryshok.entity.CuttingBoardBlockEntity;
import com.opryshok.recipe.ModRecipeSerializer;
import com.opryshok.recipe.ModRecipeTypes;
import eu.pb4.factorytools.api.recipe.CountedIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import static net.minecraft.recipe.book.RecipeBookCategories.CRAFTING_MISC;

public record CuttingBoardRecipe (String group, CountedIngredient input, ItemStack output) implements Recipe<CuttingBoardInput> {
    public static final MapCodec<CuttingBoardRecipe> CODEC = RecordCodecBuilder.mapCodec(x -> x.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(CuttingBoardRecipe::group),
                    CountedIngredient.CODEC.fieldOf("input").forGetter(CuttingBoardRecipe::input),
                    ItemStack.CODEC.fieldOf("output").forGetter(CuttingBoardRecipe::output)
            ).apply(x, CuttingBoardRecipe::new)
    );
    public static RecipeEntry<CuttingBoardRecipe> of(String string, CountedIngredient ingredient, ItemStack output) {
        return new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, BorukvaFood.id("cutting_board/" + string)), new CuttingBoardRecipe("", ingredient, output));
    }

    @Override
    public boolean matches(CuttingBoardInput input, World world) {
        if(world.isClient()){
            return false;
        }
        return this.input.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(CuttingBoardInput input, RegistryWrapper.WrapperLookup lookup) {
        return output;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<CuttingBoardInput>> getSerializer() {
        return ModRecipeSerializer.CUTTING_BOARD;
    }

    @Override
    public RecipeType<? extends Recipe<CuttingBoardInput>> getType() {
        return ModRecipeTypes.CUTTING_BOARD;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.NONE;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return CRAFTING_MISC;
    }
    public void applyRecipeUse(CuttingBoardBlockEntity inventory, World world) {
        inventory.getStack(0).decrement(1);
        if(inventory.getStack(0).isEmpty()){
            inventory.setStack(0, ItemStack.EMPTY);
        }
    }
}
