package com.phantom.originiumarts

import com.phantom.originiumarts.common.weather.CatastropheServerManager
import net.minecraft.server.level.ServerLevel
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber
object ServerSetup {

    fun setup() {

    }

    @SubscribeEvent
    fun onWorldTick(event: TickEvent.WorldTickEvent) {
        if (event.world.isClientSide || event.phase == TickEvent.Phase.START) {
            return
        }
        (event.world as? ServerLevel)?.let { level ->
            val catastropheManager = CatastropheServerManager.get(level)
            catastropheManager?.tick(level)
        }
    }

}