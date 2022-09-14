package com.phantom.originiumarts.client.particle

import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Vector3f
import com.phantom.originiumarts.client.createByAlpha
import com.phantom.originiumarts.client.createByBeta
import com.phantom.originiumarts.client.createByGamma
import com.phantom.originiumarts.client.mul
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.Mth
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import kotlin.math.PI
import kotlin.random.Random

class PokerCardParticle(
    world: ClientLevel,
    xIn: Double,
    yIn: Double,
    zIn: Double,
    motionXIn: Double,
    motionYIn: Double,
    motionZIn: Double
) : TextureSheetParticle(world, xIn, yIn, zIn, motionXIn, motionYIn, motionZIn) {

    private var initVertexes: Array<Vector3f>
    private val rotationMatrix = createByGamma(PI / 180 * Random.nextDouble(1.0, 8.0))
        .mul(createByBeta(PI / 180 * Random.nextDouble(1.0, 8.0)))

    init {
        lifetime = Random.nextInt(100, 200)
        gravity = 0.24f

        val initMatrix = createByAlpha(Random.nextDouble(-PI, PI))
            .mul(createByBeta(Random.nextDouble(-PI, PI)))
            .mul(createByGamma(Random.nextDouble(-PI, PI)))

        initVertexes = arrayOf(
            Vector3f(-1.0f, -1.0f, 0.0f).mul(initMatrix),
            Vector3f(-1.0f, 1.0f, 0.0f).mul(initMatrix),
            Vector3f(1.0f, 1.0f, 0.0f).mul(initMatrix),
            Vector3f(1.0f, -1.0f, 0.0f).mul(initMatrix)
        )
    }

    override fun tick() {
        xo = x
        yo = y
        zo = z
        if (!onGround) {
            initVertexes = initVertexes.map {
                it.mul(rotationMatrix)
            }.toTypedArray()
        }
        if (age++ >= lifetime) {
            remove()
        } else {
            yd -= 0.04 * gravity.toDouble()
            move(xd, yd, zd)
            xd *= friction.toDouble()
            yd *= friction.toDouble()
            zd *= friction.toDouble()
            if (onGround) {
                xd *= 0.0
                zd *= 0.0
            }
        }
    }

    override fun render(vertexConsumer: VertexConsumer, camera: Camera, value: Float) {
        val vec3 = camera.position
        val f = (Mth.lerp(value.toDouble(), xo, x) - vec3.x()).toFloat()
        val f1 = (Mth.lerp(value.toDouble(), yo, y) - vec3.y()).toFloat()
        val f2 = (Mth.lerp(value.toDouble(), zo, z) - vec3.z()).toFloat()
        val avector3f = arrayOf(
            initVertexes[0].copy(),
            initVertexes[1].copy(),
            initVertexes[2].copy(),
            initVertexes[3].copy()
        )
        val f4 = getQuadSize(value)

        for (i in 0..3) {
            val vector3f = avector3f[i]
            vector3f.mul(f4)
            vector3f.add(f, f1, f2)
        }
        val f7 = this.u0
        val f8 = this.u1
        val f5 = this.v0
        val f6 = this.v1
        val j = getLightColor(value)
        vertexConsumer
            .vertex(avector3f[0].x().toDouble(), avector3f[0].y().toDouble(), avector3f[0].z().toDouble())
            .uv(f8, f6)
            .color(rCol, gCol, bCol, alpha)
            .uv2(j)
            .endVertex()
        vertexConsumer
            .vertex(avector3f[1].x().toDouble(), avector3f[1].y().toDouble(), avector3f[1].z().toDouble())
            .uv(f8, f5)
            .color(rCol, gCol, bCol, alpha)
            .uv2(j)
            .endVertex()
        vertexConsumer
            .vertex(avector3f[2].x().toDouble(), avector3f[2].y().toDouble(), avector3f[2].z().toDouble())
            .uv(f7, f5)
            .color(rCol, gCol, bCol, alpha)
            .uv2(j)
            .endVertex()
        vertexConsumer
            .vertex(avector3f[3].x().toDouble(), avector3f[3].y().toDouble(), avector3f[3].z().toDouble())
            .uv(f7, f6)
            .color(rCol, gCol, bCol, alpha)
            .uv2(j)
            .endVertex()
    }

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
                val particle = PokerCardParticle(level, xIn, yIn, zIn, motionXIn, motionYIn, motionZIn)
                particle.pickSprite(sprite)
                return particle
            }
        }
    }
}