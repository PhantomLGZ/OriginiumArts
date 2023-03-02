package com.phantom.originiumarts.client.weather

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.phantom.originiumarts.client.ClientSetup
import com.phantom.originiumarts.client.adjustBrightnessContrast
import com.phantom.originiumarts.common.weather.CatastropheClientManager
import com.phantom.originiumarts.common.weather.getCatastropheSkyColorInClient
import net.minecraft.client.CloudStatus
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import net.minecraftforge.client.ICloudRenderHandler
import kotlin.math.floor
import kotlin.math.max

object OACloudRenderHandler : ICloudRenderHandler {

    private val CLOUDS_LOCATION = ResourceLocation("textures/environment/clouds.png")

    private var cloudBuffer: VertexBuffer? = null
    private var prevCloudX = Int.MIN_VALUE
    private var prevCloudY = Int.MIN_VALUE
    private var prevCloudZ = Int.MIN_VALUE
    private var prevCloudColor = Vec3.ZERO
    private var prevCloudsType: CloudStatus? = null
    private var generateClouds: Boolean = true

    override fun render(
        ticks: Int,
        partialTick: Float,
        poseStack: PoseStack,
        level: ClientLevel,
        minecraft: Minecraft,
        camX: Double,
        camY: Double,
        camZ: Double
    ) {
        val effect = CatastropheClientManager.catastropheEffect
        val f = level.effects().cloudHeight.let {
            if (it.isNaN()) it
            else it * 0.7f + it * 0.3f * (1 - effect).toFloat()
        }
        val projectionMatrix = ClientSetup.projectionMatrix
        if (!f.isNaN()) {
            RenderSystem.disableCull()
            RenderSystem.enableBlend()
            RenderSystem.enableDepthTest()
            RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            )
            RenderSystem.depthMask(true)
            val f1 = 12.0f
            val f2 = 4.0f
            val d0 = 2.0E-4
            val d1 = ((ticks.toFloat() + partialTick) * 0.03f).toDouble()
            var d2 = (camX + d1) / 12.0
            val d3 = (f - camY.toFloat() + 0.33f).toDouble()
            var d4 = camZ / 12.0 + 0.33
            d2 -= (Mth.floor(d2 / 2048.0) * 2048).toDouble()
            d4 -= (Mth.floor(d4 / 2048.0) * 2048).toDouble()
            val f3 = (d2 - Mth.floor(d2).toDouble()).toFloat()
            val f4 = (d3 / 4.0 - Mth.floor(d3 / 4.0).toDouble()).toFloat() * 4.0f
            val f5 = (d4 - Mth.floor(d4).toDouble()).toFloat()
            val vec3 = getCloudColor(level, partialTick)
            val i = floor(d2).toInt()
            val j = floor(d3 / 4.0).toInt()
            val k = floor(d4).toInt()
            if (i != prevCloudX
                || j != prevCloudY
                || k != prevCloudZ
                || minecraft.options.cloudsType != prevCloudsType
                || prevCloudColor.distanceToSqr(vec3) > 2.0E-4
            ) {
                prevCloudX = i
                prevCloudY = j
                prevCloudZ = k
                prevCloudColor = vec3
                prevCloudsType = minecraft.options.cloudsType
                generateClouds = true
            }
            if (generateClouds) {
                generateClouds = false
                val bufferBuilder = Tesselator.getInstance().builder
                cloudBuffer?.close()
                cloudBuffer = VertexBuffer()
                buildClouds(bufferBuilder, d2, d3, d4, vec3)
                bufferBuilder.end()
                cloudBuffer?.upload(bufferBuilder)
            }
            RenderSystem.setShader { GameRenderer.getPositionTexColorNormalShader() }
            RenderSystem.setShaderTexture(0, CLOUDS_LOCATION)
            Vec3(1.0, 1.0, 1.0)
                .getCatastropheSkyColorInClient(Vec3(camX, camY, camZ)) { it.adjustBrightnessContrast(-0.2 * effect) }
                .also {
                    RenderSystem.setShaderColor(it.x.toFloat(), it.y.toFloat(), it.z.toFloat(), 1f)
                }
            CatastropheClientManager.fogColorVec3.let {
                Vec3(
                    it.x.adjustBrightnessContrast(-0.2 * effect),
                    it.y.adjustBrightnessContrast(-0.2 * effect),
                    it.z.adjustBrightnessContrast(-0.2 * effect)
                )
            }.let {
                RenderSystem.setShaderFogColor(it.x.toFloat(), it.y.toFloat(), it.z.toFloat(), 1f)
            }

            poseStack.pushPose()
            poseStack.scale(12.0f, 1.0f, 12.0f)
            poseStack.translate((-f3).toDouble(), f4.toDouble(), (-f5).toDouble())
            if (cloudBuffer != null) {
                val i1 = if (prevCloudsType == CloudStatus.FANCY) 0 else 1
                for (l in i1..1) {
                    if (l == 0) {
                        RenderSystem.colorMask(false, false, false, false)
                    } else {
                        RenderSystem.colorMask(true, true, true, true)
                    }
                    val shaderInstance = RenderSystem.getShader()
                    cloudBuffer?.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderInstance!!)
                }
            }
            poseStack.popPose()
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            RenderSystem.enableCull()
            RenderSystem.disableBlend()
        }
    }

    private fun getCloudColor(level: ClientLevel, pPartialTick: Float): Vec3 {
        val f = level.getTimeOfDay(pPartialTick)
        var f1 = Mth.cos(f * (Math.PI.toFloat() * 2f)) * 2.0f + 0.5f
        f1 = Mth.clamp(f1, 0.0f, 1.0f)
        var f2 = 1.0f
        var f3 = 1.0f
        var f4 = 1.0f
        val f5 = max(CatastropheClientManager.catastropheEffect.toFloat(), level.getRainLevel(pPartialTick))
        if (f5 > 0.0f) {
            val f6 = f2 * 0.3f + f3 * 0.59f + f4 * 0.11f * 0.6f
            val f7 = 1.0f - f5 * 0.95f
            f2 = f2 * f7 + f6 * (1.0f - f7)
            f3 = f3 * f7 + f6 * (1.0f - f7)
            f4 = f4 * f7 + f6 * (1.0f - f7)
        }
        f2 *= f1 * 0.9f + 0.1f
        f3 *= f1 * 0.9f + 0.1f
        f4 *= f1 * 0.85f + 0.15f
        val f9 = level.getThunderLevel(pPartialTick)
        if (f9 > 0.0f) {
            val f10 = f2 * 0.3f + f3 * 0.59f + f4 * 0.11f * 0.2f
            val f8 = 1.0f - f9 * 0.95f
            f2 = f2 * f8 + f10 * (1.0f - f8)
            f3 = f3 * f8 + f10 * (1.0f - f8)
            f4 = f4 * f8 + f10 * (1.0f - f8)
        }
        return Vec3(f2.toDouble(), f3.toDouble(), f4.toDouble())
    }

    private fun buildClouds(pBuilder: BufferBuilder, pX: Double, pY: Double, pZ: Double, pCloudColor: Vec3) {
        val f = 4.0f
        val f1 = 0.00390625f
        val i = 8
        val j = 4
        val f2 = 9.765625E-4f
        val f3 = floor(pX).toFloat() * 0.00390625f
        val f4 = floor(pZ).toFloat() * 0.00390625f
        val f5 = pCloudColor.x.toFloat()
        val f6 = pCloudColor.y.toFloat()
        val f7 = pCloudColor.z.toFloat()
        val f8 = f5 * 0.9f
        val f9 = f6 * 0.9f
        val f10 = f7 * 0.9f
        val f11 = f5 * 0.7f
        val f12 = f6 * 0.7f
        val f13 = f7 * 0.7f
        val f14 = f5 * 0.8f
        val f15 = f6 * 0.8f
        val f16 = f7 * 0.8f
        RenderSystem.setShader { GameRenderer.getPositionTexColorNormalShader() }
        pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL)
        val f17 = floor(pY / 4.0).toFloat() * 4.0f
        if (prevCloudsType == CloudStatus.FANCY) {
            for (k in -3..4) {
                for (l in -3..4) {
                    val f18 = (k * 8).toFloat()
                    val f19 = (l * 8).toFloat()
                    if (f17 > -5.0f) {
                        pBuilder
                            .vertex((f18 + 0.0f).toDouble(), (f17 + 0.0f).toDouble(), (f19 + 8.0f).toDouble())
                            .uv((f18 + 0.0f) * 0.00390625f + f3, (f19 + 8.0f) * 0.00390625f + f4)
                            .color(f11, f12, f13, 0.8f)
                            .normal(0.0f, -1.0f, 0.0f)
                            .endVertex()
                        pBuilder
                            .vertex((f18 + 8.0f).toDouble(), (f17 + 0.0f).toDouble(), (f19 + 8.0f).toDouble())
                            .uv((f18 + 8.0f) * 0.00390625f + f3, (f19 + 8.0f) * 0.00390625f + f4)
                            .color(f11, f12, f13, 0.8f)
                            .normal(0.0f, -1.0f, 0.0f)
                            .endVertex()
                        pBuilder
                            .vertex((f18 + 8.0f).toDouble(), (f17 + 0.0f).toDouble(), (f19 + 0.0f).toDouble())
                            .uv((f18 + 8.0f) * 0.00390625f + f3, (f19 + 0.0f) * 0.00390625f + f4)
                            .color(f11, f12, f13, 0.8f)
                            .normal(0.0f, -1.0f, 0.0f)
                            .endVertex()
                        pBuilder
                            .vertex((f18 + 0.0f).toDouble(), (f17 + 0.0f).toDouble(), (f19 + 0.0f).toDouble())
                            .uv((f18 + 0.0f) * 0.00390625f + f3, (f19 + 0.0f) * 0.00390625f + f4)
                            .color(f11, f12, f13, 0.8f)
                            .normal(0.0f, -1.0f, 0.0f)
                            .endVertex()
                    }
                    if (f17 <= 5.0f) {
                        pBuilder
                            .vertex(
                                (f18 + 0.0f).toDouble(),
                                (f17 + 4.0f - 9.765625E-4f).toDouble(),
                                (f19 + 8.0f).toDouble()
                            )
                            .uv((f18 + 0.0f) * 0.00390625f + f3, (f19 + 8.0f) * 0.00390625f + f4)
                            .color(f5, f6, f7, 0.8f)
                            .normal(0.0f, 1.0f, 0.0f)
                            .endVertex()
                        pBuilder
                            .vertex(
                                (f18 + 8.0f).toDouble(),
                                (f17 + 4.0f - 9.765625E-4f).toDouble(),
                                (f19 + 8.0f).toDouble()
                            )
                            .uv((f18 + 8.0f) * 0.00390625f + f3, (f19 + 8.0f) * 0.00390625f + f4)
                            .color(f5, f6, f7, 0.8f)
                            .normal(0.0f, 1.0f, 0.0f)
                            .endVertex()
                        pBuilder
                            .vertex(
                                (f18 + 8.0f).toDouble(),
                                (f17 + 4.0f - 9.765625E-4f).toDouble(),
                                (f19 + 0.0f).toDouble()
                            )
                            .uv((f18 + 8.0f) * 0.00390625f + f3, (f19 + 0.0f) * 0.00390625f + f4)
                            .color(f5, f6, f7, 0.8f)
                            .normal(0.0f, 1.0f, 0.0f)
                            .endVertex()
                        pBuilder
                            .vertex(
                                (f18 + 0.0f).toDouble(),
                                (f17 + 4.0f - 9.765625E-4f).toDouble(),
                                (f19 + 0.0f).toDouble()
                            )
                            .uv((f18 + 0.0f) * 0.00390625f + f3, (f19 + 0.0f) * 0.00390625f + f4)
                            .color(f5, f6, f7, 0.8f)
                            .normal(0.0f, 1.0f, 0.0f)
                            .endVertex()
                    }
                    if (k > -1) {
                        for (i1 in 0..7) {
                            pBuilder
                                .vertex(
                                    (f18 + i1.toFloat() + 0.0f).toDouble(),
                                    (f17 + 0.0f).toDouble(),
                                    (f19 + 8.0f).toDouble()
                                )
                                .uv((f18 + i1.toFloat() + 0.5f) * 0.00390625f + f3, (f19 + 8.0f) * 0.00390625f + f4)
                                .color(f8, f9, f10, 0.8f)
                                .normal(-1.0f, 0.0f, 0.0f)
                                .endVertex()
                            pBuilder
                                .vertex(
                                    (f18 + i1.toFloat() + 0.0f).toDouble(),
                                    (f17 + 4.0f).toDouble(),
                                    (f19 + 8.0f).toDouble()
                                )
                                .uv((f18 + i1.toFloat() + 0.5f) * 0.00390625f + f3, (f19 + 8.0f) * 0.00390625f + f4)
                                .color(f8, f9, f10, 0.8f)
                                .normal(-1.0f, 0.0f, 0.0f)
                                .endVertex()
                            pBuilder
                                .vertex(
                                    (f18 + i1.toFloat() + 0.0f).toDouble(),
                                    (f17 + 4.0f).toDouble(),
                                    (f19 + 0.0f).toDouble()
                                )
                                .uv((f18 + i1.toFloat() + 0.5f) * 0.00390625f + f3, (f19 + 0.0f) * 0.00390625f + f4)
                                .color(f8, f9, f10, 0.8f)
                                .normal(-1.0f, 0.0f, 0.0f)
                                .endVertex()
                            pBuilder
                                .vertex(
                                    (f18 + i1.toFloat() + 0.0f).toDouble(),
                                    (f17 + 0.0f).toDouble(),
                                    (f19 + 0.0f).toDouble()
                                )
                                .uv((f18 + i1.toFloat() + 0.5f) * 0.00390625f + f3, (f19 + 0.0f) * 0.00390625f + f4)
                                .color(f8, f9, f10, 0.8f)
                                .normal(-1.0f, 0.0f, 0.0f)
                                .endVertex()
                        }
                    }
                    if (k <= 1) {
                        for (j2 in 0..7) {
                            pBuilder
                                .vertex(
                                    (f18 + j2.toFloat() + 1.0f - 9.765625E-4f).toDouble(),
                                    (f17 + 0.0f).toDouble(),
                                    (f19 + 8.0f).toDouble()
                                )
                                .uv((f18 + j2.toFloat() + 0.5f) * 0.00390625f + f3, (f19 + 8.0f) * 0.00390625f + f4)
                                .color(f8, f9, f10, 0.8f)
                                .normal(1.0f, 0.0f, 0.0f)
                                .endVertex()
                            pBuilder
                                .vertex(
                                    (f18 + j2.toFloat() + 1.0f - 9.765625E-4f).toDouble(),
                                    (f17 + 4.0f).toDouble(),
                                    (f19 + 8.0f).toDouble()
                                )
                                .uv((f18 + j2.toFloat() + 0.5f) * 0.00390625f + f3, (f19 + 8.0f) * 0.00390625f + f4)
                                .color(f8, f9, f10, 0.8f)
                                .normal(1.0f, 0.0f, 0.0f)
                                .endVertex()
                            pBuilder
                                .vertex(
                                    (f18 + j2.toFloat() + 1.0f - 9.765625E-4f).toDouble(),
                                    (f17 + 4.0f).toDouble(),
                                    (f19 + 0.0f).toDouble()
                                )
                                .uv((f18 + j2.toFloat() + 0.5f) * 0.00390625f + f3, (f19 + 0.0f) * 0.00390625f + f4)
                                .color(f8, f9, f10, 0.8f)
                                .normal(1.0f, 0.0f, 0.0f)
                                .endVertex()
                            pBuilder
                                .vertex(
                                    (f18 + j2.toFloat() + 1.0f - 9.765625E-4f).toDouble(),
                                    (f17 + 0.0f).toDouble(),
                                    (f19 + 0.0f).toDouble()
                                )
                                .uv((f18 + j2.toFloat() + 0.5f) * 0.00390625f + f3, (f19 + 0.0f) * 0.00390625f + f4)
                                .color(f8, f9, f10, 0.8f)
                                .normal(1.0f, 0.0f, 0.0f)
                                .endVertex()
                        }
                    }
                    if (l > -1) {
                        for (k2 in 0..7) {
                            pBuilder
                                .vertex(
                                    (f18 + 0.0f).toDouble(),
                                    (f17 + 4.0f).toDouble(),
                                    (f19 + k2.toFloat() + 0.0f).toDouble()
                                )
                                .uv((f18 + 0.0f) * 0.00390625f + f3, (f19 + k2.toFloat() + 0.5f) * 0.00390625f + f4)
                                .color(f14, f15, f16, 0.8f)
                                .normal(0.0f, 0.0f, -1.0f)
                                .endVertex()
                            pBuilder
                                .vertex(
                                    (f18 + 8.0f).toDouble(),
                                    (f17 + 4.0f).toDouble(),
                                    (f19 + k2.toFloat() + 0.0f).toDouble()
                                )
                                .uv((f18 + 8.0f) * 0.00390625f + f3, (f19 + k2.toFloat() + 0.5f) * 0.00390625f + f4)
                                .color(f14, f15, f16, 0.8f)
                                .normal(0.0f, 0.0f, -1.0f)
                                .endVertex()
                            pBuilder
                                .vertex(
                                    (f18 + 8.0f).toDouble(),
                                    (f17 + 0.0f).toDouble(),
                                    (f19 + k2.toFloat() + 0.0f).toDouble()
                                )
                                .uv((f18 + 8.0f) * 0.00390625f + f3, (f19 + k2.toFloat() + 0.5f) * 0.00390625f + f4)
                                .color(f14, f15, f16, 0.8f)
                                .normal(0.0f, 0.0f, -1.0f)
                                .endVertex()
                            pBuilder.vertex(
                                (f18 + 0.0f).toDouble(),
                                (f17 + 0.0f).toDouble(),
                                (f19 + k2.toFloat() + 0.0f).toDouble()
                            )
                                .uv((f18 + 0.0f) * 0.00390625f + f3, (f19 + k2.toFloat() + 0.5f) * 0.00390625f + f4)
                                .color(f14, f15, f16, 0.8f)
                                .normal(0.0f, 0.0f, -1.0f)
                                .endVertex()
                        }
                    }
                    if (l <= 1) {
                        for (l2 in 0..7) {
                            pBuilder
                                .vertex(
                                    (f18 + 0.0f).toDouble(),
                                    (f17 + 4.0f).toDouble(),
                                    (f19 + l2.toFloat() + 1.0f - 9.765625E-4f).toDouble()
                                )
                                .uv((f18 + 0.0f) * 0.00390625f + f3, (f19 + l2.toFloat() + 0.5f) * 0.00390625f + f4)
                                .color(f14, f15, f16, 0.8f)
                                .normal(0.0f, 0.0f, 1.0f)
                                .endVertex()
                            pBuilder
                                .vertex(
                                    (f18 + 8.0f).toDouble(),
                                    (f17 + 4.0f).toDouble(),
                                    (f19 + l2.toFloat() + 1.0f - 9.765625E-4f).toDouble()
                                )
                                .uv((f18 + 8.0f) * 0.00390625f + f3, (f19 + l2.toFloat() + 0.5f) * 0.00390625f + f4)
                                .color(f14, f15, f16, 0.8f)
                                .normal(0.0f, 0.0f, 1.0f)
                                .endVertex()
                            pBuilder
                                .vertex(
                                    (f18 + 8.0f).toDouble(),
                                    (f17 + 0.0f).toDouble(),
                                    (f19 + l2.toFloat() + 1.0f - 9.765625E-4f).toDouble()
                                )
                                .uv((f18 + 8.0f) * 0.00390625f + f3, (f19 + l2.toFloat() + 0.5f) * 0.00390625f + f4)
                                .color(f14, f15, f16, 0.8f)
                                .normal(0.0f, 0.0f, 1.0f)
                                .endVertex()
                            pBuilder
                                .vertex(
                                    (f18 + 0.0f).toDouble(),
                                    (f17 + 0.0f).toDouble(),
                                    (f19 + l2.toFloat() + 1.0f - 9.765625E-4f).toDouble()
                                )
                                .uv((f18 + 0.0f) * 0.00390625f + f3, (f19 + l2.toFloat() + 0.5f) * 0.00390625f + f4)
                                .color(f14, f15, f16, 0.8f)
                                .normal(0.0f, 0.0f, 1.0f)
                                .endVertex()
                        }
                    }
                }
            }
        } else {
            val j1 = 1
            val k1 = 32
            for (l1 in -32 until 32 step 32) {
                for (i2 in -32 until 32 step 32) {
                    pBuilder.vertex((l1 + 0).toDouble(), f17.toDouble(), (i2 + 32).toDouble())
                        .uv((l1 + 0).toFloat() * 0.00390625f + f3, (i2 + 32).toFloat() * 0.00390625f + f4)
                        .color(f5, f6, f7, 0.8f)
                        .normal(0.0f, -1.0f, 0.0f)
                        .endVertex()
                    pBuilder.vertex((l1 + 32).toDouble(), f17.toDouble(), (i2 + 32).toDouble())
                        .uv((l1 + 32).toFloat() * 0.00390625f + f3, (i2 + 32).toFloat() * 0.00390625f + f4)
                        .color(f5, f6, f7, 0.8f)
                        .normal(0.0f, -1.0f, 0.0f)
                        .endVertex()
                    pBuilder.vertex((l1 + 32).toDouble(), f17.toDouble(), (i2 + 0).toDouble())
                        .uv((l1 + 32).toFloat() * 0.00390625f + f3, (i2 + 0).toFloat() * 0.00390625f + f4)
                        .color(f5, f6, f7, 0.8f)
                        .normal(0.0f, -1.0f, 0.0f)
                        .endVertex()
                    pBuilder.vertex((l1 + 0).toDouble(), f17.toDouble(), (i2 + 0).toDouble())
                        .uv((l1 + 0).toFloat() * 0.00390625f + f3, (i2 + 0).toFloat() * 0.00390625f + f4)
                        .color(f5, f6, f7, 0.8f)
                        .normal(0.0f, -1.0f, 0.0f)
                        .endVertex()
                }
            }
        }
    }

}