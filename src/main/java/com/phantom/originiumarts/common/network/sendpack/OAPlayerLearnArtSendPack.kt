package com.phantom.originiumarts.common.network.sendpack

import com.phantom.originiumarts.common.ArtsManager.getArtById
import com.phantom.originiumarts.common.network.OANetworking
import com.phantom.originiumarts.common.capability.getOACapability
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class OAPlayerLearnArtSendPack(private val artId: Int) : ISendPack {

    constructor(buffer: FriendlyByteBuf) : this(buffer.readInt())

    override fun toBytes(buffer: FriendlyByteBuf) {
        buffer.writeInt(artId)
    }

    override fun handler(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            val sender = ctx.get().sender
            sender?.let { player ->
                val art = artId.getArtById()
                art.onLearning(player)
                player.giveExperiencePoints(-art.consumeExperienceValue())
                val learnedArts = player.getOACapability()?.learnedArtIds
                learnedArts?.find { it == artId } ?: learnedArts?.add(artId)
                learnedArts?.sort()
                OANetworking.syncCapability(player)
            }
        }
        ctx.get().packetHandled = true
    }

}