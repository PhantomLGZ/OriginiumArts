package com.phantom.originiumarts.client.gui

import com.mojang.blaze3d.vertex.*
import com.phantom.originiumarts.client.TextKey
import com.phantom.originiumarts.client.drawLine
import com.phantom.originiumarts.client.drawQuad
import com.phantom.originiumarts.client.isWithinAngularRange
import com.phantom.originiumarts.common.capability.*
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability.LimitedRangeNum.Companion.maxValue
import com.phantom.originiumarts.common.network.OANetworking
import com.phantom.originiumarts.common.network.sendpack.OACapSetSendPack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.sounds.SoundEvents
import kotlin.math.*

class AttrManageGui : Screen(TranslatableComponent(KEY_ATTR_MANAGE_GUI)) {

    private val player = Minecraft.getInstance().player
    private val cap = player?.getOACapability()
    private var sixDimList = listOf<SixDimInfo>()

    private var count = 0
    private val maxCount = 50

    private var centerX = 0.0
    private var centerY = 0.0
    private var length = 0.0

    private var isHolding = false
    private var holdingCount = 0
    private val maxHoldingCount = 40.0
    private var holdInfo: SixDimInfo? = null

    private var isCoolDown = false
    private var coolDownCount = 0
    private var maxCoolDownCount = 10.0

    private var toastCount = 0
    private var toastText = TextComponent.EMPTY

    data class SixDimInfo(
        val cap: OriginiumArtsCapability.LimitedRangeNum?,
        val angle: Double,
        val title: Component,
        val targetStr: String,
        val level: Component = cap?.getLevel()?.getComponent() ?: TextComponent.EMPTY,
        val value: Int = cap?.getValue() ?: 0
    )

    override fun init() {
        super.init()
        centerX = width * 2 / 3.0
        centerY = height / 2.0
        length = height / 3.0
        updateCap()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, flag: Int): Boolean {
        sixDimList.forEach {
            if (it.angle.withinTheScope(mouseX - centerX, mouseY - centerY)) {
                holdInfo = it
                isHolding = true
                playDownSound()
                return true
            }
        }
        return super.mouseClicked(mouseX, mouseY, flag)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, flag: Int): Boolean {
        isHolding = false
        holdingCount = 0
        holdInfo = null
        return super.mouseReleased(mouseX, mouseY, flag)
    }

    private fun playDownSound() {
        Minecraft.getInstance()
            .soundManager
            .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
    }

    private fun playLevelUpSound() {
        Minecraft.getInstance()
            .soundManager
            .play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f))
    }

    override fun tick() {
        super.tick()
        updateCap()
        if (isCoolDown) {
            coolDownCount++
            if (coolDownCount >= maxCoolDownCount) {
                isCoolDown = false
                coolDownCount = 0
            }
        } else if (isHolding && player != null) {
            holdInfo?.cap?.let { c ->
                if (c.getValue() >= maxValue) {
                    showToast(TranslatableComponent(TextKey.KEY_TEXT_FULL_NOW))
                } else if (player.totalExperience < c.getLevel().costExp()) {
                    showToast(
                        TranslatableComponent(TextKey.KEY_TEXT_NEED_EXPERIENCE)
                            .append(": ${c.getLevel().costExp()}")
                    )
                } else {
                    holdingCount++
                    holding()
                    showToast(TextComponent.EMPTY, 0)
                }
            }
        }
        if (toastCount > 0) {
            toastCount--
            if (toastCount == 0) {
                toastText = TextComponent.EMPTY
            }
        }
    }

    private fun updateCap() {
        sixDimList = listOf(
            SixDimInfo(
                cap?.strength,
                PI / 6,
                TranslatableComponent(TextKey.KEY_TEXT_STRENGTH),
                TARGET_KEY_STRENGTH
            ),
            SixDimInfo(
                cap?.mobility,
                PI / 2,
                TranslatableComponent(TextKey.KEY_TEXT_MOBILITY),
                TARGET_KEY_MOBILITY
            ),
            SixDimInfo(
                cap?.endurance,
                5 * PI / 6,
                TranslatableComponent(TextKey.KEY_TEXT_ENDURANCE),
                TARGET_KEY_ENDURANCE
            ),
            SixDimInfo(
                cap?.tacticalAcumen,
                -5 * PI / 6,
                TranslatableComponent(TextKey.KEY_TEXT_TACTICAL_ACUMEN),
                TARGET_KEY_TACTICAL_ACUMEN
            ),
            SixDimInfo(
                cap?.combatSkill,
                -PI / 6,
                TranslatableComponent(TextKey.KEY_TEXT_COMBAT_SKILL),
                TARGET_KEY_COMBAT_SKILL
            ),
            SixDimInfo(
                cap?.artsAdaptability,
                -PI / 2,
                TranslatableComponent(TextKey.KEY_TEXT_ARTS_ADAPTABILITY),
                TARGET_KEY_ARTS_ADAPTABILITY
            )
        )
    }

    private fun holding() {
        if (holdingCount >= maxHoldingCount) {
            holdInfo?.let {
                OANetworking.sendToServer(
                    OACapSetSendPack(
                        it.targetStr,
                        1,
                        it.cap?.getLevel()?.costExp() ?: 0,
                        true
                    )
                )
                playLevelUpSound()
                isCoolDown = true
                holdingCount = 0
            }
        }
    }

    private fun showToast(text: Component, count: Int = 40) {
        toastText = text
        toastCount = count
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, partialTicks)
        if (count < maxCount) {
            count++
        }
        if (isHolding) {
            var inScope = false
            sixDimList.forEach {
                if (it.angle.withinTheScope(mouseX - centerX, mouseY - centerY)) {
                    inScope = true
                    if (it != holdInfo) {
                        holdInfo = it
                        holdingCount = 0
                    }
                }
            }
            if (!inScope) {
                holdInfo = null
                holdingCount = 0
            }
        }
        drawSixDimBackground()
        drawTickMarks()
        drawSixDim()
        drawAttrName(poseStack, mouseX, mouseY)
        drawAttrLevel(poseStack)
        drawToast(poseStack)
    }

    private fun drawSixDimBackground() {
        drawHalfSixDimBackground(2, 1, 0, 3)
        drawHalfSixDimBackground(3, 0, 4, 5)
        drawHalfHoldingBackground(2, 1, 0, 3)
        drawHalfHoldingBackground(3, 0, 4, 5)
    }

    private fun drawHalfSixDimBackground(i1: Int, i2: Int, i3: Int, i4: Int) {
        drawQuad(
            Pair(centerX + length * cos(sixDimList[i1].angle), centerY + length * sin(sixDimList[i1].angle)),
            Pair(centerX + length * cos(sixDimList[i2].angle), centerY + length * sin(sixDimList[i2].angle)),
            Pair(centerX + length * cos(sixDimList[i3].angle), centerY + length * sin(sixDimList[i3].angle)),
            Pair(centerX + length * cos(sixDimList[i4].angle), centerY + length * sin(sixDimList[i4].angle)),
            0xCC333333
        )
    }

    private fun drawHalfHoldingBackground(i1: Int, i2: Int, i3: Int, i4: Int) {
        val alpha =
            (0x66 * if (isCoolDown) (1 - coolDownCount / maxCoolDownCount) else (holdingCount / maxHoldingCount))
                .roundToInt()
        val tLength = (if (isCoolDown) 1.0 else holdingCount / maxHoldingCount) * length
        drawQuad(
            Pair(
                centerX + tLength * cos(sixDimList[i1].angle),
                centerY + tLength * sin(sixDimList[i1].angle)
            ),
            Pair(
                centerX + tLength * cos(sixDimList[i2].angle),
                centerY + tLength * sin(sixDimList[i2].angle)
            ),
            Pair(
                centerX + tLength * cos(sixDimList[i3].angle),
                centerY + tLength * sin(sixDimList[i3].angle)
            ),
            Pair(
                centerX + tLength * cos(sixDimList[i4].angle),
                centerY + tLength * sin(sixDimList[i4].angle)
            ),
            0x00FF00 + (alpha shl 24).toLong()
        )
    }

    private fun drawTickMarks() {
        for (p in 2..8 step 2) {
            sixDimList.forEach {
                drawLine(
                    Pair(
                        centerX + length / 10 * p * cos(it.angle + PI / 3),
                        centerY + length / 10 * p * sin(it.angle + PI / 3)
                    ), Pair(
                        centerX + length / 10 * p * cos(it.angle),
                        centerY + length / 10 * p * sin(it.angle)
                    ), 0x33CCCCCC
                )
            }
        }
        sixDimList.forEach {
            drawLine(
                Pair(centerX, centerY),
                Pair(centerX + length * cos(it.angle), centerY + length * sin(it.angle)),
                0x66000000
            )
        }
    }

    private fun drawSixDim() {
        val process = sin(sqrt(count.toDouble() / maxCount) * PI / 2)
        drawHalfSixDim(2, 1, 0, 3, process)
        drawHalfSixDim(3, 0, 4, 5, process)
    }

    private fun drawHalfSixDim(i1: Int, i2: Int, i3: Int, i4: Int, process: Double) {
        drawQuad(
            Pair(
                centerX + length / 100 * sixDimList[i1].value * process * cos(sixDimList[i1].angle),
                centerY + length / 100 * sixDimList[i1].value * process * sin(sixDimList[i1].angle)
            ),
            Pair(
                centerX + length / 100 * sixDimList[i2].value * process * cos(sixDimList[i2].angle),
                centerY + length / 100 * sixDimList[i2].value * process * sin(sixDimList[i2].angle)
            ),
            Pair(
                centerX + length / 100 * sixDimList[i3].value * process * cos(sixDimList[i3].angle),
                centerY + length / 100 * sixDimList[i3].value * process * sin(sixDimList[i3].angle)
            ),
            Pair(
                centerX + length / 100 * sixDimList[i4].value * process * cos(sixDimList[i4].angle),
                centerY + length / 100 * sixDimList[i4].value * process * sin(sixDimList[i4].angle)
            ),
            0x88FFFFFF
        )
    }

    private fun drawAttrName(poseStack: PoseStack, mouseX: Int, mouseY: Int) {
        val dx = mouseX - centerX
        val dy = mouseY - centerY
        val enlarge = 1.1
        sixDimList.forEach {
            drawCapString(
                poseStack,
                it.title,
                length * enlarge,
                it.angle,
                it.angle.getAttrNameColor(dx, dy)
            )
        }
    }

    private fun Double.getAttrNameColor(dx: Double, dy: Double, halfRange: Double = PI / 6): Int {
        return if (this.withinTheScope(dx, dy, halfRange)) 0xFFFFFF
        else 0xAAAAAA
    }

    private fun Double.withinTheScope(dx: Double, dy: Double, halfRange: Double = PI / 6): Boolean =
        this.isWithinAngularRange(halfRange, dx, dy)
                && hypot(dx, dy) in (length * 0.8..length * 1.3)

    private fun drawAttrLevel(poseStack: PoseStack) {
        sixDimList.forEach {
            drawCapString(
                poseStack,
                it.level,
                it.value.flexibleDisplayLength(),
                it.angle
            )
        }
    }

    private fun Int.flexibleDisplayLength(): Double {
        val threshold = 50.0
        val factor = 0.5 * (threshold - this) / threshold
        return length / 100 * this + (length * factor * factor.absoluteValue)
    }

    private fun drawCapString(
        poseStack: PoseStack, text: Component?, length: Double, angle: Double, rgb: Int = 0xE3E3E3
    ) {
        drawCenteredString(
            poseStack,
            font,
            text ?: TextComponent.EMPTY,
            (centerX + length * cos(angle)).roundToInt(),
            (centerY + length * sin(angle)).roundToInt(),
            rgb
        )
    }

    private fun drawToast(poseStack: PoseStack) {
        if (toastCount > 0 && toastText != TextComponent.EMPTY) {
            drawCenteredString(
                poseStack,
                font,
                toastText,
                centerX.roundToInt(),
                centerY.roundToInt(),
                0xDD3333
            )
        }
    }

    override fun isPauseScreen(): Boolean = false

    companion object {
        const val KEY_ATTR_MANAGE_GUI = "gui.originiumarts.attr_manage"

        fun openGui() {
            Minecraft.getInstance().setScreen(AttrManageGui())
        }
    }

}
