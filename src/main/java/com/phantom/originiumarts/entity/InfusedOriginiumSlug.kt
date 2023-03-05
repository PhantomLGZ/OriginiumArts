package com.phantom.originiumarts.entity

import com.phantom.originiumarts.OriginiumArtsMod
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level

class InfusedOriginiumSlug(type: EntityType<out AbstractOriginiumSlug>, worldLevel: Level) :
    AbstractOriginiumSlug(type, worldLevel) {

    var iosDeathTime = 0

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.let {
            it.addGoal(1, MeleeAttackGoal(this, 1.0, true))
            it.addGoal(2, MoveTowardsTargetGoal(this, 0.5, 32.0f))
        }
    }

    override fun tickDeath() {
        ++this.iosDeathTime
        if (this.iosDeathTime == 40 && !level.isClientSide()) {
            level.broadcastEntityEvent(this, 60.toByte())
            this.remove(RemovalReason.KILLED)
            level.explode(this, this.x, this.y, this.z, 2f, Explosion.BlockInteraction.NONE)
        }
    }

    override fun getDeathSound(): SoundEvent = SoundEvents.CREEPER_PRIMED

    override fun getBreedOffspring(pLevel: ServerLevel, pOtherParent: AgeableMob): AgeableMob? {
        return null
    }

    fun getTextureLocation(): ResourceLocation {
        return TEXTURE_LIST[0]
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {
        super.readAdditionalSaveData(pCompound)
        if (pCompound.contains("iosDeathTime")) {
            iosDeathTime = pCompound.getInt("iosDeathTime")
        }
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag) {
        super.addAdditionalSaveData(pCompound)
        pCompound.putInt("iosDeathTime", iosDeathTime)
    }

    override fun getExperienceReward(pPlayer: Player): Int {
        return super.getExperienceReward(pPlayer)
    }

    companion object {
        fun prepareAttributes(): AttributeSupplier.Builder {
            return createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.1)
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
        }

        private val TEXTURE_LIST = listOf(
            ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/infused_originium_slug.png")
        )
    }

}