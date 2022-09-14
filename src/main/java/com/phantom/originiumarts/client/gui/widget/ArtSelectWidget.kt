package com.phantom.originiumarts.client.gui.widget

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.phantom.originiumarts.client.isWithinAngularRange
import com.phantom.originiumarts.client.normAngle
import com.phantom.originiumarts.common.arts.AbstractArts
import com.phantom.originiumarts.common.arts.ArtEmpty
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Widget
import kotlin.math.*

class ArtSelectWidget(
    baseAngle: Double,
    private val centerX: Int,
    private val centerY: Int,
    width: Int,
    val art: AbstractArts = ArtEmpty
) : GuiComponent(), Widget {

    private val length = width / 6.0
    private val scale = (width / 16.0 / 260.0).toFloat()

    private val normAngle = baseAngle.normAngle()
    private val x = centerX / scale + length / scale * cos(normAngle) - 130
    private val y = centerY / scale + length / scale * sin(normAngle) - 130

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        poseStack.pushPose()
        poseStack.scale(scale, scale, 0f)
        when {
            isMouseOver(mouseX.toDouble(), mouseY.toDouble()) -> 1.0f
            else -> 0.5f
        }.let {
            RenderSystem.setShaderColor(it, it, it, 1.0f)
        }
        RenderSystem.setShaderTexture(0, art.getIconResource())
        blit(
            poseStack,
            x.roundToInt(),
            y.roundToInt(),
            0,
            0,
            260,
            260
        )
        poseStack.popPose()
    }

    fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        val dx = mouseX - centerX
        val dy = mouseY - centerY
        return hypot(dx, dy) >= length / 5 &&
                normAngle.isWithinAngularRange(PI / 10, dx, dy)
    }
}