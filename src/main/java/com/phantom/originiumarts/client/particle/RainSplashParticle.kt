package com.phantom.originiumarts.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.SimpleParticleType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

class RainSplashParticle(
    world: ClientLevel,
    xIn: Double,
    yIn: Double,
    zIn: Double,
    val motionXIn: Double,
    val motionYIn: Double,
    val motionZIn: Double
) : TextureSheetParticle(world, xIn, yIn, zIn, 0.0, 0.0, 0.0) {

    init {
        xd *= 0.3
        yd = Math.random() * 0.2 + 0.1
        zd *= 0.3
        setSize(0.01f, 0.01f)
        gravity = 0.04f
        lifetime = (8.0 / (Math.random() * 0.8 + 0.2)).toInt()
        setColor(
            motionXIn.toFloat(),
            motionYIn.toFloat(),
            motionZIn.toFloat()
        )
    }


    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE
    }

    override fun tick() {
        xo = x
        yo = y
        zo = z
        if (lifetime-- <= 0) {
            this.remove()
        } else {
            yd -= gravity.toDouble()
            move(xd, yd, zd)
            xd *= 0.98
            yd *= 0.98
            zd *= 0.98
            if (onGround) {
                if (Math.random() < 0.5) {
                    this.remove()
                }
                xd *= 0.7
                zd *= 0.7
            }
            val blockPos = BlockPos(x, y, z)
            val d0 = level.getBlockState(blockPos).getCollisionShape(level, blockPos)
                .max(
                    Direction.Axis.Y, x - blockPos.x.toDouble(),
                    z - blockPos.z.toDouble()
                ).coerceAtLeast(level.getFluidState(blockPos).getHeight(level, blockPos).toDouble())
            if (d0 > 0.0 && y < blockPos.y.toDouble() + d0) {
                this.remove()
            }
        }
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
                val particle = RainSplashParticle(level, xIn, yIn, zIn, motionXIn, motionYIn, motionZIn)
                particle.pickSprite(sprite)
                return particle
            }
        }
    }

}