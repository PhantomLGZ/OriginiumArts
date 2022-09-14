package com.phantom.originiumarts.common.network.sendpack

import com.phantom.originiumarts.common.capability.*
import com.phantom.originiumarts.common.network.OANetworking
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class OAClearLearnedArtsSendPack() : ISendPack {

    constructor(buffer: FriendlyByteBuf) : this()

    override fun toBytes(buffer: FriendlyByteBuf) {

    }

    override fun handler(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            ctx.get().sender?.run {
                getOACapability()?.let {
                    it.learnedArtIds.removeAll(it.learnedArtIds)
                }
                OANetworking.syncCapability(this)
            }
        }
        ctx.get().packetHandled = true
    }
}
