package com.phantom.originiumarts.datagenerate

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.block.BlockRegister
import net.minecraft.data.DataGenerator
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ConfiguredModel
import net.minecraftforge.client.model.generators.ModelProvider
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
        layersBlock(BlockRegister.ORIGINIUM_DUST.get())
    }

    private fun layersBlock(block: Block) {
        getVariantBuilder(block).forAllStates { state ->
            val layers = state.getValue(BlockStateProperties.LAYERS)
            val model = if (layers == 8) {
                models().cubeAll("${name(block)}_block", blockTexture(block))
            } else {
                models()
                    .withExistingParent(
                        "${name(block)}_height${layers * 2}",
                        ModelProvider.BLOCK_FOLDER + "/thin_block"
                    )
                    .texture("particle", blockTexture(block))
                    .texture("texture", blockTexture(block)).apply {
                        element().to(16f, (layers * 2).toFloat(), 16f).allFaces { k, f ->
                            f.texture("#texture").cullface(k)
                        }
                    }
            }
            return@forAllStates ConfiguredModel.builder()
                .modelFile(model)
                .build()
        }
    }

    private fun name(block: Block): String? {
        return block.registryName!!.path
    }

}