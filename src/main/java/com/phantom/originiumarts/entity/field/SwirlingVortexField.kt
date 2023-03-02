package com.phantom.originiumarts.entity.field

import com.phantom.originiumarts.common.arts.ArtSwirlingVortex
import com.phantom.originiumarts.entity.getEntitiesAround
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class SwirlingVortexField(
    entityType: EntityType<SwirlingVortexField>,
    level: Level
) : BaseFieldEntity(entityType, level) {

    constructor(
        entityType: EntityType<SwirlingVortexField>,
        level: Level,
        pos: Vec3,
        livingEntity: LivingEntity?
    ) : this(entityType, level) {
        setPos(pos)
        owner = livingEntity
    }

    init {
        setCycle(5)
        setDiameter(16f)
    }

    override fun onEffect() {
        getEntitiesAround(16.0, Mob::class.java).forEach {
            it.knockback(1.0, it.x - x, it.z - z)
        }
    }

    override fun makeParticle() {
        for (i in 0..2) {
            val x1 = x + (random.nextDouble() * 2 - 1) * getDiameter() / 2
            val y1 = y + (random.nextDouble() * 2 - 1) * getDiameter() / 4
            val z1 = z + (random.nextDouble() * 2 - 1) * getDiameter() / 2
            ArtSwirlingVortex.makeParticle(
                Vec3(x1, y1, z1),
                level,
                position()
            )
        }
    }
}