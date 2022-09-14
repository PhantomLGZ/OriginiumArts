package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.common.capability.OriginiumArtsCapability
import com.phantom.originiumarts.common.capability.getOACapability
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraftforge.common.ForgeMod
import java.util.*
import kotlin.math.min

object ArtMobileCombat : AbstractArts(
    uniqueName = "mobile_combat",
    checkCapInfo = CheckCapInfo(
        mobility = OriginiumArtsCapability.ValueLevel.STANDARD
    )
) {

    init {
        burden = 1.0
        needUseTick = 0
    }

    override fun onUse(player: Player) {
        if (!player.level.isClientSide) {
            player.level.playSound(
                null,
                player,
                SoundEvents.UI_BUTTON_CLICK,
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )
            player.getOACapability()?.let { cap ->
                val toSetValue = when (cap.mobility.getLevel()) {
                    OriginiumArtsCapability.ValueLevel.FLAWED -> -0.2
                    OriginiumArtsCapability.ValueLevel.NORMAL -> 0.0
                    OriginiumArtsCapability.ValueLevel.STANDARD -> 0.4
                    OriginiumArtsCapability.ValueLevel.EXCELLENT -> 1.0
                    OriginiumArtsCapability.ValueLevel.OUTSTANDING -> 2.0
                    OriginiumArtsCapability.ValueLevel.UNKNOWN -> 4.0
                }
                player.getAttribute(Attributes.MOVEMENT_SPEED)?.setMobilityModifier(
                    "art_mobile_combat_movement_speed",
                    toSetValue
                )
                player.getAttribute(Attributes.FLYING_SPEED)?.setMobilityModifier(
                    "art_mobile_combat_flying_speed",
                    toSetValue
                )
                player.getAttribute(ForgeMod.SWIM_SPEED.get())?.setMobilityModifier(
                    "art_mobile_combat_swim_speed",
                    toSetValue
                )
            }
        }
    }

    private fun AttributeInstance.setMobilityModifier(
        key: String,
        changeValue: Double
    ) {
        val uuid = UUID.nameUUIDFromBytes(key.toByteArray())
        if (getModifier(uuid)?.amount == changeValue) {
            removeModifier(uuid)
            addTransientModifier(
                AttributeModifier(
                    uuid,
                    key,
                    min(changeValue, 0.0),
                    AttributeModifier.Operation.MULTIPLY_BASE
                )
            )
        } else {
            removeModifier(uuid)
            addTransientModifier(
                AttributeModifier(
                    uuid,
                    key,
                    changeValue,
                    AttributeModifier.Operation.MULTIPLY_BASE
                )
            )
        }
    }

    override fun consumeExperienceValue(): Int = 0

}