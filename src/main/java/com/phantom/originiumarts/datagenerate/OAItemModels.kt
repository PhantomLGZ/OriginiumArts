package com.phantom.originiumarts.datagenerate

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.block.BlockRegister
import com.phantom.originiumarts.item.I3DItem
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.data.DataGenerator
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.common.ForgeSpawnEggItem
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.registries.RegistryObject
import java.util.function.Consumer

class OAItemModels(generator: DataGenerator?, existingFileHelper: ExistingFileHelper?) :
    ItemModelProvider(generator, OriginiumArtsMod.MOD_ID, existingFileHelper) {

    override fun registerModels() {
        BlockRegister.BLOCKS.entries.forEach(
            Consumer { block: RegistryObject<Block> ->
                val name = block.blockName()
                withExistingParent(name, modLoc("block/${name}"))
            }
        )
        ItemRegister.ITEMS.entries.forEach(
            Consumer { item: RegistryObject<Item> ->
                item.itemName().also {
                    when (item.get()) {
                        is ForgeSpawnEggItem -> {
                            withExistingParent(
                                it,
                                mcLoc("item/template_spawn_egg")
                            )
                        }
                        is I3DItem -> {}
                        is BlockItem -> {}
                        else -> {
                            withExistingParent(it, GENERATED)
                                .texture("layer0", modLoc("item/${it}"))
                        }
                    }
                }
            }
        )
//        ItemRegister.ORIGINIUM_HOST_ROCK_ITEM.setCubeAll()
//        ItemRegister.ORIGINIUM_CLUSTER_ITEM.setCross()
//        ItemRegister.ORIGINIUM_LARGE_BUD_ITEM.setCross()
//        ItemRegister.ORIGINIUM_MEDIUM_BUD_ITEM.setCross()
//        ItemRegister.ORIGINIUM_SMALL_BUD_ITEM.setCross()
        withExistingParent(ItemRegister.OPERATING_BED_ITEM.itemName(), modLoc("block/operating_bed"))
    }

    private fun RegistryObject<out Item>.setCubeAll() {
        val name = this.itemName()
        cubeAll(name, modLoc("item/${name}"))
    }

    private fun RegistryObject<out Item>.setCross() {
        val name = this.itemName()
        cross(name, modLoc("item/${name}"))
    }

    private fun RegistryObject<out Item>.setCubeColumn() {
        this.itemName().also {
            cubeColumn(
                it,
                modLoc("item/${it}_side"),
                modLoc("item/${it}_end")
            )
        }
    }

    companion object {
        val GENERATED = ResourceLocation("item/generated")
    }

}