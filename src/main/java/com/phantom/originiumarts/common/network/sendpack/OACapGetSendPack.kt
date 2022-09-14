package com.phantom.originiumarts.common.network.sendpack

import com.phantom.originiumarts.common.capability.getOACapability
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class OACapGetSendPack(
    private val capNbt: CompoundTag?
) : ISendPack {

    constructor(buffer: FriendlyByteBuf) : this(
        capNbt = buffer.readNbt()
    )

    override fun toBytes(buffer: FriendlyByteBuf) {
        buffer.writeNbt(capNbt)
    }

    override fun handler(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            Minecraft.getInstance().player?.getOACapability()?.deserializeNBT(capNbt)
        }
        ctx.get().packetHandled = true
    }
}