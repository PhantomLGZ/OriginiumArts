package com.phantom.originiumarts.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.logging.LogUtils
import com.phantom.originiumarts.client.TextKey
import com.phantom.originiumarts.client.gui.widget.CapabilitySliderButton
import com.phantom.originiumarts.common.capability.*
import com.phantom.originiumarts.common.network.*
import com.phantom.originiumarts.common.network.sendpack.OACapSetSendPack
import com.phantom.originiumarts.common.network.sendpack.OACatastropheIntensitySendPack
import com.phantom.originiumarts.common.network.sendpack.OAClearLearnedArtsSendPack
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
import kotlin.math.roundToInt


class TestItemGui :
    Screen(TranslatableComponent(ItemRegister.TEST_ITEM.get().descriptionId)) {

    private var cap = Minecraft.getInstance().player?.getOACapability()

    override fun init() {
        super.init()
        val length = (width / 2.0).roundToInt()
        val wide = (length / 10.0).roundToInt()
        LogUtils.getLogger().info("OAM: test ${cap?.getAllInfo()}")
        addRenderableWidget(
            CapabilitySliderButton(
                x = 10,
                y = wide / 2,
                width = length,
                height = wide,
                baseMsg = TranslatableComponent(TextKey.KEY_TEXT_STRENGTH),
                initValue = cap?.strength?.getValue() ?: 0
            ) { value ->
                OANetworking.sendToServer(OACapSetSendPack(TARGET_KEY_STRENGTH, value))
            }
        )
        addRenderableWidget(
            CapabilitySliderButton(
                x = 10,
                y = wide / 2 * 4,
                width = length,
                height = wide,
                baseMsg = TranslatableComponent(TextKey.KEY_TEXT_MOBILITY),
                initValue = cap?.mobility?.getValue() ?: 0
            ) { value ->
                OANetworking.sendToServer(OACapSetSendPack(TARGET_KEY_MOBILITY, value))
            }
        )
        addRenderableWidget(
            CapabilitySliderButton(
                x = 10,
                y = wide / 2 * 7,
                width = length,
                height = wide,
                baseMsg = TranslatableComponent(TextKey.KEY_TEXT_ENDURANCE),
                initValue = cap?.endurance?.getValue() ?: 0
            ) { value ->
                OANetworking.sendToServer(OACapSetSendPack(TARGET_KEY_ENDURANCE, value))
            }
        )
        addRenderableWidget(
            CapabilitySliderButton(
                x = 10,
                y = wide / 2 * 10,
                width = length,
                height = wide,
                baseMsg = TranslatableComponent(TextKey.KEY_TEXT_TACTICAL_ACUMEN),
                initValue = cap?.tacticalAcumen?.getValue() ?: 0
            ) { value ->
                OANetworking.sendToServer(OACapSetSendPack(TARGET_KEY_TACTICAL_ACUMEN, value))
            }
        )
        addRenderableWidget(
            CapabilitySliderButton(
                x = 10,
                y = wide / 2 * 13,
                width = length,
                height = wide,
                baseMsg = TranslatableComponent(TextKey.KEY_TEXT_COMBAT_SKILL),
                initValue = cap?.combatSkill?.getValue() ?: 0
            ) { value ->
                OANetworking.sendToServer(OACapSetSendPack(TARGET_KEY_COMBAT_SKILL, value))
            }
        )
        addRenderableWidget(
            CapabilitySliderButton(
                x = 10,
                y = wide / 2 * 16,
                width = length,
                height = wide,
                baseMsg = TranslatableComponent(TextKey.KEY_TEXT_ARTS_ADAPTABILITY),
                initValue = cap?.artsAdaptability?.getValue() ?: 0
            ) { value ->
                OANetworking.sendToServer(OACapSetSendPack(TARGET_KEY_ARTS_ADAPTABILITY, value))
            }
        )
        addRenderableWidget(
            Button(
                30 + length,
                wide / 2,
                length / 3,
                wide,
                TranslatableComponent(TextKey.KEY_TEXT_CLEAR_LEARNED_ARTS)
            ) {
                OANetworking.sendToServer(OAClearLearnedArtsSendPack())
            }
        )
        val catastrophe = EditBox(
            font,
            30 + length,
            wide / 2 * 4,
            length / 4,
            wide,
            TextComponent("0")
        )
        addRenderableWidget(catastrophe)
        addRenderableWidget(
            Button(
                40 + length / 4 * 5,
                wide / 2 * 4,
                length / 4,
                wide,
                TranslatableComponent(TextKey.KEY_TEXT_SET_CATASTROPHE_INTENSITY)
            ) {
                catastrophe.value.toIntOrNull()?.let {
                    OANetworking.sendToServer(OACatastropheIntensitySendPack(it))
                }
            }
        )
        addRenderableWidget(
            Button(
                50 + length / 4 * 6,
                wide / 2 * 4,
                length / 4,
                wide,
                TranslatableComponent(TextKey.KEY_TEXT_ADD_CATASTROPHE)
            ) {
                catastrophe.value.toIntOrNull()?.let {
                    OANetworking.sendToServer(OACatastropheIntensitySendPack(it, true))
                }
            }
        )
//        addRenderableWidget(
//            object : AbstractSliderButton(
//                30 + length,
//                wide / 2 * 7,
//                length * 2 / 3,
//                wide,
//                TextComponent("temp"),
//                MeteorRenderer.temp / 360.0
//            ) {
//                override fun updateMessage() {
//                    message = TextComponent((value.toFloat() * 360).toString())
//                }
//
//                override fun applyValue() {
//                    MeteorRenderer.temp = value.toFloat() * 360
//                }
//            }
//        )
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, partialTicks)
    }

    override fun isPauseScreen(): Boolean = false

    companion object {

        fun openGui() {
            Minecraft.getInstance().setScreen(TestItemGui())
        }
    }
}