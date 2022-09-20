package com.phantom.originiumarts.entity

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.entity.projectile.EtchedAmmo
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.RangedAttackMob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class DroneMonster(type: EntityType<DroneMonster>, worldLevel: Level) :
    AbstractDrone(type, worldLevel), Enemy, RangedAttackMob {

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.let {
            it.addGoal(1, DroneRangedAttackGoal(this, 2.0, 40, 12f))
        }
        targetSelector.let {
            it.addGoal(2, NearestAttackableTargetGoal(this, Player::class.java, true))
        }
    }

    override fun performRangedAttack(pTarget: LivingEntity, pVelocity: Float) {
        if (!level.isClientSide) {
            level.playSound(
                null,
                this,
                SoundRegister.E_VEHICLE_N.get(),
                SoundSource.HOSTILE,
                1.0f,
                1.0f
            )
            val dir = pTarget.eyePosition.subtract(this.position()).normalize()
            level.addFreshEntity(EtchedAmmo(EntityRegister.ETCHED_AMMO.get(), level).apply {
                owner = this@DroneMonster
                setPos(this@DroneMonster.position())
                deltaMovement = dir
                baseDamage = 2.0
            })
        }
    }

    fun getTextureLocation(): ResourceLocation {
        return TEXTURE_LIST[getDroneMonsterType()]
    }

    fun setDroneMonsterType(value: Int) {
        entityData.set(DATA_TYPE_ID, value)
    }

    fun getDroneMonsterType(): Int = entityData.get(DATA_TYPE_ID)

    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(DATA_TYPE_ID, TEXTURE_LIST.indices.random())
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putInt("DroneMonsterType", getDroneMonsterType())
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        setDroneMonsterType(tag.getInt("DroneMonsterType"))
    }

    companion object {

        private val DATA_TYPE_ID = SynchedEntityData.defineId(
            DroneMonster::class.java, EntityDataSerializers.INT
        )

        fun prepareAttributes(): AttributeSupplier.Builder {
            return createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0)
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.1)
                .add(Attributes.FLYING_SPEED, 0.1)
        }

        private val TEXTURE_LIST = listOf(
            ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/drone_monster.png"),
            ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/drone_monster1.png")
        )
    }

}