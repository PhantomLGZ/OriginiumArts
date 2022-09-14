package com.phantom.originiumarts.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class OriginiumClusterBlock(pSize: Int, pOffset: Int) : AmethystBlock(
    Properties.of(Material.AMETHYST)
        .strength(1f)
        .lightLevel { 5 }
        .sound(SoundType.AMETHYST_CLUSTER)
        .noOcclusion()
        .randomTicks()
        .requiresCorrectToolForDrops()
), SimpleWaterloggedBlock {

    var northAabb: VoxelShape = box(
        pOffset.toDouble(),
        pOffset.toDouble(),
        (16 - pSize).toDouble(),
        (16 - pOffset).toDouble(),
        (16 - pOffset).toDouble(),
        16.0
    )
    var southAabb: VoxelShape = box(
        pOffset.toDouble(),
        pOffset.toDouble(),
        0.0,
        (16 - pOffset).toDouble(),
        (16 - pOffset).toDouble(),
        pSize.toDouble()
    )
    var eastAabb: VoxelShape = box(
        0.0,
        pOffset.toDouble(),
        pOffset.toDouble(),
        pSize.toDouble(),
        (16 - pOffset).toDouble(),
        (16 - pOffset).toDouble()
    )
    var westAabb: VoxelShape = box(
        (16 - pSize).toDouble(),
        pOffset.toDouble(),
        pOffset.toDouble(),
        16.0,
        (16 - pOffset).toDouble(),
        (16 - pOffset).toDouble()
    )
    var upAabb: VoxelShape = box(
        pOffset.toDouble(),
        0.0,
        pOffset.toDouble(),
        (16 - pOffset).toDouble(),
        pSize.toDouble(),
        (16 - pOffset).toDouble()
    )
    var downAabb: VoxelShape = box(
        pOffset.toDouble(),
        (16 - pSize).toDouble(),
        pOffset.toDouble(),
        (16 - pOffset).toDouble(),
        16.0,
        (16 - pOffset).toDouble()
    )

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.UP)
        )
    }

    override fun getShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape {
        return when (pState.getValue(FACING)) {
            Direction.NORTH -> northAabb
            Direction.SOUTH -> southAabb
            Direction.EAST -> eastAabb
            Direction.WEST -> westAabb
            Direction.DOWN -> downAabb
            Direction.UP -> upAabb
            else -> upAabb
        }
    }

    override fun canSurvive(pState: BlockState, pLevel: LevelReader, pPos: BlockPos): Boolean {
        val direction = pState.getValue(FACING)
        val blockpos = pPos.relative(direction.opposite)
        return pLevel.getBlockState(blockpos).isFaceSturdy(pLevel, blockpos, direction)
    }

    override fun updateShape(
        pState: BlockState,
        pDirection: Direction,
        pNeighborState: BlockState,
        pLevel: LevelAccessor,
        pCurrentPos: BlockPos,
        pNeighborPos: BlockPos
    ): BlockState {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel))
        }
        return if (pDirection == pState.getValue(FACING).opposite
            && !pState.canSurvive(pLevel, pCurrentPos)
        )
            Blocks.AIR.defaultBlockState()
        else pState
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? {
        val levelaccessor: LevelAccessor = pContext.level
        val blockpos = pContext.clickedPos
        return defaultBlockState()
            .setValue(WATERLOGGED, levelaccessor.getFluidState(blockpos).type == Fluids.WATER)
            .setValue(FACING, pContext.clickedFace)
    }

    override fun rotate(pState: BlockState, pRotation: Rotation): BlockState {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)))
    }

    override fun mirror(pState: BlockState, pMirror: Mirror): BlockState {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)))
    }

    override fun getFluidState(pState: BlockState): FluidState {
        return if (pState.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(pState)
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block?, BlockState?>) {
        pBuilder.add(WATERLOGGED, FACING)
    }

    override fun getPistonPushReaction(pState: BlockState): PushReaction {
        return PushReaction.DESTROY
    }

    companion object {
        val WATERLOGGED = BlockStateProperties.WATERLOGGED
        val FACING = BlockStateProperties.FACING
    }

}