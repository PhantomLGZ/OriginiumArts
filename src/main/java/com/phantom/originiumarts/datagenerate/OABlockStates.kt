package com.phantom.originiumarts.datagenerate

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.block.BlockRegister
import net.minecraft.data.DataGenerator
import net.minecraft.world.level.block.Block
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.common.data.ExistingFileHelper

class OABlockStates(gen: DataGenerator, helper: ExistingFileHelper) :
    BlockStateProvider(gen, OriginiumArtsMod.MOD_ID, helper) {

    override fun registerStatesAndModels() {
        simpleBlock(BlockRegister.ORIGINIUM_HOST_ROCK.get())
        BlockRegister.ORIGINIUM_CLUSTER.get().let {
            directionalBlock(it, models().cross(name(it), blockTexture(it)))
        }
        BlockRegister.ORIGINIUM_LARGE_BUD.get().let {
            directionalBlock(it, models().cross(name(it), blockTexture(it)))
        }
        BlockRegister.ORIGINIUM_MEDIUM_BUD.get().let {
            directionalBlock(it, models().cross(name(it), blockTexture(it)))
        }
        BlockRegister.ORIGINIUM_SMALL_BUD.get().let {
            directionalBlock(it, models().cross(name(it), blockTexture(it)))
        }
    }

    private fun name(block: Block): String? {
        return block.registryName!!.path
    }

}