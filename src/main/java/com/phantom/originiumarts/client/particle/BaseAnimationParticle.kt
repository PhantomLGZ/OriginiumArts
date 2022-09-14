package com.phantom.originiumarts.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle

open class BaseAnimationParticle(
    world: ClientLevel,
    xIn: Double,
    yIn: Double,
    zIn: Double,
    motionXIn: Double,
    motionYIn: Double,
    motionZIn: Double,
    private val sprites: SpriteSet
) : TextureSheetParticle(world, xIn, yIn, zIn, motionXIn, motionYIn, motionZIn) {

    override fun tick() {
        xo = x
        yo = y
        zo = z
        setSpriteFromAge(sprites)
        if (age++ >= lifetime) {
            remove()
        } else {
            yd -= 0.04 * gravity.toDouble()
            move(xd, yd, zd)
            xd *= friction.toDouble()
            yd *= friction.toDouble()
            zd *= friction.toDouble()
            if (onGround) {
                remove()
            }
        }
    }

    override fun getRenderType(): ParticleRenderType =
        ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT

}