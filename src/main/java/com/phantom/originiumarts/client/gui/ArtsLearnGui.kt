package com.phantom.originiumarts.client.gui

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.phantom.originiumarts.client.gui.widget.ArtsTreePage
import com.phantom.originiumarts.client.gui.widget.ArtsWidget
import com.phantom.originiumarts.common.arts.ArtEmpty
import com.phantom.originiumarts.common.ArtsManager
import com.phantom.originiumarts.common.ArtsManager.getArtById
import com.phantom.originiumarts.common.network.OANetworking
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.item.ArtsUnitItem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.world.InteractionHand
import kotlin.math.roundToInt

class ArtsLearnGui : Screen(TranslatableComponent(KEY_ARTS_LEARN_GUI)) {

    private var draggingArtWidget: ArtsWidget? = null

    private var artsPage: ArtsTreePage? = null

    private var itemArtSlotIds = IntArray(0)
    private var playerArtSlotIds = IntArray(0)

    private var widgetWidth = 0.0
    private var scale = 0f

    override fun init() {
        super.init()
        widgetWidth = width / 16.0
        scale = (widgetWidth / 260).toFloat()

        val player = Minecraft.getInstance().player
        val itemStack = player?.getItemInHand(InteractionHand.MAIN_HAND)
        val item = itemStack?.item
        if (item is ArtsUnitItem) {
            val savedIds = itemStack.getOrCreateTag()
                .getIntArray(ArtsManager.KEY_ITEMSTACK_ARTS)
            itemArtSlotIds = IntArray(item.slotSize)
            itemArtSlotIds.forEachIndexed { i, _ ->
                itemArtSlotIds[i] = savedIds.getOrElse(i) { ArtEmpty.uniqueID }
            }
            itemArtSlotIds.forEachIndexed { index, artId ->
                addRenderableWidget(ArtsWidget(
                    x = (width / 6.0 - widgetWidth * 9 / 4.0).roundToInt(),
                    y = (height / 6.0 + index * widgetWidth * 9 / 8.0).roundToInt(),
                    size = widgetWidth,
                    art = artId.getArtById(),
                    slotCount = index
                ).apply {
                    isLearned = true
                    isFromItem = true
                })
            }
        }
        playerArtSlotIds = player?.getOACapability()?.installedArtIds?.toIntArray() ?: IntArray(0)
        playerArtSlotIds.forEachIndexed { index, artId ->
            addRenderableWidget(ArtsWidget(
                x = (width / 6.0 - widgetWidth * 9 / 8.0).roundToInt(),
                y = (height / 6.0 + index * widgetWidth * 9 / 8.0).roundToInt(),
                size = widgetWidth,
                art = artId.getArtById(),
                slotCount = index
            ).apply {
                isLearned = true
                isFromItem = false
            })
        }

        artsPage = ArtsTreePage(
            width * 2 / 3,
            height * 2 / 3,
            widgetWidth
        ) {
            isDragging = true
            draggingArtWidget = this
        }
        addRenderableWidget(artsPage!!)
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, partialTicks)
        if (isDragging) {
            draggingArtWidget?.let {
                poseStack.pushPose()
                poseStack.scale(scale, scale, 0f)
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
                RenderSystem.setShaderTexture(0, it.art.getIconResource())
                blit(
                    poseStack,
                    ((mouseX / scale) - 130).roundToInt(),
                    ((mouseY / scale) - 130).roundToInt(),
                    0,
                    0,
                    260,
                    260
                )
                poseStack.popPose()
            }
        }
    }

    private fun setTo(from: ArtsWidget, to: ArtsWidget) {
        val temp = from.art
        if (from.isSlot && to.isSlot) {
            from.art = to.art
            if (from.isFromItem) {
                itemArtSlotIds[from.slotCount] = to.art.uniqueID
            } else {
                playerArtSlotIds[from.slotCount] = to.art.uniqueID
            }
        }
        if (to.isSlot) {
            to.art = temp
            if (to.isFromItem) {
                itemArtSlotIds[to.slotCount] = temp.uniqueID
            } else {
                playerArtSlotIds[to.slotCount] = temp.uniqueID
            }
        }
    }

    private fun onWhichWidget(mouseX: Double, mouseY: Double): GuiEventListener? {
        return children().find {
            it.isMouseOver(mouseX, mouseY)
        }
    }

    override fun mouseDragged(
        mouseX: Double, mouseY: Double, flag: Int, dx: Double, dy: Double
    ): Boolean {
        artsPage?.let {
            if (it.mouseDragged(mouseX, mouseY, flag, dx, dy)) {
                return false
            }
        }
        if (!isDragging) {
            val widget = onWhichWidget(mouseX, mouseY)
            if (widget is ArtsWidget && widget.art !is ArtEmpty && widget.isLearned) {
                draggingArtWidget = widget
                isDragging = true
                artsPage?.isDragging = true
            }
        }
        return isDragging
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, flag: Int): Boolean {
        if (isDragging) {
            val toArtWidget = onWhichWidget(mouseX, mouseY)
            draggingArtWidget?.let { from ->
                if (toArtWidget != null && toArtWidget is ArtsWidget) {
                    setTo(from, toArtWidget)
                } else if (from.isSlot) {
                    from.art = ArtEmpty
                    if (from.isFromItem) {
                        itemArtSlotIds[from.slotCount] = ArtEmpty.uniqueID
                    } else {
                        playerArtSlotIds[from.slotCount] = ArtEmpty.uniqueID
                    }
                }
            }
            isDragging = false
            artsPage?.isDragging = false
            draggingArtWidget = null
            return true
        }
        if (artsPage?.isScrolling == true) {
            artsPage?.isScrolling = false
        }
        return false
    }

    override fun onClose() {
        OANetworking.sendSetItemArts(itemArtSlotIds)
        OANetworking.sendSetPlayerArts(playerArtSlotIds)
        super.onClose()
    }

    override fun isPauseScreen(): Boolean = false

    companion object {

        const val KEY_ARTS_LEARN_GUI = "gui.originiumarts.arts_learn"

        fun openGui() {
            Minecraft.getInstance().setScreen(ArtsLearnGui())
        }
    }

}