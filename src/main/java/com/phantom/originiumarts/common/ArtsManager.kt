package com.phantom.originiumarts.common

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.common.arts.*
import net.minecraft.resources.ResourceLocation

object ArtsManager {

    val ARTS_MAP = mutableMapOf(
        Pair(ArtIgnition.uniqueID, ArtInfo(ArtIgnition, 1, 1)),
        Pair(ArtSwirlingVortex.uniqueID, ArtInfo(ArtSwirlingVortex, 1, 2)),
        Pair(ArtDiffusionCurrent.uniqueID, ArtInfo(ArtDiffusionCurrent, 1, 3)),
        Pair(ArtMobileCombat.uniqueID, ArtInfo(ArtMobileCombat, 2, 1)),
        Pair(ArtTrick.uniqueID, ArtInfo(ArtTrick, 2, 2)),
        Pair(ArtFineBlending.uniqueID, ArtInfo(ArtFineBlending, 2, 3)),
        Pair(ArtEMP.uniqueID, ArtInfo(ArtEMP, 2, 4)),
        Pair(ArtZeroPointBurst.uniqueID, ArtInfo(ArtZeroPointBurst, 2, 5)),
        Pair(ArtScorchedEarth.uniqueID, ArtInfo(ArtScorchedEarth, 2, 6)),
        Pair(ArtHighSpeedChant.uniqueID, ArtInfo(ArtHighSpeedChant, 3, 1)),
        Pair(ArtAntiGravity.uniqueID, ArtInfo(ArtAntiGravity, 3, 2)),
        Pair(ArtGloriousShards.uniqueID, ArtInfo(ArtGloriousShards, 3, 3)),
    )

    data class ArtInfo(
        val art: AbstractArts,
        val row: Int,
        val col: Int
    )

    const val KEY_ITEMSTACK_ARTS = "key_itemstack_arts"

    val LOCKED_RESOURCE = ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/art/art_locked.png")

    fun Int.getArtById(): AbstractArts {
        return ARTS_MAP[this]?.art ?: ArtEmpty
    }

    fun Int.getArtInfoById(): ArtInfo? {
        return ARTS_MAP[this]
    }

}