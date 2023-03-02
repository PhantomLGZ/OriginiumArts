package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability
import com.phantom.originiumarts.common.capability.getArtEffectFactor
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.entity.field.ParticleGeneratorField
import com.phantom.originiumarts.entity.getEntitiesAround
import com.phantom.originiumarts.item.ArtsUnitItem
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.EntityDamageSource
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.random.Random

object ArtZeroPointBurst : AbstractArts(
    uniqueName = "zero_point_burst",
    checkCapInfo = CheckCapInfo(artsAdaptability = OriginiumArtsCapability.ValueLevel.STANDARD)
) {

    init {
        burden = 30.0
        needUseTick = 20
    }

    override fun onUse(player: Player, artsUnitItem: ArtsUnitItem) {
        super.onUse(player, artsUnitItem)
        if (!player.level.isClientSide) {
            player.level.playSound(
                null,
                player,
                SoundRegister.MAGIC_ICE.get(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )
            val damage = 3.0 * player.getArtEffectFactor(artsUnitItem)
            player.getEntitiesAround(16.0, Mob::class.java).forEach {
                it.hurt(EntityDamageSource(getNameKey(), player), damage.toFloat())
                it.knockback(2.0, player.x - it.x, player.z - it.z)
            }
            player.level.addFreshEntity(
                ParticleGeneratorField(
                    EntityRegister.PARTICLE_GENERATOR_FIELD.get(),
                    player.level,
                    player.position(),
                    this,
                    10,
                    16f,
                    7
                )
            )
        }
    }

    override fun makeParticle(pos: Vec3, level: Level) {
        level.addParticle(
            ParticleTypes.SNOWFLAKE,
            pos.x,
            pos.y,
            pos.z,
            Random.nextDouble() * 0.2,
            Random.nextDouble() * 0.4,
            Random.nextDouble() * 0.2
        )
    }

    override fun consumeExperienceValue(): Int = 1500

}