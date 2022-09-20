package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.client.ParticleRegister
import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.entity.field.FineBlendingField
import com.phantom.originiumarts.entity.projectile.ArtBall
import com.phantom.originiumarts.entity.setEffectFactorByEntity
import com.phantom.originiumarts.item.ArtsUnitItem
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3

object ArtFineBlending : AbstractArts(
    uniqueName = "fine_blending",
    checkCapInfo = CheckCapInfo(artsAdaptability = OriginiumArtsCapability.ValueLevel.STANDARD)
) {

    private val itemStack = ItemStack(ItemRegister.FINE_BLENDING_ART.get())

    init {
        burden = 30.0
        needUseTick = 20
        gravity = 0.03
    }

    override fun onUse(player: Player, artsUnitItem: ArtsUnitItem) {
        super.onUse(player, artsUnitItem)
        val level = player.level
        if (!level.isClientSide) {
            level.playSound(
                null,
                player.x,
                player.y,
                player.z,
                SoundRegister.BOTTLE_N.get(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )
            level.addFreshEntity(
                ArtBall(EntityRegister.ART_BALL.get(), level, player, this).apply {
                    setEffectFactor(artsUnitItem.effectFactor)
                }
            )
        }
    }

    override fun onHitEntity(fromEntity: LivingEntity?, projectile: Entity, entityHitResult: EntityHitResult) {
        val hitEntity = entityHitResult.entity
        if (!hitEntity.level.isClientSide) {
            hitEntity.level.playSound(
                null,
                hitEntity,
                SoundEvents.GLASS_BREAK,
                SoundSource.AMBIENT,
                1.0f,
                1.0f
            )
            hitEntity.level.addFreshEntity(
                FineBlendingField(
                    EntityRegister.FINE_BLENDING_FIELD.get(),
                    hitEntity.level,
                    hitEntity.position(),
                    fromEntity
                ).apply {
                    setEffectFactorByEntity(projectile)
                }
            )
        }
    }

    override fun onHitBlock(fromEntity: LivingEntity?, projectile: Entity, blockHitResult: BlockHitResult) {
        if (!projectile.level.isClientSide) {
            projectile.level.playSound(
                null,
                blockHitResult.blockPos,
                SoundEvents.GLASS_BREAK,
                SoundSource.AMBIENT,
                1.0f,
                1.0f
            )
            projectile.level.addFreshEntity(
                FineBlendingField(
                    EntityRegister.FINE_BLENDING_FIELD.get(),
                    projectile.level,
                    blockHitResult.location,
                    fromEntity
                ).apply {
                    setEffectFactorByEntity(projectile)
                }
            )
        }
    }

    override fun makeParticle(pos: Vec3, level: Level) {
        level.addParticle(
            ParticleRegister.FRAGRANCE.get(),
            pos.x,
            pos.y,
            pos.z,
            0.0,
            0.0,
            0.0,
        )
    }

    override fun getItemStack(): ItemStack {
        return itemStack
    }

    override fun consumeExperienceValue(): Int = 1500

}