package com.phantom.originiumarts.common

import com.phantom.originiumarts.block.BlockRegister
import net.minecraft.core.Holder
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.data.worldgen.features.OreFeatures
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.levelgen.placement.*

object OrePlacementFeature {

    val DEEPSLATE_ORIGINIUM_HOST_ROCK: Holder<PlacedFeature> = PlacementUtils.register(
        "deepslate_originium_host_rock",
        FeatureUtils.register(
            "deepslate_originium_host_rock",
            Feature.ORE,
            OreConfiguration(
                OreFeatures.DEEPSLATE_ORE_REPLACEABLES,
                BlockRegister.ORIGINIUM_HOST_ROCK.get().defaultBlockState(),
                5
            )
        ),
        commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-60), VerticalAnchor.belowTop(0)))
    )

    val ORIGINIUM_HOST_ROCK: Holder<PlacedFeature> = PlacementUtils.register(
        "originium_host_rock",
        FeatureUtils.register(
            "originium_host_rock",
            Feature.ORE,
            OreConfiguration(
                OreFeatures.STONE_ORE_REPLACEABLES,
                BlockRegister.ORIGINIUM_HOST_ROCK.get().defaultBlockState(),
                2
            )
        ),
        commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-60), VerticalAnchor.belowTop(0)))
    )

    private fun orePlacement(modifier1: PlacementModifier, modifier2: PlacementModifier): List<PlacementModifier> {
        return listOf(modifier1, InSquarePlacement.spread(), modifier2, BiomeFilter.biome())
    }

    private fun commonOrePlacement(pCount: Int, pHeightRange: PlacementModifier): List<PlacementModifier> {
        return orePlacement(CountPlacement.of(pCount), pHeightRange)
    }

    private fun rareOrePlacement(pChance: Int, pHeightRange: PlacementModifier): List<PlacementModifier> {
        return orePlacement(RarityFilter.onAverageOnceEvery(pChance), pHeightRange)
    }

}