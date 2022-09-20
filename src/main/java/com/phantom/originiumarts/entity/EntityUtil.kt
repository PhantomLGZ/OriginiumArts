package com.phantom.originiumarts.entity

import com.phantom.originiumarts.entity.field.BaseFieldEntity
import com.phantom.originiumarts.entity.projectile.ArtBall
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.AABB

fun <T : LivingEntity> Entity.getEntitiesAround(diameter: Double, clazz: Class<T>): List<T> {
    val list = level.getEntitiesOfClass(
        clazz,
        AABB.ofSize(eyePosition, diameter, diameter, diameter)
    )
//    LogUtils.getLogger().info("$this getEntitiesAround $list")
    return list
}

fun List<LivingEntity>.getNearestEntity(centerEntity: Entity): LivingEntity? {
    var length = Double.MAX_VALUE
    var resultEntity: LivingEntity? = null
    this.forEach {
        val dis = it.position().distanceTo(centerEntity.position())
        if (dis < length) {
            length = dis
            resultEntity = it
        }
    }
    return resultEntity
}

fun <T : BaseFieldEntity<T>> T.setEffectFactorByEntity(entity: Entity) {
    if (entity is ArtBall) {
        setEffectFactor(entity.getEffectFactor())
    }
}