package com.phantom.originiumarts.datagenerate

import com.phantom.originiumarts.OriginiumArtsMod
import net.minecraft.data.DataGenerator
import net.minecraft.data.tags.BlockTagsProvider
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraftforge.common.data.ExistingFileHelper

class OAItemTags(generator: DataGenerator, blockTags: BlockTagsProvider, helper: ExistingFileHelper) :
    ItemTagsProvider(generator, blockTags, OriginiumArtsMod.MOD_ID, helper) {

    override fun addTags() {}

    override fun getName(): String {
        return "Originium Arts Tags"
    }

}