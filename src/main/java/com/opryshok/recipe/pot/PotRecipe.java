package com.opryshok.recipe.pot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.opryshok.BorukvaFood;
import com.opryshok.entity.PotBlockEntity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.minecraft.recipe.book.RecipeBookCategories.CRAFTING_MISC;

public record PotRecipe(String group, List<CountedIngredient> input, CountedIngredient bowl, ItemStack output,
                        double time) implements Recipe<PotInput> {
    public static final MapCodec<PotRecipe> CODEC = RecordCodecBuilder.mapCodec(x -> x.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(PotRecipe::group),
            CountedIngredient.LIST_CODEC.fieldOf("input").forGetter(PotRecipe::input),
            CountedIngredient.CODEC.optionalFieldOf("bowl", CountedIngredient.EMPTY).forGetter(PotRecipe::bowl),
            ItemStack.CODEC.fieldOf("output").forGetter(PotRecipe::output),
            Codec.DOUBLE.optionalFieldOf("time", 0d).forGetter(PotRecipe::time)
        ).apply(x, PotRecipe::new)
    );

    public static RecipeEntry<PotRecipe> of(String string, List<CountedIngredient> ingredient, CountedIngredient bowl, ItemStack output, double time) {
        return new RecipeEntry<>(RegistryKey.of(RegistryKeys.RECIPE, BorukvaFood.id("pot/" + string)), new PotRecipe("", ingredient, bowl, output, time));
    }

    @Override
    public boolean matches(PotInput inventory, World world) {
        if (world.isClient()) {
            return false;
        }

        List<ItemStack> inventoryStacks = new ArrayList<>();
        for (ItemStack stack : inventory.stacks()) {
            if (!stack.isEmpty()) {
                inventoryStacks.add(stack);
            }
        }
        if (this.input.size() > inventoryStacks.size() || this.input.size() < inventoryStacks.size()) {
            return false;
        }

        ItemStack bowlStack = inventory.bowl();
        if (!this.bowl.ingredient().isEmpty()) {
            if (!this.bowl.test(bowlStack) || bowlStack.getCount() < this.bowl.count()) {
                return false;
            }
        }

        List<CountedIngredient> recipeInputs = new ArrayList<>(this.input);

        for (CountedIngredient recipeInput : recipeInputs) {
            Optional<Ingredient> optional = recipeInput.ingredient();
            boolean found = false;
            for (int j = 0; j < inventoryStacks.size(); j++) {
                ItemStack stack = inventoryStacks.get(j);
                if (!stack.isEmpty() && optional.isPresent() && optional.get().test(stack)) {
                    inventoryStacks.remove(j);
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }


        return true;
    }

    @Override
    public ItemStack craft(PotInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.output.copy();
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<PotInput>> getSerializer() {
        return ModRecipeSerializer.POT;
    }

    @Override
    public RecipeType<? extends Recipe<PotInput>> getType() {
        return ModRecipeTypes.POT;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.NONE;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return CRAFTING_MISC;
    }

    @Override
    public double time() {
        return time;
    }

    public void applyRecipeUse(PotBlockEntity inventory, World world) {
        for (int i = 0; i < 6; i++) {
            var stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                stack.decrement(1);
                if (stack.isEmpty()) {
                    inventory.setStack(i, ItemStack.EMPTY);
                }
            }
        }

        var bowlStack = inventory.getStack(6);
        if (!bowlStack.isEmpty()) {
            bowlStack.decrement(this.bowl.count());
            if (bowlStack.isEmpty()) {
                inventory.setStack(6, ItemStack.EMPTY);
            }
        }
    }
}
