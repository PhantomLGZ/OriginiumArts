package com.phantom.originiumarts.entity.projectile

import com.phantom.originiumarts.block.BlockRegister
import com.phantom.originiumarts.block.OriginiumDust
import com.phantom.originiumarts.common.SoundRegister
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SnowLayerBlock
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3
import kotlin.math.roundToInt

class Meteor(
    entityType: EntityType<Meteor>,
    level: Level
) : AbstractProjectile(entityType, level) {

    override fun tick() {
        super.tick()
        updateRotation()
    }

    override fun onHitEntity(pResult: EntityHitResult) {
        super.onHitEntity(pResult)
        if (!level.isClientSide) {
            onHitInServer(pResult.location)
        }
        if (pResult.entity.type != this.type) {
            remove(RemovalReason.KILLED)
        }
    }

    override fun onHitBlock(pResult: BlockHitResult) {
        super.onHitBlock(pResult)
        if (!level.isClientSide) {
            onHitInServer(pResult.location)
        }
        remove(RemovalReason.KILLED)
    }

    private fun onHitInServer(pos: Vec3) {
        level.explode(
            null,
            pos.x,
            pos.y,
            pos.z,
            getSize() / 2,
            Explosion.BlockInteraction.DESTROY
        )
        val l = (getSize() / 4).roundToInt()
        for (i in -l..l) {
            for (j in -l..l) {
                for (k in -l..l) {
                    val blockPos = BlockPos(pos.add(i.toDouble(), j.toDouble(), k.toDouble()))
                    val blockState = level.getBlockState(blockPos)
                    val blockStateBelow = level.getBlockState(blockPos.below())
                    val r = random.nextDouble()
                    if (r < 0.6) {
                        if ((Block.isFaceFull(blockStateBelow.getCollisionShape(level, blockPos.below()), Direction.UP)
                                    || (blockStateBelow.`is`(BlockRegister.ORIGINIUM_DUST.get())
                                    && blockStateBelow.getValue(OriginiumDust.LAYERS) == 8)
                                    || (blockStateBelow.`is`(Blocks.SNOW)
                                    && blockStateBelow.getValue(SnowLayerBlock.LAYERS) == 8))
                            && (blockState.isAir
                                    || blockState.`is`(BlockRegister.ORIGINIUM_DUST.get())
                                    || blockState.`is`(Blocks.SNOW)
                                    || blockState.`is`(Blocks.GRASS))
                        ) {
                            when {
                                r < 0.01 -> level.setBlockAndUpdate(
                                    blockPos,
                                    BlockRegister.ORIGINIUM_HOST_ROCK.get().defaultBlockState()
                                )
                                r < 0.2 -> level.setBlockAndUpdate(
                                    blockPos,
                                    BlockRegister.ORIGINIUM_LARGE_BUD.get().defaultBlockState()
                                )
                                r < 0.4 -> level.setBlockAndUpdate(
                                    blockPos,
                                    BlockRegister.ORIGINIUM_MEDIUM_BUD.get().defaultBlockState()
                                )
                                else -> level.setBlockAndUpdate(
                                    blockPos,
                                    BlockRegister.ORIGINIUM_SMALL_BUD.get().defaultBlockState()
                                )
                            }
                        }
                    }
                }
            }
        }
        level.playSound(
            null,
            pos.x,
            pos.y,
            pos.z,
            SoundRegister.EXPLOSION.get(),
            SoundSource.AMBIENT,
            getSize() * 5,
            1.0f
        )
    }

    fun getSize(): Float = entityData.get(DATA_SIZE)

    fun setSize(value: Float) {
        entityData.set(DATA_SIZE, value)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(DATA_SIZE, 16f)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        setSize(tag.getFloat("size"))
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putFloat("size", getSize())
    }

    companion object {

        val DATA_SIZE: EntityDataAccessor<Float> = SynchedEntityData.defineId(
            Meteor::class.java, EntityDataSerializers.FLOAT
        )
    }
}