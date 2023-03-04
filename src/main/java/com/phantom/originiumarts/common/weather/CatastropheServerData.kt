package com.phantom.originiumarts.common.weather

import com.phantom.originiumarts.block.BlockRegister
import com.phantom.originiumarts.block.OriginiumDust
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.entity.projectile.Meteor
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SnowLayerBlock
import net.minecraft.world.level.levelgen.Heightmap
import java.util.*
import kotlin.math.*

data class CatastropheServerData(
    var posX: Double,
    val posY: Double,
    var posZ: Double,
    val vX: Double,
    val vZ: Double,
    var tickCount: Int = 0,
    val lifetime: Int,
    val intensity: Int
) {
    fun tick(level: ServerLevel) {
        tickCount++
        posX += vX
        posZ += vZ

        level.getNearestPlayer(posX, posY, posZ, BASE_EFFECT_DISTANCE * 2, false)?.let { player ->
            val random = Random()
            val r = random.nextGaussian().coerceIn(-1.0..1.0) * BASE_EFFECT_DISTANCE
            val a = random.nextDouble(2 * PI)
            val blockPos = level
                .getHeightmapPos(
                    Heightmap.Types.MOTION_BLOCKING,
                    BlockPos((posX + r * cos(a)).roundToInt(), 0, (posZ + r * sin(a)).roundToInt())
                )
            val l = random.nextDouble()
            val blockState = level.getBlockState(blockPos)
            if (l < 0.1) {
                when {
                    blockState.`is`(BlockRegister.ORIGINIUM_LARGE_BUD.get()) -> {
                        level.setBlockAndUpdate(blockPos, BlockRegister.ORIGINIUM_CLUSTER.get().defaultBlockState())
                    }
                    blockState.`is`(BlockRegister.ORIGINIUM_MEDIUM_BUD.get()) -> {
                        level.setBlockAndUpdate(blockPos, BlockRegister.ORIGINIUM_LARGE_BUD.get().defaultBlockState())
                    }
                    blockState.`is`(BlockRegister.ORIGINIUM_SMALL_BUD.get()) -> {
                        level.setBlockAndUpdate(blockPos, BlockRegister.ORIGINIUM_MEDIUM_BUD.get().defaultBlockState())
                    }
                }
            }
            val blockStateBelow = level.getBlockState(blockPos.below())
            if ((Block.isFaceFull(blockStateBelow.getCollisionShape(level, blockPos.below()), Direction.UP)
                        || (blockStateBelow.`is`(BlockRegister.ORIGINIUM_DUST.get())
                        && blockStateBelow.getValue(OriginiumDust.LAYERS) == 8)
                        || (blockStateBelow.`is`(Blocks.SNOW)
                        && blockStateBelow.getValue(SnowLayerBlock.LAYERS) == 8))
                && (blockState.isAir || blockState.`is`(BlockRegister.ORIGINIUM_DUST.get()))
            ) {
                when {
                    l < 0.01 -> {
                        level.setBlockAndUpdate(blockPos, BlockRegister.ORIGINIUM_CLUSTER.get().defaultBlockState())
                    }
                    l < 0.04 -> {
                        level.setBlockAndUpdate(blockPos, BlockRegister.ORIGINIUM_LARGE_BUD.get().defaultBlockState())
                    }
                    l < 0.08 -> {
                        level.setBlockAndUpdate(blockPos, BlockRegister.ORIGINIUM_MEDIUM_BUD.get().defaultBlockState())
                    }
                    l < 0.2 -> {
                        level.setBlockAndUpdate(blockPos, BlockRegister.ORIGINIUM_SMALL_BUD.get().defaultBlockState())
                    }
                    else -> {
                        level.setBlockAndUpdate(blockPos, BlockRegister.ORIGINIUM_DUST.get().defaultBlockState())
                    }
                }
            }

            val l1 = random.nextDouble()
            if (intensity >= 6 && l1 < 0.01) {
                val r1 = random.nextDouble(BASE_EFFECT_DISTANCE / 2)
                val a1 = random.nextDouble(2 * PI)
                level.addFreshEntity(Meteor(EntityRegister.METEOR.get(), level).apply {
                    setPos(
                        posX + r1 * cos(a1),
                        255.0,
                        posZ + r1 * sin(a1)
                    )
                    setDeltaMovement(
                        random.nextDouble(-2.0, 2.0),
                        random.nextDouble(-4.0, -1.0),
                        random.nextDouble(-2.0, 2.0)
                    )
                    setLifetime(4000)
                    setGravity(0.04f)
                    setSize(2f.pow(random.nextInt(2, 5)))
                    setSpeedFactor(random.nextFloat(1f, 2f))
                })
            }
        }
    }

    fun needRemove(): Boolean = tickCount > lifetime

    fun getTransition(): Float {
        val length = min(lifetime / 100f, 100f)
        return if (tickCount < length) {
            tickCount / length
        } else if (lifetime - tickCount < length) {
            lifetime - tickCount / length
        } else {
            1f
        }
    }

}
