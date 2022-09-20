package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability.ValueLevel
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.common.EffectRegister
import com.phantom.originiumarts.common.capability.getArtEffectFactor
import com.phantom.originiumarts.item.ArtsUnitItem
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.player.Player
import kotlin.math.roundToInt

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

    override fun onUse(player: Player, artsUnitItem: ArtsUnitItem) {
        super.onUse(player, artsUnitItem)
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
                        (when (it.artsAdaptability.getLevel()) {
                            ValueLevel.EXCELLENT -> 2 * 300
                            ValueLevel.OUTSTANDING -> 3 * 300
                            ValueLevel.UNKNOWN -> 6 * 300
                            else -> 0
                        } * player.getArtEffectFactor(artsUnitItem)).roundToInt(),
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