package com.phantom.originiumarts.entity

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.field.FineBlendingField
import com.phantom.originiumarts.entity.field.GloriousShardsField
import com.phantom.originiumarts.entity.field.ParticleGeneratorField
import com.phantom.originiumarts.entity.field.SwirlingVortexField
import com.phantom.originiumarts.entity.model.*
import com.phantom.originiumarts.entity.projectile.*
import com.phantom.originiumarts.entity.renderer.*
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.minecraftforge.event.entity.EntityAttributeCreationEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject


@Mod.EventBusSubscriber(modid = OriginiumArtsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object EntityRegister {

    val ENTITY_TYPE: DeferredRegister<EntityType<*>> =
        DeferredRegister.create(ForgeRegistries.ENTITIES, OriginiumArtsMod.MOD_ID)

    val ORIGINIUM_SLUG: RegistryObject<EntityType<OriginiumSlug>> = ENTITY_TYPE.register("originium_slug") {
        EntityType.Builder.of({ type, worldLevel ->
            OriginiumSlug(type, worldLevel)
        }, MobCategory.MONSTER)
            .sized(0.6f, 0.4f)
            .clientTrackingRange(8)
            .setShouldReceiveVelocityUpdates(false)
            .build("originium_slug")
    }

    val ACID_ORIGINIUM_SLUG: RegistryObject<EntityType<AcidOriginiumSlug>> =
        ENTITY_TYPE.register("acid_originium_slug") {
            EntityType.Builder.of({ type, worldLevel ->
                AcidOriginiumSlug(type, worldLevel)
            }, MobCategory.MONSTER)
                .sized(0.6f, 0.4f)
                .clientTrackingRange(8)
                .setShouldReceiveVelocityUpdates(false)
                .build("acid_originium_slug")
        }

    val DRONE_MONSTER: RegistryObject<EntityType<DroneMonster>> = ENTITY_TYPE.register("drone_monster") {
        EntityType.Builder.of({ type, worldLevel ->
            DroneMonster(type, worldLevel)
        }, MobCategory.MONSTER)
            .sized(1.0f, 0.6f)
            .clientTrackingRange(8)
            .setShouldReceiveVelocityUpdates(false)
            .build("drone_monster")
    }

    val RIOTER: RegistryObject<EntityType<AbstractRioter>> = ENTITY_TYPE.register("rioter") {
        EntityType.Builder.of({ type, worldLevel ->
            Rioter(type, worldLevel)
        }, MobCategory.MONSTER)
            .sized(0.8f, 2.0f)
            .clientTrackingRange(8)
            .setShouldReceiveVelocityUpdates(false)
            .build("rioter")
    }

    val COCKTAIL_THROWER: RegistryObject<EntityType<AbstractRioter>> = ENTITY_TYPE.register("cocktail_thrower") {
        EntityType.Builder.of({ type, worldLevel ->
            CocktailThrower(type, worldLevel)
        }, MobCategory.MONSTER)
            .sized(0.8f, 2.0f)
            .clientTrackingRange(8)
            .setShouldReceiveVelocityUpdates(false)
            .build("cocktail_thrower")
    }

    val RAY_ENTITY: RegistryObject<EntityType<RayEntity>> = ENTITY_TYPE.register("ray_entity") {
        EntityType.Builder.of({ type, worldLevel ->
            RayEntity(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.1f, 0.1f)
            .build("ray_entity")
    }

    // Projectile
    val ETCHED_AMMO: RegistryObject<EntityType<EtchedAmmo>> = ENTITY_TYPE.register("etched_ammo") {
        EntityType.Builder.of({ type, worldLevel ->
            EtchedAmmo(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.25f, 0.25f)
            .build("etched_ammo")
    }

    val ART_BALL: RegistryObject<EntityType<ArtBall>> = ENTITY_TYPE.register("art_ball") {
        EntityType.Builder.of({ type, worldLevel ->
            ArtBall(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .build("art_ball")
    }

    val FLAME_BALL: RegistryObject<EntityType<ArtBall>> = ENTITY_TYPE.register("flame_ball") {
        EntityType.Builder.of({ type, worldLevel ->
            ArtBall(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.8f, 0.8f)
            .build("flame_ball")
    }

    val ORIGINIUM_ACID: RegistryObject<EntityType<OriginiumAcid>> = ENTITY_TYPE.register("originium_acid") {
        EntityType.Builder.of({ type, worldLevel ->
            OriginiumAcid(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.2f, 0.2f)
            .build("originium_acid")
    }

    val COCKTAIL: RegistryObject<EntityType<CocktailEntity>> = ENTITY_TYPE.register("cocktail") {
        EntityType.Builder.of({ type, worldLevel ->
            CocktailEntity(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.2f, 0.4f)
            .build("cocktail")
    }

    val METEOR: RegistryObject<EntityType<Meteor>> = ENTITY_TYPE.register("meteor") {
        EntityType.Builder.of({ type, worldLevel ->
            Meteor(type, worldLevel)
        }, MobCategory.MISC)
            .sized(2f, 2f)
            .clientTrackingRange(200)
            .setTrackingRange(200)
            .build("meteor")
    }

    // Field
    val FINE_BLENDING_FIELD: RegistryObject<EntityType<FineBlendingField>> =
        ENTITY_TYPE.register("fine_blending_field") {
            EntityType.Builder.of({ type, worldLevel ->
                FineBlendingField(type, worldLevel)
            }, MobCategory.MISC)
                .sized(0.1f, 0.1f)
                .build("fine_blending_field")
        }

    val SWIRLING_VORTEX_FIELD: RegistryObject<EntityType<SwirlingVortexField>> =
        ENTITY_TYPE.register("swirling_vortex_field") {
            EntityType.Builder.of({ type, worldLevel ->
                SwirlingVortexField(type, worldLevel)
            }, MobCategory.MISC)
                .sized(0.1f, 0.1f)
                .build("swirling_vortex_field")
        }

    val GLORIOUS_SHARDS_FIELD: RegistryObject<EntityType<GloriousShardsField>> =
        ENTITY_TYPE.register("glorious_shards_field") {
            EntityType.Builder.of({ type, worldLevel ->
                GloriousShardsField(type, worldLevel)
            }, MobCategory.MISC)
                .sized(0.1f, 0.1f)
                .build("glorious_shards_field")
        }

    val PARTICLE_GENERATOR_FIELD: RegistryObject<EntityType<ParticleGeneratorField>> =
        ENTITY_TYPE.register("particle_generator_field") {
            EntityType.Builder.of({ type, worldLevel ->
                ParticleGeneratorField(type, worldLevel)
            }, MobCategory.MISC)
                .sized(0.1f, 0.1f)
                .build("particle_generator_field")
        }

    @SubscribeEvent
    fun onAttributeCreate(event: EntityAttributeCreationEvent) {
        event.put(ORIGINIUM_SLUG.get(), OriginiumSlug.prepareAttributes().build())
        event.put(ACID_ORIGINIUM_SLUG.get(), AcidOriginiumSlug.prepareAttributes().build())
        event.put(DRONE_MONSTER.get(), DroneMonster.prepareAttributes().build())
        event.put(RIOTER.get(), AbstractRioter.prepareAttributes().build())
        event.put(COCKTAIL_THROWER.get(), AbstractRioter.prepareAttributes().build())
    }

    @SubscribeEvent
    fun onRegisterLayers(event: RegisterLayerDefinitions) {
        event.registerLayerDefinition(OriginiumSlugModel.LAYER, OriginiumSlugModel::createBodyLayer)
        event.registerLayerDefinition(AcidOriginiumSlugModel.LAYER, AcidOriginiumSlugModel::createBodyLayer)
        event.registerLayerDefinition(DroneMonsterModel.LAYER, DroneMonsterModel::createBodyLayer)
        event.registerLayerDefinition(RioterModel.LAYER, RioterModel::createBodyLayer)
        event.registerLayerDefinition(EtchedAmmoModel.LAYER, EtchedAmmoModel::createBodyLayer)
        event.registerLayerDefinition(MeteorModel.LAYER, MeteorModel::createBodyLayer)
    }

    @SubscribeEvent
    fun onRegisterRenderer(event: RegisterRenderers) {
        event.registerEntityRenderer(ORIGINIUM_SLUG.get()) { OriginiumSlugRenderer(it) }
        event.registerEntityRenderer(ACID_ORIGINIUM_SLUG.get()) { AcidOriginiumSlugRenderer(it) }
        event.registerEntityRenderer(DRONE_MONSTER.get()) { DroneMonsterRenderer(it) }
        event.registerEntityRenderer(RIOTER.get()) { RioterRenderer(it) }
        event.registerEntityRenderer(COCKTAIL_THROWER.get()) { RioterRenderer(it) }
        event.registerEntityRenderer(RAY_ENTITY.get()) { RayEntityRenderer(it) }
        event.registerEntityRenderer(ETCHED_AMMO.get()) { EtchedAmmoRenderer(it) }
        event.registerEntityRenderer(ART_BALL.get()) { ArtBallRenderer(it) }
        event.registerEntityRenderer(FLAME_BALL.get()) { ArtBallRenderer(it) }
        event.registerEntityRenderer(ORIGINIUM_ACID.get()) { ItemProjectileRenderer(it) }
        event.registerEntityRenderer(COCKTAIL.get()) { ItemProjectileRenderer(it) }
        event.registerEntityRenderer(METEOR.get()) { MeteorRenderer(it) }
        event.registerEntityRenderer(FINE_BLENDING_FIELD.get()) { BlankEntityRenderer(it) }
        event.registerEntityRenderer(SWIRLING_VORTEX_FIELD.get()) { BlankEntityRenderer(it) }
        event.registerEntityRenderer(GLORIOUS_SHARDS_FIELD.get()) { BlankEntityRenderer(it) }
        event.registerEntityRenderer(PARTICLE_GENERATOR_FIELD.get()) { BlankEntityRenderer(it) }
    }

}