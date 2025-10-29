package com.opryshok.datagen;

import com.opryshok.BorukvaFood;
import com.opryshok.block.ModBlocks;
import com.opryshok.item.ModItems;
import com.opryshok.recipe.cuttingBoard.CuttingBoardRecipe;
import com.opryshok.recipe.pan.PanRecipe;
import com.opryshok.recipe.pot.PotRecipe;
import com.opryshok.utils.ModTags;
import eu.pb4.factorytools.api.recipe.CountedIngredient;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModRecipeGenerator extends RecipeGenerator {
    private final RegistryEntryLookup<Item> itemLookup;
    protected ModRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
        super(registries, exporter);
        itemLookup = registries.getOrThrow(RegistryKeys.ITEM);
    }

    @Override
    public void generate() {
        compressBlockRecipe(ModBlocks.BEETROOT_CRATE_ITEM, Items.BEETROOT, exporter);
        compressBlockRecipe(ModBlocks.CABBAGE_CRATE_ITEM, ModItems.CABBAGE, exporter);
        compressBlockRecipe(ModBlocks.CARROT_CRATE_ITEM, Items.CARROT, exporter);
        compressBlockRecipe(ModBlocks.CHILLI_CRATE_ITEM, ModItems.CHILLI_PEPPER, exporter);
        compressBlockRecipe(ModBlocks.CORN_CRATE_ITEM, ModItems.CORN, exporter);
        compressBlockRecipe(ModBlocks.CUCUMBER_CRATE_ITEM, ModItems.CUCUMBER, exporter);
        compressBlockRecipe(ModBlocks.LETTUCE_CRATE_ITEM, ModItems.LETTUCE, exporter);
        compressBlockRecipe(ModBlocks.POTATO_CRATE_ITEM, Items.POTATO, exporter);
        compressBlockRecipe(ModBlocks.TOMATO_CRATE_ITEM, ModItems.TOMATO, exporter);
        compressBlockRecipe(ModBlocks.ONION_CRATE_ITEM, ModItems.ONION, exporter);
        compressBlockRecipe(ModBlocks.RICE_CRATE_ITEM, ModItems.RICE, exporter);
        compressBlockRecipe(ModBlocks.CHORUS_CRATE_ITEM, Items.CHORUS_FRUIT, exporter);

        seedsRecipe(ModItems.TOMATO, ModItems.TOMATO_SEEDS, exporter);
        seedsRecipe(ModItems.CABBAGE, ModItems.CABBAGE_SEEDS, exporter);
        seedsRecipe(ModItems.CHILLI_PEPPER, ModItems.CHILLI_PEPPER_SEEDS, exporter);
        seedsRecipe(ModItems.CORN, ModItems.CORN_SEEDS, exporter);
        seedsRecipe(ModItems.CUCUMBER, ModItems.CUCUMBER_SEEDS, exporter);
        seedsRecipe(ModItems.LETTUCE, ModItems.LETTUCE_SEEDS, exporter);
        seedsRecipe(ModItems.ONION, ModItems.ONION_SEEDS, exporter);

        offerSmelting(List.of(ModItems.HOGLIN_MEAT), RecipeCategory.FOOD, ModItems.COOKED_HOGLIN_MEAT, 0.35f, 200, "cooked_hoglin_meat");
        offerSmelting(List.of(ModItems.BEEF_SLICES), RecipeCategory.FOOD, ModItems.COOKED_BEEF_SLICES, 0.35f, 200, "cooked_beef_slices");
        offerSmelting(List.of(ModItems.MUTTON_SLICES), RecipeCategory.FOOD, ModItems.COOKED_MUTTON_SLICES, 0.35f, 200, "cooked_mutton_slices");
        offerSmelting(List.of(ModItems.CHICKEN_LEG), RecipeCategory.FOOD, ModItems.COOKED_CHICKEN_LEG, 0.35f, 200, "cooked_chicken_leg");
        offerSmelting(List.of(ModItems.SQUID_RING), RecipeCategory.FOOD, ModItems.COOKED_SQUID_RING, 0.35f, 200, "cooked_squid_ring");

        createShaped(RecipeCategory.MISC, ModBlocks.STOVE_ITEM, 1)
                .pattern("SSS")
                .pattern("G G")
                .pattern("GGG")
                .input('S', ConventionalItemTags.IRON_INGOTS)
                .input('G', Blocks.MUD_BRICKS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(Blocks.MUD_BRICKS), conditionsFromItem(Blocks.MUD_BRICKS))
                .offerTo(exporter);

        createShaped(RecipeCategory.MISC, ModBlocks.PAN_ITEM, 1)
                .pattern(" SS")
                .pattern(" SS")
                .pattern("G  ")
                .input('S', ConventionalItemTags.IRON_INGOTS)
                .input('G', ConventionalItemTags.NORMAL_BRICKS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(Items.BRICK), conditionsFromItem(Items.BRICK))
                .offerTo(exporter);


        createShapeless(RecipeCategory.MISC, ModBlocks.CUTTING_BOARD_ITEM, 1)
                .input(ingredientFromTag(ItemTags.WOODEN_SLABS))
                .criterion("has_slabs", conditionsFromTag(ItemTags.WOODEN_SLABS))
                .offerTo(exporter);

        createShaped(RecipeCategory.TOOLS, ModItems.KNIFE, 1)
                .pattern("I ")
                .pattern("S ")
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .input('S', ConventionalItemTags.WOODEN_RODS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter);

        createShaped(RecipeCategory.TOOLS, ModItems.HARVEST_SICKLE, 1)
                .pattern("II")
                .pattern(" S")
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .input('S', ConventionalItemTags.WOODEN_RODS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter);

        createShapeless(RecipeCategory.TOOLS, ModItems.FERTILIZER, 4)
                .input(ConventionalItemTags.AMETHYST_GEMS)
                .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModItems.BLACKCURRANT_COOKIE, 8)
                .pattern("WBW")
                .input('W', ConventionalItemTags.WHEAT_CROPS)
                .input('B', ModItems.BLACKCURRANTS)
                .criterion(hasItem(ModItems.BLACKCURRANTS), conditionsFromItem(ModItems.BLACKCURRANTS))
                .criterion(hasItem(Items.WHEAT), conditionsFromItem(Items.WHEAT))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModItems.SWEET_BERRY_COOKIE, 8)
                .pattern("WBW")
                .input('W', ConventionalItemTags.WHEAT_CROPS)
                .input('B', Items.SWEET_BERRIES)
                .criterion(hasItem(Items.SWEET_BERRIES), conditionsFromItem(Items.SWEET_BERRIES))
                .criterion(hasItem(Items.WHEAT), conditionsFromItem(Items.WHEAT))
                .offerTo(exporter);

        createShaped(RecipeCategory.TOOLS, ModItems.SOIL_ANALIZATOR, 1)
                .pattern(" S ")
                .pattern("SRS")
                .pattern("SSS")
                .input('S', ConventionalItemTags.COPPER_INGOTS)
                .input('R', ConventionalItemTags.REDSTONE_DUSTS)
                .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModItems.BEEF_SANDWICH, 1)
                .pattern(" S ")
                .pattern("LBK")
                .pattern(" S ")
                .input('S', ModItems.BREAD_SLICE)
                .input('L', ModTags.Items.FOODS_VEGETABLES_LETTUCE)
                .input('B', Items.COOKED_BEEF)
                .input('K', ModItems.KETCHUP)
                .criterion(hasItem(Items.COOKED_BEEF), conditionsFromItem(Items.COOKED_BEEF))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModItems.TOMATO_SANDWICH, 1)
                .pattern(" S ")
                .pattern("LTM")
                .pattern(" S ")
                .input('S', ModItems.BREAD_SLICE)
                .input('L', ModTags.Items.FOODS_VEGETABLES_LETTUCE)
                .input('T', ModTags.Items.CROPS_TOMATO)
                .input('M', ModItems.MAYONNAISE)
                .criterion(hasItem(ModItems.TOMATO), conditionsFromItem(ModItems.TOMATO))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.CHOCOLATE_BAR, 1)
                .input(ConventionalItemTags.COCOA_BEAN_CROPS)
                .input(Items.SUGAR)
                .criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS))
                .criterion(hasItem(Items.SUGAR), conditionsFromItem(Items.SUGAR))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.ICE_CREAM, 1)
                .input(ModItems.WAFFLE)
                .input(Items.SNOWBALL)
                .criterion(hasItem(ModItems.WAFFLE), conditionsFromItem(ModItems.WAFFLE))
                .criterion(hasItem(Items.SNOWBALL), conditionsFromItem(Items.SNOWBALL))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.CHOCOLATE_ICE_CREAM, 1)
                .input(ModItems.WAFFLE)
                .input(Items.SNOWBALL)
                .input(ingredientFromTag(ModTags.Items.createTag("chocolate")))
                .criterion(hasItem(ModItems.WAFFLE), conditionsFromItem(ModItems.WAFFLE))
                .criterion(hasItem(Items.SNOWBALL), conditionsFromItem(Items.SNOWBALL))
                .criterion(hasItem(ModItems.CHOCOLATE_BAR), conditionsFromItem(ModItems.CHOCOLATE_BAR))
                .offerTo(exporter);

        campfireCookingRecipe(exporter, ModItems.BEEF_BARBECUE, ModItems.COOKED_BEEF_BARBECUE);
        campfireCookingRecipe(exporter, ModItems.VEGAN_BARBECUE, ModItems.COOKED_VEGAN_BARBECUE);

        createShapeless(RecipeCategory.FOOD, ModItems.KETCHUP, 1)
                .input(ModTags.Items.CROPS_TOMATO)
                .input(ingredientFromTag(ModTags.Items.createTag("dusts/salt")))
                .input(Items.SUGAR)
                .criterion(hasItem(ModItems.TOMATO), conditionsFromItem(ModItems.TOMATO))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.MAYONNAISE, 1)
                .input(ingredientFromTag(ModTags.Items.createTag("cooking_oil")))
                .input(ConventionalItemTags.EGGS)
                .input(ingredientFromTag(ModTags.Items.createTag("lemon")))
                .criterion(hasItem(Items.EGG), conditionsFromItem(Items.EGG))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.ENDER_JAM, 1)
                .input(ConventionalItemTags.ENDER_PEARLS)
                .input(ConventionalItemTags.ENDER_PEARLS)
                .criterion(hasItem(Items.ENDER_PEARL), conditionsFromItem(Items.ENDER_PEARL))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModItems.VEGAN_BARBECUE, 1)
                .pattern("P  ")
                .pattern("L  ")
                .pattern("S  ")
                .input('P', ModTags.Items.PLANT_FOOD)
                .input('L', Ingredient.ofItems(ModItems.CUCUMBER_SLICES, ModItems.TOMATO_SLICES))
                .input('S', ConventionalItemTags.WOODEN_RODS)
                .criterion("has_item_with_tag", conditionsFromTag(ModTags.Items.PLANT_FOOD))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModItems.BEEF_BARBECUE, 1)
                .pattern("B  ")
                .pattern("B  ")
                .pattern("S  ")
                .input('B', ModTags.Items.FOODS_RAW_MEATS_RAW_BEEF)
                .input('S', ConventionalItemTags.WOODEN_RODS)
                .criterion(hasItem(ModItems.BEEF_SLICES), conditionsFromItem(ModItems.BEEF_SLICES))
                .offerTo(exporter);

        createShaped(RecipeCategory.BREWING, ModItems.EMPTY_JAR, 1)
                .pattern(" S ")
                .pattern("G G")
                .pattern(" G ")
                .input('S', ItemTags.WOODEN_SLABS)
                .input('G', ConventionalItemTags.GLASS_PANES)
                .criterion(hasItem(Items.GLASS_PANE), conditionsFromItem(Items.GLASS_PANE))
                .criterion("has_slabs", conditionsFromTag(ItemTags.WOODEN_SLABS))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModItems.PICKLE_JAR, 1)
                .pattern("SPS")
                .pattern("CCC")
                .pattern(" E ")
                .input('S', ingredientFromTag(ModTags.Items.createTag("salt")))
                .input('P', Items.SUGAR)
                .input('C', ModTags.Items.CROPS_CUCUMBER)
                .input('E', ingredientFromTag(ModTags.Items.createTag("jars/empty")))
                .criterion(hasItem(ModItems.EMPTY_JAR), conditionsFromItem(ModItems.EMPTY_JAR))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.APPLE_CANDY, 1)
                .input(Items.APPLE)
                .input(Items.SUGAR)
                .input(ConventionalItemTags.WOODEN_RODS)
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.HONEY_CANDY, 3)
                .input(ConventionalItemTags.HONEY_DRINKS)
                .input(Items.SUGAR)
                .criterion(hasItem(Items.HONEY_BOTTLE), conditionsFromItem(Items.HONEY_BOTTLE))
                .criterion(hasItem(Items.SUGAR), conditionsFromItem(Items.SUGAR))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.JACK_CANDY, 3)
                .input(ModItems.WAFFLE)
                .input(ingredientFromTag(ModTags.Items.createTag("chocolate")))
                .criterion(hasItem(ModItems.WAFFLE), conditionsFromItem(ModItems.WAFFLE))
                .criterion(hasItem(ModItems.CHOCOLATE_BAR), conditionsFromItem(ModItems.CHOCOLATE_BAR))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.PUMPKIN_CANDY, 3)
                .input(ConventionalItemTags.NORMAL_PUMPKINS)
                .input(Items.SUGAR)
                .criterion(hasItem(Items.PUMPKIN), conditionsFromItem(Items.PUMPKIN))
                .criterion(hasItem(Items.SUGAR), conditionsFromItem(Items.SUGAR))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.LEMON_CANDY, 3)
                .input(ingredientFromTag(ModTags.Items.createTag("lemon")))
                .input(Items.SUGAR)
                .criterion(hasItem(ModItems.LEMON), conditionsFromItem(ModItems.LEMON))
                .criterion(hasItem(Items.SUGAR), conditionsFromItem(Items.SUGAR))
                .offerTo(exporter);

        createShaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SALT_BLOCK_ITEM, 1)
                .pattern("SS")
                .pattern("SS")
                .input('S', ingredientFromTag(ModTags.Items.createTag("salt")))
                .criterion(hasItem(ModItems.SALT), conditionsFromItem(ModItems.SALT))
                .offerTo(exporter);
        createShapeless(RecipeCategory.FOOD, ModItems.SALT, 4)
                .input(ModBlocks.SALT_BLOCK_ITEM, 1)
                .criterion(hasItem(ModBlocks.SALT_BLOCK_ITEM), conditionsFromItem(ModBlocks.SALT_BLOCK_ITEM))
                .offerTo(exporter, Identifier.of(BorukvaFood.MOD_ID, getRecipeName(ModBlocks.SALT_BLOCK_ITEM) + "_to_" + getRecipeName(ModItems.SALT)).toString());

        planksRecipe(ModTags.Items.AVOCADO_LOGS, ModBlocks.AVOCADO_PLANKS_ITEM, exporter);
        planksRecipe(ModTags.Items.LEMON_LOGS, ModBlocks.LEMON_PLANKS_ITEM, exporter);

        stairsRecipe(ModBlocks.AVOCADO_PLANKS_ITEM, ModBlocks.AVOCADO_STAIRS_ITEM, exporter);
        stairsRecipe(ModBlocks.LEMON_PLANKS_ITEM, ModBlocks.LEMON_STAIRS_ITEM, exporter);

        woodRecipe(ModBlocks.AVOCADO_LOG_ITEM, ModBlocks.AVOCADO_WOOD_ITEM, exporter);
        woodRecipe(ModBlocks.LEMON_LOG_ITEM, ModBlocks.LEMON_WOOD_ITEM, exporter);
        woodRecipe(ModBlocks.STRIPPED_AVOCADO_LOG_ITEM, ModBlocks.STRIPPED_AVOCADO_WOOD_ITEM, exporter);
        woodRecipe(ModBlocks.STRIPPED_LEMON_LOG_ITEM, ModBlocks.STRIPPED_LEMON_WOOD_ITEM, exporter);

        pieRecipe(ModItems.HONEY_PIE, ingredientFromTag(ConventionalItemTags.HONEY_DRINKS), exporter);
        pieRecipe(ModItems.APPLE_PIE, Ingredient.ofItems(Items.APPLE), exporter);
        pieRecipe(ModItems.LEMON_PIE, ingredientFromTag(ModTags.Items.createTag("lemon")), exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.CHEESE, 1)
                .input(ConventionalItemTags.MILK_BUCKETS)
                .input(ingredientFromTag(ModTags.Items.createTag("salt")))
                .input(ConventionalItemTags.EGGS)
                .criterion(hasItem(Items.MILK_BUCKET), conditionsFromItem(Items.MILK_BUCKET))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.WAFFLE, 2)
                .input(ConventionalItemTags.WHEAT_CROPS)
                .input(Items.SUGAR)
                .input(ConventionalItemTags.WHEAT_CROPS)
                .criterion(hasItem(Items.WHEAT), conditionsFromItem(Items.WHEAT))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.LAVASH, 2)
                .input(ConventionalItemTags.WHEAT_CROPS)
                .input(ingredientFromTag(ModTags.Items.createTag("salt")))
                .input(ConventionalItemTags.WHEAT_CROPS)
                .criterion(hasItem(Items.WHEAT), conditionsFromItem(Items.WHEAT))
                .offerTo(exporter);

        popcornRecipe(ModItems.CHEESE_POPCORN, ingredientFromTag(ModTags.Items.createTag("cheese")), exporter);
        popcornRecipe(ModItems.SALT_POPCORN, ingredientFromTag(ModTags.Items.createTag("salt")), exporter);
        popcornRecipe(ModItems.CHOCOLATE_POPCORN, ingredientFromTag(ModTags.Items.createTag("chocolate")), exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.GUACAMOLE, 1)
                .input(ingredientFromTag(ModTags.Items.createTag("avocado")))
                .input(Items.BOWL)
                .criterion(hasItem(ModItems.AVOCADO), conditionsFromItem(ModItems.AVOCADO))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.SHAWARMA, 1)
                .input(ingredientFromTag(ModTags.Items.createTag("spice")))
                .input(ModTags.Items.FOODS_COOKED_MEATS_COOKED_BEEF)
                .input(ModItems.KETCHUP)
                .input(ModItems.MAYONNAISE)
                .input(Ingredient.ofItems(ModItems.TOMATO_SLICES, ModItems.CUCUMBER_SLICES))
                .input(ModTags.Items.FOODS_VEGETABLES_LETTUCE)
                .input(ModItems.LAVASH)
                .criterion(hasItem(ModItems.COOKED_BEEF_SLICES), conditionsFromItem(ModItems.COOKED_BEEF_BARBECUE))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.VEGETABLE_SALAD, 1)
                .input(ModTags.Items.FOODS_VEGETABLES_LETTUCE)
                .input(ingredientFromTag(ModTags.Items.createTag("cooking_oil")))
                .input(ingredientFromTag(ModTags.Items.createTag("foods/sauce")))
                .input(ModItems.CUCUMBER_SLICES)
                .input(ModItems.TOMATO_SLICES)
                .input(Items.BOWL)
                .criterion(hasItem(ModItems.LETTUCE), conditionsFromItem(ModItems.LETTUCE))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.BEEF_SALAD, 1)
                .input(ModTags.Items.FOODS_VEGETABLES_LETTUCE)
                .input(ingredientFromTag(ModTags.Items.createTag("cooking_oil")))
                .input(ModTags.Items.FOODS_COOKED_MEATS_COOKED_BEEF)
                .input(ModItems.CUCUMBER_SLICES)
                .input(ModItems.TOMATO_SLICES)
                .input(Items.BOWL)
                .criterion(hasItem(ModItems.LETTUCE), conditionsFromItem(ModItems.LETTUCE))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.OIL, 1)
                .input(ModItems.SUNFLOWER_SEED)
                .input(ModItems.SUNFLOWER_SEED)
                .criterion(hasItem(ModItems.SUNFLOWER_SEED), conditionsFromItem(ModItems.SUNFLOWER_SEED))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModBlocks.MEAT_PIZZA_ITEM, 1)
                .pattern("KMS")
                .pattern("JCJ")
                .pattern("WWW")
                .input('K', ModItems.KETCHUP)
                .input('M', ModItems.MAYONNAISE)
                .input('S', ModItems.TOMATO_SLICES)
                .input('J', ingredientFromTag(ItemTags.MEAT))
                .input('C', ingredientFromTag(ModTags.Items.createTag("cheese")))
                .input('W', ConventionalItemTags.WHEAT_CROPS)
                .criterion(hasItem(ModItems.KETCHUP), conditionsFromItem(ModItems.KETCHUP))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModBlocks.VEGAN_PIZZA_ITEM, 1)
                .pattern("KMS")
                .pattern("JCJ")
                .pattern("WWW")
                .input('K', ModItems.KETCHUP)
                .input('M', ModItems.MAYONNAISE)
                .input('S', ModItems.TOMATO_SLICES)
                .input('J', ConventionalItemTags.MUSHROOMS)
                .input('C', ingredientFromTag(ModTags.Items.createTag("cheese")))
                .input('W', ConventionalItemTags.WHEAT_CROPS)
                .criterion(hasItem(Items.BROWN_MUSHROOM), conditionsFromItem(Items.BROWN_MUSHROOM))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModBlocks.CHOCOLATE_CAKE_ITEM, 1)
                .pattern("MMM")
                .pattern("CBC")
                .pattern("WWW")
                .input('M', ConventionalItemTags.MILK_BUCKETS)
                .input('C', ingredientFromTag(ModTags.Items.createTag("chocolate")))
                .input('B', ModItems.BLACKCURRANTS)
                .input('W', ConventionalItemTags.WHEAT_CROPS)
                .criterion(hasItem(ModItems.CHOCOLATE_BAR), conditionsFromItem(ModItems.CHOCOLATE_BAR))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModBlocks.HONEY_CAKE_ITEM, 1)
                .pattern("MMM")
                .pattern("CBC")
                .pattern("WWW")
                .input('M', ConventionalItemTags.MILK_BUCKETS)
                .input('C', ConventionalItemTags.HONEY_DRINKS)
                .input('B', ModItems.GOOSEBERRY)
                .input('W', ConventionalItemTags.WHEAT_CROPS)
                .criterion(hasItem(Items.HONEY_BOTTLE), conditionsFromItem(Items.HONEY_BOTTLE))
                .offerTo(exporter);

        createShaped(RecipeCategory.REDSTONE, ModBlocks.FERTILIZER_SPRAYER_ITEM, 1)
                .pattern("III")
                .pattern("CDC")
                .pattern("RRR")
                .input('R', ConventionalItemTags.REDSTONE_DUSTS)
                .input('C', Items.SMOOTH_STONE)
                .input('D', Items.DISPENSER.asItem())
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .criterion(hasItem(Items.DISPENSER), conditionsFromItem(Items.DISPENSER))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.RICE, 3)
                .input(ModItems.RICE_PANICLE)
                .criterion(hasItem(ModItems.RICE_PANICLE), conditionsFromItem(ModItems.RICE_PANICLE))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.SALMON_NIGIRI, 3)
                .input(ModTags.Items.FOODS_RAW_FISHES_SALMON)
                .input(ModTags.Items.CROPS_RICE)
                .input(ingredientFromTag(ModTags.Items.createTag("sea_lettuce")))
                .criterion(hasItem(ModItems.SALMON_FILLET), conditionsFromItem(ModItems.SALMON_FILLET))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.SALMON_MAKI, 3)
                .input(ingredientFromTag(ModTags.Items.createTag("sea_lettuce")))
                .input(ModTags.Items.FOODS_RAW_FISHES_SALMON)
                .input(ModTags.Items.CROPS_RICE)
                .input(Ingredient.ofItems(ModItems.CUCUMBER_SLICES, ModItems.GUACAMOLE))
                .criterion(hasItem(ModItems.SALMON_FILLET), conditionsFromItem(ModItems.SALMON_FILLET))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.SALMON_URAMAKI, 3)
                .input(ingredientFromTag(ModTags.Items.createTag("sea_lettuce")))
                .input(ModTags.Items.FOODS_RAW_FISHES_SALMON)
                .input(ModTags.Items.CROPS_RICE)
                .input(Ingredient.ofItems(ModItems.CUCUMBER_SLICES, ModItems.GUACAMOLE))
                .input(ModItems.MAYONNAISE)
                .criterion(hasItem(ModItems.SALMON_FILLET), conditionsFromItem(ModItems.SALMON_FILLET))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.ONIGIRI, 1)
                .input(ingredientFromTag(ModTags.Items.createTag("sea_lettuc")))
                .input(ModTags.Items.CROPS_RICE)
                .criterion(hasItem(ModItems.RICE), conditionsFromItem(ModItems.RICE))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModItems.SAUERKRAUT, 1)
                .pattern("SGS")
                .pattern("CJC")
                .pattern(" B ")
                .input('S', ingredientFromTag(ModTags.Items.createTag("salt")))
                .input('G', Items.SUGAR)
                .input('C', ModTags.Items.CROPS_CABBAGE)
                .input('J', ConventionalItemTags.CARROT_CROPS)
                .input('B', Items.BOWL)
                .criterion(hasItem(ModItems.CABBAGE), conditionsFromItem(ModItems.CABBAGE))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModItems.RICE_BOWL, 1)
                .pattern("RRR")
                .pattern(" B ")
                .input('R', ModTags.Items.CROPS_RICE)
                .input('B', Items.BOWL)
                .criterion(hasItem(ModItems.RICE), conditionsFromItem(ModItems.RICE))
                .offerTo(exporter);

        createShaped(RecipeCategory.MISC, ModBlocks.POT_ITEM, 1)
                .pattern("B B")
                .pattern("I I")
                .pattern("III")
                .input('B', ConventionalItemTags.NORMAL_BRICKS)
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter);

        of(exporter,
                PotRecipe.of("borsch", List.of(
                        
                                CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ConventionalItemTags.POTATO_CROPS)),
                                CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ConventionalItemTags.BEETROOT_CROPS)),
                                CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.CROPS_ONION)),
                                CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.CROPS_CABBAGE)),
                                CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ConventionalItemTags.RAW_MEAT_FOODS)),
                                CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.createTag("salt")))
                        ), CountedIngredient.ofItems(1, Items.BOWL), new ItemStack(ModItems.BORSCH, 1), 120),
                PotRecipe.of("broth", List.of(
                        CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ConventionalItemTags.CARROT_CROPS)),
                        CountedIngredient.ofItems(1, Items.CHICKEN),
                        CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.CROPS_ONION)),
                        CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.createTag("cooking_oil"))),
                        CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.createTag("salt")))
                ), CountedIngredient.ofItems(1, Items.BOWL), new ItemStack(ModItems.BROTH, 1), 120),
                PotRecipe.of("rotten_soup", List.of(
                        CountedIngredient.ofTag(1, itemLookup.getOrThrow(ModTags.Items.ROTTEN_SOUP_INGREDIENTS)),
                        CountedIngredient.ofTag(1, itemLookup.getOrThrow(ModTags.Items.ROTTEN_SOUP_INGREDIENTS)),
                        CountedIngredient.ofTag(1, itemLookup.getOrThrow(ModTags.Items.ROTTEN_SOUP_INGREDIENTS)),
                        CountedIngredient.ofTag(1, itemLookup.getOrThrow(ModTags.Items.ROTTEN_SOUP_INGREDIENTS))
                ), CountedIngredient.ofItems(1, Items.BOWL), new ItemStack(ModItems.ROTTEN_SOUP, 1), 120),
                PotRecipe.of("boiled_corn", List.of(CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.CROPS_CORN))), CountedIngredient.EMPTY, new ItemStack(ModItems.BOILED_CORN, 1), 80),
                PotRecipe.of("fungus_stew", List.of(
                        CountedIngredient.ofItems(1, Items.CRIMSON_FUNGUS),
                        CountedIngredient.ofItems(1, Items.CRIMSON_FUNGUS),
                        CountedIngredient.ofItems(1, Items.WARPED_FUNGUS),
                        CountedIngredient.ofItems(1, Items.WARPED_FUNGUS)
                ), CountedIngredient.ofItems(1, Items.BOWL), new ItemStack(ModItems.FUNGUS_STEW, 1), 120),
                PotRecipe.of("ender_jam_stew_with_chorus_fruit", List.of(
                        CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.createTag("jam"))),
                        CountedIngredient.ofItems(1, ModItems.ENDER_INFECTED_ONION),
                        CountedIngredient.ofItems(1, Items.CHORUS_FRUIT)
                ), CountedIngredient.ofItems(1, Items.BOWL), new ItemStack(ModItems.ENDER_JAM_STEW_WITH_CHORUS_FRUIT, 1), 120)
        );
        of(exporter,
                CuttingBoardRecipe.of("tomato_slices", CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.CROPS_TOMATO)), new ItemStack(ModItems.TOMATO_SLICES, 3)),
                CuttingBoardRecipe.of("cucumber_slices", CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.CROPS_CUCUMBER)), new ItemStack(ModItems.CUCUMBER_SLICES, 3)),
                CuttingBoardRecipe.of("sunflower_seed", CountedIngredient.ofItems(1, Items.SUNFLOWER), new ItemStack(ModItems.SUNFLOWER_SEED, 4)),
                CuttingBoardRecipe.of("salmon_fillet", CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ItemTags.FISHES)), new ItemStack(ModItems.SALMON_FILLET, 2)),
                CuttingBoardRecipe.of("nori", CountedIngredient.ofItems(1, Items.DRIED_KELP), new ItemStack(ModItems.NORI, 2)),
                CuttingBoardRecipe.of("cooked_beef_slices", CountedIngredient.ofItems(1, Items.COOKED_BEEF), new ItemStack(ModItems.COOKED_BEEF_SLICES, 2)),
                CuttingBoardRecipe.of("beef_slices", CountedIngredient.ofItems(1, Items.BEEF), new ItemStack(ModItems.BEEF_SLICES, 2)),
                CuttingBoardRecipe.of("bread_slice", CountedIngredient.ofItems(1, Items.BREAD), new ItemStack(ModItems.BREAD_SLICE, 4)),
                CuttingBoardRecipe.of("hot_spice", CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.CROPS_CHILLI_PEPPER)), new ItemStack(ModItems.HOT_SPICE, 1)),
                CuttingBoardRecipe.of("salo", CountedIngredient.ofItems(1, Items.PORKCHOP), new ItemStack(ModItems.SALO, 2)),
                CuttingBoardRecipe.of("chicken_leg", CountedIngredient.ofItems(1, Items.CHICKEN), new ItemStack(ModItems.CHICKEN_LEG, 2)),
                CuttingBoardRecipe.of("cooked_chicken_leg", CountedIngredient.ofItems(1, Items.COOKED_CHICKEN), new ItemStack(ModItems.COOKED_CHICKEN_LEG, 2)),
                CuttingBoardRecipe.of("mutton_slices", CountedIngredient.ofItems(1, Items.MUTTON), new ItemStack(ModItems.MUTTON_SLICES, 2)),
                CuttingBoardRecipe.of("cooked_mutton_slices", CountedIngredient.ofItems(1, Items.COOKED_MUTTON), new ItemStack(ModItems.COOKED_MUTTON_SLICES, 2)),
                CuttingBoardRecipe.of("peeled_squid_tentacles", CountedIngredient.ofItems(1, ModItems.SQUID_TENTACLES), new ItemStack(ModItems.PEELED_SQUID_TENTACLES)),
                CuttingBoardRecipe.of("squid_ring", CountedIngredient.ofItems(1, ModItems.PEELED_SQUID_TENTACLES), new ItemStack(ModItems.SQUID_RING, 3)),
                CuttingBoardRecipe.of("nether_bun_slice", CountedIngredient.ofItems(1, ModItems.NETHER_BUN), new ItemStack(ModItems.NETHER_BUN_SLICE, 2))
        );
        of(exporter,
                PanRecipe.of("cooked_chicken", CountedIngredient.ofItems(1, Items.CHICKEN), new ItemStack(Items.COOKED_CHICKEN), 100),
                PanRecipe.of("dried_kelp", CountedIngredient.ofItems(1, Items.KELP), new ItemStack(Items.DRIED_KELP), 40),
                PanRecipe.of("cooked_beef", CountedIngredient.ofItems(1, Items.BEEF), new ItemStack(Items.COOKED_BEEF), 100),
                PanRecipe.of("cooked_mutton", CountedIngredient.ofItems(1, Items.MUTTON), new ItemStack(Items.COOKED_MUTTON), 100),
                PanRecipe.of("cooked_porkchop", CountedIngredient.ofItems(1, Items.PORKCHOP), new ItemStack(Items.COOKED_PORKCHOP), 100),
                PanRecipe.of("cooked_salmon", CountedIngredient.ofItems(1, Items.SALMON), new ItemStack(Items.COOKED_SALMON), 100),
                PanRecipe.of("baked_potato", CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ConventionalItemTags.POTATO_CROPS)), new ItemStack(Items.BAKED_POTATO), 100),
                PanRecipe.of("cooked_cod", CountedIngredient.ofItems(1, Items.COD), new ItemStack(Items.COOKED_COD), 100),
                PanRecipe.of("cooked_rabbit", CountedIngredient.ofItems(1, Items.RABBIT), new ItemStack(Items.COOKED_RABBIT), 100),
                PanRecipe.of("cooked_beef_slices", CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.FOODS_RAW_MEATS_RAW_BEEF)), new ItemStack(ModItems.COOKED_BEEF_SLICES), 100),
                PanRecipe.of("popcorn", CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.SEEDS_CORN)), new ItemStack(ModItems.POPCORN), 40),
                PanRecipe.of("roasted_sunflower_seed", CountedIngredient.ofItems(1, ModItems.SUNFLOWER_SEED), new ItemStack(ModItems.ROASTED_SUNFLOWER_SEED), 100),
                PanRecipe.of("cooked_chicken_leg", CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.FOODS_RAW_MEATS_RAW_CHICKEN)), new ItemStack(ModItems.COOKED_CHICKEN_LEG), 100),
                PanRecipe.of("cooked_mutton_slices", CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.FOODS_RAW_MEATS_RAW_MUTTON)), new ItemStack(ModItems.COOKED_MUTTON_SLICES), 100),
                PanRecipe.of("cooked_squid_ring", CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.FOODS_RAW_FISHES_SQUID)), new ItemStack(ModItems.COOKED_SQUID_RING), 100),
                PanRecipe.of("cooked_hoglin_meat", CountedIngredient.ofTag(1, this.itemLookup.getOrThrow(ModTags.Items.FOODS_RAW_MEATS_RAW_HOGLIN)), new ItemStack(ModItems.COOKED_HOGLIN_MEAT), 100)
        );

        compressBlockRecipe(ModBlocks.NETHER_HAY_ITEM, ModItems.NETHER_WHEAT, exporter);
        createShaped(RecipeCategory.FOOD, ModBlocks.FUNGUS_PIZZA_ITEM, 1)
                .pattern("FKC")
                .pattern("FKC")
                .pattern("WWW")
                .input('K', ModItems.KETCHUP)
                .input('F', Items.WARPED_FUNGUS)
                .input('C', Items.CRIMSON_FUNGUS)
                .input('W', ConventionalItemTags.WHEAT_CROPS)
                .criterion(hasItem(Items.CRIMSON_FUNGUS), conditionsFromItem(Items.CRIMSON_FUNGUS))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.NETHER_BUN, 3)
                .input(ModTags.Items.CROPS_NETHER_WHEAT)
                .input(ModTags.Items.CROPS_NETHER_WHEAT)
                .criterion(hasItem(ModItems.NETHER_WHEAT), conditionsFromItem(ModItems.NETHER_WHEAT_SEEDS))
                .offerTo(exporter);
        offerSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.AVOCADO_SLAB_ITEM, ModBlocks.AVOCADO_PLANKS_ITEM);
        offerSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LEMON_SLAB_ITEM, ModBlocks.LEMON_PLANKS_ITEM);
        offerPressurePlateRecipe(ModBlocks.LEMON_PRESSURE_PLATE_ITEM, ModBlocks.LEMON_PLANKS_ITEM);
        offerPressurePlateRecipe(ModBlocks.AVOCADO_PRESSURE_PLATE_ITEM, ModBlocks.AVOCADO_PLANKS_ITEM);
        offerButtonRecipe(exporter, ModBlocks.AVOCADO_BUTTON_ITEM, ModBlocks.AVOCADO_PLANKS_ITEM);
        offerButtonRecipe(exporter, ModBlocks.LEMON_BUTTON_ITEM, ModBlocks.LEMON_PLANKS_ITEM);
        offerFenceRecipe(exporter, ModBlocks.AVOCADO_FENCE_ITEM, ModBlocks.AVOCADO_PLANKS_ITEM);
        offerFenceRecipe(exporter, ModBlocks.LEMON_FENCE_ITEM, ModBlocks.LEMON_PLANKS_ITEM);
        offerFenceGateRecipe(exporter, ModBlocks.AVOCADO_FENCE_GATE_ITEM, ModBlocks.AVOCADO_PLANKS_ITEM);
        offerFenceGateRecipe(exporter, ModBlocks.LEMON_FENCE_GATE_ITEM, ModBlocks.LEMON_PLANKS_ITEM);
        offerTrapdoorRecipe(exporter, ModBlocks.AVOCADO_TRAPDOOR_ITEM, ModBlocks.AVOCADO_PLANKS_ITEM);
        offerTrapdoorRecipe(exporter, ModBlocks.LEMON_TRAPDOOR_ITEM, ModBlocks.LEMON_PLANKS_ITEM);
        offerDoorRecipe(exporter, ModBlocks.AVOCADO_DOOR_ITEM, ModBlocks.AVOCADO_PLANKS_ITEM);
        offerDoorRecipe(exporter, ModBlocks.LEMON_DOOR_ITEM, ModBlocks.LEMON_PLANKS_ITEM);

        createShapeless(RecipeCategory.FOOD, ModItems.CHORUS_FRUIT_IN_CHOCOLATE_ON_A_STICK, 1)
                .input(ConventionalItemTags.WOODEN_RODS)
                .input(Items.CHORUS_FRUIT)
                .input(ingredientFromTag(ModTags.Items.createTag("chocolate")))
                .criterion(hasItem(Items.CHORUS_FRUIT), conditionsFromItem(Items.CHORUS_FRUIT))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.CHORUS_FRUITS_WITH_ENDER_JAM, 1)
                .input(Items.BOWL)
                .input(Items.CHORUS_FRUIT)
                .input(ingredientFromTag(ModTags.Items.createTag("jam")))
                .criterion(hasItem(ModItems.ENDER_JAM), conditionsFromItem(ModItems.ENDER_JAM))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.ENDER_PIE, 1)
                .input(ConventionalItemTags.WHEAT_CROPS)
                .input(ConventionalItemTags.EGGS)
                .input(ingredientFromTag(ModTags.Items.createTag("jam")))
                .criterion(hasItem(ModItems.ENDER_JAM), conditionsFromItem(ModItems.ENDER_JAM))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModItems.GOLDEN_CHORUS_FRUIT, 1)
                .pattern("GGG")
                .pattern("GCG")
                .pattern("GGG")
                .input('G', ConventionalItemTags.GOLD_INGOTS)
                .input('C', Items.CHORUS_FRUIT)
                .criterion(hasItem(Items.CHORUS_FRUIT), conditionsFromItem(Items.CHORUS_FRUIT))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModItems.BREAD_SLICE_WITH_HONEY, 1)
                .pattern(" H ")
                .pattern(" B ")
                .input('H', ConventionalItemTags.HONEY_DRINKS)
                .input('B', ModItems.BREAD_SLICE)
                .criterion(hasItem(ModItems.BREAD_SLICE), conditionsFromItem(ModItems.BREAD_SLICE))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.BUTTER, 1)
                .input(ConventionalItemTags.MILK_BUCKETS)
                .criterion(hasItem(Items.MILK_BUCKET), conditionsFromItem(Items.MILK_BUCKET))
                .offerTo(exporter);

        createShapeless(RecipeCategory.FOOD, ModItems.CROISSANT, 1)
                .input(ingredientFromTag(ModTags.Items.createTag("butter")))
                .input(ConventionalItemTags.WHEAT_CROPS)
                .input(ConventionalItemTags.EGGS)
                .input(Items.SUGAR)
                .criterion(hasItem(ModItems.BUTTER), conditionsFromItem(ModItems.BUTTER))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModItems.GOLDEN_BREAD, 1)
                .pattern("GGG")
                .pattern("GBG")
                .pattern("GGG")
                .input('G', ConventionalItemTags.GOLD_INGOTS)
                .input('B', ConventionalItemTags.BREAD_FOODS)
                .criterion(hasItem(Items.BREAD), conditionsFromItem(Items.BREAD))
                .offerTo(exporter);

        createShaped(RecipeCategory.FOOD, ModItems.HOGLIN_SANDWICH, 1)
                .pattern(" B ")
                .pattern(" H ")
                .pattern(" B ")
                .input('B', ModItems.NETHER_BUN_SLICE)
                .input('H', ModTags.Items.FOODS_COOKED_MEATS_COOKED_HOGLIN)
                .criterion(hasItem(ModItems.NETHER_BUN_SLICE), conditionsFromItem(ModItems.NETHER_BUN_SLICE))
                .offerTo(exporter);
        seedsRecipe(ModItems.ENDER_INFECTED_ONION, ModItems.ENDER_INFECTED_ONION_SEEDS, exporter);
    }
    private void offerDoorRecipe(RecipeExporter exporter, Item output, Item input) {
        createDoorRecipe(output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
    }
    private void offerTrapdoorRecipe(RecipeExporter exporter, Item output, Item input){
        createTrapdoorRecipe(output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
    }

    private void offerButtonRecipe(RecipeExporter exporter, Item output, Item input){
        createButtonRecipe(output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
    }

    private void offerFenceRecipe(RecipeExporter exporter, Item output, Item input){
        createFenceRecipe(output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
    }

    private void offerFenceGateRecipe(RecipeExporter exporter, Item output, Item input){
        createFenceGateRecipe(output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
    }

    private void campfireCookingRecipe(RecipeExporter exporter, Item input, Item output) {
        CookingRecipeJsonBuilder.create(Ingredient.ofItems(input), RecipeCategory.FOOD, output, 0, 600, RecipeSerializer.CAMPFIRE_COOKING, CampfireCookingRecipe::new)
                .criterion(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter);
    }

    private void compressBlockRecipe(Item blockItem, Item item, RecipeExporter exporter){
        createShaped(RecipeCategory.DECORATIONS, blockItem, 1)
                .pattern("SSS")
                .pattern("SSS")
                .pattern("SSS")
                .input('S', item)
                .criterion(hasItem(item), conditionsFromItem(item))
                .offerTo(exporter);
        createShapeless(RecipeCategory.FOOD, item, 9)
                .input(blockItem, 1)
                .criterion(hasItem(blockItem), conditionsFromItem(blockItem))
                .offerTo(exporter, Identifier.of(BorukvaFood.MOD_ID, getRecipeName(blockItem) + "_to_" + getRecipeName(item)).toString());
    }
    private void seedsRecipe(Item item, Item seeds, RecipeExporter exporter){
        createShapeless(RecipeCategory.FOOD, seeds, 3)
                .input(item)
                .criterion(hasItem(item), conditionsFromItem(item))
                .offerTo(exporter, Identifier.of(BorukvaFood.MOD_ID, getRecipeName(item) + "_to_" + getRecipeName(seeds)).toString());
    }
    private void planksRecipe(TagKey<Item> log, Item planks, RecipeExporter exporter){
        createShapeless(RecipeCategory.BUILDING_BLOCKS, planks, 4)
                .input(log)
                .criterion("has_log", conditionsFromTag(log))
                .offerTo(exporter);
    }
    private void woodRecipe(Item log, Item wood, RecipeExporter exporter){
        createShaped(RecipeCategory.BUILDING_BLOCKS, wood, 3)
                .pattern("SS")
                .pattern("SS")
                .input('S', log)
                .criterion(hasItem(log), conditionsFromItem(log))
                .offerTo(exporter);
    }
    private void stairsRecipe(Item planks, Item stairs, RecipeExporter exporter){
        createShaped(RecipeCategory.BUILDING_BLOCKS, stairs, 4)
                .pattern("S  ")
                .pattern("SS ")
                .pattern("SSS")
                .input('S', planks)
                .criterion(hasItem(planks), conditionsFromItem(planks))
                .offerTo(exporter);
    }
    private void pieRecipe(Item item, Ingredient ingredient, RecipeExporter exporter){
        createShapeless(RecipeCategory.FOOD, item, 1)
                .input(ingredient)
                .input(Items.SUGAR)
                .input(ConventionalItemTags.EGGS)
                .criterion("has_egg", conditionsFromTag(ConventionalItemTags.EGGS))
                .offerTo(exporter);
    }
    private void popcornRecipe(Item item, Ingredient ingredient, RecipeExporter exporter){
        createShapeless(RecipeCategory.FOOD, item, 1)
                .input(ModItems.POPCORN)
                .input(ModItems.POPCORN)
                .input(ingredient)
                .input(Items.PAPER)
                .criterion(hasItem(ModItems.POPCORN), conditionsFromItem(ModItems.POPCORN))
                .offerTo(exporter);
    }
    public void of(RecipeExporter exporter, RecipeEntry<?>... recipes) {
        for (var recipe : recipes) {
            exporter.accept(recipe.id(), recipe.value(), null);
        }
    }
}