package com.phantom.originiumarts.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle
import kotlin.random.Random

open class BaseLoopAnimationParticle(
    world: ClientLevel,
    xIn: Double,
    yIn: Double,
    zIn: Double,
    motionXIn: Double,
    motionYIn: Double,
    motionZIn: Double,
    private val sprites: SpriteSet
) : TextureSheetParticle(world, xIn, yIn, zIn, motionXIn, motionYIn, motionZIn) {

    private var tickCount = 0
    var spriteSize = 16

    init {
        age = Random.nextInt(0, lifetime)
    }

    override fun tick() {
        xo = x
        yo = y
        zo = z
        age += lifetime / spriteSize
        age %= lifetime
        if (!onGround) {
            setSpriteFromAge(sprites)
        }
        if (tickCount++ >= lifetime) {
            this.remove()
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

    override fun getRenderType(): ParticleRenderType =
        ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT

}