package com.phantom.originiumarts.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import com.phantom.originiumarts.common.capability.getOACapability
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object ArtsHud : GuiComponent() {

    private val width = Minecraft.getInstance().window.guiScaledWidth
    private val height = Minecraft.getInstance().window.guiScaledHeight

    fun render(
        stack: PoseStack,
        percent: Double = 0.0
    ) {
        val centerX = width / 2
        val centerY = height / 2
        val length = (width / 20.0).roundToInt()
        val size = max(1, (length / 20.0).roundToInt())
        val burden = Minecraft.getInstance().player?.getOACapability()?.getBurden() ?: 0.0
        stack.pushPose()
        fill(
            stack,
            (centerX - length + (length - size) * percent).roundToInt(),
            centerY + length,
            (centerX + length - (length - size) * percent).roundToInt(),
            centerY + length + size * 2,
            (Color.getHSBColor((0.33 - 0.33 * min(burden, 100.0) / 100).toFloat(), 1f, 1f).rgb + 0xFF000000).toInt()
        )
        stack.popPose()
    }

}