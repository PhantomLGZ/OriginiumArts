package com.phantom.originiumarts.common.network.sendpack

import com.phantom.originiumarts.common.capability.getOACapability
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class OASetPlayerArtsSendPack(private val artIds: IntArray) : ISendPack {

    constructor(buffer: FriendlyByteBuf) : this(
        artIds = buffer.readVarIntArray()
    )

    override fun toBytes(buffer: FriendlyByteBuf) {
        buffer.writeVarIntArray(artIds)
    }

    override fun handler(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            ctx.get().sender?.getOACapability()?.let {
                it.installedArtIds.clear()
                it.installedArtIds.addAll(artIds.toList())
            }
        }
        ctx.get().packetHandled = true
    }

}