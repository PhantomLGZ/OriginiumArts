package com.phantom.originiumarts.entity.projectile

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.IndirectEntityDamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseFireBlock
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3

class CocktailEntity(
    entityType: EntityType<CocktailEntity>,
    level: Level
) : ItemProjectile(entityType, level) {

    init {
        setLifetime(200)
        setGravity(0.06f)
        isNoGravity = false
    }

    override fun makeParticle() {
        level.addParticle(
            ParticleTypes.ENTITY_EFFECT,
            getRandomX(0.1),
            this.randomY,
            getRandomZ(0.1),
            1.0,
            1.0,
            1.0
        )
    }

    override fun poseStackInRender(poseStack: PoseStack) {
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(50.0f))
    }

    override fun onHitBlock(pResult: BlockHitResult) {
        super.onHitBlock(pResult)
        if (!level.isClientSide) {
            playHitSound(pResult.location)
            setFireBlockAround(
                pResult.location,
                pResult.direction
            )
        }
    }

    override fun onHitEntity(pResult: EntityHitResult) {
        super.onHitEntity(pResult)
        if (!level.isClientSide) {
            val entity = pResult.entity
            playHitSound(entity.position())
            setFireBlockAround(pResult.location, Direction.UP)
            if (entity is LivingEntity) {
                entity.hurt(IndirectEntityDamageSource("cocktail", this, owner), 2f)
            }
        }
    }

    private fun playHitSound(pos: Vec3) {
        level.playSound(
            null,
            pos.x,
            pos.y,
            pos.z,
            SoundEvents.GLASS_BREAK,
            SoundSource.AMBIENT,
            1.0f,
            1.0f
        )
    }

    private fun setFireBlockAround(pos: Vec3, direction: Direction) {
        val xi = random.nextInt(2)
        val yi = random.nextInt(2)
        val zi = random.nextInt(2)
        for (dx in xi - 1..xi) {
            for (dy in yi - 1..yi) {
                for (dz in zi - 1..zi) {
                    val blockPos = BlockPos(pos.x + dx, pos.y + dy, pos.z + dz)
                    if (!level.isEmptyBlock(blockPos)) {
                        val targetBlock = blockPos.relative(direction)
                        if (level.isEmptyBlock(targetBlock)) {
                            level.setBlockAndUpdate(targetBlock, BaseFireBlock.getState(level, targetBlock))
                        }
                    }
                }
            }
        }
    }

    companion object {
        val ITEM_STACK = ItemStack(ItemRegister.COCKTAIL.get())
    }

    override fun getItemStack(): ItemStack = ITEM_STACK
}