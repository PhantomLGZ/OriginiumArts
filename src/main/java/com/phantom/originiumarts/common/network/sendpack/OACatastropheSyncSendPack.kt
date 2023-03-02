package com.phantom.originiumarts.common.network.sendpack

import com.phantom.originiumarts.common.weather.CatastropheClientManager
import com.phantom.originiumarts.common.weather.CatastropheClientData
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class OACatastropheSyncSendPack(private val list: List<CatastropheClientData>) : ISendPack {

    constructor(buffer: FriendlyByteBuf) : this(
        list = buffer.readList {
            val posX = it.readDouble()
            val posY = it.readDouble()
            val posZ = it.readDouble()
            val intensity = it.readInt()
            val transition = it.readFloat()
            CatastropheClientData(posX, posY, posZ, intensity, transition)
        }.filterNotNull()
    )

    override fun toBytes(buffer: FriendlyByteBuf) {
        buffer.writeCollection(list) { buf, info ->
            buf.writeDouble(info.posX)
            buf.writeDouble(info.posY)
            buf.writeDouble(info.posZ)
            buf.writeInt(info.intensity)
            buf.writeFloat(info.transition)
        }
    }

    override fun handler(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            CatastropheClientManager.catastropheInfoList = list
            CatastropheClientManager.update()
        }
        ctx.get().packetHandled = true
    }
}