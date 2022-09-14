package com.phantom.originiumarts.common.network.sendpack

import com.phantom.originiumarts.common.EffectRegister
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.effect.MobEffectInstance
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class OAAcuteOripathySendPack(
    private var duration: Int = 0,
    private val amplifier: Int = 0
) : ISendPack {

    constructor(buffer: FriendlyByteBuf) : this(
        buffer.readInt(),
        buffer.readInt()
    )

    override fun toBytes(buffer: FriendlyByteBuf) {
        buffer.writeInt(duration)
        buffer.writeInt(amplifier)
    }

    override fun handler(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            ctx.get().sender?.addEffect(
                MobEffectInstance(
                    EffectRegister.ACUTE_ORIPATHY.get(),
                    duration,
                    amplifier
                )
            )
        }
        ctx.get().packetHandled = true
    }

}