package com.phantom.originiumarts.client

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.client.particle.*
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ParticleRegister {

    val PARTICLE_TYPES: DeferredRegister<ParticleType<*>> =
        DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, OriginiumArtsMod.MOD_ID)

    val FALLEN_PETAL: RegistryObject<SimpleParticleType> = PARTICLE_TYPES.register("fallen_petal") {
        SimpleParticleType(false)
    }

    val FALLEN_LEAVE: RegistryObject<SimpleParticleType> = PARTICLE_TYPES.register("fallen_leave") {
        SimpleParticleType(false)
    }

    val COLORED_PAPER: RegistryObject<SimpleParticleType> = PARTICLE_TYPES.register("colored_paper") {
        SimpleParticleType(false)
    }

    val ENERGY_BALL: RegistryObject<SimpleParticleType> = PARTICLE_TYPES.register("energy_ball") {
        SimpleParticleType(false)
    }

    val LIGHTNING_SPARK: RegistryObject<SimpleParticleType> = PARTICLE_TYPES.register("lightning_spark") {
        SimpleParticleType(false)
    }

    val FIRE_SPARK: RegistryObject<SimpleParticleType> = PARTICLE_TYPES.register("fire_spark") {
        SimpleParticleType(false)
    }

    val PHOTOSPHERE: RegistryObject<SimpleParticleType> = PARTICLE_TYPES.register("photosphere") {
        SimpleParticleType(false)
    }

    val POKER_CARD: RegistryObject<SimpleParticleType> = PARTICLE_TYPES.register("poker_card") {
        SimpleParticleType(false)
    }

    val FRAGRANCE: RegistryObject<SimpleParticleType> = PARTICLE_TYPES.register("fragrance") {
        SimpleParticleType(false)
    }

    val RAIN_SPLASH: RegistryObject<SimpleParticleType> = PARTICLE_TYPES.register("rain_splash") {
        SimpleParticleType(false)
    }

    @SubscribeEvent
    fun onParticleFactoryRegistration(event: ParticleFactoryRegisterEvent) {
        Minecraft.getInstance().particleEngine.let {
            it.register(FALLEN_PETAL.get()) { spriteSet ->
                FallenPetalParticle.Companion.Factory(spriteSet)
            }
            it.register(FALLEN_LEAVE.get()) { spriteSet ->
                FallenLeaveParticle.Companion.Factory(spriteSet)
            }
            it.register(COLORED_PAPER.get()) { spriteSet ->
                ColoredPaperParticle.Companion.Factory(spriteSet)
            }
            it.register(ENERGY_BALL.get()) { spriteSet ->
                EnergyBallParticle.Companion.Factory(spriteSet)
            }
            it.register(LIGHTNING_SPARK.get()) { spriteSet ->
                LightningSparkParticle.Companion.Factory(spriteSet)
            }
            it.register(FIRE_SPARK.get()) { spriteSet ->
                FireSparkParticle.Companion.Factory(spriteSet)
            }
            it.register(PHOTOSPHERE.get()) { spriteSet ->
                PhotosphereParticle.Companion.Factory(spriteSet)
            }
            it.register(POKER_CARD.get()) { spriteSet ->
                PokerCardParticle.Companion.Factory(spriteSet)
            }
            it.register(FRAGRANCE.get()) { spriteSet ->
                FragranceParticle.Companion.Factory(spriteSet)
            }
            it.register(RAIN_SPLASH.get()) { spriteSet ->
                RainSplashParticle.Companion.Factory(spriteSet)
            }
        }
    }

}