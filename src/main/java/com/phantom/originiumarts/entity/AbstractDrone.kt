package com.phantom.originiumarts.entity

import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.FlyingMob
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
import net.minecraft.world.entity.monster.RangedAttackMob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.BiomeDictionary
import thedarkcolour.kotlinforforge.kotlin.enumSetOf
import kotlin.random.Random

abstract class AbstractDrone(
    type: EntityType<out AbstractDrone>,
    worldLevel: Level
) : FlyingMob(type, worldLevel) {

    init {
        moveControl = DroneMoveControl(this)
        navigation = FlyingPathNavigation(this, level)
    }

    override fun registerGoals() {
        goalSelector.let {
            it.addGoal(5, DroneRandomFloatAroundGoal(this))
            it.addGoal(6, LookAtPlayerGoal(this, Player::class.java, 12f))
            it.addGoal(6, DroneRandomLookAroundGoal(this))
        }
    }

    override fun getHeadRotSpeed(): Int = 20

    companion object {

        fun checkSpawnRules(
            entityType: EntityType<out AbstractDrone>,
            levelAccessor: LevelAccessor,
            mobSpawnType: MobSpawnType,
            blockPos: BlockPos,
            random: java.util.Random
        ): Boolean {
            val biome = levelAccessor.biomeManager.getBiome(blockPos).unwrapKey()
            return BiomeDictionary.hasType(biome.get(), BiomeDictionary.Type.OVERWORLD)
                    && !BiomeDictionary.hasType(biome.get(), BiomeDictionary.Type.OCEAN)
        }
    }

    class DroneMoveControl(drone: AbstractDrone) : MoveControl(drone) {

        private var count = 0
        private var oPos = Vec3.ZERO

        override fun tick() {
            if (operation == Operation.MOVE_TO) {
                val dis = Vec3(wantedX, wantedY, wantedZ).subtract(mob.position())
                if (hasReach(dis)) {
                    setStop()
                } else {

                    val d0 = wantedX - mob.x
                    val d1 = wantedZ - mob.z
                    val d2 = wantedY - mob.y
                    val d3 = d0 * d0 + d2 * d2 + d1 * d1
                    if (d3 < 2.5000003E-7) {
                        mob.setZza(0.0f)
                        return
                    }
                    val f9 = (Mth.atan2(d1, d0) * (180f / Math.PI.toFloat()).toDouble()).toFloat() - 90.0f
                    mob.yRot = rotlerp(mob.yRot, f9, 90.0f)

                    mob.deltaMovement = mob.deltaMovement
                        .add(dis.normalize().scale(0.5))
                        .normalize()
                        .scale((mob.getAttribute(Attributes.FLYING_SPEED)?.value ?: 1.0) * speedModifier)
                    if (oPos.subtract(mob.position()).length() > 0.1) {
                        oPos = mob.position()
                        count = 0
                    } else {
                        count++
                        if (count > 100) {
                            setStop()
                        }
                    }
                }
            } else {
                super.tick()
            }
        }

        private fun setStop() {
            count = 0
            operation = Operation.WAIT
            mob.navigation.stop()
        }

        private fun hasReach(dis: Vec3): Boolean {
            return dis.length() < 0.5
        }
    }

    class DroneRandomFloatAroundGoal(private val drone: AbstractDrone) : Goal() {
        init {
            flags = enumSetOf(Flag.MOVE)
        }

        override fun canUse(): Boolean {
            return !drone.moveControl.hasWanted() && Random.nextInt() % 64 == 0
        }

        override fun canContinueToUse(): Boolean {
            return false
        }

        override fun start() {
            val random = drone.random
            val d0 = drone.x + ((random.nextFloat() * 2.0f - 1.0f) * 8.0f).toDouble()
            val d1 = drone.y + ((random.nextFloat() * 2.0f - 1.0f) * 2.0f).toDouble()
            val d2 = drone.z + ((random.nextFloat() * 2.0f - 1.0f) * 8.0f).toDouble()
            if (drone.level.getBlockState(BlockPos(d0, d1, d2)).isAir) {
                drone.navigation.moveTo(d0, d1, d2, 1.0)
            }
        }
    }

    class DroneRandomLookAroundGoal(private val drone: AbstractDrone) : RandomLookAroundGoal(drone) {
        override fun canUse(): Boolean {
            return super.canUse() && drone.target?.isAlive != true
        }

        override fun canContinueToUse(): Boolean {
            return super.canContinueToUse() && drone.target?.isAlive != true
        }
    }

    class DroneRangedAttackGoal(
        private val mob: AbstractDrone,
        private val speedModifier: Double,
        private val attackInterval: Int,
        private val attackRadius: Float
    ) : Goal() {

        private var target: LivingEntity? = null
        private var attackTime = -1
        private var seeTime = 0

        override fun canUse(): Boolean {
            val livingEntity = mob.target
            return if (livingEntity?.isAlive == true) {
                target = livingEntity
                true
            } else {
                false
            }
        }

        override fun canContinueToUse(): Boolean {
            return canUse() || !mob.getNavigation().isDone
        }

        override fun stop() {
            target = null
            this.seeTime = 0
            this.attackTime = -1
        }

        override fun requiresUpdateEveryTick(): Boolean {
            return true
        }

        override fun tick() {
            target?.let {
                val d0 = mob.distanceTo(it)
                val flag = mob.sensing.hasLineOfSight(it)
                if (flag) {
                    ++seeTime
                    mob.lookAt(it, 30f, 30f)
                } else {
                    seeTime = 0
                }
                if (d0 < attackRadius.toDouble() && this.seeTime >= 5) {
                    mob.getNavigation().stop()
                } else {
                    mob.getNavigation().moveTo(it, this.speedModifier)
                }
                mob.getLookControl().setLookAt(it, 30.0f, 30.0f)
                if (--attackTime <= 0 && flag) {
                    val f = d0 / this.attackRadius
                    val f1 = Mth.clamp(f, 0.1f, 1.0f)
                    if (mob is RangedAttackMob && d0 <= attackRadius) {
                        mob.performRangedAttack(it, f1)
                    }
                    attackTime = attackInterval
                }
            }
        }
    }

}