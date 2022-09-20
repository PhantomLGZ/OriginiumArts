package com.phantom.originiumarts.common

import com.phantom.originiumarts.OriginiumArtsMod
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object SoundRegister {

    val SOUNDS: DeferredRegister<SoundEvent> =
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, OriginiumArtsMod.MOD_ID)

    val E_AIM: RegistryObject<SoundEvent> = SOUNDS.register("enemy.aim") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "enemy.aim"))
    }

    val E_VEHICLE_N: RegistryObject<SoundEvent> = SOUNDS.register("enemy.vehicle_n") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "enemy.vehicle_n"))
    }

    val BERRYHEAL_S: RegistryObject<SoundEvent> = SOUNDS.register("player.berryheal_s") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "player.berryheal_s"))
    }

    val BOTTLE_N: RegistryObject<SoundEvent> = SOUNDS.register("player.bottle_n") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "player.bottle_n"))
    }

    val ELECWANDOVERLOAD: RegistryObject<SoundEvent> = SOUNDS.register("player.elecwandoverload") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "player.elecwandoverload"))
    }

    val ENERGYSHOT: RegistryObject<SoundEvent> = SOUNDS.register("player.energyshot") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "player.energyshot"))
    }

    val FIREMAG_N: RegistryObject<SoundEvent> = SOUNDS.register("player.firemag_n") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "player.firemag_n"))
    }

    val MAGIC_ICE: RegistryObject<SoundEvent> = SOUNDS.register("player.magic_ice") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "player.magic_ice"))
    }

    val MAGIC_N: RegistryObject<SoundEvent> = SOUNDS.register("player.magic_n") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "player.magic_n"))
    }

    val PAPER_S: RegistryObject<SoundEvent> = SOUNDS.register("player.paper_s") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "player.paper_s"))
    }

    val PARTICULATEBOOST: RegistryObject<SoundEvent> = SOUNDS.register("player.particulateboost") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "player.particulateboost"))
    }

    val PISTOL_N: RegistryObject<SoundEvent> = SOUNDS.register("player.pistol_n") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "player.pistol_n"))
    }

    val RECALLBELL: RegistryObject<SoundEvent> = SOUNDS.register("player.recallbell") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "player.recallbell"))
    }

    val SNAPPING: RegistryObject<SoundEvent> = SOUNDS.register("player.snapping") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "player.snapping"))
    }

    val CUREMECHA: RegistryObject<SoundEvent> = SOUNDS.register("curemecha") {
        SoundEvent(ResourceLocation(OriginiumArtsMod.MOD_ID, "curemecha"))
    }

}