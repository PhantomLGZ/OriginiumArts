package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.client.ParticleRegister
import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.entity.field.SwirlingVortexField
import com.phantom.originiumarts.entity.projectile.ArtBall
import com.phantom.originiumarts.entity.setEffectFactorByEntity
import com.phantom.originiumarts.item.ArtsUnitItem
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3
import kotlin.random.Random

object ArtSwirlingVortex : AbstractArts(
    uniqueName = "swirling_vortex",
    checkCapInfo = CheckCapInfo(artsAdaptability = OriginiumArtsCapability.ValueLevel.NORMAL)
) {

    private val itemStack = ItemStack(ItemRegister.SWIRLING_VORTEX_ART.get())

    init {
        burden = 30.0
        needUseTick = 20
        gravity = 0.02
    }

    override fun onUse(player: Player, artsUnitItem: ArtsUnitItem) {
        super.onUse(player, artsUnitItem)
        if (!player.level.isClientSide) {
            player.level.playSound(
                null,
                player,
                SoundRegister.ENERGYSHOT.get(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )
            player.level.addFreshEntity(
                ArtBall(EntityRegister.ART_BALL.get(), player.level, player, this).apply {
                    setLifetime(20)
                    setEffectFactor(artsUnitItem.effectFactor)
                })
        }
    }

    override fun onHitEntity(fromEntity: LivingEntity?, projectile: Entity, entityHitResult: EntityHitResult) {
        val hitEntity = entityHitResult.entity
        if (!hitEntity.level.isClientSide) {
            hitEntity.level.playSound(
                null,
                hitEntity,
                SoundRegister.CUREMECHA.get(),
                SoundSource.PLAYERS,
                1f,
                1f
            )
            hitEntity.level.addFreshEntity(
                SwirlingVortexField(
                    EntityRegister.SWIRLING_VORTEX_FIELD.get(),
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
        val level = projectile.level
        if (!level.isClientSide) {
            level.playSound(
                null,
                blockHitResult.blockPos,
                SoundRegister.CUREMECHA.get(),
                SoundSource.PLAYERS,
                1f,
                1f
            )
            level.addFreshEntity(
                SwirlingVortexField(
                    EntityRegister.SWIRLING_VORTEX_FIELD.get(),
                    level,
                    blockHitResult.location,
                    fromEntity
                ).apply {
                    setEffectFactorByEntity(projectile)
                }
            )
        }
    }

    override fun onTimeOut(fromEntity: LivingEntity?, projectile: Entity, level: Level) {
        if (!level.isClientSide) {
            level.playSound(
                null,
                projectile,
                SoundRegister.CUREMECHA.get(),
                SoundSource.PLAYERS,
                1f,
                1f
            )
            level.addFreshEntity(
                SwirlingVortexField(
                    EntityRegister.SWIRLING_VORTEX_FIELD.get(),
                    level,
                    projectile.position(),
                    fromEntity
                ).apply {
                    setEffectFactorByEntity(projectile)
                }
            )
        }
    }

    override fun makeParticle(pos: Vec3, level: Level) {
        level.addParticle(
            ParticleRegister.ENERGY_BALL.get(),
            pos.x + (Random.nextDouble() * 2 - 1) * 0.2,
            pos.y + (Random.nextDouble() * 2 - 1) * 0.2,
            pos.z + (Random.nextDouble() * 2 - 1) * 0.2,
            pos.x,
            pos.y,
            pos.z
        )
    }

    override fun makeParticle(pos: Vec3, level: Level, dir: Vec3) {
        level.addParticle(ParticleRegister.ENERGY_BALL.get(), pos.x, pos.y, pos.z, dir.x, dir.y, dir.z)
    }

    override fun getItemStack(): ItemStack = itemStack

    override fun consumeExperienceValue(): Int = 1000

}