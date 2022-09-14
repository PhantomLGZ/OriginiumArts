package com.phantom.originiumarts.entity.projectile

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import java.util.function.BiFunction
import java.util.function.Predicate

abstract class BaseProjectile<T : BaseProjectile<T>>(
    entityType: EntityType<T>,
    level: Level
) : Projectile(entityType, level) {

    private val projectileTargets: Predicate<Entity> = Predicate<Entity> { input ->
        input != null && input.isPickable && !input.isSpectator
    }

    private var isInitialized = false

    constructor(
        entityType: EntityType<T>,
        level: Level,
        shooter: LivingEntity
    ) : this(entityType, level) {
        owner = shooter
        deltaMovement = shooter.lookAngle
        this.setPos(shooter.eyePosition.x, shooter.eyePosition.y - 0.2f, shooter.eyePosition.z)
        updateHeading()
    }

    override fun tick() {
        super.tick()

        if (!isInitialized) {
            deltaMovement = deltaMovement.multiply(
                getSpeedFactor().toDouble(),
                getSpeedFactor().toDouble(),
                getSpeedFactor().toDouble()
            )
            isInitialized = true
        }

        deltaMovement = Vec3(
            deltaMovement.x,
            deltaMovement.y - if (!isNoGravity) getGravity() else 0f,
            deltaMovement.z
        )

        if (!level.isClientSide) {
            val start = position()
            var end = start.add(deltaMovement)
            val blockResult = findBlockByPath(
                level,
                ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)
            )
            blockResult?.let {
                if (it.type != HitResult.Type.MISS) {
                    end = it.location
                }
            }
            val entityResult = findEntityOnPath(start, end, owner)

            if (entityResult != null) {
                onHitEntity(entityResult)
            } else if (blockResult != null && blockResult.type != HitResult.Type.MISS) {
                onHitBlock(blockResult)
            }
        }

        setPos(position().add(deltaMovement))

        if (tickCount >= getLifetime()) {
            onTimeOut()
        }
    }

    open fun onTimeOut() {
        remove(RemovalReason.KILLED)
    }

    private fun updateHeading() {
        if (xRotO == 0.0f && yRotO == 0.0f) {
            val horizontalDistance = deltaMovement.horizontalDistance()
            yRot = (Mth.atan2(deltaMovement.x(), deltaMovement.z()) * (180.0 / Math.PI)).toFloat()
            xRot = (Mth.atan2(deltaMovement.y(), horizontalDistance) * (180.0 / Math.PI)).toFloat()
            yRotO = yRot
            xRotO = xRot
        }
    }

    private fun findBlockByPath(
        world: Level,
        context: ClipContext,
    ): BlockHitResult? {
        return performRayTrace(context,
            { rayTraceContext, blockPos ->
                val blockState = world.getBlockState(blockPos)
                val fluidState = world.getFluidState(blockPos)
                val startVec = rayTraceContext.from
                val endVec = rayTraceContext.to
                val blockShape = rayTraceContext.getBlockShape(blockState, world, blockPos)
                val blockResult = world.clipWithInteractionOverride(startVec, endVec, blockPos, blockShape, blockState)
                val fluidShape = rayTraceContext.getFluidShape(fluidState, world, blockPos)
                val fluidResult = fluidShape.clip(startVec, endVec, blockPos)
                val blockDistance = blockResult?.let {
                    rayTraceContext.from.distanceToSqr(it.location)
                } ?: Double.MAX_VALUE
                val fluidDistance = fluidResult?.let {
                    rayTraceContext.from.distanceToSqr(it.location)
                } ?: Double.MAX_VALUE
                if (blockDistance <= fluidDistance) blockResult else fluidResult
            }) { rayTraceContext ->
            val vec3 = rayTraceContext.from.subtract(rayTraceContext.to)
            BlockHitResult.miss(
                rayTraceContext.to,
                Direction.getNearest(vec3.x, vec3.y, vec3.z),
                BlockPos(rayTraceContext.to)
            )
        }
    }

    private fun <T> performRayTrace(
        context: ClipContext,
        hitFunction: BiFunction<ClipContext, BlockPos, T>,
        func: (context: ClipContext) -> T
    ): T {
        val startVec = context.from
        val endVec = context.to
        return if (startVec == endVec) {
            func(context)
        } else {
            val startX = Mth.lerp(-0.0000001, endVec.x, startVec.x)
            val startY = Mth.lerp(-0.0000001, endVec.y, startVec.y)
            val startZ = Mth.lerp(-0.0000001, endVec.z, startVec.z)
            val endX = Mth.lerp(-0.0000001, startVec.x, endVec.x)
            val endY = Mth.lerp(-0.0000001, startVec.y, endVec.y)
            val endZ = Mth.lerp(-0.0000001, startVec.z, endVec.z)
            var blockX = Mth.floor(endX)
            var blockY = Mth.floor(endY)
            var blockZ = Mth.floor(endZ)
            val mutablePos = BlockPos.MutableBlockPos(blockX, blockY, blockZ)
            val t = hitFunction.apply(context, mutablePos)
            if (t != null) {
                return t
            }
            val deltaX = startX - endX
            val deltaY = startY - endY
            val deltaZ = startZ - endZ
            val signX = Mth.sign(deltaX)
            val signY = Mth.sign(deltaY)
            val signZ = Mth.sign(deltaZ)
            val d9 = if (signX == 0) Double.MAX_VALUE else signX.toDouble() / deltaX
            val d10 = if (signY == 0) Double.MAX_VALUE else signY.toDouble() / deltaY
            val d11 = if (signZ == 0) Double.MAX_VALUE else signZ.toDouble() / deltaZ
            var d12 = d9 * if (signX > 0) 1.0 - Mth.frac(endX) else Mth.frac(endX)
            var d13 = d10 * if (signY > 0) 1.0 - Mth.frac(endY) else Mth.frac(endY)
            var d14 = d11 * if (signZ > 0) 1.0 - Mth.frac(endZ) else Mth.frac(endZ)
            while (d12 <= 1.0 || d13 <= 1.0 || d14 <= 1.0) {
                if (d12 < d13) {
                    if (d12 < d14) {
                        blockX += signX
                        d12 += d9
                    } else {
                        blockZ += signZ
                        d14 += d11
                    }
                } else if (d13 < d14) {
                    blockY += signY
                    d13 += d10
                } else {
                    blockZ += signZ
                    d14 += d11
                }
                val t1 = hitFunction.apply(context, mutablePos.set(blockX, blockY, blockZ))
                if (t1 != null) {
                    return t1
                }
            }
            func(context)
        }
    }

    private fun findEntityOnPath(startVec: Vec3, endVec: Vec3, shooter: Entity? = null): EntityHitResult? {
        var hitEntity: Entity? = null
        val delta = Vec3(
            endVec.x - startVec.x,
            endVec.y - startVec.y,
            endVec.z - startVec.z
        )
        val entities = level.getEntities(
            this,
            boundingBox.expandTowards(delta).inflate(1.0),
            projectileTargets
        )
        var closestDistance = Double.MAX_VALUE
        for (entity in entities) {
            if (entity != shooter) {
                val result = getHitResult(entity, startVec, endVec) ?: continue
                val hitPos = result.location
                val distanceToHit = startVec.distanceTo(hitPos)
                if (distanceToHit < closestDistance) {
                    hitEntity = entity
                    closestDistance = distanceToHit
                }
            }
        }
        return if (hitEntity != null) EntityHitResult(hitEntity) else null
    }

    private fun getHitResult(entity: Entity, startVec: Vec3, endVec: Vec3): EntityHitResult? {
        val expandHeight = if (entity is Player && !entity.isCrouching()) 0.0625 else 0.0
        var boundingBox = entity.boundingBox
        boundingBox = boundingBox.expandTowards(0.0, expandHeight, 0.0)
        var hitPos = boundingBox.clip(startVec, endVec).orElse(null)
        val grownHitPos = boundingBox.inflate(
            0.0,
            0.0,
            0.0
        ).clip(startVec, endVec).orElse(null)
        if (hitPos == null && grownHitPos != null) {
            val raytraceResult: HitResult? = findBlockByPath(
                level,
                ClipContext(startVec, grownHitPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)
            )
            if (raytraceResult!!.type == HitResult.Type.BLOCK) {
                return null
            }
            hitPos = grownHitPos
        }

        return hitPos?.let { EntityHitResult(entity) }
    }

    fun getGravity(): Float = entityData.get(DATA_GRAVITY)

    fun setGravity(value: Float) {
        entityData.set(DATA_GRAVITY, value)
    }

    fun getLifetime(): Int = entityData.get(DATA_LIFETIME)

    fun setLifetime(value: Int) {
        entityData.set(DATA_LIFETIME, value)
    }

    fun getSpeedFactor(): Float = entityData.get(DATA_SPEED_FACTOR)

    fun setSpeedFactor(value: Float) {
        entityData.set(DATA_SPEED_FACTOR, value)
    }

    override fun defineSynchedData() {
        entityData.define(DATA_LIFETIME, 200)
        entityData.define(DATA_SPEED_FACTOR, 1f)
        entityData.define(DATA_GRAVITY, 0f)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        setLifetime(tag.getInt("lifetime"))
        setSpeedFactor(tag.getFloat("speedFactor"))
        setGravity(tag.getFloat("gravity"))
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putInt("lifetime", getLifetime())
        tag.putFloat("speedFactor", getSpeedFactor())
        tag.putFloat("gravity", getGravity())
    }

    companion object {

        val DATA_LIFETIME: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            BaseProjectile::class.java, EntityDataSerializers.INT
        )

        val DATA_SPEED_FACTOR: EntityDataAccessor<Float> = SynchedEntityData.defineId(
            BaseProjectile::class.java, EntityDataSerializers.FLOAT
        )

        val DATA_GRAVITY: EntityDataAccessor<Float> = SynchedEntityData.defineId(
            BaseProjectile::class.java, EntityDataSerializers.FLOAT
        )

    }

}