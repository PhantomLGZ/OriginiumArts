package com.phantom.originiumarts.client

import com.mojang.blaze3d.shaders.FogShape
import com.mojang.math.Matrix4f
import com.mojang.math.Vector3f
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.block.BlockRegister
import com.phantom.originiumarts.client.gui.ArtsChangeGui
import com.phantom.originiumarts.client.gui.ArtsHud
import com.phantom.originiumarts.client.gui.ArtsLearnGui
import com.phantom.originiumarts.client.weather.OACloudRenderHandler
import com.phantom.originiumarts.client.weather.OASkyRenderHandler
import com.phantom.originiumarts.client.weather.OAWeatherParticleRenderHandler
import com.phantom.originiumarts.client.weather.OAWeatherRenderHandler
import com.phantom.originiumarts.common.ArtsManager.getArtById
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.common.capability.getUseDurationAmplifier
import com.phantom.originiumarts.common.network.OANetworking
import com.phantom.originiumarts.common.network.sendpack.OAClickSendPack
import com.phantom.originiumarts.common.weather.CatastropheClientManager
import com.phantom.originiumarts.common.weather.CatastropheType
import com.phantom.originiumarts.common.weather.getAffectingCatastropheType
import com.phantom.originiumarts.common.weather.getCatastropheSkyColorInClient
import com.phantom.originiumarts.item.ArtsUnitItem
import com.phantom.originiumarts.item.IOverrideAttack
import com.phantom.originiumarts.item.OAStandardPistol
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.*
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import org.lwjgl.glfw.GLFW
import kotlin.math.*

@EventBusSubscriber(modid = OriginiumArtsMod.MOD_ID, value = [Dist.CLIENT])
object ClientSetup {

    var projectionMatrix: Matrix4f = Matrix4f()

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
            val item = player.getItemInHand(InteractionHand.MAIN_HAND).item
            if (item is IOverrideAttack) {
                when {
                    event.isAttack -> {
                        item.onClickInClient(player)
                        OANetworking.sendToServer(OAClickSendPack())
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
                    event.matrixStack, if (needUseTick == 0) 0.0 else min(useDuration / needUseTick, 1.0)
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

    @SubscribeEvent
    fun onClientTickEvent(event: TickEvent.ClientTickEvent) {
        CatastropheClientManager.tick()
    }

    @SubscribeEvent
    fun onFogColors(event: EntityViewRenderEvent.FogColors) {
        CatastropheClientManager.catastropheEffect.let {
            val color = Vec3(
                event.red.toDouble(), event.green.toDouble(), event.blue.toDouble()
            ).getCatastropheSkyColorInClient(event.camera.position) { it.adjustBrightnessContrast(-0.1) }
            CatastropheClientManager.fogColorVec3 = Vec3(color.x, color.y, color.z)
            event.red = color.x.toFloat()
            event.green = color.y.toFloat()
            event.blue = color.z.toFloat()
        }
    }

    @SubscribeEvent
    fun onRenderFogEvent(event: EntityViewRenderEvent.RenderFogEvent) {
        Minecraft.getInstance().level?.let { level ->
            CatastropheClientManager.catastropheEffect.let { effect ->
                if (effect > 0.001) {
                    val factor = when (getAffectingCatastropheType(level, event.camera.position)) {
                        CatastropheType.METEOR_SHOWERS ->
                            (1 - (effect / 4).pow(2)).toFloat()
                        CatastropheType.SANDSTORM ->
                            (1 - effect).toFloat().pow(4)
                        CatastropheType.BLIZZARD ->
                            (1 - effect).toFloat().pow(2)
                        CatastropheType.RAINSTORM ->
                            (1 - effect).toFloat()
                        CatastropheType.STORM ->
                            (1 - effect / 2).toFloat()
                        else -> (1 - effect / 2).toFloat()
                    }
                    event.nearPlaneDistance = event.nearPlaneDistance * factor.pow(3)
                    event.farPlaneDistance = max(1f, event.farPlaneDistance * factor + 10f * effect.toFloat())
                    event.fogShape = if (effect > 0.1) FogShape.SPHERE else FogShape.CYLINDER
                    event.isCanceled = true
                }
            }
        }
    }

    @SubscribeEvent
    fun onLoad(event: WorldEvent.Load) {
        (event.world as? ClientLevel)?.effects()?.let {
            it.weatherRenderHandler = OAWeatherRenderHandler
            it.weatherParticleRenderHandler = OAWeatherParticleRenderHandler
            it.skyRenderHandler = OASkyRenderHandler
            it.cloudRenderHandler = OACloudRenderHandler
        }
    }

    @SubscribeEvent
    fun onRenderLevelLastEvent(event: RenderLevelLastEvent) {
        projectionMatrix = event.projectionMatrix
    }

}