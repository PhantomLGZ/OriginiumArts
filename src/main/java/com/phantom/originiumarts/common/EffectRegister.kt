package com.phantom.originiumarts.common

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.common.effect.AcuteOripathyMobEffect
import com.phantom.originiumarts.common.effect.GainExperienceMobEffect
import com.phantom.originiumarts.common.effect.HighSpeedChantMobEffect
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraftforge.common.ForgeMod
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import java.util.*

object EffectRegister {

    val EFFECTS: DeferredRegister<MobEffect> =
        DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, OriginiumArtsMod.MOD_ID)

    val ACUTE_ORIPATHY: RegistryObject<MobEffect> = EFFECTS.register("acute_oripathy") {
        AcuteOripathyMobEffect()
            .addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                UUID.nameUUIDFromBytes("acute_oripathy_speed_down".toByteArray()).toString(),
                -0.20,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            )
    }

    val GAIN_EXPERIENCE: RegistryObject<MobEffect> = EFFECTS.register("gain_experience") { GainExperienceMobEffect() }

    val HIGH_SPEED_CHANT: RegistryObject<MobEffect> = EFFECTS.register("high_speed_chant") { HighSpeedChantMobEffect() }

    val CORROSION: RegistryObject<MobEffect> = EFFECTS.register("corrosion") {
        object : MobEffect(MobEffectCategory.HARMFUL, 0x93C588) {}
            .addAttributeModifier(
                Attributes.ARMOR,
                UUID.nameUUIDFromBytes("corrosion_armor_down".toByteArray()).toString(),
                -0.40,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            )
    }

    val PARALYSIS: RegistryObject<MobEffect> = EFFECTS.register("paralysis") {
        object : MobEffect(MobEffectCategory.HARMFUL, 0xFFFEC3) {}
            .addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                UUID.nameUUIDFromBytes("paralysis_movement_speed_down".toByteArray()).toString(),
                -0.15,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            )
            .addAttributeModifier(
                Attributes.FLYING_SPEED,
                UUID.nameUUIDFromBytes("paralysis_flying_speed_down".toByteArray()).toString(),
                -0.15,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            )
            .addAttributeModifier(
                ForgeMod.SWIM_SPEED.get(),
                UUID.nameUUIDFromBytes("paralysis_swim_speed_down".toByteArray()).toString(),
                -0.15,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            )
    }

}