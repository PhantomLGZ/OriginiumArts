package com.phantom.originiumarts.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LightLayer
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.*
import kotlin.math.min


class OriginiumDust : Block(Properties.of(Material.TOP_SNOW)
    .randomTicks()
    .strength(0.1F)
    .requiresCorrectToolForDrops()
    .sound(SoundType.SNOW)
    .isViewBlocking { pState, pLevel, pPos ->
        pState.getValue(LAYERS) >= 8
    }) {

    init {
        registerDefaultState(stateDefinition.any().setValue(LAYERS, Integer.valueOf(1)))
    }

    override fun isPathfindable(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pType: PathComputationType
    ): Boolean {
        return when (pType) {
            PathComputationType.LAND -> pState.getValue(LAYERS) < 5
            PathComputationType.WATER -> false
            PathComputationType.AIR -> false
            else -> false
        }
    }

    override fun getShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape {
        return SHAPE_BY_LAYER[pState.getValue(LAYERS)]
    }

    override fun getCollisionShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape {
        return SHAPE_BY_LAYER[pState.getValue(LAYERS) - 1]
    }

    override fun getBlockSupportShape(
        pState: BlockState,
        pReader: BlockGetter,
        pPos: BlockPos
    ): VoxelShape {
        return SHAPE_BY_LAYER[pState.getValue(LAYERS)]
    }

    override fun getVisualShape(
        pState: BlockState,
        pReader: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape {
        return SHAPE_BY_LAYER[pState.getValue(LAYERS)]
    }

    override fun updateShape(
        pState: BlockState,
        pFacing: Direction,
        pFacingState: BlockState,
        pLevel: LevelAccessor,
        pCurrentPos: BlockPos,
        pFacingPos: BlockPos
    ): BlockState {
        return if (!pState.canSurvive(pLevel, pCurrentPos))
            Blocks.AIR.defaultBlockState()
        else
            super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos)
    }

    override fun randomTick(pState: BlockState, pLevel: ServerLevel, pPos: BlockPos, pRandom: Random) {
        if (pLevel.getBrightness(LightLayer.BLOCK, pPos) > 11) {
            dropResources(pState, pLevel, pPos)
            pLevel.removeBlock(pPos, false)
        }
    }

    override fun canBeReplaced(pState: BlockState, pUseContext: BlockPlaceContext): Boolean {
        val i = pState.getValue(LAYERS)
        return if (pUseContext.itemInHand.`is`(asItem()) && i < 8) {
            if (pUseContext.replacingClickedOnBlock()) {
                pUseContext.clickedFace == Direction.UP
            } else {
                true
            }
        } else {
            i == 1
        }
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? {
        val blockState = pContext.level.getBlockState(pContext.clickedPos)
        return if (blockState.`is`(this)) {
            val i = blockState.getValue(LAYERS)
            blockState.setValue(LAYERS, min(8, i + 1))
        } else {
            super.getStateForPlacement(pContext)
        }
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block?, BlockState?>) {
        pBuilder.add(LAYERS)
    }

    companion object {
        val LAYERS: IntegerProperty = BlockStateProperties.LAYERS
        val SHAPE_BY_LAYER = arrayOf(
            Shapes.empty(),
            box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
            box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
        )
    }

}