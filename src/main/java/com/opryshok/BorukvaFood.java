package com.opryshok;

import com.opryshok.block.ModBlocks;
import com.opryshok.block.bushes.BlackcurrantsBush;
import com.opryshok.block.bushes.GooseberryBush;
import com.opryshok.block.bushes.GrapeCrop;
import com.opryshok.block.cooking.CuttingBoard;
import com.opryshok.block.cooking.Pan;
import com.opryshok.block.cooking.Pot;
import com.opryshok.block.cooking.Stove;
import com.opryshok.block.crops.*;
import com.opryshok.block.food.*;
import com.opryshok.commands.ModCommands;
import com.opryshok.config.ModConfig;
import com.opryshok.entity.ModEntities;
import com.opryshok.item.CompostItem;
import com.opryshok.item.ModItems;
import com.opryshok.polydex.PolydexCompat;
import com.opryshok.polydex.PolydexTextures;
import com.opryshok.recipe.ModRecipeSerializer;
import com.opryshok.recipe.ModRecipeTypes;
import com.opryshok.ui.GuiTextures;
import com.opryshok.ui.UiResourceCreator;
import com.opryshok.utils.*;
import com.opryshok.world.gen.ModWorldGeneration;
import eu.midnightdust.lib.config.EntryInfo;
import eu.midnightdust.lib.config.MidnightConfig;
import eu.midnightdust.lib.util.PlatformFunctions;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class BorukvaFood implements ModInitializer {

	public static final String MOD_ID = "borukva-food";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MidnightConfig configInstance;

	@Override
	public void onInitialize() {
        configInstance = MidnightConfig.createInstance(MOD_ID, ModConfig.class);
        for(Field field : ModConfig.class.getFields()) {
            if ((field.isAnnotationPresent(MidnightConfig.Entry.class) || field.isAnnotationPresent(MidnightConfig.Comment.class)) && !field.isAnnotationPresent(MidnightConfig.Server.class) && !field.isAnnotationPresent(MidnightConfig.Hidden.class) && PlatformFunctions.isClientEnv()) {
                configInstance.addClientEntry(field, new EntryInfo(field, MOD_ID));
            }
        }
        configInstance.loadValuesFromJson();

		ModCommands.register();
		ModItems.registerModItems();
		ModBlocks.registerBlocks();
		ModEntities.register();
		CompostableItems.register();
		ModWorldGeneration.generateModWorldGen();
		ModifyLootTables.modifyLootTables();
		BorukvaFoodUtil.registerWood();
		ModCustomTrades.registerCustomTrades();
		UiResourceCreator.setup();
		GuiTextures.register();
		PolydexTextures.register();
		PolydexCompat.register();
		DispenserBlock.registerBehavior(ModItems.COMPOST, CompostItem.COMPOST_BEHAVIOR);
		ModRecipeTypes.register();
		ModRecipeSerializer.register();
		if (PolymerResourcePackUtils.addModAssets(MOD_ID)) {
			ResourcePackExtras.forDefault().addBridgedModelsFolder(id("block"), id("item"), id("sgui"));
			LOGGER.info("Successfully added mod assets for " + MOD_ID);
		} else {
			LOGGER.error("Failed to add mod assets for " + MOD_ID);
		}
		initModels();
		PolymerResourcePackUtils.markAsRequired();
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void initModels(){
		Stove.Model.LIT_FALSE.isEmpty();
		Stove.Model.LIT_TRUE.isEmpty();
		Pan.Model.MODEL.isEmpty();
		CuttingBoard.Model.MODEL.isEmpty();
		BlackcurrantsBush.Model.MODELS.forEach(ItemStack::isEmpty);
		GooseberryBush.Model.MODELS.forEach(ItemStack::isEmpty);
		MeatPizza.Model.MEAT_MODEL.forEach(ItemStack::isEmpty);
		VeganPizza.Model.MODEL.forEach(ItemStack::isEmpty);
		TomatoCrop.Model.MODELS.forEach(ItemStack::isEmpty);
		OnionCrop.Model.MODELS.forEach(ItemStack::isEmpty);
		LettuceCrop.Model.MODELS.forEach(ItemStack::isEmpty);
		CucumberCrop.Model.MODELS.forEach(ItemStack::isEmpty);
		CornCrop.Model.MODELS.forEach(ItemStack::isEmpty);
		ChilliPepperCrop.Model.MODELS.forEach(ItemStack::isEmpty);
		CabbageCrop.Model.MODELS.forEach(ItemStack::isEmpty);
		ChocolateCake.Model.CHOCOLATE_MODEL.forEach(ItemStack::isEmpty);
		HoneyCake.Model.HONEY_MODEL.forEach(ItemStack::isEmpty);
		FertilizerSprayerBlock.Model.MODEL_ON.isEmpty();
		FertilizerSprayerBlock.Model.MODEL_OFF.isEmpty();
		RiceCrop.Model.MODELS.forEach(ItemStack::isEmpty);
		Pot.Model.MODEL.isEmpty();
		FungusPizza.Model.FUNGUS_MODEL.forEach(ItemStack::isEmpty);
		NetherWheatCrop.Model.MODELS.forEach(ItemStack::isEmpty);
		EnderInfectedOnionCrop.Model.MODELS.forEach(ItemStack::isEmpty);
		GrapeCrop.Model.MODELS.forEach(ItemStack::isEmpty);
		DoorModels.register();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}