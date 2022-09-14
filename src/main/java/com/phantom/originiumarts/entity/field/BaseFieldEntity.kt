package com.phantom.originiumarts.entity.field

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.Level

abstract class BaseFieldEntity<T : BaseFieldEntity<T>>(
    entityType: EntityType<T>,
    level: Level
) : Projectile(entityType, level) {

    private var cycleCount = 0
    private var lifeCount = 0

    override fun tick() {
        super.tick()
        if (level.isClientSide) {
            makeParticle()
        }
        cycleCount++
        if (cycleCount >= getCycle()) {
            onEffect()
            cycleCount = 0
        }
        lifeCount++
        if (lifeCount >= getLifetime()) {
            discard()
        }
    }

    open fun onEffect() {}

    open fun makeParticle() {}

    override fun defineSynchedData() {
        entityData.define(DATA_LIFETIME, 200)
        entityData.define(DATA_CYCLE, 10)
        entityData.define(DATA_DIAMETER, 2f)
    }

    fun getLifetime(): Int = entityData.get(DATA_LIFETIME)

    fun setLifetime(value: Int) {
        entityData.set(DATA_LIFETIME, value)
    }

    fun getCycle(): Int = entityData.get(DATA_CYCLE)

    fun setCycle(value: Int) {
        entityData.set(DATA_CYCLE, value)
    }

    fun getDiameter(): Float = entityData.get(DATA_DIAMETER)

    fun setDiameter(value: Float) {
        entityData.set(DATA_DIAMETER, value)
    }

    override fun readAdditionalSaveData(compoundTag: CompoundTag) {
        setLifetime(compoundTag.getInt("lifeTime"))
        setCycle(compoundTag.getInt("cycle"))
        setDiameter(compoundTag.getFloat("diameter"))
    }

    override fun addAdditionalSaveData(compoundTag: CompoundTag) {
        compoundTag.putInt("lifeTime", getLifetime())
        compoundTag.putInt("cycle", getCycle())
        compoundTag.putFloat("diameter", getDiameter())
    }

    companion object {

        val DATA_LIFETIME: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            BaseFieldEntity::class.java, EntityDataSerializers.INT
        )

        val DATA_CYCLE: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            BaseFieldEntity::class.java, EntityDataSerializers.INT
        )

        val DATA_DIAMETER: EntityDataAccessor<Float> = SynchedEntityData.defineId(
            BaseFieldEntity::class.java, EntityDataSerializers.FLOAT
        )

    }
}