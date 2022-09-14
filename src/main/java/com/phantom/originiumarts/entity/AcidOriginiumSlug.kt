package com.phantom.originiumarts.entity

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.projectile.OriginiumAcid
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal
import net.minecraft.world.entity.ai.goal.RangedAttackGoal
import net.minecraft.world.entity.monster.RangedAttackMob
import net.minecraft.world.level.Level

class AcidOriginiumSlug(type: EntityType<out AbstractOriginiumSlug>, worldLevel: Level) :
    AbstractOriginiumSlug(type, worldLevel), RangedAttackMob {

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.let {
            it.addGoal(1, RangedAttackGoal(this, 1.0, 40, 6.0F))
            it.addGoal(2, MoveTowardsTargetGoal(this, 0.5, 32.0f))
        }
    }

    override fun tick() {
        super.tick()
        if (getAttackTick() < 20) {
            setAttackTick(getAttackTick() + 1)
        }
    }

    override fun performRangedAttack(pTarget: LivingEntity, pVelocity: Float) {
        if (!level.isClientSide) {
            setAttackTick(0)
            val lookDir = pTarget.eyePosition.subtract(eyePosition)
            val dh = lookDir.horizontalDistance()
            val dy = pTarget.eyePosition.y - eyePosition.y
            val dir = lookDir.subtract(0.0, lookDir.y, 0.0).normalize()
                .add(0.0, dy / dh + 0.1 * dh, 0.0)

            level.addFreshEntity(OriginiumAcid(EntityRegister.ORIGINIUM_ACID.get(), level).apply {
                owner = this
                setPos(this@AcidOriginiumSlug.eyePosition)
                deltaMovement = dir
            })
        }
    }

    fun getTextureLocation(): ResourceLocation {
        return TEXTURE_LIST[getOriginiumSlugType()]
    }

    override fun getBreedOffspring(pLevel: ServerLevel, pOtherParent: AgeableMob): AgeableMob? {
        return null
    }

    private fun getOriginiumSlugType(): Int {
        return entityData.get(DATA_TYPE_ID)
    }

    private fun setOriginiumSlugType(index: Int) {
        entityData.set(
            DATA_TYPE_ID,
            if (index in TEXTURE_LIST.indices) index else TEXTURE_LIST.indices.random()
        )
    }

    fun getAttackTick(): Int = entityData.get(DATA_ATTACK_TICK)

    fun setAttackTick(value: Int) {
        entityData.set(DATA_ATTACK_TICK, value)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(DATA_TYPE_ID, TEXTURE_LIST.indices.random())
        entityData.define(DATA_ATTACK_TICK, 20)
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putInt("OriginiumSlugType", getOriginiumSlugType())
        tag.putInt("attack_tick", getAttackTick())
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        setOriginiumSlugType(tag.getInt("OriginiumSlugType"))
        setAttackTick(tag.getInt("attack_tick"))
    }

    companion object {
        fun prepareAttributes(): AttributeSupplier.Builder {
            return createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0)
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
        }

        private val DATA_TYPE_ID = SynchedEntityData.defineId(
            AcidOriginiumSlug::class.java, EntityDataSerializers.INT
        )

        private val DATA_ATTACK_TICK = SynchedEntityData.defineId(
            AcidOriginiumSlug::class.java, EntityDataSerializers.INT
        )

        private val TEXTURE_LIST = listOf(
            ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/acid_originium_slug.png"),
            ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/acid_originium_slug1.png")
        )

    }

}