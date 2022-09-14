package com.phantom.originiumarts.common.effect

import net.minecraft.world.effect.InstantenousMobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import kotlin.math.pow
import kotlin.math.roundToInt

class GainExperienceMobEffect :
    InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0xDBF600) {

    override fun applyEffectTick(livingEntity: LivingEntity, amplifier: Int) {
        if (livingEntity is Player) {
            livingEntity.giveExperiencePoints(3 * (5f.pow(amplifier - 1)).roundToInt())
        }
    }

}