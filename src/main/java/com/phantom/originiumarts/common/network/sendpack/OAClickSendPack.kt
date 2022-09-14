package com.phantom.originiumarts.common.network.sendpack

import com.phantom.originiumarts.item.IOverrideAttack
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.InteractionHand
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class OAClickSendPack(friendlyByteBuf: FriendlyByteBuf? = null) : ISendPack {

    override fun toBytes(buffer: FriendlyByteBuf) {
    }

    override fun handler(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            val sender = ctx.get().sender
            val item = sender?.getItemInHand(InteractionHand.MAIN_HAND)?.item
            if (item is IOverrideAttack) {
                item.onClick(sender)
            }
        }
        ctx.get().packetHandled = true
    }

}