package com.phantom.originiumarts.common.network.sendpack

import com.mojang.logging.LogUtils
import com.phantom.originiumarts.common.capability.*
import com.phantom.originiumarts.common.network.*
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class OACapSetSendPack(
    private val targetStr: String,
    private val value: Int,
    private val costExp: Int = 0,
    private val isChange: Boolean = false
) : ISendPack {
    private val logger = LogUtils.getLogger()

    constructor(buffer: FriendlyByteBuf) : this(
        targetStr = buffer.readUtf(),
        value = buffer.readInt(),
        costExp = buffer.readInt(),
        isChange = buffer.readBoolean()
    )

    override fun toBytes(buffer: FriendlyByteBuf) {
        buffer.writeUtf(targetStr)
        buffer.writeInt(value)
        buffer.writeInt(costExp)
        buffer.writeBoolean(isChange)
    }

    override fun handler(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            val sender = ctx.get().sender
            logger.info("${sender?.displayName?.string}:: $targetStr ${if (isChange) "+" else ""}= $value")
            sender?.run {
                if (costExp != 0) {
                    giveExperiencePoints(-costExp)
                }
                when (targetStr) {
                    TARGET_KEY_STRENGTH ->
                        if (isChange) changeStrength(value)
                        else setStrength(value)
                    TARGET_KEY_MOBILITY ->
                        if (isChange) changeMobility(value)
                        else setMobility(value)
                    TARGET_KEY_ENDURANCE ->
                        if (isChange) changeEndurance(value)
                        else setEndurance(value)
                    TARGET_KEY_TACTICAL_ACUMEN ->
                        if (isChange) changeTacticalAcumen(value)
                        else setTacticalAcumen(value)
                    TARGET_KEY_COMBAT_SKILL ->
                        if (isChange) changeCombatSkill(value)
                        else setCombatSkill(value)
                    TARGET_KEY_ARTS_ADAPTABILITY ->
                        if (isChange) changeArtsAdaptability(value)
                        else setArtsAdaptability(value)
                    else -> {
                        logger.error("${displayName.string}:: $targetStr = $value \ntargetStr is incorrect")
                    }
                }
                OANetworking.syncCapability(this)
            }
        }
        ctx.get().packetHandled = true
    }
}