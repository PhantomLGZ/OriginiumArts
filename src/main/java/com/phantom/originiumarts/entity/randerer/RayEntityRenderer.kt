package com.phantom.originiumarts.entity.randerer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Matrix3f
import com.mojang.math.Matrix4f
import com.mojang.math.Vector3f
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.RayEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import kotlin.math.max


class RayEntityRenderer(
    context: EntityRendererProvider.Context
) : EntityRenderer<RayEntity>(context) {

    private val resourceLocation = ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/white.png")
    private val RENDER_TYPE = RenderType.entityCutoutNoCull(resourceLocation)

    override fun render(
        entity: RayEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        renderTypeBuffer: MultiBufferSource,
        light: Int
    ) {
        poseStack.pushPose()
        poseStack.mulPose(Vector3f.YN.rotationDegrees(entity.yRot))
        poseStack.mulPose(Vector3f.XP.rotationDegrees(entity.xRot))

        val size = 0.01
        val length = entity.getLength().toDouble()

        val m4 = poseStack.last().pose()
        val m3 = poseStack.last().normal()
        val vertexConsumer = renderTypeBuffer.getBuffer(RENDER_TYPE)

        vertex(vertexConsumer, m4, m3, light, Vec3(-size, -size, 0.0), 0, 1)
        vertex(vertexConsumer, m4, m3, light, Vec3(size, -size, 0.0), 1, 1)
        vertex(vertexConsumer, m4, m3, light, Vec3(size, size, 0.0), 1, 0)
        vertex(vertexConsumer, m4, m3, light, Vec3(-size, size, 0.0), 0, 0)

        vertex(vertexConsumer, m4, m3, light, Vec3(-size, -size, length), 0, 1)
        vertex(vertexConsumer, m4, m3, light, Vec3(size, -size, length), 1, 1)
        vertex(vertexConsumer, m4, m3, light, Vec3(size, size, length), 1, 0)
        vertex(vertexConsumer, m4, m3, light, Vec3(-size, size, length), 0, 0)

        vertex(vertexConsumer, m4, m3, light, Vec3(-size, size, 0.0), 0, 1)
        vertex(vertexConsumer, m4, m3, light, Vec3(size, size, 0.0), 1, 1)
        vertex(vertexConsumer, m4, m3, light, Vec3(size, size, length), 1, 0)
        vertex(vertexConsumer, m4, m3, light, Vec3(-size, size, length), 0, 0)

        vertex(vertexConsumer, m4, m3, light, Vec3(-size, -size, 0.0), 0, 1)
        vertex(vertexConsumer, m4, m3, light, Vec3(size, -size, 0.0), 1, 1)
        vertex(vertexConsumer, m4, m3, light, Vec3(size, -size, length), 1, 0)
        vertex(vertexConsumer, m4, m3, light, Vec3(-size, -size, length), 0, 0)

        vertex(vertexConsumer, m4, m3, light, Vec3(size, -size, 0.0), 0, 1)
        vertex(vertexConsumer, m4, m3, light, Vec3(size, -size, length), 1, 1)
        vertex(vertexConsumer, m4, m3, light, Vec3(size, size, length), 1, 0)
        vertex(vertexConsumer, m4, m3, light, Vec3(size, size, 0.0), 0, 0)

        vertex(vertexConsumer, m4, m3, light, Vec3(-size, -size, 0.0), 0, 1)
        vertex(vertexConsumer, m4, m3, light, Vec3(-size, -size, length), 1, 1)
        vertex(vertexConsumer, m4, m3, light, Vec3(-size, size, length), 1, 0)
        vertex(vertexConsumer, m4, m3, light, Vec3(-size, size, 0.0), 0, 0)

        poseStack.popPose()
        super.render(entity, entityYaw, partialTicks, poseStack, renderTypeBuffer, 15728880)
    }

    private fun vertex(
        vertexConsumer: VertexConsumer,
        matrix4f: Matrix4f,
        matrix3f: Matrix3f,
        light: Int,
        diff: Vec3,
        u: Int,
        v: Int
    ) {
        vertexConsumer.vertex(matrix4f, diff.x.toFloat(), diff.y.toFloat(), diff.z.toFloat())
            .color(255, 255, 255, 255)
            .uv(u.toFloat(), v.toFloat())
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(light)
            .normal(matrix3f, 0.0f, 1.0f, 0.0f)
            .endVertex()
    }

    override fun shouldRender(
        pLivingEntity: RayEntity,
        pCamera: Frustum,
        pCamX: Double,
        pCamY: Double,
        pCamZ: Double
    ): Boolean {
        return true
    }

    override fun getBlockLightLevel(entity: RayEntity, blockPos: BlockPos): Int {
        return max(super.getBlockLightLevel(entity, blockPos), 15)
    }

    override fun getTextureLocation(entity: RayEntity): ResourceLocation =
        resourceLocation

}