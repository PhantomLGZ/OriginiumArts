package com.phantom.originiumarts.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.core.particles.SimpleParticleType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import kotlin.random.Random

class PhotosphereParticle(
    world: ClientLevel,
    xIn: Double,
    yIn: Double,
    zIn: Double,
    motionXIn: Double,
    motionYIn: Double,
    motionZIn: Double
) : TextureSheetParticle(world, xIn, yIn, zIn, motionXIn, motionYIn, motionZIn) {

    init {
        lifetime = Random.nextInt(10, 60)
        gravity = -0.08f
        hasPhysics = false
    }

    override fun getLightColor(value: Float): Int = 15728880

    override fun getRenderType(): ParticleRenderType =
        ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT

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
                val particle = PhotosphereParticle(level, xIn, yIn, zIn, motionXIn, motionYIn, motionZIn)
                particle.pickSprite(sprite)
                return particle
            }
        }
    }

}