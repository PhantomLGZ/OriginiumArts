package com.phantom.originiumarts.entity.projectile

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.damagesource.IndirectEntityDamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.level.Level
import net.minecraft.world.phys.*


class EtchedAmmo : AbstractProjectile {

    var baseDamage = 5.0
    val damageModifyList = mutableMapOf<String, Float>()

    constructor(
        entityType: EntityType<EtchedAmmo>,
        level: Level
    ) : super(entityType, level)

    constructor(
        entityType: EntityType<EtchedAmmo>,
        level: Level,
        shooter: LivingEntity
    ) : super(entityType, level, shooter)

    init {
        setSpeedFactor(4f)
        isNoGravity = true
    }

    override fun tick() {
        super.tick()
        if (level.isClientSide) {
            makeParticle()
        }
    }

    private fun makeParticle() {
        for (i in 0..1) {
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
    }

    private fun getDamage(): Float {
        var damage = baseDamage.toFloat()
        damageModifyList.forEach {
            damage *= it.value
        }
        return damage
    }

    override fun onHitBlock(p_37258_: BlockHitResult) {
        if (!level.isClientSide) {
            remove(RemovalReason.KILLED)
        }
        super.onHitBlock(p_37258_)
    }

    override fun onHitEntity(entityHitResult: EntityHitResult) {
        if (!level.isClientSide) {
            remove(RemovalReason.KILLED)
            entityHitResult.entity.hurt(
                IndirectEntityDamageSource("bullet", this, owner).setProjectile(),
                getDamage()
            )
        }
        super.onHitEntity(entityHitResult)
    }

    override fun shouldRenderAtSqrDistance(p_19883_: Double): Boolean {
        return true
    }
}