package com.phantom.originiumarts.common.network

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.common.ArtsManager
import com.phantom.originiumarts.common.network.sendpack.*
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkDirection.*
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel
import java.util.function.BiConsumer

object OANetworking {

    private const val VERSION = "1.0"
    private var ID: Int = 0

    private fun nextID(): Int = ID++

    var INSTANCE: SimpleChannel? = null

    fun registerMessage() {
        INSTANCE = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation(OriginiumArtsMod.MOD_ID, "oa_cap_network"))
            .networkProtocolVersion { VERSION }
            .clientAcceptedVersions { it == VERSION }
            .serverAcceptedVersions { it == VERSION }
            .simpleChannel()
        OACapSetSendPack::class.java.baseMessage(PLAY_TO_SERVER)
        OACapGetSendPack::class.java.baseMessage(PLAY_TO_CLIENT)
        OAClickSendPack::class.java.baseMessage(PLAY_TO_SERVER)
        OASetItemArtsSendPack::class.java.baseMessage(PLAY_TO_SERVER)
        OASetPlayerArtsSendPack::class.java.baseMessage(PLAY_TO_SERVER)
        OAPlayerLearnArtSendPack::class.java.baseMessage(PLAY_TO_SERVER)
        OASetSelectedArtSendPack::class.java.baseMessage(PLAY_TO_SERVER)
        OAAcuteOripathySendPack::class.java.baseMessage(PLAY_TO_SERVER)
        OAClearLearnedArtsSendPack::class.java.baseMessage(PLAY_TO_SERVER)
        OAAntiGravitySendPack::class.java.baseMessage(PLAY_TO_SERVER)
    }

    private fun <T : ISendPack> Class<T>.baseMessage(direction: NetworkDirection) {
        INSTANCE?.messageBuilder(this, nextID(), direction)
            ?.decoder { this.getConstructor(FriendlyByteBuf::class.java).newInstance(it) }
            ?.encoder { pack, buf ->
                pack.toBytes(buf)
            }
            ?.consumer(BiConsumer { pack, ctx ->
                pack.handler(ctx)
            })
            ?.add()
    }

    fun sendToServer(msg: ISendPack) {
        INSTANCE?.sendToServer(msg)
    }

    fun syncCapability(player: ServerPlayer) {
        INSTANCE?.sendTo(
            OACapGetSendPack(player.getOACapability()?.serializeNBT()),
            player.connection.connection,
            PLAY_TO_CLIENT
        )
    }

    fun sendLeftClickOnArtsUnitItem() {
        INSTANCE?.sendToServer(OAClickSendPack())
    }

    fun sendSetItemArts(artIds: IntArray) {
        Minecraft.getInstance().player?.getItemInHand(InteractionHand.MAIN_HAND)?.tag
            ?.putIntArray(ArtsManager.KEY_ITEMSTACK_ARTS, artIds)
        INSTANCE?.sendToServer(OASetItemArtsSendPack(artIds))
    }

    fun sendSetPlayerArts(artIds: IntArray) {
        Minecraft.getInstance().player?.getOACapability()?.let {
            it.installedArtIds.clear()
            it.installedArtIds.addAll(artIds.toList())
        }
        INSTANCE?.sendToServer(OASetPlayerArtsSendPack(artIds))
    }

    fun sendPlayerLearnArt(artIds: Int) {
        INSTANCE?.sendToServer(OAPlayerLearnArtSendPack(artIds))
    }

    fun sendSelectedArt(artId: Int) {
        Minecraft.getInstance().player?.getOACapability()?.selectedArtId = artId
        INSTANCE?.sendToServer(OASetSelectedArtSendPack(artId))
    }

}