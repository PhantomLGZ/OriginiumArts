package com.phantom.originiumarts.datagenerate

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.block.BlockRegister
import net.minecraft.data.DataGenerator
import net.minecraft.data.tags.BlockTagsProvider
import net.minecraft.tags.BlockTags
import net.minecraftforge.common.data.ExistingFileHelper

class OABlockTags(generator: DataGenerator, helper: ExistingFileHelper) :
    BlockTagsProvider(generator, OriginiumArtsMod.MOD_ID, helper) {

    override fun addTags() {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(BlockRegister.ORIGINIUM_HOST_ROCK.get())
            .add(BlockRegister.ORIGINIUM_CLUSTER.get())
            .add(BlockRegister.ORIGINIUM_LARGE_BUD.get())
            .add(BlockRegister.ORIGINIUM_MEDIUM_BUD.get())
            .add(BlockRegister.ORIGINIUM_SMALL_BUD.get())
        tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .add(BlockRegister.ORIGINIUM_HOST_ROCK.get())
            .add(BlockRegister.ORIGINIUM_CLUSTER.get())
            .add(BlockRegister.ORIGINIUM_LARGE_BUD.get())
            .add(BlockRegister.ORIGINIUM_MEDIUM_BUD.get())
            .add(BlockRegister.ORIGINIUM_SMALL_BUD.get())
    }

    override fun getName(): String {
        return "Originium Arts Tags"
    }

}