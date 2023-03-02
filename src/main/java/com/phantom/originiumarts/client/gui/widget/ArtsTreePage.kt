package com.phantom.originiumarts.client.gui.widget

import com.mojang.blaze3d.vertex.PoseStack
import com.phantom.originiumarts.common.arts.AbstractArts
import com.phantom.originiumarts.common.ArtsManager
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.common.network.OANetworking
import com.phantom.originiumarts.common.network.sendpack.OAPlayerLearnArtSendPack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Widget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.network.chat.Style
import kotlin.math.max
import kotlin.math.roundToInt

class ArtsTreePage(
    private val width: Int,
    private val height: Int,
    private val widgetSize: Double,
    private val startDragging: ArtsWidget.() -> Unit
) : GuiComponent(), Widget, GuiEventListener, NarratableEntry {

    private var scrollX = 0.0
    private var scrollY = 0.0

    private var maxX = 0.0
    private var maxY = 0.0
    private val startX = (width / 4.0).roundToInt()
    private val startY = (height / 4.0).roundToInt()

    private val font = Minecraft.getInstance().font

    var isDragging = false
    var isScrolling = false

    private val arts: List<ArtsWidget>

    init {
        val learnedArts = Minecraft.getInstance().player?.getOACapability()?.learnedArtIds
        arts = ArtsManager.ARTS_MAP.map {
            maxX = max(maxX, it.value.col * widgetSize * 1.5 - width / 2)
            maxY = max(maxY, it.value.row * widgetSize * 1.5 - height / 2)
            ArtsWidget(
                x = (widgetSize * 1.5 + it.value.col * widgetSize * 1.5 + startX).roundToInt(),
                y = (it.value.row * widgetSize * 1.5 + startY).roundToInt(),
                size = widgetSize,
                art = it.value.art,
                onClicked = {
                    Minecraft.getInstance().player?.let { player ->
                        if (art.learningUnmetConditions(player).isEmpty()) {
                            OANetworking.sendToServer(OAPlayerLearnArtSendPack(art.uniqueID))
                            isLearned = true
                        }
                    }
                }).apply {
                isLearned = learnedArts?.contains(it.key) ?: false
            }
        }
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        poseStack.pushPose()
        fill(
            poseStack,
            startX,
            startY,
            startX + width,
            startY + height,
            0x88aaaaaa.toInt()
        )
        arts.forEach {
            it.draw(
                poseStack,
                mouseX,
                mouseY,
                scrollX.roundToInt(),
                scrollY.roundToInt(),
                (startX..(startX + width)),
                (startY..(startY + height))
            )
        }
        arts.forEach {
            if (it.isMouseOver(mouseX.toDouble(), mouseY.toDouble())) {
                drawTooltip(poseStack, it.x - scrollX.roundToInt(), it.y - scrollY.roundToInt(), it.art)
                it.draw(
                    poseStack,
                    mouseX,
                    mouseY,
                    scrollX.roundToInt(),
                    scrollY.roundToInt()
                )
            }
        }
        poseStack.popPose()
    }

    private fun drawTooltip(poseStack: PoseStack, x: Int, y: Int, art: AbstractArts) {
        poseStack.pushPose()
        fill(poseStack, startX, startY, startX + width, startY + height, 0xCC000000.toInt())
        val offset = font.lineHeight
        var titleLine = 1
        drawString(
            poseStack,
            font,
            art.name,
            x + (1.1 * widgetSize).roundToInt(),
            y,
            0xFFFFFFFF.toInt()
        )
        Minecraft.getInstance().player?.let { player ->
            art.learningUnmetConditions(player).forEach {
                drawString(
                    poseStack,
                    font,
                    it,
                    x + (1.1 * widgetSize).roundToInt(),
                    y + offset * titleLine,
                    0xFFDD3333.toInt()
                )
                titleLine++
            }
        }
        val textStartY = max(y + 1.1 * widgetSize, y + 1.1 * titleLine * offset).roundToInt()
        Language.getInstance()
            .getVisualOrder(findOptimalLines(art.getDescription(), width * 2 / 5))
            .forEachIndexed { index, formattedText ->
                drawString(
                    poseStack,
                    font,
                    formattedText,
                    x,
                    textStartY + offset * index,
                    0xFFFFFFFF.toInt()
                )
            }
        poseStack.popPose()
    }

    private fun findOptimalLines(component: Component, width: Int): List<FormattedText> {
        val stringSplitter = Minecraft.getInstance().font.splitter
        return stringSplitter.splitLines(component, width, Style.EMPTY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        arts.forEach {
            if (it.mouseClicked(mouseX, mouseY, button)) {
                return true
            }
        }
        return false
    }

    override fun mouseDragged(
        mouseX: Double,
        mouseY: Double,
        flag: Int,
        dx: Double,
        dy: Double
    ): Boolean {
        if (isMouseOver(mouseX, mouseY) && !isScrolling && !isDragging) {
            arts.forEach {
                if (it.isMouseOver(mouseX, mouseY) && it.isLearned) {
                    isDragging = true
                    it.startDragging()
                    return false
                }
            }
        }
        if (isMouseOver(mouseX, mouseY) || isScrolling) {
            if (!isDragging) {
                isScrolling = true
                scrollX = (scrollX - dx).coerceIn(0.0..maxX)
                scrollY = (scrollY - dy).coerceIn(0.0..maxY)
                return true
            }
        }
        return false
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return mouseX >= startX
                && mouseY >= startY
                && mouseX <= startX + width
                && mouseY <= startY + height
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}

    override fun narrationPriority(): NarratableEntry.NarrationPriority {
        return NarratableEntry.NarrationPriority.NONE
    }

}