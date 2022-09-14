package com.phantom.originiumarts.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.core.particles.SimpleParticleType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import kotlin.math.abs
import kotlin.random.Random

class FragranceParticle(
    world: ClientLevel,
    xIn: Double,
    yIn: Double,
    zIn: Double,
    motionXIn: Double,
    motionYIn: Double,
    motionZIn: Double
) : TextureSheetParticle(world, xIn, yIn, zIn, motionXIn, motionYIn, motionZIn) {

    init {
        lifetime = 20 + Random.nextInt(0, 40)
        gravity = 0f
        quadSize = random.nextFloat() * 0.3f + 0.3f
        alpha = 0f
        hasPhysics = false
        setColor(
            (0.2157 + (random.nextDouble() - 0.5) * 0.05).toFloat(),
            (0.9176 + (random.nextDouble() - 0.5) * 0.05).toFloat(),
            (1.0 - random.nextDouble() * 0.02).toFloat()
        )
    }

    private var isFirst = true

    override fun tick() {
        super.tick()
        if (isFirst) {
            isFirst = false
            xd *= 0
            yd = abs(yd) * 0.1 + 0.02
            zd *= 0
        }
        if (age <= 10) {
            alpha = age / 10f
        } else if (lifetime - age < 10) {
            alpha = (lifetime - age) / 10f
        }
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
                val particle = FragranceParticle(level, xIn, yIn, zIn, motionXIn, motionYIn, motionZIn)
                particle.pickSprite(sprite)
                return particle
            }
        }
    }

}