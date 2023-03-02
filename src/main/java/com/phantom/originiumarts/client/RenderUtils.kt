package com.phantom.originiumarts.client

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.phantom.originiumarts.OriginiumArtsMod
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.resources.ResourceLocation
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.tan

val RL_WHITE = ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/white.png")

fun drawQuad(
    lb: Pair<Double, Double>,
    rb: Pair<Double, Double>,
    rt: Pair<Double, Double>,
    lt: Pair<Double, Double>,
    argb: Long
) {
    val a = (argb shr 24 and 255).toInt()
    val r = (argb shr 16 and 255).toInt()
    val g = (argb shr 8 and 255).toInt()
    val b = (argb and 255).toInt()
    val bufferbuilder = Tesselator.getInstance().builder
    RenderSystem.enableBlend()
    RenderSystem.disableTexture()
    RenderSystem.defaultBlendFunc()
    RenderSystem.setShader { GameRenderer.getPositionColorShader() }
    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)
    bufferbuilder.vertex(lb.first, lb.second, 0.0).color(r, g, b, a).endVertex()
    bufferbuilder.vertex(rb.first, rb.second, 0.0).color(r, g, b, a).endVertex()
    bufferbuilder.vertex(rt.first, rt.second, 0.0).color(r, g, b, a).endVertex()
    bufferbuilder.vertex(lt.first, lt.second, 0.0).color(r, g, b, a).endVertex()
    bufferbuilder.end()
    BufferUploader.end(bufferbuilder)
    RenderSystem.enableTexture()
    RenderSystem.disableBlend()
}

fun drawLine(
    p1: Pair<Double, Double>,
    p2: Pair<Double, Double>,
    argb: Long
) {
    val a = (argb shr 24 and 255).toInt()
    val r = (argb shr 16 and 255).toInt()
    val g = (argb shr 8 and 255).toInt()
    val b = (argb and 255).toInt()
    val bufferbuilder = Tesselator.getInstance().builder
    RenderSystem.enableBlend()
    RenderSystem.disableTexture()
    RenderSystem.defaultBlendFunc()
    RenderSystem.setShader { GameRenderer.getPositionColorShader() }
    bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR)
    bufferbuilder.vertex(
        p1.first, p1.second, 0.0
    ).color(r, g, b, a).endVertex()
    bufferbuilder.vertex(
        p2.first, p2.second, 0.0
    ).color(r, g, b, a).endVertex()
    bufferbuilder.end()
    BufferUploader.end(bufferbuilder)
    RenderSystem.enableTexture()
    RenderSystem.disableBlend()
}

fun Double.isWithinAngularRange(halfRange: Double, dx: Double, dy: Double): Boolean {
    val angle = atan2(dy, dx).normAngle()
    return (angle in (this - halfRange)..(this + halfRange) || angle + 2 * PI in (this - halfRange)..(this + halfRange) || angle - 2 * PI in (this - halfRange)..(this + halfRange))
}

fun Double.normAngle(): Double {
    return when {
        this > PI -> this - 2 * PI
        this < -PI -> this + 2 * PI
        else -> this
    }
}

fun hardLightMixColor(base: Double, mix: Double): Double =
    if (mix > 0.5) (1 - (1 - mix) * (1 - base) * 2)
    else base * mix * 2

fun FloatArray.adjustBrightnessContrast(brightness: Double = 0.0, contrast: Double = 0.0): FloatArray {
    val b = brightness.coerceIn(-1.0..1.0)
    val c = contrast.coerceIn(-1.0..1.0)
    val k = tan(PI * (45 + 44 * c) / 180)
    val output = this.map {
        ((it - 0.5 * (1 - b)) * k + 0.5 * (1 + b)).coerceIn(0.0..1.0).toFloat()
    }.toFloatArray()
    return output
}

fun Double.adjustBrightnessContrast(brightness: Double = 0.0, contrast: Double = 0.0): Double {
    val b = brightness.coerceIn(-1.0..1.0)
    val c = contrast.coerceIn(-1.0..1.0)
    val k = tan(PI * (45 + 44 * c) / 180)
    return ((this - 0.5 * (1 - b)) * k + 0.5 * (1 + b)).coerceIn(0.0..1.0)
}