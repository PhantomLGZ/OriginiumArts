package com.phantom.originiumarts.entity.field

import com.phantom.originiumarts.common.arts.ArtFineBlending
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability.ValueLevel
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.entity.getEntitiesAround
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class FineBlendingField(
    entityType: EntityType<FineBlendingField>,
    level: Level
) : BaseFieldEntity<FineBlendingField>(entityType, level) {

    private var countdown = 5
    private var healValue = 0.5f

    init {
        setCycle(10)
        setDiameter(4f)
    }

    constructor(
        entityType: EntityType<FineBlendingField>,
        level: Level,
        pos: Vec3,
        livingEntity: LivingEntity?
    ) : this(entityType, level) {
        owner = livingEntity
        setPos(pos)
    }

    override fun setOwner(entity: Entity?) {
        super.setOwner(entity)
        if (entity is Player) {
            setup(entity)
        }
    }

    private fun setup(player: Player?) {
        player?.getOACapability()?.let {
            setLifetime(
                when (it.artsAdaptability.getLevel()) {
                    ValueLevel.FLAWED -> 40
                    ValueLevel.NORMAL -> 100
                    ValueLevel.STANDARD -> 200
                    ValueLevel.EXCELLENT -> 300
                    ValueLevel.OUTSTANDING -> 300
                    ValueLevel.UNKNOWN -> 400
                }
            )
            healValue = when (it.artsAdaptability.getLevel()) {
                ValueLevel.FLAWED -> 0.1f
                ValueLevel.NORMAL -> 0.5f
                ValueLevel.STANDARD -> 0.5f
                ValueLevel.EXCELLENT -> 0.5f
                ValueLevel.OUTSTANDING -> 1f
                ValueLevel.UNKNOWN -> 2f
            }
            setDiameter(
                when (it.artsAdaptability.getLevel()) {
                    ValueLevel.FLAWED -> 4f
                    ValueLevel.NORMAL -> 4f
                    ValueLevel.STANDARD -> 4f
                    ValueLevel.EXCELLENT -> 5f
                    ValueLevel.OUTSTANDING -> 6f
                    ValueLevel.UNKNOWN -> 8f
                }
            )
        }
    }

    override fun onEffect() {
        getEntitiesAround(getDiameter().toDouble(), LivingEntity::class.java).forEach {
            it.heal(healValue)
        }
    }

    override fun makeParticle() {
        if (countdown > 0) {
            countdown--
            for (i in 0..20) {
                level.addParticle(
                    ParticleTypes.ENTITY_EFFECT,
                    getRandomX(0.3),
                    this.randomY,
                    getRandomZ(0.3),
                    0.2157,
                    0.9176,
                    1.0
                )
            }
        }
        if (random.nextDouble() > 0.7) {
            ArtFineBlending.makeParticle(
                Vec3(
                    x + (random.nextDouble() * 2 - 1) * getDiameter() / 2,
                    y + random.nextDouble() + 0.2,
                    z + (random.nextDouble() * 2 - 1) * getDiameter() / 2
                ),
                level
            )
        }
    }

}