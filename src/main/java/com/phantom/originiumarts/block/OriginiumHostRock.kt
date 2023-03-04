package com.phantom.originiumarts.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.AmethystBlock
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.material.PushReaction
import java.util.*

class OriginiumHostRock : AmethystBlock(
    Properties.of(Material.AMETHYST)
        .strength(30.0f, 1200.0f)
        .randomTicks()
        .sound(SoundType.AMETHYST)
        .requiresCorrectToolForDrops()
) {

    override fun getPistonPushReaction(pState: BlockState): PushReaction {
        return PushReaction.DESTROY
    }

    override fun randomTick(pState: BlockState, pLevel: ServerLevel, pPos: BlockPos, pRandom: Random) {
        if (pRandom.nextInt(5) == 0) {
            val direction = DIRECTIONS[pRandom.nextInt(DIRECTIONS.size)]
            val blockpos = pPos.relative(direction)
            val blockstate = pLevel.getBlockState(blockpos)
            val block = if (canClusterGrowAtState(blockstate)) {
                BlockRegister.ORIGINIUM_SMALL_BUD.get()
            } else if (blockstate.`is`(BlockRegister.ORIGINIUM_SMALL_BUD.get())
                && blockstate.getValue(OriginiumClusterBlock.FACING) == direction
            ) {
                BlockRegister.ORIGINIUM_MEDIUM_BUD.get()
            } else if (blockstate.`is`(BlockRegister.ORIGINIUM_MEDIUM_BUD.get())
                && blockstate.getValue(OriginiumClusterBlock.FACING) == direction
            ) {
                BlockRegister.ORIGINIUM_LARGE_BUD.get()
            } else if (blockstate.`is`(BlockRegister.ORIGINIUM_LARGE_BUD.get())
                && blockstate.getValue(OriginiumClusterBlock.FACING) == direction
            ) {
                BlockRegister.ORIGINIUM_CLUSTER.get()
            } else null
            if (block != null) {
                pLevel.setBlockAndUpdate(
                    blockpos,
                    block.defaultBlockState()
                        .setValue(OriginiumClusterBlock.FACING, direction)
                        .setValue(OriginiumClusterBlock.WATERLOGGED, blockstate.fluidState.type == Fluids.WATER)
                )
            }
        }
    }

    companion object {

        val GROWTH_CHANCE = 5
        private val DIRECTIONS = Direction.values()

        fun canClusterGrowAtState(pState: BlockState): Boolean {
            return pState.isAir || pState.`is`(Blocks.WATER) && pState.fluidState.amount == 8
        }
    }

}