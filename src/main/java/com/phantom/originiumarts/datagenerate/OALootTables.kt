package com.phantom.originiumarts.datagenerate

import com.phantom.originiumarts.block.BlockRegister
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.data.DataGenerator

class OALootTables(dataGenerator: DataGenerator) : BaseLootTableProvider(dataGenerator) {

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
                2f,
                4f
            )
        lootTables[BlockRegister.OPERATING_BED_BLOCK.get()] =
            createSimpleTable(
                "operating_bed",
                BlockRegister.OPERATING_BED_BLOCK.get()
            )
    }

}