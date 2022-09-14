package com.phantom.originiumarts.datagenerate

import com.google.gson.GsonBuilder
import net.minecraft.advancements.critereon.EnchantmentPredicate
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.advancements.critereon.MinMaxBounds
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.HashCache
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.LootTables
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry
import net.minecraft.world.level.storage.loot.entries.DynamicLoot
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.*
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.predicates.MatchTool
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import org.apache.logging.log4j.LogManager
import java.io.IOException

abstract class BaseLootTableProvider(private val generator: DataGenerator) :
    LootTableProvider(generator) {

    val lootTables = mutableMapOf<Block, LootTable.Builder>()
    protected abstract fun addTables()

    fun createStandardTable(name: String, block: Block, type: BlockEntityType<*>): LootTable.Builder {
        val builder = LootPool.lootPool()
            .name(name)
            .setRolls(ConstantValue.exactly(1f))
            .add(
                LootItem.lootTableItem(block)
                    .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                    .apply(
                        CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                            .copy("Info", "BlockEntityTag.Info", CopyNbtFunction.MergeStrategy.REPLACE)
                            .copy("Inventory", "BlockEntityTag.Inventory", CopyNbtFunction.MergeStrategy.REPLACE)
                            .copy("Energy", "BlockEntityTag.Energy", CopyNbtFunction.MergeStrategy.REPLACE)
                    )
                    .apply(
                        SetContainerContents.setContents(type)
                            .withEntry(DynamicLoot.dynamicEntry(ResourceLocation("minecraft", "contents")))
                    )
            )
        return LootTable.lootTable().withPool(builder)
    }

    fun createTempTable(name: String, block: Block): LootTable.Builder {
        return LootTable.lootTable()
    }

    fun createSimpleTable(name: String, block: Block): LootTable.Builder {
        val builder = LootPool.lootPool()
            .name(name)
            .setRolls(ConstantValue.exactly(1f))
            .add(LootItem.lootTableItem(block))
        return LootTable.lootTable().withPool(builder)
    }

    fun createSilkTouchTable(
        name: String,
        block: Block,
        lootItem: Item,
        min: Float,
        max: Float
    ): LootTable.Builder {
        val builder = LootPool.lootPool()
            .name(name)
            .setRolls(ConstantValue.exactly(1f))
            .add(
                AlternativesEntry.alternatives(
                    LootItem.lootTableItem(block)
                        .`when`(
                            MatchTool.toolMatches(
                                ItemPredicate.Builder.item()
                                    .hasEnchantment(
                                        EnchantmentPredicate(
                                            Enchantments.SILK_TOUCH,
                                            MinMaxBounds.Ints.atLeast(1)
                                        )
                                    )
                            )
                        ),
                    LootItem.lootTableItem(lootItem)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                        .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 1))
                        .apply(ApplyExplosionDecay.explosionDecay())
                )
            )
        return LootTable.lootTable().withPool(builder)
    }

    override fun run(cache: HashCache) {
        addTables()
        val tables: MutableMap<ResourceLocation, LootTable> = HashMap()
        for ((key, value) in lootTables) {
            tables[key.lootTable] = value.setParamSet(LootContextParamSets.BLOCK).build()
        }
        writeTables(cache, tables)
    }

    private fun writeTables(cache: HashCache, tables: Map<ResourceLocation, LootTable>) {
        val outputFolder = this.generator.outputFolder
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
        return "Originium Arts LootTables"
    }

    companion object {
        private val LOGGER = LogManager.getLogger()
        private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    }
}