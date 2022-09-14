package com.phantom.originiumarts.client.gui.widget

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.phantom.originiumarts.common.arts.AbstractArts
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Widget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.sounds.SoundManager
import net.minecraft.sounds.SoundEvents
import kotlin.math.max
import kotlin.math.roundToInt


class ArtsWidget(
    val x: Int,
    val y: Int,
    private val size: Double,
    var art: AbstractArts,
    val slotCount: Int = -1,
    private val onClicked: ArtsWidget.() -> Unit = {}
) : GuiComponent(), Widget, GuiEventListener, NarratableEntry {

    private val scale = (size / 260.0).toFloat()

    private var _scrollX = 0
    private var _scrollY = 0

    var isLearned = false
    var isFromItem = false
    var isSlot = slotCount != -1

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        draw(poseStack, mouseX, mouseY)
    }

    fun draw(
        poseStack: PoseStack,
        mouseX: Int,
        mouseY: Int,
        scrollX: Int = 0,
        scrollY: Int = 0,
        xRange: IntRange = (Int.MIN_VALUE..Int.MAX_VALUE),
        yRange: IntRange = (Int.MIN_VALUE..Int.MAX_VALUE)
    ) {
        _scrollX = scrollX
        _scrollY = scrollY
        poseStack.pushPose()
        poseStack.scale(scale, scale, 0f)
        when {
            isLearned -> 1f
            isMouseOver(mouseX.toDouble(), mouseY.toDouble()) -> 0.6f
            else -> 0.2f
        }.let {
            RenderSystem.setShaderColor(it, it, it, 1.0f)
        }
        RenderSystem.setShaderTexture(0, art.getIconResource())
        val posX = x - scrollX
        val posY = y - scrollY
        val pTransX = when {
            posX < xRange.first -> {
                ((xRange.first - posX) / scale).roundToInt().coerceIn(0..260)
            }
            else -> 0
        }
        val pTransY = when {
            posY < yRange.first -> {
                ((yRange.first - posY) / scale).roundToInt().coerceIn(0..260)
            }
            else -> 0
        }
        val pWidth = when {
            posX < xRange.first -> {
                260 - ((xRange.first - posX) / scale).roundToInt().coerceIn(0..260)
            }
            posX + size > xRange.last -> {
                260 - ((posX + size - xRange.last) / scale).roundToInt().coerceIn(0..260)
            }
            else -> 260
        }
        val pHeight = when {
            posY < yRange.first -> {
                260 - ((yRange.first - posY) / scale).roundToInt().coerceIn(0..260)
            }
            posY + size > yRange.last -> {
                260 - ((posY + size - yRange.last) / scale).roundToInt().coerceIn(0..260)
            }
            else -> 260
        }
        blit(
            poseStack,
            (max(posX, xRange.first) / scale).roundToInt(),
            (max(posY, yRange.first) / scale).roundToInt(),
            pTransX,
            pTransY,
            pWidth,
            pHeight
        )
        poseStack.popPose()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, flag: Int): Boolean {
        return if (!isLearned && isMouseOver(mouseX, mouseY) && isValidClickButton(flag)) {
            playDownSound(Minecraft.getInstance().soundManager)
            onClicked()
            true
        } else {
            false
        }
    }

    private fun playDownSound(soundManager: SoundManager) {
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return (mouseX + _scrollX) in (x.toDouble()..(x + size))
                && (mouseY + _scrollY) in (y.toDouble()..(y + size))
    }

    private fun isValidClickButton(flag: Int): Boolean {
        return flag == 0
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}

    override fun narrationPriority(): NarratableEntry.NarrationPriority {
        return NarratableEntry.NarrationPriority.NONE
    }
}