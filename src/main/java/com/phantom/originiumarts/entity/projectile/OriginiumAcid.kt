package com.phantom.originiumarts.entity.projectile

import com.phantom.originiumarts.common.EffectRegister
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.damagesource.IndirectEntityDamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult

class OriginiumAcid(
    entityType: EntityType<OriginiumAcid>,
    level: Level
) : ItemProjectile(entityType, level) {

    init {
        setLifetime(200)
        setGravity(0.2f)
        isNoGravity = false
    }

    override fun makeParticle() {
        level.addParticle(
            ParticleTypes.ENTITY_EFFECT,
            getRandomX(0.1),
            this.randomY,
            getRandomZ(0.1),
            1.0,
            1.0,
            1.0
        )
    }

    override fun onHitEntity(pResult: EntityHitResult) {
        super.onHitEntity(pResult)
        if (!level.isClientSide) {
            val entity = pResult.entity
            if (entity is LivingEntity) {
                entity.addEffect(MobEffectInstance(EffectRegister.CORROSION.get(), 400, 0))
                entity.hurt(IndirectEntityDamageSource("originium_slug", this, owner), 2f)
            }
        }
    }

    companion object {
        private val ITEM_STACK = ItemStack(ItemRegister.ORIGINIUM_ACID.get())
    }

    override fun getItemStack(): ItemStack = ITEM_STACK
}