package com.phantom.originiumarts.common

import com.phantom.originiumarts.entity.*
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.SpawnPlacements
import net.minecraft.world.level.biome.MobSpawnSettings
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraftforge.event.world.BiomeLoadingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod


@Mod.EventBusSubscriber
object CommonSetup {

    fun setup() {
        SpawnPlacements.register(
            EntityRegister.ORIGINIUM_SLUG.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            AbstractOriginiumSlug::checkOriginiumSlugSpawnRules
        )
        SpawnPlacements.register(
            EntityRegister.ACID_ORIGINIUM_SLUG.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            AbstractOriginiumSlug::checkOriginiumSlugSpawnRules
        )
        SpawnPlacements.register(
            EntityRegister.DRONE_MONSTER.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING,
            AbstractDrone::checkSpawnRules
        )
        SpawnPlacements.register(
            EntityRegister.RIOTER.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            AbstractRioter::checkSpawnRules
        )
        SpawnPlacements.register(
            EntityRegister.COCKTAIL_THROWER.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            AbstractRioter::checkSpawnRules
        )
    }

    @SubscribeEvent
    fun onBiomeLoadingEvent(event: BiomeLoadingEvent) {
        event.spawns.addSpawn(
            MobCategory.MONSTER,
            MobSpawnSettings.SpawnerData(EntityRegister.ORIGINIUM_SLUG.get(), 4, 4, 4)
        )
        event.spawns.addSpawn(
            MobCategory.MONSTER,
            MobSpawnSettings.SpawnerData(EntityRegister.ACID_ORIGINIUM_SLUG.get(), 2, 2, 2)
        )
        event.spawns.addSpawn(
            MobCategory.MONSTER,
            MobSpawnSettings.SpawnerData(EntityRegister.DRONE_MONSTER.get(), 4, 1, 4)
        )
        event.spawns.addSpawn(
            MobCategory.MONSTER,
            MobSpawnSettings.SpawnerData(EntityRegister.RIOTER.get(), 2, 2, 4)
        )
        event.spawns.addSpawn(
            MobCategory.MONSTER,
            MobSpawnSettings.SpawnerData(EntityRegister.COCKTAIL_THROWER.get(), 2, 1, 1)
        )
        event.generation.addFeature(
            GenerationStep.Decoration.UNDERGROUND_ORES,
            OrePlacementFeature.DEEPSLATE_ORIGINIUM_HOST_ROCK
        )
    }
}