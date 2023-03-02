package com.phantom.originiumarts.common.weather

import com.phantom.originiumarts.client.hardLightMixColor
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.util.CubicSampler
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.BiomeDictionary
import kotlin.math.hypot


const val BASE_EFFECT_DISTANCE = 100.0
const val BASE_VISIBLE_DISTANCE = BASE_EFFECT_DISTANCE * 4

fun CatastropheClientData.getDistance(x: Double, z: Double): Double =
    hypot(posX - x, posZ - z)

fun CatastropheServerData.toClientData(): CatastropheClientData =
    CatastropheClientData(this.posX, this.posY, this.posZ, this.intensity, this.getTransition())

fun Vec3.getCatastropheSkyColorInClient(pos: Vec3, colorHandle: (value: Double) -> Double = { it }): Vec3 {
    val catastrophe = CatastropheClientManager.nearestCatastropheInfo
    return if (catastrophe != null) {
        val color = getCatastropheColorInClient(pos, catastrophe)
        val effect = CatastropheClientManager.catastropheEffect
        val mixColorR = hardLightMixColor(this.x, effect * color.x)
        val mixColorG = hardLightMixColor(this.y, effect * color.y)
        val mixColorB = hardLightMixColor(this.z, effect * color.z)
        val skyColorR = this.x * (1 - effect) + mixColorR * effect
        val skyColorG = this.y * (1 - effect) + mixColorG * effect
        val skyColorB = this.z * (1 - effect) + mixColorB * effect
        Vec3(colorHandle(skyColorR), colorHandle(skyColorG), colorHandle(skyColorB))
    } else {
        this
    }
}

fun getCatastropheColorInClient(
    pos: Vec3,
    catastrophe: CatastropheClientData,
    colorHandle: (value: Double) -> Double = { it }
): Vec3 {
    return Minecraft.getInstance().level?.let {
        val vec3: Vec3 = pos.subtract(2.0, 2.0, 2.0).scale(0.25)
        val color = CubicSampler.gaussianSampleVec3(vec3) { px: Int, py: Int, pz: Int ->
            CatastropheClientData.color(it.getBiome(BlockPos(px, 62, pz)).unwrapKey().get(), catastrophe.intensity)
        }
        Vec3(colorHandle(color.x), colorHandle(color.y), colorHandle(color.z))
    } ?: Vec3(0.5, 0.5, 0.5)
}

fun getAffectingCatastropheType(level: ClientLevel, pos: Vec3): CatastropheType {
    val clientData = CatastropheClientManager.nearestCatastropheInfo
    val blockPos = BlockPos(pos)
    val biome = level.getBiome(blockPos).value()
    val i = clientData?.intensity
    return if (BiomeDictionary.hasType(level.getBiome(blockPos).unwrapKey().get(), BiomeDictionary.Type.OVERWORLD)
        && i != null
        && CatastropheClientManager.catastropheEffect > 0
    ) {
        when {
            i >= 6 -> CatastropheType.METEOR_SHOWERS
            i >= 3 && biome.precipitation != Biome.Precipitation.NONE && biome.warmEnoughToRain(blockPos) -> CatastropheType.RAINSTORM
            i >= 3 && biome.precipitation != Biome.Precipitation.NONE && !biome.warmEnoughToRain(blockPos) -> CatastropheType.BLIZZARD
            i > 0 && biome.precipitation == Biome.Precipitation.NONE -> CatastropheType.BLIZZARD
            else -> CatastropheType.NULL
        }
    } else {
        CatastropheType.NULL
    }
}
