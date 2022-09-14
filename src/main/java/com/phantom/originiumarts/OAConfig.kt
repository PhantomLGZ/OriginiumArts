package com.phantom.originiumarts

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig


object OAConfig {

    fun register() {
        ModLoadingContext.get().registerConfig(
            ModConfig.Type.COMMON,
            COMMON_BUILDER.build()
        )
    }

    private val COMMON_BUILDER: ForgeConfigSpec.Builder = ForgeConfigSpec.Builder()
        .comment("Settings for the Originium Arts")
        .push("general")

    val SIX_DIM_ATTR_MIN: ForgeConfigSpec.IntValue = COMMON_BUILDER
        .comment("The minimum value of the six-dimensional initial attribute")
        .defineInRange("attr_min", 0, 0, 100)
    val SIX_DIM_ATTR_MAX: ForgeConfigSpec.IntValue = COMMON_BUILDER
        .comment("The maximum value of the six-dimensional initial attribute")
        .defineInRange("attr_max", 40, 0, 100)
    val SIX_DIM_ATTR_RESET: ForgeConfigSpec.BooleanValue = COMMON_BUILDER
        .comment("Will capability reset on death")
        .define("capability_reset", false)

}