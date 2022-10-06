package com.phantom.originiumarts.entity

import com.phantom.originiumarts.OriginiumArtsMod
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal
import net.minecraft.world.level.Level


class OriginiumSlug(type: EntityType<out AbstractOriginiumSlug>, worldLevel: Level) :
    AbstractOriginiumSlug(type, worldLevel) {

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.let {
            it.addGoal(1, MeleeAttackGoal(this, 1.0, true))
            it.addGoal(2, MoveTowardsTargetGoal(this, 0.5, 32.0f))
        }
    }

    fun getTextureLocation(): ResourceLocation {
        return TEXTURE_LIST[getOriginiumSlugType()]
    }

    override fun getBreedOffspring(serverLevel: ServerLevel, ageableMob: AgeableMob): AgeableMob? {
        return null
    }

    companion object {

        private val DATA_TYPE_ID = SynchedEntityData.defineId(
            OriginiumSlug::class.java, EntityDataSerializers.INT
        )

        private val TEXTURE_LIST = listOf(
            ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/originium_slug.png"),
            ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/originium_slug1.png"),
            ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/originium_slug2.png"),
            ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/originium_slug3.png"),
            ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/originium_slug4.png"),
            ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/originium_slug5.png"),
            ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/originium_slug6.png")
        )

        fun prepareAttributes(): AttributeSupplier.Builder {
            return createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.1)
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
        }

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

    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(DATA_TYPE_ID, TEXTURE_LIST.indices.random())
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putInt("OriginiumSlugType", getOriginiumSlugType())
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        setOriginiumSlugType(tag.getInt("OriginiumSlugType"))
    }
}