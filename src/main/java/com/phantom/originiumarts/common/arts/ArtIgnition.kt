package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.common.capability.OriginiumArtsCapability.ValueLevel.*
import com.phantom.originiumarts.client.ParticleRegister
import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.capability.getArtEffectFactor
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.entity.projectile.ArtBall
import com.phantom.originiumarts.item.ArtsUnitItem
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
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3
import kotlin.random.Random

object ArtIgnition : AbstractArts(
    uniqueName = "ignition",
    checkCapInfo = CheckCapInfo(artsAdaptability = NORMAL)
) {

    private val itemStack = ItemStack(ItemRegister.IGNITION_ART.get())

    init {
        burden = 10.0
        needUseTick = 25
    }

    override fun onUse(player: Player, artsUnitItem: ArtsUnitItem) {
        super.onUse(player, artsUnitItem)
        val level = player.level
        if (!level.isClientSide) {
            level.playSound(
                null,
                player,
                SoundRegister.FIREMAG_N.get(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )
            level.addFreshEntity(ArtBall(EntityRegister.ART_BALL.get(), level, player, this).apply {
                setEffectFactor(artsUnitItem.effectFactor)
            })
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

    override fun onHitEntity(fromEntity: LivingEntity?, projectile: Entity, entityHitResult: EntityHitResult) {
        val hitEntity = entityHitResult.entity
        if (!hitEntity.level.isClientSide) {
            val factor = (fromEntity as? Player).getArtEffectFactor(projectile)
            hitEntity.hurt(
                IndirectEntityDamageSource(getNameKey(), projectile, fromEntity)
                    .setIsFire()
                    .setProjectile(),
                (3.0 * factor).toFloat()
            )
            hitEntity.setSecondsOnFire(5)
        }
    }

    override fun onHitBlock(fromEntity: LivingEntity?, projectile: Entity, blockHitResult: BlockHitResult) {
        val level = projectile.level
        if (!level.isClientSide) {
            val blockPos = blockHitResult.blockPos.relative(blockHitResult.direction)
            if (level.isEmptyBlock(blockPos)) {
                level.setBlockAndUpdate(blockPos, BaseFireBlock.getState(level, blockPos))
            }
        }
    }

    override fun getItemStack(): ItemStack {
        return itemStack
    }

    override fun consumeExperienceValue(): Int = 1000

}