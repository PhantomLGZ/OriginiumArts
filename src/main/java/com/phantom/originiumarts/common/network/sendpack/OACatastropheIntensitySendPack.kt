package com.phantom.originiumarts.common.network.sendpack

import com.phantom.originiumarts.common.weather.CatastropheServerManager
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class OACatastropheIntensitySendPack(var intensity: Int, var isAdd: Boolean = false) : ISendPack {

    constructor(buffer: FriendlyByteBuf) : this(
        intensity = buffer.readInt(),
        isAdd = buffer.readBoolean()
    )

    override fun toBytes(buffer: FriendlyByteBuf) {
        buffer.writeInt(intensity)
        buffer.writeBoolean(isAdd)
    }

    override fun handler(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            if (isAdd) {
                CatastropheServerManager
                    .get(ctx.get().sender!!.level)
                    ?.addCatastrophe(ctx.get().sender!!, intensity)
            } else {
                CatastropheServerManager
                    .get(ctx.get().sender!!.level)
                    ?.setIntensity(intensity)
            }
        }
        ctx.get().packetHandled = true
    }
}