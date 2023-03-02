package com.phantom.originiumarts.client.weather

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.math.Vector3f
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.client.ClientSetup
import com.phantom.originiumarts.client.adjustBrightnessContrast
import com.phantom.originiumarts.client.normAngle
import com.phantom.originiumarts.common.weather.*
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.material.FogType
import net.minecraft.world.phys.Vec3
import net.minecraftforge.client.ISkyRenderHandler
import kotlin.math.*
import kotlin.random.Random

object OASkyRenderHandler : ISkyRenderHandler {

    private val MOON_LOCATION = ResourceLocation("textures/environment/moon_phases.png")
    private val SUN_LOCATION = ResourceLocation("textures/environment/sun.png")
    private val END_SKY_LOCATION = ResourceLocation("textures/environment/end_sky.png")
    private val METEOR_LOCATION = ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/environment/meteor.png")

    private var starBuffer: VertexBuffer? = null
    private var skyBuffer: VertexBuffer? = null
    private var darkBuffer: VertexBuffer? = null
    private val meteors = mutableListOf<MeteorInfo>()

    init {
        createStars()
        createLightSky()
        createDarkSky()
        for (i in 0..20) {
            meteors.add(MeteorInfo())
        }
    }

    override fun render(
        ticks: Int,
        partialTick: Float,
        poseStack: PoseStack,
        level: ClientLevel,
        minecraft: Minecraft
    ) {
        val camera = minecraft.gameRenderer.mainCamera
        val flag2 = minecraft.level!!.effects().isFoggyAt(Mth.floor(camera.position.x), Mth.floor(camera.position.y))
                || minecraft.gui.bossOverlay.shouldCreateWorldFog()
        val fogtype: FogType = camera.fluidInCamera
        if (!flag2) {
            if (fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA) {
                val cameraEntity = camera.entity
                if (cameraEntity is LivingEntity) {
                    if (cameraEntity.hasEffect(MobEffects.BLINDNESS)) {
                        return
                    }
                }
                when (minecraft.level?.effects()?.skyType()) {
                    DimensionSpecialEffects.SkyType.END -> {
                        renderEndSky(poseStack)
                    }
                    DimensionSpecialEffects.SkyType.NORMAL -> {
                        renderNormalSky(camera, ticks, partialTick, poseStack, level, minecraft)
                    }
                    else -> {}
                }
            }
        }
    }

    /**
     * copy from [net.minecraft.client.renderer.LevelRenderer.renderEndSky]
     */
    private fun renderEndSky(pPoseStack: PoseStack) {
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.depthMask(false)
        RenderSystem.setShader { GameRenderer.getPositionTexColorShader() }
        RenderSystem.setShaderTexture(0, END_SKY_LOCATION)
        val tesselator = Tesselator.getInstance()
        val bufferBuilder = tesselator.builder
        for (i in 0..5) {
            pPoseStack.pushPose()
            if (i == 1) {
                pPoseStack.mulPose(Vector3f.XP.rotationDegrees(90.0f))
            }
            if (i == 2) {
                pPoseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0f))
            }
            if (i == 3) {
                pPoseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f))
            }
            if (i == 4) {
                pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0f))
            }
            if (i == 5) {
                pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0f))
            }
            val matrix4f = pPoseStack.last().pose()
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -100.0f).uv(0.0f, 0.0f).color(40, 40, 40, 255).endVertex()
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).uv(0.0f, 16.0f).color(40, 40, 40, 255).endVertex()
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).uv(16.0f, 16.0f).color(40, 40, 40, 255).endVertex()
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -100.0f).uv(16.0f, 0.0f).color(40, 40, 40, 255).endVertex()
            tesselator.end()
            pPoseStack.popPose()
        }
        RenderSystem.depthMask(true)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    private fun renderNormalSky(
        camera: Camera,
        ticks: Int,
        partialTick: Float,
        poseStack: PoseStack,
        level: ClientLevel,
        minecraft: Minecraft
    ) {
        val projectionMatrix = ClientSetup.projectionMatrix
        RenderSystem.disableTexture()
        val vec3 = level.getSkyColor(camera.position, partialTick)
            .getCatastropheSkyColorInClient(camera.position)
        val skyColorR = vec3.x.toFloat()
        val skyColorG = vec3.y.toFloat()
        val skyColorB = vec3.z.toFloat()
        FogRenderer.levelFogColor()
        val bufferBuilder = Tesselator.getInstance().builder
        RenderSystem.depthMask(false)
        RenderSystem.setShaderColor(skyColorR, skyColorG, skyColorB, 1.0f)
        val shaderInstance = RenderSystem.getShader()
        skyBuffer?.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderInstance!!)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        level.effects().getSunriseColor(level.getTimeOfDay(partialTick), partialTick)?.let {
            renderSunrise(
                it,
                if (sin(level.getSunAngle(partialTick)) < 0.0f) 180.0f else 0.0f,
                poseStack,
                bufferBuilder
            )
        }
        CatastropheClientManager.catastropheInfoList.forEach {
            val angle = (-180 * atan2(it.posX - camera.position.x, it.posZ - camera.position.z).normAngle() / PI - 90)
                .run { if (this < -180) this + 360 else this }
            val distance = it.getDistance(camera.position.x, camera.position.z)
            if (distance < BASE_VISIBLE_DISTANCE) {
                val alpha = min(1 - (distance / (BASE_VISIBLE_DISTANCE)).pow(2), it.transition.toDouble())
                val para = ((BASE_VISIBLE_DISTANCE - distance) / (BASE_VISIBLE_DISTANCE)).toFloat()
                val color = getCatastropheColorInClient(Vec3(it.posX, 62.0, it.posZ), it) { value ->
                    value.adjustBrightnessContrast(-0.2 * para)
                }
                renderCatastropheColor(
                    floatArrayOf(color.x.toFloat(), color.y.toFloat(), color.z.toFloat(), alpha.toFloat() * 0.8f),
                    angle.toFloat(),
                    poseStack,
                    bufferBuilder,
                    para
                )
            }
        }
        RenderSystem.enableTexture()
        RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )
        if (getAffectingCatastropheType(level, camera.position) == CatastropheType.METEOR_SHOWERS) {
            drawMeteorShowers(poseStack, bufferBuilder, ticks)
        }
        poseStack.pushPose()
        val f11 = 1.0f - max(
            level.getRainLevel(partialTick),
            min(1f, CatastropheClientManager.catastropheEffect.toFloat() * 2)
        )
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, f11)
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-90.0f))
        poseStack.mulPose(Vector3f.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360.0f))
        val matrix4f1 = poseStack.last().pose()
        var f12 = 30.0f
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, SUN_LOCATION)
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
        bufferBuilder.vertex(matrix4f1, -f12, 100.0f, -f12).uv(0.0f, 0.0f).endVertex()
        bufferBuilder.vertex(matrix4f1, f12, 100.0f, -f12).uv(1.0f, 0.0f).endVertex()
        bufferBuilder.vertex(matrix4f1, f12, 100.0f, f12).uv(1.0f, 1.0f).endVertex()
        bufferBuilder.vertex(matrix4f1, -f12, 100.0f, f12).uv(0.0f, 1.0f).endVertex()
        bufferBuilder.end()
        BufferUploader.end(bufferBuilder)
        f12 = 20.0f
        RenderSystem.setShaderTexture(0, MOON_LOCATION)
        val k: Int = level.moonPhase
        val l = k % 4
        val i1 = k / 4 % 2
        val f13 = (l + 0).toFloat() / 4.0f
        val f14 = (i1 + 0).toFloat() / 2.0f
        val f15 = (l + 1).toFloat() / 4.0f
        val f16 = (i1 + 1).toFloat() / 2.0f
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
        bufferBuilder.vertex(matrix4f1, -f12, -100.0f, f12).uv(f15, f16).endVertex()
        bufferBuilder.vertex(matrix4f1, f12, -100.0f, f12).uv(f13, f16).endVertex()
        bufferBuilder.vertex(matrix4f1, f12, -100.0f, -f12).uv(f13, f14).endVertex()
        bufferBuilder.vertex(matrix4f1, -f12, -100.0f, -f12).uv(f15, f14).endVertex()
        bufferBuilder.end()
        BufferUploader.end(bufferBuilder)
        RenderSystem.disableTexture()
        val f9 = level.getStarBrightness(partialTick) * f11
        if (f9 > 0.0f) {
            RenderSystem.setShaderColor(f9, f9, f9, f9)
            FogRenderer.setupNoFog()
            starBuffer?.drawWithShader(
                poseStack.last().pose(),
                projectionMatrix,
                GameRenderer.getPositionShader()!!
            )
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.disableBlend()
        poseStack.popPose()
        RenderSystem.disableTexture()
        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f)
        val d0 =
            minecraft.player!!.getEyePosition(partialTick).y - level.levelData.getHorizonHeight(level)
        if (d0 < 0.0) {
            poseStack.pushPose()
            poseStack.translate(0.0, 12.0, 0.0)
            darkBuffer?.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderInstance!!)
            poseStack.popPose()
        }
        if (level.effects().hasGround()) {
            RenderSystem.setShaderColor(
                skyColorR * 0.2f + 0.04f,
                skyColorG * 0.2f + 0.04f,
                skyColorB * 0.6f + 0.1f,
                1.0f
            )
        } else {
            RenderSystem.setShaderColor(skyColorR, skyColorG, skyColorB, 1.0f)
        }
        RenderSystem.enableTexture()
        RenderSystem.depthMask(true)
    }

    private fun renderSunrise(
        floatArray: FloatArray,
        zRotationDegrees: Float,
        poseStack: PoseStack,
        bufferBuilder: BufferBuilder
    ) {
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        RenderSystem.disableTexture()
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        poseStack.pushPose()
        poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0f))
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(zRotationDegrees))
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0f))
        val f3 = floatArray[0]
        val f4 = floatArray[1]
        val f5 = floatArray[2]
        val matrix4f = poseStack.last().pose()
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR)
        bufferBuilder.vertex(matrix4f, 0f, 100f, 0f)
            .color(f3, f4, f5, floatArray[3])
            .endVertex()
        for (j in 0..16) {
            val a = j.toFloat() * (PI.toFloat() * 2f) / 16.0f
            val s = Mth.sin(a)
            val c = Mth.cos(a)
            bufferBuilder.vertex(matrix4f, s * 120f, c * 120f, -c * 40f * floatArray[3])
                .color(floatArray[0], floatArray[1], floatArray[2], 0.0f)
                .endVertex()
        }
        bufferBuilder.end()
        BufferUploader.end(bufferBuilder)
        poseStack.popPose()
    }

    private fun renderCatastropheColor(
        floatArray: FloatArray,
        zRotationDegrees: Float,
        poseStack: PoseStack,
        bufferBuilder: BufferBuilder,
        size: Float
    ) {
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        RenderSystem.disableTexture()
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        poseStack.pushPose()
        poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0f))
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(zRotationDegrees))
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0f))
        val f3 = floatArray[0]
        val f4 = floatArray[1]
        val f5 = floatArray[2]
        val matrix4f = poseStack.last().pose()
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR)
        bufferBuilder.vertex(matrix4f, 0f, 100f - 20 * size, 0f)
            .color(f3, f4, f5, floatArray[3])
            .endVertex()
        for (j in 0..16) {
            val a = j * (PI.toFloat() * 2f) / 16.0f
            val s = Mth.sin(a)
            val c = Mth.cos(a)
            bufferBuilder.vertex(
                matrix4f,
                s * (80 + 40 * size),
                c * (100 - 150 * size),
                -c * (20 + 20 * (1 - size))
            )
                .color(floatArray[0], floatArray[1], floatArray[2], 0f)
                .endVertex()
        }
        bufferBuilder.end()
        BufferUploader.end(bufferBuilder)
        poseStack.popPose()
    }

    private class MeteorInfo(
        val yp: Float = Random.nextDouble(-120.0, -60.0).toFloat(),
        val zp: Float = Random.nextDouble(-30.0, 30.0).toFloat(),
        val xp: Float = Random.nextInt(360).toFloat(),
        val size: Float = Random.nextDouble(0.2, 1.0).toFloat(),
        val height: Float = Random.nextDouble(0.6, 1.0).toFloat() * 100f,
        val speed: Float = Random.nextDouble(1.5, 3.0).toFloat()
    )

    private fun drawMeteorShowers(poseStack: PoseStack, bufferBuilder: BufferBuilder, ticks: Int) {
        val effect = CatastropheClientManager.catastropheEffect
        meteors.forEach {
            poseStack.pushPose()
            poseStack.mulPose(Vector3f.YP.rotationDegrees(it.yp))
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(it.zp))
            poseStack.mulPose(Vector3f.XP.rotationDegrees((it.xp + ticks * it.speed) % 360))
            val matrix4f1 = poseStack.last().pose()
            val f12 = 4.0f * it.size
            val t1 = 0.8f * it.size
            val t2 = 0.8f * it.size
            val tLength = 80 * it.size
            val trackColor = floatArrayOf(0.9f, 0.8f, 0.63f)

            RenderSystem.setShaderColor(1f, 1f, 1f, effect.toFloat())
            RenderSystem.setShader { GameRenderer.getPositionColorShader() }
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)
            bufferBuilder.vertex(matrix4f1, -t1, it.height, -t1 - tLength)
                .color(trackColor[0], trackColor[1], trackColor[2], 0f)
                .endVertex()
            bufferBuilder.vertex(matrix4f1, t1, it.height, -t1 - tLength)
                .color(trackColor[0], trackColor[1], trackColor[2], 0f)
                .endVertex()
            bufferBuilder.vertex(matrix4f1, t1, it.height, t1)
                .color(trackColor[0], trackColor[1], trackColor[2], 1f)
                .endVertex()
            bufferBuilder.vertex(matrix4f1, -t1, it.height, t1)
                .color(trackColor[0], trackColor[1], trackColor[2], 1f)
                .endVertex()

            bufferBuilder.vertex(matrix4f1, -t1 - t2 * 0.5f, it.height, -t1 - tLength)
                .color(trackColor[0], trackColor[1], trackColor[2], 0f)
                .endVertex()
            bufferBuilder.vertex(matrix4f1, t1, it.height, -t1 - tLength)
                .color(trackColor[0], trackColor[1], trackColor[2], 0f)
                .endVertex()
            bufferBuilder.vertex(matrix4f1, t1, it.height, t1)
                .color(trackColor[0], trackColor[1], trackColor[2], 1f)
                .endVertex()
            bufferBuilder.vertex(matrix4f1, -t1 - t2, it.height, t1)
                .color(trackColor[0], trackColor[1], trackColor[2], 0f)
                .endVertex()

            bufferBuilder.vertex(matrix4f1, -t1, it.height, -t1 - tLength)
                .color(trackColor[0], trackColor[1], trackColor[2], 0f)
                .endVertex()
            bufferBuilder.vertex(matrix4f1, t1 + t2 * 0.5f, it.height, -t1 - tLength)
                .color(trackColor[0], trackColor[1], trackColor[2], 0f)
                .endVertex()
            bufferBuilder.vertex(matrix4f1, t1 + t2, it.height, t1)
                .color(trackColor[0], trackColor[1], trackColor[2], 0f)
                .endVertex()
            bufferBuilder.vertex(matrix4f1, -t1, it.height, t1)
                .color(trackColor[0], trackColor[1], trackColor[2], 1f)
                .endVertex()
            bufferBuilder.end()
            BufferUploader.end(bufferBuilder)

            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            RenderSystem.setShaderTexture(0, METEOR_LOCATION)
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
            bufferBuilder.vertex(matrix4f1, -f12, it.height, -f12).uv(0.0f, 0.0f).endVertex()
            bufferBuilder.vertex(matrix4f1, f12, it.height, -f12).uv(1.0f, 0.0f).endVertex()
            bufferBuilder.vertex(matrix4f1, f12, it.height, f12).uv(1.0f, 1.0f).endVertex()
            bufferBuilder.vertex(matrix4f1, -f12, it.height, f12).uv(0.0f, 1.0f).endVertex()
            bufferBuilder.end()
            BufferUploader.end(bufferBuilder)

            poseStack.popPose()
        }
    }

    /**
     * copy from [net.minecraft.client.renderer.LevelRenderer.createDarkSky]
     */
    private fun createDarkSky() {
        val tesselator = Tesselator.getInstance()
        val bufferbuilder = tesselator.builder
        darkBuffer?.close()
        darkBuffer = VertexBuffer()
        buildSkyDisc(bufferbuilder, -16.0f)
        darkBuffer!!.upload(bufferbuilder)
    }

    /**
     * copy from [net.minecraft.client.renderer.LevelRenderer.createLightSky]
     */
    private fun createLightSky() {
        val tesselator = Tesselator.getInstance()
        val bufferbuilder = tesselator.builder
        skyBuffer?.close()
        skyBuffer = VertexBuffer()
        buildSkyDisc(bufferbuilder, 16.0f)
        skyBuffer!!.upload(bufferbuilder)
    }

    /**
     * copy from [net.minecraft.client.renderer.LevelRenderer.buildSkyDisc]
     */
    private fun buildSkyDisc(pBuilder: BufferBuilder, pY: Float) {
        val f = sign(pY) * 512.0f
        val f1 = 512.0f
        RenderSystem.setShader { GameRenderer.getPositionShader() }
        pBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION)
        pBuilder.vertex(0.0, pY.toDouble(), 0.0).endVertex()
        var i = -180
        while (i <= 180) {
            pBuilder.vertex(
                (f * Mth.cos(i.toFloat() * (Math.PI.toFloat() / 180f))).toDouble(),
                pY.toDouble(),
                (512.0f * Mth.sin(i.toFloat() * (Math.PI.toFloat() / 180f))).toDouble()
            ).endVertex()
            i += 45
        }
        pBuilder.end()
    }

    /**
     * copy from [net.minecraft.client.renderer.LevelRenderer.createStars]
     */
    private fun createStars() {
        val tesselator = Tesselator.getInstance()
        val bufferbuilder = tesselator.builder
        RenderSystem.setShader { GameRenderer.getPositionShader() }
        starBuffer?.close()
        starBuffer = VertexBuffer()
        drawStars(bufferbuilder)
        bufferbuilder.end()
        starBuffer!!.upload(bufferbuilder)
    }

    /**
     * copy from [net.minecraft.client.renderer.LevelRenderer.drawStars]
     */
    private fun drawStars(pBuilder: BufferBuilder) {
        val random = Random(10842L)
        pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION)
        for (i in 0..1499) {
            var d0 = (random.nextFloat() * 2.0f - 1.0f).toDouble()
            var d1 = (random.nextFloat() * 2.0f - 1.0f).toDouble()
            var d2 = (random.nextFloat() * 2.0f - 1.0f).toDouble()
            val d3 = (0.15f + random.nextFloat() * 0.1f).toDouble()
            var d4 = d0 * d0 + d1 * d1 + d2 * d2
            if (d4 < 1.0 && d4 > 0.01) {
                d4 = 1.0 / sqrt(d4)
                d0 *= d4
                d1 *= d4
                d2 *= d4
                val d5 = d0 * 100.0
                val d6 = d1 * 100.0
                val d7 = d2 * 100.0
                val d8 = atan2(d0, d2)
                val d9 = sin(d8)
                val d10 = cos(d8)
                val d11 = atan2(sqrt(d0 * d0 + d2 * d2), d1)
                val d12 = sin(d11)
                val d13 = cos(d11)
                val d14 = random.nextDouble() * PI * 2.0
                val d15 = sin(d14)
                val d16 = cos(d14)
                for (j in 0..3) {
                    val d17 = 0.0
                    val d18 = ((j and 2) - 1).toDouble() * d3
                    val d19 = ((j + 1 and 2) - 1).toDouble() * d3
                    val d20 = 0.0
                    val d21 = d18 * d16 - d19 * d15
                    val d22 = d19 * d16 + d18 * d15
                    val d23 = d21 * d12 + 0.0 * d13
                    val d24 = 0.0 * d12 - d21 * d13
                    val d25 = d24 * d9 - d22 * d10
                    val d26 = d22 * d9 + d24 * d10
                    pBuilder.vertex(d5 + d25, d6 + d23, d7 + d26).endVertex()
                }
            }
        }
    }
}