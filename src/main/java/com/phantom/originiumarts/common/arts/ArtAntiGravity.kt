package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.common.network.OANetworking
import com.phantom.originiumarts.common.network.sendpack.OAAntiGravitySendPack
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player

object ArtAntiGravity : AbstractArts(
    uniqueName = "anti_gravity",
    checkCapInfo = CheckCapInfo(
        mobility = OriginiumArtsCapability.ValueLevel.STANDARD,
        artsAdaptability = OriginiumArtsCapability.ValueLevel.EXCELLENT
    )
) {

    init {
        burden = 6.0
        needUseTick = 0
    }

    override fun onUse(player: Player) {
        super.onUse(player)
        if (!player.level.isClientSide) {
            player.level.playSound(
                null,
                player,
                SoundRegister.BERRYHEAL_S.get(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )
        } else {
            player.getOACapability()?.let {
                it.gravityCount = 30
                it.gravityChange -= 0.03
                OANetworking.sendToServer(OAAntiGravitySendPack(it.gravityChange))
            }
        }
    }

    override fun consumeExperienceValue(): Int = 2000

}