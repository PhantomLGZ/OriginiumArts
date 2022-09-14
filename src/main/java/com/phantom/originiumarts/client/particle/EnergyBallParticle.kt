package com.phantom.originiumarts.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.core.particles.SimpleParticleType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import kotlin.random.Random

class EnergyBallParticle(
    world: ClientLevel,
    xIn: Double,
    yIn: Double,
    zIn: Double,
    private val centerX: Double,
    private val centerY: Double,
    private val centerZ: Double
) : TextureSheetParticle(world, xIn, yIn, zIn, centerX, centerY, centerZ) {

    init {
        lifetime = Random.nextInt(20, 40)
        gravity = 0f
        hasPhysics = false
    }

    private var rx = 0.0
    private var ry = 0.0
    private var rz = 0.0

    override fun tick() {
        xo = x
        yo = y
        zo = z
        rx += (Random.nextDouble() * 2 - 1) * 0.002
        ry += (Random.nextDouble() * 2 - 1) * 0.002
        rz += (Random.nextDouble() * 2 - 1) * 0.002
        xd = (centerX - x) / 16 + rx
        yd = (centerY - y) / 16 + ry
        zd = (centerZ - z) / 16 + rz
        if (age++ >= lifetime) {
            this.remove()
        } else {
            move(xd, yd, zd)
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
                centerXIn: Double,
                centerYIn: Double,
                centerZIn: Double
            ): Particle {
                val particle = EnergyBallParticle(level, xIn, yIn, zIn, centerXIn, centerYIn, centerZIn)
                particle.pickSprite(sprite)
                return particle
            }
        }
    }

}