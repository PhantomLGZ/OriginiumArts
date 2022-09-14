package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.client.ParticleRegister
import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability.ValueLevel
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.entity.RayEntity
import com.phantom.originiumarts.entity.getEntitiesAround
import com.phantom.originiumarts.entity.getNearestEntity
import com.phantom.originiumarts.entity.projectile.ArtBall
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.IndirectEntityDamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

object ArtDiffusionCurrent : AbstractArts(
    uniqueName = "diffusion_current",
    checkCapInfo = CheckCapInfo(
        artsAdaptability = ValueLevel.NORMAL
    )
) {

    init {
        burden = 20.0
        needUseTick = 20
    }

    override fun onUse(player: Player) {
        super.onUse(player)
        if (!player.level.isClientSide) {
            player.level.playSound(
                null,
                player,
                SoundRegister.MAGIC_N.get(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )
            player.level.addFreshEntity(ArtBall(EntityRegister.ART_BALL.get(), player.level, player, this).apply {
                setLifetime(50)
                setSpeedFactor(4f)
            })
        }
    }

    override fun onHitEntity(fromEntity: LivingEntity?, projectile: Entity, hitEntity: LivingEntity) {
        if (!hitEntity.level.isClientSide) {
            val hitList = mutableListOf(hitEntity).apply {
                if (fromEntity != null) {
                    add(0, fromEntity)
                }
            }
            val damage = 2.5 * ((fromEntity as? Player)?.getOACapability()?.artDamageFactor?.toDouble() ?: 1.0)
            for (i in 0..2) {
                val last = hitList.last()
                last.hurt(
                    IndirectEntityDamageSource(getNameKey(), projectile, fromEntity)
                        .setMagic(),
                    damage.toFloat()
                )
                val hit = last.getEntitiesAround(4.0, Mob::class.java)
                    .toMutableList().apply { removeAll(hitList.toSet()) }
                    .getNearestEntity(last) ?: break
                hitList.add(hit)
            }
            for (i in 0..hitList.size - 2) {
                val firstEntity = hitList[i]
                val secondEntity = hitList[i + 1]
                firstEntity.level.addFreshEntity(RayEntity(EntityRegister.RAY_ENTITY.get(), firstEntity.level).apply {
                    setPos(firstEntity.eyePosition.subtract(0.0, 0.05, 0.0))
                    setLookDirection(secondEntity.eyePosition.subtract(firstEntity.eyePosition).normalize())
                    setLength(firstEntity.distanceTo(secondEntity))
                    setLifetime(5)
                })
            }
        }
    }

    override fun makeParticle(pos: Vec3, level: Level) {
        level.addParticle(
            ParticleRegister.LIGHTNING_SPARK.get(),
            pos.x,
            pos.y,
            pos.z,
            0.0,
            0.0,
            0.0
        )
    }

    override fun getItemStack(): ItemStack = ItemStack.EMPTY

    override fun consumeExperienceValue(): Int = 1000

}