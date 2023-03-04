package com.phantom.originiumarts.datagenerate

import com.google.gson.GsonBuilder
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.HashCache
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.LootTables
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import org.apache.logging.log4j.LogManager
import java.io.IOException

class OAEntityLootTables(private val generator: DataGenerator) : LootTableProvider(generator) {

    private val lootTables = mutableMapOf<EntityType<*>, LootTable.Builder>()

    private fun addTables() {
        lootTables[EntityRegister.ORIGINIUM_SLUG.get()] = LootTable.lootTable()
            .withPool(
                createSimplePool(
                    item = ItemRegister.DRILL_BATTLE_RECORD.get(),
                    itemCountMin = 0f,
                    itemCountMax = 1f,
                    enchantCountMin = 0f,
                    enchantCountMax = 1f
                )
            )
        lootTables[EntityRegister.ACID_ORIGINIUM_SLUG.get()] = LootTable.lootTable()
            .withPool(
                createSimplePool(
                    item = ItemRegister.DRILL_BATTLE_RECORD.get(),
                    itemCountMin = 0f,
                    itemCountMax = 2f,
                    enchantCountMin = 0f,
                    enchantCountMax = 2f
                )
            )
        lootTables[EntityRegister.DRONE_MONSTER.get()] = LootTable.lootTable()
            .withPool(
                createSimplePool(
                    item = ItemRegister.FRONTLINE_BATTLE_RECORD.get(),
                    itemCountMin = 0f,
                    itemCountMax = 1f,
                    enchantCountMin = 0f,
                    enchantCountMax = 1f
                )
            )
        lootTables[EntityRegister.RIOTER.get()] = LootTable.lootTable()
            .withPool(
                createSimplePool(
                    item = ItemRegister.FRONTLINE_BATTLE_RECORD.get(),
                    itemCountMin = 0f,
                    itemCountMax = 1f,
                    enchantCountMin = 0f,
                    enchantCountMax = 1f
                )
            )
        lootTables[EntityRegister.COCKTAIL_THROWER.get()] = LootTable.lootTable()
            .withPool(
                createSimplePool(
                    item = ItemRegister.FRONTLINE_BATTLE_RECORD.get(),
                    itemCountMin = 0f,
                    itemCountMax = 1f,
                    enchantCountMin = 0f,
                    enchantCountMax = 1f
                )
            )
    }

    fun createSimplePool(
        item: ItemLike,
        rolls: NumberProvider = ConstantValue.exactly(1.0f),
        itemCountMin: Float,
        itemCountMax: Float,
        enchantCountMin: Float,
        enchantCountMax: Float,
    ): LootPool.Builder {
        return LootPool.lootPool().setRolls(rolls)
            .add(
                LootItem.lootTableItem(item)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(itemCountMin, itemCountMax)))
                    .apply(
                        LootingEnchantFunction.lootingMultiplier(
                            UniformGenerator.between(
                                enchantCountMin,
                                enchantCountMax
                            )
                        )
                    )
            )
    }

    override fun run(cache: HashCache) {
        addTables()
        val tables: MutableMap<ResourceLocation, LootTable> = HashMap()
        for ((key, value) in lootTables) {
            tables[key.defaultLootTable] = value.setParamSet(LootContextParamSets.ENTITY).build()
        }
        writeTables(cache, tables)
    }

    private fun writeTables(cache: HashCache, tables: Map<ResourceLocation, LootTable>) {
        val outputFolder = generator.outputFolder
        tables.forEach { (key: ResourceLocation, lootTable: LootTable?) ->
            val path = outputFolder.resolve("data/" + key.namespace + "/loot_tables/" + key.path + ".json")
            try {
                DataProvider.save(GSON, cache, LootTables.serialize(lootTable), path)
            } catch (e: IOException) {
                LOGGER.error("Couldn't write loot table {}", path, e)
            }
        }
    }

    override fun getName(): String {
        return "Originium Arts Entity LootTables"
    }

    companion object {
        private val LOGGER = LogManager.getLogger()
        private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    }

}