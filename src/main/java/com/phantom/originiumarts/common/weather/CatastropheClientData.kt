package com.phantom.originiumarts.common.weather

import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.BiomeDictionary

/**
 * see [CatastropheServerData]
 */
data class CatastropheClientData(
    val posX: Double,
    val posY: Double,
    val posZ: Double,
    val intensity: Int,
    val transition: Float
) {
    companion object{
        fun color(biome: ResourceKey<Biome>, intensity: Int): Vec3 =
            when {
                intensity >= 6 -> // METEOR_SHOWERS
                    Vec3(0.60, 0.14, 0.10)
                BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY) -> // SANDSTORM
                    Vec3(0.36, 0.19, 0.12)
                intensity >= 3 && BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD) -> // BLIZZARD
                    Vec3(0.26, 0.19, 0.22)
                intensity >= 3 -> // RAINSTORM
                    Vec3(0.20, 0.05, 0.10)
                else -> //STORM
                    Vec3(0.20, 0.03, 0.03)
            }
    }
}
