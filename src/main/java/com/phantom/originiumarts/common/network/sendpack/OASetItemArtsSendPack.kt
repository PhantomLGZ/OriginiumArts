package com.phantom.originiumarts.common.network.sendpack

import com.phantom.originiumarts.common.ArtsManager
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.InteractionHand
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class OASetItemArtsSendPack(private val artIds: IntArray) : ISendPack {

    constructor(buffer: FriendlyByteBuf) : this(
        artIds = buffer.readVarIntArray()
    )

    override fun toBytes(buffer: FriendlyByteBuf) {
        buffer.writeVarIntArray(artIds)
    }

    override fun handler(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            val sender = ctx.get().sender
            sender?.getItemInHand(InteractionHand.MAIN_HAND)?.getOrCreateTag()
                ?.putIntArray(ArtsManager.KEY_ITEMSTACK_ARTS, artIds)
        }
        ctx.get().packetHandled = true
    }
}