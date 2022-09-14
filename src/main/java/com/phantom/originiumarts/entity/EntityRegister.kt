package com.phantom.originiumarts.entity

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.field.FineBlendingField
import com.phantom.originiumarts.entity.field.GloriousShardsField
import com.phantom.originiumarts.entity.field.ParticleGeneratorField
import com.phantom.originiumarts.entity.field.SwirlingVortexField
import com.phantom.originiumarts.entity.model.AcidOriginiumSlugModel
import com.phantom.originiumarts.entity.model.EtchedAmmoModel
import com.phantom.originiumarts.entity.model.OriginiumSlugModel
import com.phantom.originiumarts.entity.projectile.ArtBall
import com.phantom.originiumarts.entity.projectile.EtchedAmmo
import com.phantom.originiumarts.entity.projectile.OriginiumAcid
import com.phantom.originiumarts.entity.randerer.*
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.minecraftforge.event.entity.EntityAttributeCreationEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries


@Mod.EventBusSubscriber(modid = OriginiumArtsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object EntityRegister {

    val ENTITY_TYPE = DeferredRegister.create(ForgeRegistries.ENTITIES, OriginiumArtsMod.MOD_ID)

    val ORIGINIUM_SLUG = ENTITY_TYPE.register("originium_slug") {
        EntityType.Builder.of({ type, worldLevel ->
            OriginiumSlug(type, worldLevel)
        }, MobCategory.MONSTER)
            .sized(0.6f, 0.4f)
            .clientTrackingRange(8)
            .setShouldReceiveVelocityUpdates(false)
            .build("originium_slug")
    }

    val ACID_ORIGINIUM_SLUG = ENTITY_TYPE.register("acid_originium_slug") {
        EntityType.Builder.of({ type, worldLevel ->
            AcidOriginiumSlug(type, worldLevel)
        }, MobCategory.MONSTER)
            .sized(0.6f, 0.4f)
            .clientTrackingRange(8)
            .setShouldReceiveVelocityUpdates(false)
            .build("acid_originium_slug")
    }

    val RAY_ENTITY = ENTITY_TYPE.register("ray_entity") {
        EntityType.Builder.of({ type, worldLevel ->
            RayEntity(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.1f, 0.1f)
            .clientTrackingRange(4)
            .build("ray_entity")
    }

    // Projectile
    val ETCHED_AMMO = ENTITY_TYPE.register("etched_ammo") {
        EntityType.Builder.of({ type, worldLevel ->
            EtchedAmmo(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.25f, 0.25f)
            .clientTrackingRange(4)
            .build("etched_ammo")
    }

    val ART_BALL = ENTITY_TYPE.register("art_ball") {
        EntityType.Builder.of({ type, worldLevel ->
            ArtBall(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .clientTrackingRange(4)
            .build("art_ball")
    }

    val FLAME_BALL = ENTITY_TYPE.register("flame_ball") {
        EntityType.Builder.of({ type, worldLevel ->
            ArtBall(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.8f, 0.8f)
            .clientTrackingRange(4)
            .build("flame_ball")
    }

    val ORIGINIUM_ACID = ENTITY_TYPE.register("originium_acid") {
        EntityType.Builder.of({ type, worldLevel ->
            OriginiumAcid(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.2f, 0.2f)
            .clientTrackingRange(4)
            .build("originium_acid")
    }

    // Field
    val FINE_BLENDING_FIELD = ENTITY_TYPE.register("fine_blending_field") {
        EntityType.Builder.of({ type, worldLevel ->
            FineBlendingField(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.1f, 0.1f)
            .clientTrackingRange(4)
            .build("fine_blending_field")
    }

    val SWIRLING_VORTEX_FIELD = ENTITY_TYPE.register("swirling_vortex_field") {
        EntityType.Builder.of({ type, worldLevel ->
            SwirlingVortexField(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.1f, 0.1f)
            .clientTrackingRange(4)
            .build("swirling_vortex_field")
    }

    val GLORIOUS_SHARDS_FIELD = ENTITY_TYPE.register("glorious_shards_field") {
        EntityType.Builder.of({ type, worldLevel ->
            GloriousShardsField(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.1f, 0.1f)
            .clientTrackingRange(4)
            .build("glorious_shards_field")
    }

    val PARTICLE_GENERATOR_FIELD = ENTITY_TYPE.register("particle_generator_field") {
        EntityType.Builder.of({ type, worldLevel ->
            ParticleGeneratorField(type, worldLevel)
        }, MobCategory.MISC)
            .sized(0.1f, 0.1f)
            .clientTrackingRange(4)
            .build("particle_generator_field")
    }

    @SubscribeEvent
    fun onAttributeCreate(event: EntityAttributeCreationEvent) {
        event.put(ORIGINIUM_SLUG.get(), OriginiumSlug.prepareAttributes().build())
        event.put(ACID_ORIGINIUM_SLUG.get(), AcidOriginiumSlug.prepareAttributes().build())
    }

    @SubscribeEvent
    fun onRegisterLayers(event: RegisterLayerDefinitions) {
        event.registerLayerDefinition(OriginiumSlugModel.LAYER, OriginiumSlugModel::createBodyLayer)
        event.registerLayerDefinition(AcidOriginiumSlugModel.LAYER, AcidOriginiumSlugModel::createBodyLayer)
        event.registerLayerDefinition(EtchedAmmoModel.LAYER, EtchedAmmoModel::createBodyLayer)
    }

    @SubscribeEvent
    fun onRegisterRenderer(event: RegisterRenderers) {
        event.registerEntityRenderer(ORIGINIUM_SLUG.get()) { OriginiumSlugRenderer(it) }
        event.registerEntityRenderer(ACID_ORIGINIUM_SLUG.get()) { AcidOriginiumSlugRenderer(it) }
        event.registerEntityRenderer(RAY_ENTITY.get()) { RayEntityRenderer(it) }
        event.registerEntityRenderer(ETCHED_AMMO.get()) { EtchedAmmoRenderer(it) }
        event.registerEntityRenderer(ART_BALL.get()) { ArtBallRenderer(it) }
        event.registerEntityRenderer(FLAME_BALL.get()) { ArtBallRenderer(it) }
        event.registerEntityRenderer(ORIGINIUM_ACID.get()) { OriginiumAcidRenderer(it) }
        event.registerEntityRenderer(FINE_BLENDING_FIELD.get()) { BaseFieldRenderer(it) }
        event.registerEntityRenderer(SWIRLING_VORTEX_FIELD.get()) { BaseFieldRenderer(it) }
        event.registerEntityRenderer(GLORIOUS_SHARDS_FIELD.get()) { BaseFieldRenderer(it) }
        event.registerEntityRenderer(PARTICLE_GENERATOR_FIELD.get()) { BaseFieldRenderer(it) }
    }

}