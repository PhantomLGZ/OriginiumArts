package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.client.ParticleRegister
import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability.ValueLevel
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.common.EffectRegister
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.entity.field.ParticleGeneratorField
import com.phantom.originiumarts.entity.getEntitiesAround
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

object ArtEMP : AbstractArts(
    uniqueName = "emp",
    checkCapInfo = CheckCapInfo(artsAdaptability = ValueLevel.STANDARD)
) {

    init {
        burden = 30.0
        needUseTick = 15
    }

    override fun onUse(player: Player) {
        super.onUse(player)
        if (!player.level.isClientSide) {
            player.level.playSound(
                null,
                player,
                SoundRegister.ELECWANDOVERLOAD.get(),
                SoundSource.PLAYERS,
                1f,
                1f
            )
            player.getOACapability()?.let { cap ->
                player.getEntitiesAround(20.0, Mob::class.java).forEach {
                    it.addEffect(
                        MobEffectInstance(
                            EffectRegister.PARALYSIS.get(),
                            100,
                            when (cap.artsAdaptability.getLevel()) {
                                ValueLevel.FLAWED -> 0
                                ValueLevel.NORMAL -> 0
                                ValueLevel.STANDARD -> 0
                                ValueLevel.EXCELLENT -> 1
                                ValueLevel.OUTSTANDING -> 2
                                ValueLevel.UNKNOWN -> 3
                            }
                        )
                    )
                }
            }
            player.level.addFreshEntity(
                ParticleGeneratorField(
                    EntityRegister.PARTICLE_GENERATOR_FIELD.get(),
                    player.level,
                    player.position(),
                    this,
                    10,
                    20f,
                    10
                )
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

    override fun consumeExperienceValue(): Int = 1500

}