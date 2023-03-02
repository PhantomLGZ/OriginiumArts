package com.phantom.originiumarts.entity.field

import com.phantom.originiumarts.common.ArtsManager.getArtById
import com.phantom.originiumarts.common.arts.AbstractArts
import com.phantom.originiumarts.common.arts.ArtEmpty
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class ParticleGeneratorField(
    entityType: EntityType<ParticleGeneratorField>,
    level: Level
) : BaseFieldEntity(entityType, level) {

    constructor(
        entityType: EntityType<ParticleGeneratorField>,
        level: Level,
        pos: Vec3,
        art: AbstractArts,
        lifeTime: Int,
        diameter: Float,
        amount: Int
    ) : this(entityType, level) {
        setPos(pos)
        setArtId(art.uniqueID)
        setLifetime(lifeTime)
        setDiameter(diameter)
        setAmount(amount)
    }

    override fun makeParticle() {
        for (i in 0..getAmount()) {
            getArtId().getArtById().makeParticle(
                Vec3(
                    x + (random.nextDouble() * 2 - 1) * getDiameter() / 2,
                    y + random.nextDouble() + 0.2,
                    z + (random.nextDouble() * 2 - 1) * getDiameter() / 2
                ),
                level
            )
        }
    }

    fun getAmount(): Int = entityData.get(DATA_AMOUNT)

    fun setAmount(value: Int) {
        entityData.set(DATA_AMOUNT, value)
    }

    fun getArtId(): Int = entityData.get(DATA_ART_ID)

    fun setArtId(id: Int) {
        entityData.set(DATA_ART_ID, id)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(DATA_AMOUNT, 1)
        entityData.define(DATA_ART_ID, ArtEmpty.uniqueID)
    }

    override fun readAdditionalSaveData(compoundTag: CompoundTag) {
        super.readAdditionalSaveData(compoundTag)
        setAmount(compoundTag.getInt("amount"))
        setArtId(compoundTag.getInt("artId"))
    }

    override fun addAdditionalSaveData(compoundTag: CompoundTag) {
        super.addAdditionalSaveData(compoundTag)
        compoundTag.putInt("amount", getAmount())
        compoundTag.putInt("artId", getArtId())
    }

    companion object {

        val DATA_AMOUNT: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            ParticleGeneratorField::class.java, EntityDataSerializers.INT
        )

        val DATA_ART_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            ParticleGeneratorField::class.java, EntityDataSerializers.INT
        )

    }
}