package com.phantom.originiumarts.block

import com.phantom.originiumarts.client.gui.AttrManageGui
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape


class OperatingBedBlock : HorizontalDirectionalBlock(
    Properties.of(Material.WOOL)
        .strength(1f)
        .sound(SoundType.WOOL)
) {

    override fun use(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        return if (level.isClientSide) {
            AttrManageGui.openGui()
            InteractionResult.SUCCESS
        } else {
            InteractionResult.CONSUME
        }
    }


    override fun updateEntityAfterFallOn(blockGetter: BlockGetter, entity: Entity) {
        if (entity.isSuppressingBounce) {
            super.updateEntityAfterFallOn(blockGetter, entity)
        } else {
            bounceUp(entity)
        }
    }

    private fun bounceUp(entity: Entity) {
        val vec3 = entity.deltaMovement
        if (vec3.y < 0.0) {
            val d0 = if (entity is LivingEntity) 1.0 else 0.8
            entity.setDeltaMovement(vec3.x, -vec3.y * 0.66 * d0, vec3.z)
        }
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {
        return when (getConnectedDirection(blockState).opposite) {
            Direction.NORTH -> NORTH_SHAPE
            Direction.SOUTH -> SOUTH_SHAPE
            Direction.WEST -> WEST_SHAPE
            else -> EAST_SHAPE
        }
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState? {
        val direction = blockPlaceContext.horizontalDirection
        val blockpos = blockPlaceContext.clickedPos
        val blockpos1 = blockpos.relative(direction)
        val level = blockPlaceContext.level
        return if (level.getBlockState(blockpos1).canBeReplaced(blockPlaceContext)
            && level.worldBorder.isWithinBounds(blockpos1)
        ) defaultBlockState().setValue(FACING, direction)
        else null
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, BlockStateProperties.BED_PART, BlockStateProperties.OCCUPIED)
    }

    companion object {
        val NORTH_SHAPE: VoxelShape = box(0.0, 0.1, -16.0, 16.0, 16.0, 28.0)
        val SOUTH_SHAPE: VoxelShape = box(0.0, 0.1, -12.0, 16.0, 16.0, 32.0)
        val WEST_SHAPE: VoxelShape = box(-16.0, 0.1, 0.0, 28.0, 16.0, 16.0)
        val EAST_SHAPE: VoxelShape = box(-12.0, 0.1, 0.0, 32.0, 16.0, 16.0)

        fun getConnectedDirection(blockState: BlockState): Direction {
            return blockState.getValue(FACING)
        }
    }

}