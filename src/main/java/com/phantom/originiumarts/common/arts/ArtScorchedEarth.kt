package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.client.ParticleRegister
import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.entity.projectile.ArtBall
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.IndirectEntityDamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseFireBlock
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import kotlin.random.Random

object ArtScorchedEarth : AbstractArts(
    uniqueName = "scorched_earth",
    checkCapInfo = CheckCapInfo(
        artsAdaptability = OriginiumArtsCapability.ValueLevel.STANDARD
    )
) {

    private val itemStack = ItemStack(ItemRegister.SCORCHED_EARTH_ART.get())

    init {
        burden = 3.0
        needUseTick = 0
    }

    override fun onUse(player: Player) {
        super.onUse(player)
        if (!player.level.isClientSide) {
            player.level.playSound(
                null,
                player,
                SoundRegister.FIREMAG_N.get(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )
            player.level.addFreshEntity(ArtBall(EntityRegister.FLAME_BALL.get(), player.level, player, this).apply {
                setLifetime(20)
                setSpeedFactor(0.5f)
            })
        }
    }

    override fun onHitEntity(fromEntity: LivingEntity?, projectile: Entity, hitEntity: LivingEntity) {
        var factor = 1.0
        if (fromEntity is Player) {
            factor = fromEntity.getOACapability()?.artDamageFactor?.toDouble() ?: 1.0
        }
        hitEntity.hurt(
            IndirectEntityDamageSource(getNameKey(), projectile, fromEntity)
                .setIsFire()
                .setProjectile(),
            (1.5 * factor).toFloat()
        )
        hitEntity.setSecondsOnFire(1)
    }

    override fun onHitBlock(fromEntity: LivingEntity?, blockHitResult: BlockHitResult, level: Level) {
        if (!level.isClientSide) {
            val blockPos = blockHitResult.blockPos.relative(blockHitResult.direction)
            if (level.isEmptyBlock(blockPos)) {
                level.setBlockAndUpdate(blockPos, BaseFireBlock.getState(level, blockPos))
            }
        }
    }

    override fun makeParticle(pos: Vec3, level: Level) {
        level.addParticle(
            ParticleRegister.FIRE_SPARK.get(),
            pos.x,
            pos.y,
            pos.z,
            Random.nextDouble(-0.2, 0.2),
            Random.nextDouble(-0.2, 0.2),
            Random.nextDouble(-0.2, 0.2)
        )
    }

    override fun getItemStack(): ItemStack = itemStack

    override fun consumeExperienceValue(): Int = 1500

}