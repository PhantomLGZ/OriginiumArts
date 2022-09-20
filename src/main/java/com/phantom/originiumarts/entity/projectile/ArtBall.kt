package com.phantom.originiumarts.entity.projectile

import com.phantom.originiumarts.common.ArtsManager.getArtById
import com.phantom.originiumarts.common.arts.AbstractArts
import com.phantom.originiumarts.common.arts.ArtEmpty
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3

class ArtBall : BaseProjectile<ArtBall> {

    constructor(
        entityType: EntityType<ArtBall>,
        level: Level
    ) : super(entityType, level)

    constructor(
        entityType: EntityType<ArtBall>,
        level: Level,
        livingEntity: LivingEntity,
        art: AbstractArts
    ) : super(entityType, level, livingEntity) {
        setArtId(art.uniqueID)
        setGravity(art.gravity.toFloat())
        isNoGravity = art.gravity == 0.0
    }

    override fun tick() {
        super.tick()
        if (level.isClientSide) {
            getArtId().getArtById().makeParticle(
                Vec3(getRandomX(0.5), randomY, getRandomZ(0.5)),
                level
            )
        }
    }

    override fun onHitEntity(entityHitResult: EntityHitResult) {
        remove(RemovalReason.KILLED)
        val entity = entityHitResult.entity
        val from = owner
        if (entity is LivingEntity && from is LivingEntity) {
            getArtId().getArtById().onHitEntity(from, this, entityHitResult)
        }
        super.onHitEntity(entityHitResult)
    }

    override fun onHitBlock(blockHitResult: BlockHitResult) {
        remove(RemovalReason.KILLED)
        val from = owner
        if (from is LivingEntity) {
            getArtId().getArtById().onHitBlock(from, this, blockHitResult)
        }
        super.onHitBlock(blockHitResult)
    }

    override fun onTimeOut() {
        val from = owner
        if (from is LivingEntity) {
            getArtId().getArtById().onTimeOut(from, this, level)
        }
        super.onTimeOut()
    }

    fun getArtId(): Int = entityData.get(DATA_ART_ID)

    fun setArtId(id: Int) {
        entityData.set(DATA_ART_ID, id)
    }

    fun getEffectFactor(): Float = entityData.get(DATA_EFFECT_FACTOR)

    fun setEffectFactor(value: Float) {
        entityData.set(DATA_EFFECT_FACTOR, value)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(DATA_ART_ID, ArtEmpty.uniqueID)
        entityData.define(DATA_EFFECT_FACTOR, 1f)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        setArtId(tag.getInt("artId"))
        setEffectFactor(tag.getFloat("effect_factor"))
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putInt("artId", getArtId())
        tag.putFloat("effect_factor", getEffectFactor())
    }

    companion object {
        val DATA_ART_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            ArtBall::class.java, EntityDataSerializers.INT
        )
        val DATA_EFFECT_FACTOR: EntityDataAccessor<Float> = SynchedEntityData.defineId(
            ArtBall::class.java, EntityDataSerializers.FLOAT
        )
    }
}