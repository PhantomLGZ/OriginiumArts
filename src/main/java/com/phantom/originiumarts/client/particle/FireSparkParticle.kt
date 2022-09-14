package com.phantom.originiumarts.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

class FireSparkParticle(
    world: ClientLevel,
    xIn: Double,
    yIn: Double,
    zIn: Double,
    motionXIn: Double,
    motionYIn: Double,
    motionZIn: Double,
    sprites: SpriteSet
) : BaseAnimationParticle(world, xIn, yIn, zIn, motionXIn, motionYIn, motionZIn, sprites) {

    init {
        lifetime = 16
    }

    override fun getLightColor(value: Float): Int = 15728880

    companion object {
        @OnlyIn(Dist.CLIENT)
        class Factory(private val sprite: SpriteSet) : ParticleProvider<SimpleParticleType> {
            override fun createParticle(
                type: SimpleParticleType,
                level: ClientLevel,
                xIn: Double,
                yIn: Double,
                zIn: Double,
                motionXIn: Double,
                motionYIn: Double,
                motionZIn: Double
            ): Particle {
                val particle = FireSparkParticle(level, xIn, yIn, zIn, motionXIn, motionYIn, motionZIn, sprite)
                particle.setSpriteFromAge(sprite)
                return particle
            }
        }
    }

}