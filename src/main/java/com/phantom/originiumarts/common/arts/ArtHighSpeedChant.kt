package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability.ValueLevel
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.common.EffectRegister
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.player.Player

object ArtHighSpeedChant : AbstractArts(
    uniqueName = "high_speed_chant",
    checkCapInfo = CheckCapInfo(
        artsAdaptability = ValueLevel.EXCELLENT
    )
) {

    init {
        burden = 80.0
        needUseTick = 40
    }

    override fun onUse(player: Player) {
        super.onUse(player)
        if (!player.level.isClientSide) {
            player.level.playSound(
                null,
                player,
                SoundRegister.PARTICULATEBOOST.get(),
                SoundSource.PLAYERS,
                0.3f,
                1.0f
            )
        }
        player.getOACapability()?.let {
            if (it.artsAdaptability.getValue() >= ValueLevel.EXCELLENT.baseValue()) {
                player.addEffect(
                    MobEffectInstance(
                        EffectRegister.HIGH_SPEED_CHANT.get(),
                        when (it.artsAdaptability.getLevel()) {
                            ValueLevel.EXCELLENT -> 2 * 1200
                            ValueLevel.OUTSTANDING -> 3 * 1200
                            ValueLevel.UNKNOWN -> 6 * 1200
                            else -> 0
                        },
                        when (it.artsAdaptability.getLevel()) {
                            ValueLevel.EXCELLENT -> 2
                            ValueLevel.OUTSTANDING -> 2
                            ValueLevel.UNKNOWN -> 4
                            else -> 0
                        }
                    )
                )
            }
        }
    }

    override fun consumeExperienceValue(): Int = 2000

}