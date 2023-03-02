package com.phantom.originiumarts.entity

import com.phantom.originiumarts.entity.field.BaseFieldEntity
import com.phantom.originiumarts.entity.projectile.ArtBall
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB

fun <T : Entity> Entity.getEntitiesAround(diameter: Double, clazz: Class<T>): List<T> {
    val list = level.getEntitiesOfClass(
        clazz,
        AABB.ofSize(eyePosition, diameter, diameter, diameter)
    )
//    LogUtils.getLogger().info("$this getEntitiesAround $list")
    return list
}

fun List<Entity>.getNearestEntity(centerEntity: Entity): Entity? {
    var length = Double.MAX_VALUE
    var resultEntity: Entity? = null
    this.forEach {
        val dis = it.position().distanceTo(centerEntity.position())
        if (dis < length) {
            length = dis
            resultEntity = it
        }
    }
    return resultEntity
}

fun BaseFieldEntity.setEffectFactorByEntity(entity: Entity) {
    if (entity is ArtBall) {
        setEffectFactor(entity.getEffectFactor())
    }
}