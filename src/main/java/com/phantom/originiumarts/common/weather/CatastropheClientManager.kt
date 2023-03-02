package com.phantom.originiumarts.common.weather

import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.pow

object CatastropheClientManager {

    var catastropheInfoList = listOf<CatastropheClientData>()
    var nearestCatastropheInfo: CatastropheClientData? = null
    var catastropheEffect = 0.0
    var catastropheNextEffect = 0.0
    var fogColorVec3 = Vec3(1.0, 1.0, 1.0)

    fun tick() {
        if ((catastropheNextEffect - catastropheEffect).absoluteValue > 0.01) {
            catastropheEffect += (catastropheNextEffect - catastropheEffect) * 0.05
        } else {
            catastropheEffect = catastropheNextEffect
        }
    }

    fun update() {
        Minecraft.getInstance().player?.let {
            catastropheNextEffect = getNearestEffectIntensity(it.x, it.z)
        }
    }

    fun getNearestEffectIntensity(x: Double, z: Double): Double {
        return getNearestCatastrophe(x, z)?.let {
            nearestCatastropheInfo = it
            val distance = it.getDistance(x, z)
            if (distance < BASE_EFFECT_DISTANCE) calculateEffectIntensity(distance) else 0.0
        } ?: 0.0
    }

    fun calculateEffectIntensity(distance: Double): Double =
        max(0.0, 1 - (distance / BASE_EFFECT_DISTANCE).pow(4))

    fun getNearestCatastrophe(x: Double, z: Double): CatastropheClientData? {
        return catastropheInfoList.minByOrNull { (it.posX - x).pow(2) + (it.posZ - z).pow(2) }
    }

}