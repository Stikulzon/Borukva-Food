package com.opryshok.recipe.pan;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.opryshok.BorukvaFood;
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

public record PanRecipe (String group, CountedIngredient input, ItemStack output, double time) implements Recipe<PanInput> {
    public static final MapCodec<PanRecipe> CODEC = RecordCodecBuilder.mapCodec(x -> x.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(PanRecipe::group),
                    CountedIngredient.CODEC.fieldOf("input").forGetter(PanRecipe::input),
                    ItemStack.CODEC.fieldOf("output").forGetter(PanRecipe::output),
                    Codec.DOUBLE.optionalFieldOf("time", 0d).forGetter(PanRecipe::time)
            ).apply(x, PanRecipe::new)
    );
    public static RecipeEntry<PanRecipe> of(String string, CountedIngredient ingredient, ItemStack output, double time) {
        return new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, BorukvaFood.id("pan/" + string)), new PanRecipe("", ingredient, output, time));
    }
    @Override
    public boolean matches(PanInput input, World world) {
        if(world.isClient()){
            return false;
        }
        return this.input.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(PanInput input, RegistryWrapper.WrapperLookup lookup) {
        return output;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<PanInput>> getSerializer() {
        return ModRecipeSerializer.PAN;
    }

    @Override
    public RecipeType<? extends Recipe<PanInput>> getType() {
        return ModRecipeTypes.PAN;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.NONE;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return CRAFTING_MISC;
    }
}
