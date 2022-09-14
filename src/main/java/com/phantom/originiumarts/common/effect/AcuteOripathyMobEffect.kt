package com.phantom.originiumarts.common.effect

import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class AcuteOripathyMobEffect : MobEffect(MobEffectCategory.HARMFUL, 0x000000) {

    override fun applyEffectTick(livingEntity: LivingEntity, amplifier: Int) {
        if (livingEntity is Player) {
            livingEntity.causeFoodExhaustion(0.005f * (amplifier + 1).toFloat())
        }
        livingEntity.hurt(DamageSource.MAGIC, (amplifier + 1).toFloat())
        if (amplifier >= 1) {
            livingEntity.setSecondsOnFire(1)
            livingEntity.hurt(DamageSource.ON_FIRE, amplifier.toFloat())
        }
        if (amplifier >= 2) {
            livingEntity.hurt(DamageSource.WITHER, amplifier.toFloat())
        }
    }

    override fun isDurationEffectTick(duration: Int, amplifier: Int): Boolean {
        return true
    }
}