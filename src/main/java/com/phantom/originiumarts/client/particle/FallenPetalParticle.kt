package com.phantom.originiumarts.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.core.particles.SimpleParticleType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import kotlin.random.Random

class FallenPetalParticle(
    world: ClientLevel,
    xIn: Double,
    yIn: Double,
    zIn: Double,
    motionXIn: Double,
    motionYIn: Double,
    motionZIn: Double,
    sprites: SpriteSet
) : BaseLoopAnimationParticle(world, xIn, yIn, zIn, motionXIn, motionYIn, motionZIn, sprites) {

    init {
        lifetime = 120 + Random.nextInt(0, 120)
        gravity = 0.08f
    }

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
                val particle = FallenPetalParticle(level, xIn, yIn, zIn, motionXIn, motionYIn, motionZIn, sprite)
                particle.pickSprite(sprite)
                return particle
            }
        }
    }

}