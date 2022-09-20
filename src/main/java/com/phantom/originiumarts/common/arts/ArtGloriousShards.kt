package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.client.ParticleRegister
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.entity.field.GloriousShardsField
import com.phantom.originiumarts.entity.projectile.ArtBall
import com.phantom.originiumarts.entity.setEffectFactorByEntity
import com.phantom.originiumarts.item.ArtsUnitItem
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3

object ArtGloriousShards : AbstractArts(
    uniqueName = "glorious_shards",
    checkCapInfo = CheckCapInfo(
        artsAdaptability = OriginiumArtsCapability.ValueLevel.EXCELLENT
    )
) {

    private val itemStack = ItemStack(ItemRegister.GLORIOUS_SHARDS_ART.get())

    init {
        burden = 50.0
        needUseTick = 40
        gravity = 0.02
    }

    override fun onUse(player: Player, artsUnitItem: ArtsUnitItem) {
        super.onUse(player, artsUnitItem)
        if (!player.level.isClientSide) {
            player.level.addFreshEntity(ArtBall(EntityRegister.ART_BALL.get(), player.level, player, this).apply {
                setLifetime(200)
                setSpeedFactor(1f)
                setGravity(0.05f)
                setEffectFactor(artsUnitItem.effectFactor)
            })
        }
    }

    override fun onHitEntity(fromEntity: LivingEntity?, projectile: Entity, entityHitResult: EntityHitResult) {
        val hitEntity = entityHitResult.entity
        if (!hitEntity.level.isClientSide) {
            hitEntity.level.addFreshEntity(
                GloriousShardsField(
                    EntityRegister.GLORIOUS_SHARDS_FIELD.get(),
                    hitEntity.level
                ).apply {
                    owner = fromEntity
                    setPos(hitEntity.position())
                    setEffectFactorByEntity(projectile)
                }
            )
        }
    }

    override fun onHitBlock(fromEntity: LivingEntity?, projectile: Entity, blockHitResult: BlockHitResult) {
        if (!projectile.level.isClientSide) {
            projectile.level.addFreshEntity(
                GloriousShardsField(
                    EntityRegister.GLORIOUS_SHARDS_FIELD.get(),
                    projectile.level
                ).apply {
                    owner = fromEntity
                    setPos(blockHitResult.location)
                    setEffectFactorByEntity(projectile)
                }
            )
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

    override fun consumeExperienceValue(): Int = 2000

    override fun getItemStack(): ItemStack = itemStack

}