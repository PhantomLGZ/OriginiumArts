package com.phantom.originiumarts.datagenerate

import com.phantom.originiumarts.OriginiumArtsMod
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent

@EventBusSubscriber(modid = OriginiumArtsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object DataGenerators {

    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        if (event.includeServer()) {
            generator.addProvider(OARecipes(generator))
            generator.addProvider(OALootTables(generator))
            val blockTags = OABlockTags(generator, event.existingFileHelper)
            generator.addProvider(blockTags)
            generator.addProvider(OAItemTags(generator, blockTags, event.existingFileHelper))
        }
        if (event.includeClient()) {
            generator.addProvider(OABlockStates(generator, event.existingFileHelper))
            generator.addProvider(OAItemModels(generator, event.existingFileHelper))
            generator.addProvider(OALanguageProvider(generator, "en_us"))
            generator.addProvider(OALanguageZhProvider(generator, "zh_cn"))
        }
    }

}