package com.phantom.originiumarts.client

import com.mojang.math.Vector3f
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.block.BlockRegister
import com.phantom.originiumarts.client.gui.ArtsChangeGui
import com.phantom.originiumarts.client.gui.ArtsHud
import com.phantom.originiumarts.client.gui.ArtsLearnGui
import com.phantom.originiumarts.common.ArtsManager.getArtById
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.common.capability.getUseDurationAmplifier
import com.phantom.originiumarts.common.network.OANetworking
import com.phantom.originiumarts.item.ArtsUnitItem
import com.phantom.originiumarts.item.IOverrideAttack
import com.phantom.originiumarts.item.OAStandardPistol
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderHandEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import org.lwjgl.glfw.GLFW
import kotlin.math.min

@EventBusSubscriber(modid = OriginiumArtsMod.MOD_ID, value = [Dist.CLIENT])
object ClientSetup {

    fun setup() {
        KeyBinds.register()
        blockRenderSetup()
    }

    private fun blockRenderSetup() {
        ItemBlockRenderTypes.setRenderLayer(BlockRegister.ORIGINIUM_CLUSTER.get(), RenderType.cutout())
        ItemBlockRenderTypes.setRenderLayer(BlockRegister.ORIGINIUM_LARGE_BUD.get(), RenderType.cutout())
        ItemBlockRenderTypes.setRenderLayer(BlockRegister.ORIGINIUM_MEDIUM_BUD.get(), RenderType.cutout())
        ItemBlockRenderTypes.setRenderLayer(BlockRegister.ORIGINIUM_SMALL_BUD.get(), RenderType.cutout())
    }

    @SubscribeEvent
    fun onKeyInput(event: InputEvent.KeyInputEvent) {
        val mc = Minecraft.getInstance()
        val player = mc.player
        val screen = mc.screen
        if (player != null && screen == null) {
            when {
                event.key == KeyBinds.OPEN_ARTS_SCREEN.key.value && event.action == GLFW.GLFW_PRESS -> {
                    ArtsLearnGui.openGui()
                }
                event.key == KeyBinds.CHANGE_ARTS.key.value && event.action == GLFW.GLFW_PRESS -> {
                    ArtsChangeGui.openGui()
                }
            }
        }
        if (player != null && screen != null) {
            if (screen is ArtsChangeGui && event.key == KeyBinds.CHANGE_ARTS.key.value && event.action == GLFW.GLFW_RELEASE) {
                screen.onClose()
            }
        }
    }

    @SubscribeEvent
    fun onClickInput(event: InputEvent.ClickInputEvent) {
        val mc = Minecraft.getInstance()
        val player = mc.player
        if (player != null && mc.screen == null) {
            if (player.getItemInHand(InteractionHand.MAIN_HAND).item is IOverrideAttack) {
                when {
                    event.isAttack -> {
                        OANetworking.sendLeftClickOnArtsUnitItem()
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onOverlayRender(event: RenderGameOverlayEvent) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return
        }
        val mainItem = Minecraft.getInstance().player?.getItemInHand(InteractionHand.MAIN_HAND)?.item
        val offItem = Minecraft.getInstance().player?.getItemInHand(InteractionHand.OFF_HAND)?.item
        if (mainItem is ArtsUnitItem || offItem is ArtsUnitItem) {
            Minecraft.getInstance().player?.let {
                val needUseTick = it.getOACapability()?.selectedArtId?.getArtById()?.needUseTick ?: 1
                val useDuration =
                    it.getUseDurationAmplifier() * if (it.isUsingItem) OriginiumArtsMod.maxUseDuration - it.useItemRemainingTicks else 0
                ArtsHud.render(
                    event.matrixStack,
                    if (needUseTick == 0) 0.0 else min(useDuration / needUseTick, 1.0)
                )
            }
        }
    }

    @SubscribeEvent
    fun onRenderHandEvent(event: RenderHandEvent) {
        when (event.itemStack.item) {
            is OAStandardPistol -> {
                event.poseStack.let {
                    val process = event.equipProgress
                    val f1 = Mth.sqrt(process)
                    val f2 = Mth.sin(f1 * Math.PI.toFloat() / 2)
                    it.mulPose(Vector3f.XP.rotationDegrees(f2 * 20f))
                    it.translate(0.0, f2 * 0.6, f2 * 0.5)
                }
            }
            is ArtsUnitItem -> {

            }
        }
    }

}