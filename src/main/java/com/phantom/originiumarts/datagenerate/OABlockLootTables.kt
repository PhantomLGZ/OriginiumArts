package com.phantom.originiumarts.datagenerate

import com.phantom.originiumarts.block.BlockRegister
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.data.DataGenerator

class OABlockLootTables(dataGenerator: DataGenerator) : BaseBlockLootTableProvider(dataGenerator) {

    override fun addTables() {
        lootTables[BlockRegister.ORIGINIUM_HOST_ROCK.get()] =
            createSimpleTable(
                "originium_ore",
                BlockRegister.ORIGINIUM_HOST_ROCK.get()
            )
        lootTables[BlockRegister.ORIGINIUM_CLUSTER.get()] =
            createSilkTouchTable(
                "originium_ore",
                BlockRegister.ORIGINIUM_CLUSTER.get(),
                ItemRegister.ORIGINIUM.get(),
                1f,
                2f
            )
        lootTables[BlockRegister.ORIGINIUM_LARGE_BUD.get()] =
            createSilkTouchTable(
                "originium_ore",
                BlockRegister.ORIGINIUM_CLUSTER.get(),
                ItemRegister.ORIGINIUM_FRAGMENT.get(),
                2f,
                4f
            )
        lootTables[BlockRegister.ORIGINIUM_MEDIUM_BUD.get()] =
            createSilkTouchTable(
                "originium_ore",
                BlockRegister.ORIGINIUM_CLUSTER.get(),
                ItemRegister.ORIGINIUM_FRAGMENT.get(),
                1f,
                2f
            )
        lootTables[BlockRegister.ORIGINIUM_SMALL_BUD.get()] =
            createSilkTouchTable(
                "originium_ore",
                BlockRegister.ORIGINIUM_CLUSTER.get(),
                ItemRegister.ORIGINIUM_FRAGMENT.get(),
                0f,
                1f
            )
        lootTables[BlockRegister.OPERATING_BED_BLOCK.get()] =
            createSimpleTable(
                "operating_bed",
                BlockRegister.OPERATING_BED_BLOCK.get()
            )
    }

}