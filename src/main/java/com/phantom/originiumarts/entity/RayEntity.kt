package com.phantom.originiumarts.entity

import com.phantom.originiumarts.client.ParticleRegister
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.math.sqrt
import kotlin.random.Random

class RayEntity(
    entityType: EntityType<RayEntity>,
    level: Level
) : Entity(entityType, level) {

    private var lifeCount = 0

    override fun tick() {
        super.tick()
        if (level.isClientSide && lifeCount == 0) {
            for (i in 0..5) {
                level.addParticle(
                    ParticleRegister.LIGHTNING_SPARK.get(),
                    x + getLookDirection().x * getLength(),
                    y + getLookDirection().y * getLength(),
                    z + getLookDirection().z * getLength(),
                    Random.nextDouble(-0.02, 0.02),
                    Random.nextDouble(0.0, 0.01),
                    Random.nextDouble(-0.02, 0.02)
                )
            }
        }
        lifeCount++
        if (lifeCount >= getLifetime()) {
            discard()
        }
    }

    fun getLifetime(): Int = entityData.get(DATA_LIFETIME)

    fun setLifetime(value: Int) {
        entityData.set(DATA_LIFETIME, value)
    }

    fun setLookDirection(dir: Vec3) {
        val d3 = sqrt(dir.x * dir.x + dir.z * dir.z)
        xRot = Mth.wrapDegrees((-(Mth.atan2(dir.y, d3) * 180 / Math.PI)).toFloat())
        xRotO = xRot
        yRot = Mth.wrapDegrees((Mth.atan2(dir.z, dir.x) * 180 / Math.PI).toFloat() - 90.0f)
        yRotO = yRot
        entityData.set(DATA_LOOK_DIR_X, dir.x.toFloat())
        entityData.set(DATA_LOOK_DIR_Y, dir.y.toFloat())
        entityData.set(DATA_LOOK_DIR_Z, dir.z.toFloat())
    }

    fun getLookDirection(): Vec3 =
        Vec3(
            entityData.get(DATA_LOOK_DIR_X).toDouble(),
            entityData.get(DATA_LOOK_DIR_Y).toDouble(),
            entityData.get(DATA_LOOK_DIR_Z).toDouble()
        )

    fun setLength(value: Float) {
        entityData.set(DATA_LENGTH, value)
    }

    fun getLength() = entityData.get(DATA_LENGTH)

    override fun readAdditionalSaveData(tag: CompoundTag) {
        setLifetime(tag.getInt("lifetime"))
        setLength(tag.getFloat("length"))
        setLookDirection(
            Vec3(
                tag.getFloat("look_dir_x").toDouble(),
                tag.getFloat("look_dir_y").toDouble(),
                tag.getFloat("look_dir_z").toDouble()
            )
        )
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        tag.putInt("lifetime", getLifetime())
        tag.putFloat("length", getLength())
        getLookDirection().let {
            tag.putFloat("look_dir_x", it.x.toFloat())
            tag.putFloat("look_dir_y", it.y.toFloat())
            tag.putFloat("look_dir_z", it.z.toFloat())
        }
    }

    override fun defineSynchedData() {
        entityData.define(DATA_LIFETIME, 40)
        entityData.define(DATA_LENGTH, 0f)
        entityData.define(DATA_LOOK_DIR_X, 0f)
        entityData.define(DATA_LOOK_DIR_Y, 0f)
        entityData.define(DATA_LOOK_DIR_Z, 0f)
    }

    override fun getAddEntityPacket(): Packet<*> =
        ClientboundAddEntityPacket(this)

    companion object {

        val DATA_LIFETIME: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            RayEntity::class.java, EntityDataSerializers.INT
        )

        val DATA_LENGTH: EntityDataAccessor<Float> = SynchedEntityData.defineId(
            RayEntity::class.java, EntityDataSerializers.FLOAT
        )

        val DATA_LOOK_DIR_X: EntityDataAccessor<Float> = SynchedEntityData.defineId(
            RayEntity::class.java, EntityDataSerializers.FLOAT
        )

        val DATA_LOOK_DIR_Y: EntityDataAccessor<Float> = SynchedEntityData.defineId(
            RayEntity::class.java, EntityDataSerializers.FLOAT
        )

        val DATA_LOOK_DIR_Z: EntityDataAccessor<Float> = SynchedEntityData.defineId(
            RayEntity::class.java, EntityDataSerializers.FLOAT
        )
    }

}