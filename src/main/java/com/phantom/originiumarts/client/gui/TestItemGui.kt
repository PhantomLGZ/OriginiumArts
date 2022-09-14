package com.phantom.originiumarts.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.logging.LogUtils
import com.phantom.originiumarts.client.TextKey
import com.phantom.originiumarts.client.gui.widget.CapabilitySliderButton
import com.phantom.originiumarts.common.capability.*
import com.phantom.originiumarts.common.network.*
import com.phantom.originiumarts.common.network.sendpack.OACapSetSendPack
import com.phantom.originiumarts.common.network.sendpack.OAClearLearnedArtsSendPack
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TranslatableComponent
import kotlin.math.roundToInt


class TestItemGui :
    Screen(TranslatableComponent(ItemRegister.TEST_ITEM.get().descriptionId)) {

    private var cap = Minecraft.getInstance().player?.getOACapability()

    override fun init() {
        super.init()
        val length = (width / 2.0).roundToInt()
        val wide = (length / 10.0).roundToInt()
        LogUtils.getLogger().error("TEST ${cap?.getAllInfo()}")
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
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, partialTicks)
    }

    companion object {

        fun openGui() {
            Minecraft.getInstance().setScreen(TestItemGui())
        }
    }
}