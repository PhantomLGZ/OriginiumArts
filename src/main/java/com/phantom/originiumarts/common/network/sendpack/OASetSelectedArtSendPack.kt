package com.phantom.originiumarts.common.network.sendpack

import com.phantom.originiumarts.common.capability.getOACapability
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class OASetSelectedArtSendPack(private val artId: Int) : ISendPack {

    constructor(buffer: FriendlyByteBuf) : this(
        artId = buffer.readInt()
    )

    override fun toBytes(buffer: FriendlyByteBuf) {
        buffer.writeInt(artId)
    }

    override fun handler(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            val sender = ctx.get().sender
            sender?.getOACapability()?.selectedArtId = artId
            sender?.stopUsingItem()
        }
        ctx.get().packetHandled = true
    }
}