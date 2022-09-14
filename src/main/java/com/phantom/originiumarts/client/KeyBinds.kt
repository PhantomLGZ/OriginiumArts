package com.phantom.originiumarts.client

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraftforge.client.ClientRegistry

object KeyBinds {

    const val KEY_CATEGORY = "key.categories.originiumarts"
    const val KEY_CHANGE_ARTS = "key.originiumarts.change_arts"
    const val KEY_OPEN_ARTS_SCREEN = "key.originiumarts.open_arts_screen"

    val CHANGE_ARTS = KeyMapping(
        KEY_CHANGE_ARTS,
        InputConstants.KEY_GRAVE,
        KEY_CATEGORY
    )

    val OPEN_ARTS_SCREEN = KeyMapping(
        KEY_OPEN_ARTS_SCREEN,
        InputConstants.KEY_V,
        KEY_CATEGORY
    )

    fun register() {
        ClientRegistry.registerKeyBinding(CHANGE_ARTS)
        ClientRegistry.registerKeyBinding(OPEN_ARTS_SCREEN)
    }

}