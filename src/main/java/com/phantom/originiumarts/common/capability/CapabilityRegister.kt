package com.phantom.originiumarts.common.capability

import com.phantom.originiumarts.OAConfig
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.common.network.OANetworking
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerEvent.*
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber
object CapabilityRegister {

    @SubscribeEvent
    fun onAttachCapabilitiesPlayer(event: AttachCapabilitiesEvent<Entity>) {
        val obj = event.`object`
        if (obj is Player) {
            if (!obj.getCapability(OriginiumArtsCapabilityProvider.ORIGINIUM_ARTS_CAPABILITY).isPresent) {
                event.addCapability(
                    ResourceLocation(OriginiumArtsMod.MOD_ID, "originium_arts_cap"),
                    OriginiumArtsCapabilityProvider()
                )
            }
        }
    }

    @SubscribeEvent
    fun onPlayerCloned(event: Clone) {
        if (event.isWasDeath) {
            if (!OAConfig.SIX_DIM_ATTR_RESET.get()) {
                event.original.reviveCaps()
                event.original.getOACapability().let { old ->
                    event.player.getOACapability().let { new ->
                        new?.deserializeNBT(old?.serializeNBT())
                    }
                }
                event.original.invalidateCaps()
            }
            event.player.updateAllEffect()
        }
    }

    @SubscribeEvent
    fun onRegisterCapabilities(event: RegisterCapabilitiesEvent) {
        event.register(OriginiumArtsCapability::class.java)
    }

    @SubscribeEvent
    fun onPlayerConnect(event: PlayerLoggedInEvent) {
        val player = event.player
        if (player is ServerPlayer) {
            OANetworking.syncCapability(player)
        }
        player.updateAllEffect()
    }

    @SubscribeEvent
    fun onPlayerDisconnect(event: PlayerLoggedOutEvent) {
        val player = event.player
        player.getOACapability()?.let {
            it.healthOnDisconnect = player.health
        }
    }

    @SubscribeEvent
    fun onPlayerChangedWorld(event: PlayerChangedDimensionEvent) {
        val player = event.player
        if (player is ServerPlayer) {
            OANetworking.syncCapability(player)
        }
    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        if (player is ServerPlayer) {
            OANetworking.syncCapability(player)
        }
    }

    @SubscribeEvent
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            return
        }
        if (event.player.level.isClientSide) {
            if (!event.player.isUsingItem) {
                event.player.getOACapability()?.tick()
            }
        }
    }

}
