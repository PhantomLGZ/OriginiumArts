package com.phantom.originiumarts.common.network.sendpack

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraftforge.common.ForgeMod
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class OAAntiGravitySendPack(private val change: Double) : ISendPack {

    constructor(buffer: FriendlyByteBuf) : this(
        buffer.readDouble()
    )

    override fun toBytes(buffer: FriendlyByteBuf) {
        buffer.writeDouble(change)
    }

    override fun handler(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            ctx.get().sender?.getAttribute(ForgeMod.ENTITY_GRAVITY.get())?.let {
                val key = "art_anti_gravity_entity_gravity"
                val uuid = UUID.nameUUIDFromBytes(key.toByteArray())
                it.removeModifier(uuid)
                if (change != 0.0) {
                    ctx.get().sender?.fallDistance = 0f
                    it.addTransientModifier(AttributeModifier(uuid, key, change, AttributeModifier.Operation.ADDITION))
                }
            }
        }
        ctx.get().packetHandled = true
    }


}