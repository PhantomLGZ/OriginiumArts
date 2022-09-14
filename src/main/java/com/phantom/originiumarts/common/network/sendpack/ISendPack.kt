package com.phantom.originiumarts.common.network.sendpack

import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

interface ISendPack {

    fun toBytes(buffer: FriendlyByteBuf)

    fun handler(ctx: Supplier<NetworkEvent.Context>)

}