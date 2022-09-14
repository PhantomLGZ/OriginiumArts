package com.phantom.originiumarts.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import com.phantom.originiumarts.client.gui.widget.ArtSelectWidget
import com.phantom.originiumarts.common.arts.ArtEmpty
import com.phantom.originiumarts.common.ArtsManager
import com.phantom.originiumarts.common.ArtsManager.getArtById
import com.phantom.originiumarts.common.network.OANetworking
import com.phantom.originiumarts.common.capability.getOACapability
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.player.Input
import net.minecraft.client.player.KeyboardInput
import net.minecraft.network.chat.TextComponent
import net.minecraft.world.InteractionHand
import kotlin.math.PI

class ArtsChangeGui : Screen(TextComponent("")) {

    private var mouseX: Double = 0.0
    private var mouseY: Double = 0.0

    val input = object : Input() {
        private fun calculateImpulse(p: Boolean, n: Boolean): Float {
            return if (p == n) {
                0.0f
            } else {
                if (p) 1.0f else -1.0f
            }
        }

        override fun tick(slow: Boolean) {
            forwardImpulse = calculateImpulse(up, down)
            leftImpulse = calculateImpulse(left, right)
            if (slow) {
                leftImpulse *= 0.3f
                forwardImpulse *= 0.3f
            }
        }
    }

    override fun init() {
        super.init()
        val centerX = width / 2
        val centerY = height / 2
        val player = Minecraft.getInstance().player
        player?.input = input
        player?.getOACapability()?.installedArtIds?.forEachIndexed { i, artId ->
            addRenderableOnly(
                ArtSelectWidget(
                    -PI * 3 / 5 - i * PI / 5,
                    centerX,
                    centerY,
                    width,
                    artId.getArtById()
                )
            )
        }

        player?.getItemInHand(InteractionHand.MAIN_HAND)
            ?.tag?.getIntArray(ArtsManager.KEY_ITEMSTACK_ARTS)?.forEachIndexed { i, artId ->
                addRenderableOnly(
                    ArtSelectWidget(
                        -PI * 2 / 5 + i * PI / 5,
                        centerX,
                        centerY,
                        width,
                        artId.getArtById()
                    )
                )
            }
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, partialTicks)
        this.mouseX = mouseX.toDouble()
        this.mouseY = mouseY.toDouble()
    }

    override fun onClose() {
        renderables.map {
            it as ArtSelectWidget
        }.findLast {
            it.isMouseOver(mouseX, mouseY)
        }?.let {
            if (it.art.uniqueID != ArtEmpty.uniqueID) {
                OANetworking.sendSelectedArt(it.art.uniqueID)
            }
        }
        Minecraft.getInstance().player?.input = KeyboardInput(Minecraft.getInstance().options)
        super.onClose()
    }

    override fun isPauseScreen(): Boolean = false

    override fun keyReleased(key: Int, p_94716_: Int, p_94717_: Int): Boolean {
        val option = Minecraft.getInstance().options
        if (option.keyUp.key.value == key) {
            input.up = false
        }
        if (option.keyDown.key.value == key) {
            input.down = false
        }
        if (option.keyLeft.key.value == key) {
            input.left = false
        }
        if (option.keyRight.key.value == key) {
            input.right = false
        }
        if (option.keyJump.key.value == key) {
            input.jumping = false
        }
        if (option.keyShift.key.value == key) {
            input.shiftKeyDown = false
        }
        return super.keyReleased(key, p_94716_, p_94717_)
    }

    override fun keyPressed(key: Int, p_96553_: Int, p_96554_: Int): Boolean {
        val option = Minecraft.getInstance().options
        if (option.keyUp.key.value == key) {
            input.up = true
        }
        if (option.keyDown.key.value == key) {
            input.down = true
        }
        if (option.keyLeft.key.value == key) {
            input.left = true
        }
        if (option.keyRight.key.value == key) {
            input.right = true
        }
        if (option.keyJump.key.value == key) {
            input.jumping = true
        }
        if (option.keyShift.key.value == key) {
            input.shiftKeyDown = true
        }
        return super.keyPressed(key, p_96553_, p_96554_)
    }

    companion object {
        fun openGui() {
            val option = Minecraft.getInstance().options
            Minecraft.getInstance().setScreen(ArtsChangeGui().apply {
                input.up = option.keyUp.isDown
                input.down = option.keyDown.isDown
                input.left = option.keyLeft.isDown
                input.right = option.keyRight.isDown
                input.jumping = option.keyJump.isDown
                input.shiftKeyDown = option.keyShift.isDown
            })
        }
    }

}