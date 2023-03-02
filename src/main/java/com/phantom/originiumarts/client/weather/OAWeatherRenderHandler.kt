package com.phantom.originiumarts.client.weather

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.common.weather.CatastropheClientManager
import com.phantom.originiumarts.common.weather.CatastropheClientManager.nearestCatastropheInfo
import com.phantom.originiumarts.common.weather.CatastropheType
import com.phantom.originiumarts.common.weather.getCatastropheColorInClient
import com.phantom.originiumarts.common.weather.getAffectingCatastropheType
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.Vec3
import net.minecraftforge.client.IWeatherRenderHandler
import java.util.*
import kotlin.math.*

object OAWeatherRenderHandler : IWeatherRenderHandler {

    private val RAIN_LOCATION = ResourceLocation("textures/environment/rain.png")
    private val SNOW_LOCATION = ResourceLocation("textures/environment/snow.png")
    private val SAND_LOCATION = ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/environment/sand.png")

    private val rainSizeX = FloatArray(1024)
    private val rainSizeZ = FloatArray(1024)

    init {
        for (i in 0..31) {
            for (j in 0..31) {
                val f = (j - 16).toFloat()
                val f1 = (i - 16).toFloat()
                val f2 = Mth.sqrt(f * f + f1 * f1)
                rainSizeX[i shl 5 or j] = -f1 / f2
                rainSizeZ[i shl 5 or j] = f / f2
            }
        }
    }

    override fun render(
        ticks: Int,
        partialTick: Float,
        level: ClientLevel,
        minecraft: Minecraft,
        lightTexture: LightTexture,
        camX: Double,
        camY: Double,
        camZ: Double
    ) {
        val catastropheEffect = CatastropheClientManager.catastropheEffect.toFloat()
        val f = level.getRainLevel(partialTick)
        val isRain = f > 0.0f
        if (isRain || catastropheEffect > 0) {
            lightTexture.turnOnLightLayer()
            val iCamX = Mth.floor(camX)
            val iCamY = Mth.floor(camY)
            val iCamZ = Mth.floor(camZ)
            val range = if (Minecraft.useFancyGraphics()) 10 else 5
            val tesselator = Tesselator.getInstance()
            val bufferBuilder = tesselator.builder
            RenderSystem.disableCull()
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.enableDepthTest()

            RenderSystem.depthMask(Minecraft.useShaderTransparency())
            var textureFlag = -1
            val f1 = ticks.toFloat() + partialTick
            RenderSystem.setShader { GameRenderer.getParticleShader() }
            val color = nearestCatastropheInfo?.let {
                getCatastropheColorInClient(Vec3(camX, camY, camZ), it)
            } ?: Vec3(1.0, 1.0, 1.0)
            RenderSystem.setShaderColor(
                color.x.toFloat(),
                color.y.toFloat(),
                color.z.toFloat(),
                1.0f - when {
                    f > 0f || catastropheEffect > 0.5f -> 0f
                    else -> 2f * (0.5f - catastropheEffect)
                }
            )
            val blockPos = BlockPos.MutableBlockPos()

            for (zi in iCamZ - range..iCamZ + range) {
                for (xi in iCamX - range..iCamX + range) {
                    val l1 = (zi - iCamZ + 16) * 32 + xi - iCamX + 16
                    val rainSizeX = rainSizeX[l1].toDouble() * 0.5
                    val rainSizeZ = rainSizeZ[l1].toDouble() * 0.5
                    blockPos.set(xi.toDouble(), camY, zi.toDouble())
                    val biome = level.getBiome(blockPos).value()

                    val i2 = level.getHeight(Heightmap.Types.MOTION_BLOCKING, xi, zi)
                    val minY = max(iCamY - range, i2) - 1
                    val maxY = max(iCamY + range, i2)
                    val cenY = max(iCamY, i2)
                    if (minY != maxY) {
                        val random =
                            Random((xi * xi * 3121 + xi * 45238971 xor zi * zi * 418711 + zi * 13761).toLong())
                        blockPos.set(xi, minY, zi)
                        if (biome.precipitation != Biome.Precipitation.NONE) {
                            if (biome.warmEnoughToRain(blockPos)
                                && (isRain || getAffectingCatastropheType(
                                    level,
                                    Vec3(xi.toDouble(), camY, zi.toDouble())
                                ) == CatastropheType.RAINSTORM)
                            ) {
                                if (textureFlag != 0) {
                                    if (textureFlag >= 0) {
                                        tesselator.end()
                                    }
                                    textureFlag = 0
                                    RenderSystem.setShaderTexture(0, RAIN_LOCATION)
                                    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE)
                                }
                                val seed = ticks + xi * xi * 3121 + xi * 45238971 + zi * zi * 418711 + zi * 13761 and 31
                                val f2 = -(seed + partialTick) / 32.0f * (3.0f + random.nextFloat())
                                val f3 = sqrt((xi + 0.5 - camX).pow(2) + (zi + 0.5 - camZ).pow(2)).toFloat() /
                                        range.toFloat()
                                val alpha = ((1.0f - f3 * f3) * 0.5f + 0.5f)
                                blockPos.set(xi, cenY, zi)
                                val lightColor = LevelRenderer.getLightColor(level, blockPos)
                                bufferBuilder
                                    .vertex(xi - camX - rainSizeX + 0.5, maxY - camY, zi - camZ - rainSizeZ + 0.5)
                                    .uv(0.0f, minY * 0.25f + f2)
                                    .color(1.0f, 1.0f, 1.0f, alpha)
                                    .uv2(lightColor)
                                    .endVertex()
                                bufferBuilder
                                    .vertex(xi - camX + rainSizeX + 0.5, maxY - camY, zi - camZ + rainSizeZ + 0.5)
                                    .uv(1.0f, minY * 0.25f + f2)
                                    .color(1.0f, 1.0f, 1.0f, alpha)
                                    .uv2(lightColor)
                                    .endVertex()
                                bufferBuilder
                                    .vertex(xi - camX + rainSizeX + 0.5, minY - camY, zi - camZ + rainSizeZ + 0.5)
                                    .uv(1.0f, maxY * 0.25f + f2)
                                    .color(1.0f, 1.0f, 1.0f, alpha)
                                    .uv2(lightColor)
                                    .endVertex()
                                bufferBuilder
                                    .vertex(xi - camX - rainSizeX + 0.5, minY - camY, zi - camZ - rainSizeZ + 0.5)
                                    .uv(0.0f, maxY * 0.25f + f2)
                                    .color(1.0f, 1.0f, 1.0f, alpha)
                                    .uv2(lightColor)
                                    .endVertex()
                            } else if (isRain || getAffectingCatastropheType(
                                    level,
                                    Vec3(xi.toDouble(), camY, zi.toDouble())
                                ) == CatastropheType.BLIZZARD
                            ) {
                                if (textureFlag != 1) {
                                    if (textureFlag >= 0) {
                                        tesselator.end()
                                    }
                                    textureFlag = 1
                                    RenderSystem.setShaderTexture(0, SNOW_LOCATION)
                                    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE)
                                }
                                val f5 = -((ticks and 511) + partialTick) / 512.0f
                                val f6 = (random.nextDouble() + f1 * 0.01 * (random.nextGaussian())).toFloat()
                                val f7 = (random.nextDouble() + (f1 * random.nextGaussian()) * 0.001).toFloat()
                                val d3 = xi.toDouble() + 0.5 - camX
                                val d5 = zi.toDouble() + 0.5 - camZ
                                val f8 = sqrt(d3 * d3 + d5 * d5).toFloat() / range.toFloat()
                                val f9 = ((1.0f - f8 * f8) * 0.3f + 0.5f)
                                blockPos[xi, cenY] = zi
                                val k3 = LevelRenderer.getLightColor(level, blockPos)
                                val l3 = k3 shr 16 and '\uffff'.code
                                val i4 = k3 and '\uffff'.code
                                val j4 = (l3 * 3 + 240) / 4
                                val k4 = (i4 * 3 + 240) / 4
                                bufferBuilder
                                    .vertex(xi - camX - rainSizeX + 0.5, maxY - camY, zi - camZ - rainSizeZ + 0.5)
                                    .uv(0.0f + f6, minY.toFloat() * 0.25f + f7)
                                    .color(1.0f, 1.0f, 1.0f, f9)
                                    .uv2(k4, j4)
                                    .endVertex()
                                bufferBuilder
                                    .vertex(xi - camX + rainSizeX + 0.5, maxY - camY, zi - camZ + rainSizeZ + 0.5)
                                    .uv(1.0f + f6, minY.toFloat() * 0.25f + f7)
                                    .color(1.0f, 1.0f, 1.0f, f9)
                                    .uv2(k4, j4)
                                    .endVertex()
                                bufferBuilder
                                    .vertex(xi - camX + rainSizeX + 0.5, minY - camY, zi - camZ + rainSizeZ + 0.5)
                                    .uv(1.0f + f6, maxY.toFloat() * 0.25f + f5 + f7)
                                    .color(1.0f, 1.0f, 1.0f, f9)
                                    .uv2(k4, j4)
                                    .endVertex()
                                bufferBuilder
                                    .vertex(xi - camX - rainSizeX + 0.5, minY - camY, zi - camZ - rainSizeZ + 0.5)
                                    .uv(0.0f + f6, maxY.toFloat() * 0.25f + f5 + f7)
                                    .color(1.0f, 1.0f, 1.0f, f9)
                                    .uv2(k4, j4)
                                    .endVertex()
                            }
                        }
                        if (getAffectingCatastropheType(
                                level,
                                Vec3(xi.toDouble(), camY, zi.toDouble())
                            ) == CatastropheType.SANDSTORM
                        ) {
                            if (textureFlag != 1) {
                                if (textureFlag >= 0) {
                                    tesselator.end()
                                }
                                textureFlag = 1
                                RenderSystem.setShaderTexture(0, SAND_LOCATION)
                                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE)
                            }
                            val f5 = -((ticks and 511) + partialTick) / 512.0f
                            val f6 = (random.nextDouble() + f1 * 0.2 * (random.nextGaussian())).toFloat()
                            val f7 = (random.nextDouble() + (f1 * random.nextGaussian()) * 0.01).toFloat()
                            val d3 = xi.toDouble() + 0.5 - camX
                            val d5 = zi.toDouble() + 0.5 - camZ
                            val f8 = sqrt(d3 * d3 + d5 * d5).toFloat() / range.toFloat()
                            val f9 = ((1.0f - f8 * f8) * 0.3f + 0.5f)
                            blockPos[xi, cenY] = zi
                            val k3 = LevelRenderer.getLightColor(level, blockPos)
                            val l3 = k3 shr 16 and '\uffff'.code
                            val i4 = k3 and '\uffff'.code
                            val j4 = (l3 * 3 + 240) / 4
                            val k4 = (i4 * 3 + 240) / 4
                            bufferBuilder
                                .vertex(xi - camX - rainSizeX + 0.5, maxY - camY, zi - camZ - rainSizeZ + 0.5)
                                .uv(0.0f + f6, minY.toFloat() * 0.25f + f7)
                                .color(1.0f, 1.0f, 1.0f, f9)
                                .uv2(k4, j4)
                                .endVertex()
                            bufferBuilder
                                .vertex(xi - camX + rainSizeX + 0.5, maxY - camY, zi - camZ + rainSizeZ + 0.5)
                                .uv(1.0f + f6, minY.toFloat() * 0.25f + f7)
                                .color(1.0f, 1.0f, 1.0f, f9)
                                .uv2(k4, j4)
                                .endVertex()
                            bufferBuilder
                                .vertex(xi - camX + rainSizeX + 0.5, minY - camY, zi - camZ + rainSizeZ + 0.5)
                                .uv(1.0f + f6, maxY.toFloat() * 0.25f + f5 + f7)
                                .color(1.0f, 1.0f, 1.0f, f9)
                                .uv2(k4, j4)
                                .endVertex()
                            bufferBuilder
                                .vertex(xi - camX - rainSizeX + 0.5, minY - camY, zi - camZ - rainSizeZ + 0.5)
                                .uv(0.0f + f6, maxY.toFloat() * 0.25f + f5 + f7)
                                .color(1.0f, 1.0f, 1.0f, f9)
                                .uv2(k4, j4)
                                .endVertex()
                        }
                    }
                }
            }
            if (textureFlag >= 0) {
                tesselator.end()
            }
            RenderSystem.enableCull()
            RenderSystem.disableBlend()
            lightTexture.turnOffLightLayer()
        }
    }
}
