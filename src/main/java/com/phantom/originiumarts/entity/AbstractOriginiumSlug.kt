package com.phantom.originiumarts.entity

import net.minecraft.core.BlockPos
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.TimeUtil
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.phys.AABB
import net.minecraftforge.common.BiomeDictionary
import java.util.*

abstract class AbstractOriginiumSlug(type: EntityType<out Animal>, worldLevel: Level) :
    Animal(type, worldLevel), NeutralMob {

    private var persistentAngerTarget: UUID? = null

    override fun registerGoals() {
        goalSelector.let {
            it.addGoal(0, FloatGoal(this))
            it.addGoal(5, WaterAvoidingRandomStrollGoal(this, 0.5))
            it.addGoal(6, LookAtPlayerGoal(this, Player::class.java, 8f))
            it.addGoal(6, RandomLookAroundGoal(this))
        }
        targetSelector.let {
            it.addGoal(1, OriginiumSlugHurtByTargetGoal(this).setAlertOthers())
            it.addGoal(
                2,
                NearestAttackableTargetGoal(this, Player::class.java, 10, true, false, this::isAngryAt)
            )
            it.addGoal(3, OriginiumSlugResetUniversalAngerTargetGoal(this))
        }
    }

    override fun aiStep() {
        super.aiStep()
        if (!level.isClientSide) {
            updatePersistentAnger(level as ServerLevel, true)
        }
    }

    override fun getRemainingPersistentAngerTime(): Int = entityData.get(DATA_REMAINING_ANGER_TIME)

    override fun setRemainingPersistentAngerTime(time: Int) {
        entityData.set(DATA_REMAINING_ANGER_TIME, time)
    }

    override fun getPersistentAngerTarget(): UUID? = persistentAngerTarget

    override fun setPersistentAngerTarget(uuid: UUID?) {
        persistentAngerTarget = uuid
    }

    override fun startPersistentAngerTimer() {
        remainingPersistentAngerTime = PERSISTENT_ANGER_TIME.sample(random)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(DATA_REMAINING_ANGER_TIME, 0)
    }

    companion object {

        private val DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(
            AbstractOriginiumSlug::class.java, EntityDataSerializers.INT
        )

        private val PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39)

        fun <T : AbstractOriginiumSlug> checkOriginiumSlugSpawnRules(
            entityType: EntityType<T>,
            levelAccessor: LevelAccessor,
            mobSpawnType: MobSpawnType,
            blockPos: BlockPos,
            random: Random
        ): Boolean {
            val biome = levelAccessor.biomeManager.getBiome(blockPos).unwrapKey()
            return BiomeDictionary.hasType(biome.get(), BiomeDictionary.Type.OVERWORLD)
                    && !BiomeDictionary.hasType(biome.get(), BiomeDictionary.Type.DRY)
                    && !BiomeDictionary.hasType(biome.get(), BiomeDictionary.Type.OCEAN)
                    && !BiomeDictionary.hasType(biome.get(), BiomeDictionary.Type.RIVER)
        }
    }

    class OriginiumSlugHurtByTargetGoal(pMob: AbstractOriginiumSlug) : HurtByTargetGoal(pMob) {
        override fun alertOthers() {
            val d0 = this.followDistance
            val aabb = AABB.unitCubeFromLowerCorner(mob.position()).inflate(d0, 10.0, d0)
            val list = mob.level.getEntitiesOfClass(
                AbstractOriginiumSlug::class.java,
                aabb,
                EntitySelector.NO_SPECTATORS
            )
            mob.lastHurtByMob?.let { target ->
                list.forEach {
                    alertOther(it, target)
                }
            }
        }
    }

    class OriginiumSlugResetUniversalAngerTargetGoal(private val mob: AbstractOriginiumSlug) : Goal() {

        private var lastHurtByPlayerTimestamp = 0

        override fun start() {
            lastHurtByPlayerTimestamp = mob.lastHurtByMobTimestamp
            mob.forgetCurrentTargetAndRefreshUniversalAnger()
            getNearbyMobsOfSameType()!!
                .stream().filter { it !== mob }
                .map { it as? NeutralMob }
                .forEach { it?.forgetCurrentTargetAndRefreshUniversalAnger() }
            super.start()
        }

        private fun getNearbyMobsOfSameType(): List<Mob?>? {
            val d0 = mob.getAttributeValue(Attributes.FOLLOW_RANGE)
            val aabb = AABB.unitCubeFromLowerCorner(mob.position()).inflate(d0, 10.0, d0)
            return mob.level.getEntitiesOfClass(AbstractOriginiumSlug::class.java, aabb, EntitySelector.NO_SPECTATORS)
        }

        override fun canUse(): Boolean {
            return this.mob.level.gameRules.getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.wasHurtByPlayer()
        }

        private fun wasHurtByPlayer(): Boolean {
            return mob.lastHurtByMob != null && mob.lastHurtByMob!!.type === EntityType.PLAYER && mob.lastHurtByMobTimestamp > this.lastHurtByPlayerTimestamp
        }

    }
}