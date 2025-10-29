package com.opryshok.utils;

import com.opryshok.block.ModBlocks;
import com.opryshok.item.ModItems;
import com.opryshok.utils.duck.LootPoolBuilderHelper;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.component.ComponentPredicateTypes;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ModifyLootTables {

    public static void modifyLootTables(){
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if ((key.getValue() == LootTables.BASTION_OTHER_CHEST.getValue())){
                tableBuilder.modifyPools(builder -> builder.with(ItemEntry.builder(ModItems.NETHER_WHEAT_SEEDS)
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 4), false))
                        .weight(2)));
            }
            if ((key.getValue() == LootTables.SNIFFER_DIGGING_GAMEPLAY.getValue())) {
                LootPool.Builder pool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1f))
                        .with(ItemEntry.builder(ModItems.CORN_SEEDS))
                        .with(ItemEntry.builder(ModItems.ONION_SEEDS))
                        .with(ItemEntry.builder(ModItems.RICE_PANICLE));
                tableBuilder.pool(pool);
            }
            if ((key.getValue() == LootTables.PILLAGER_OUTPOST_CHEST.getValue())){
                LootPool.Builder pool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1f))
                        .with(ItemEntry.builder(ModItems.TOMATO))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3)));
                tableBuilder.pool(pool);
            }
            if ((key.getValue() == LootTables.SIMPLE_DUNGEON_CHEST.getValue())){
                LootPool.Builder pool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.75f))
                        .with(ItemEntry.builder(ModItems.ONION_SEEDS))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3)));
                tableBuilder.pool(pool);
            }
            if ((key.getValue() == LootTables.TRAIL_RUINS_COMMON_ARCHAEOLOGY.getValue())){
                tableBuilder.modifyPools(builder ->
                        builder.with(ItemEntry.builder(ModItems.CUCUMBER_SEEDS).weight(2))
                                .with(ItemEntry.builder(ModItems.TOMATO_SEEDS).weight(2))
                                .with(ItemEntry.builder(ModItems.LETTUCE_SEEDS).weight(2)));
            }
            if ((key.getValue() == LootTables.TRIAL_CHAMBERS_CORRIDOR_POT.getValue())){
                tableBuilder.modifyPools(builder -> builder
                        .with(ItemEntry.builder(ModItems.CABBAGE_SEEDS)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3), false))
                                .weight(75))

                        .with(ItemEntry.builder(ModItems.CHILLI_PEPPER_SEEDS)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3), false))
                                .weight(75))

                        .with(ItemEntry.builder(ModBlocks.LEMON_SAPLING_ITEM)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3), false))
                                .weight(30)));
            }
            if ((key.getValue() == LootTables.RUINED_PORTAL_CHEST.getValue())){
                tableBuilder.modifyPools(builder -> builder.with(ItemEntry.builder(ModItems.CHILLI_PEPPER_SEEDS)
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3), false))
                        .weight(40)));
            }
            if ((key.getValue() == LootTables.SHIPWRECK_SUPPLY_CHEST.getValue())){
                tableBuilder.modifyPools(builder -> builder.with(ItemEntry.builder(ModItems.CUCUMBER)
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3), false))
                        .weight(10)));
            }
            if ((key.getValue() == LootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY.getValue() || key.getValue() == LootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY.getValue())){
                tableBuilder.modifyPools(builder ->
                    builder.with(ItemEntry.builder(ModItems.CUCUMBER_SEEDS).weight(2))
                            .with(ItemEntry.builder(ModItems.TOMATO_SEEDS).weight(2))
                            .with(ItemEntry.builder(ModItems.LETTUCE_SEEDS).weight(2)));
            }
            if((key.getValue() == LootTables.END_CITY_TREASURE_CHEST.getValue())){
                LootPool.Builder pool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1f))
                        .with(ItemEntry.builder(ModItems.ENDER_INFECTED_ONION_SEEDS))
                        .with(ItemEntry.builder(ModItems.GRAPE_SAPLING));
                tableBuilder.pool(pool);
            }
            if (isEntityLootTable(EntityType.SQUID, key) || isEntityLootTable(EntityType.GLOW_SQUID, key)) {
                tableBuilder.pool(
                    LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(
                            ItemEntry.builder(ModItems.SQUID_TENTACLES)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2), false))
                                .apply(
                                    EnchantedCountIncreaseLootFunction.builder(
                                        registries,
                                        UniformLootNumberProvider.create(0, 1)
                                    )
                                )
                        )
                );
            }
            if (isEntityLootTable(EntityType.HOGLIN, key)) {
                var poolIndex = new AtomicInteger();
                tableBuilder.modifyPools(builder -> {
                    // We assume the first pool is the porkchop pool
                    if (poolIndex.get() != 0) return;

                    // remove old entries
                    ((LootPoolBuilderHelper)builder).borukva_Food$clearEntries();

                    builder.with(
                        ItemEntry.builder(ModItems.HOGLIN_MEAT)
                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F)))
                            .apply(FurnaceSmeltLootFunction.builder().conditionally(createSmeltLootCondition(registries)))
                            .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0.0F, 1.0F)))
                    );
                    poolIndex.incrementAndGet();
                });
            }
        });
    }

    private static boolean isEntityLootTable(EntityType<?> entityType, RegistryKey<LootTable> key) {
        return entityType.getLootTableKey().map(squidKey -> squidKey == key).orElse(false);
    }

    // copied from EntityLootTableGenerator.createSmeltLootCondition
    private static AnyOfLootCondition.Builder createSmeltLootCondition(RegistryWrapper.WrapperLookup registries) {
        RegistryWrapper.Impl<Enchantment> impl = registries.getOrThrow(RegistryKeys.ENCHANTMENT);
        return AnyOfLootCondition.builder(
            EntityPropertiesLootCondition.builder(
                LootContext.EntityReference.THIS, EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true))
            ),
            EntityPropertiesLootCondition.builder(
                LootContext.EntityReference.DIRECT_ATTACKER,
                EntityPredicate.Builder.create()
                    .equipment(
                        EntityEquipmentPredicate.Builder.create()
                            .mainhand(
                                ItemPredicate.Builder.create()
                                    .components(
                                        ComponentsPredicate.Builder.create()
                                            .partial(
                                                ComponentPredicateTypes.ENCHANTMENTS,
                                                EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(impl.getOrThrow(EnchantmentTags.SMELTS_LOOT), NumberRange.IntRange.ANY)))
                                            )
                                            .build()
                                    )
                            )
                    )
            )
        );
    }
}
