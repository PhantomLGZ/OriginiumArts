package com.phantom.originiumarts.entity.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Matrix4f
import com.mojang.math.Vector3f
import com.phantom.originiumarts.client.RL_WHITE
import com.phantom.originiumarts.entity.RayEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import kotlin.math.max


class RayEntityRenderer(
    context: EntityRendererProvider.Context
) : EntityRenderer<RayEntity>(context) {

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
        val vertexConsumer = renderTypeBuffer.getBuffer(RenderType.lightning())

        vertex(vertexConsumer, m4, Vec3(-size, -size, 0.0))
        vertex(vertexConsumer, m4, Vec3(-size, size, 0.0))
        vertex(vertexConsumer, m4, Vec3(size, size, 0.0))
        vertex(vertexConsumer, m4, Vec3(size, -size, 0.0))

        vertex(vertexConsumer, m4, Vec3(-size, -size, length))
        vertex(vertexConsumer, m4, Vec3(size, -size, length))
        vertex(vertexConsumer, m4, Vec3(size, size, length))
        vertex(vertexConsumer, m4, Vec3(-size, size, length))

        vertex(vertexConsumer, m4, Vec3(-size, size, 0.0))
        vertex(vertexConsumer, m4, Vec3(-size, size, length))
        vertex(vertexConsumer, m4, Vec3(size, size, length))
        vertex(vertexConsumer, m4, Vec3(size, size, 0.0))

        vertex(vertexConsumer, m4, Vec3(-size, -size, 0.0))
        vertex(vertexConsumer, m4, Vec3(size, -size, 0.0))
        vertex(vertexConsumer, m4, Vec3(size, -size, length))
        vertex(vertexConsumer, m4, Vec3(-size, -size, length))

        vertex(vertexConsumer, m4, Vec3(size, -size, 0.0))
        vertex(vertexConsumer, m4, Vec3(size, size, 0.0))
        vertex(vertexConsumer, m4, Vec3(size, size, length))
        vertex(vertexConsumer, m4, Vec3(size, -size, length))

        vertex(vertexConsumer, m4, Vec3(-size, -size, 0.0))
        vertex(vertexConsumer, m4, Vec3(-size, -size, length))
        vertex(vertexConsumer, m4, Vec3(-size, size, length))
        vertex(vertexConsumer, m4, Vec3(-size, size, 0.0))

        poseStack.popPose()
        super.render(entity, entityYaw, partialTicks, poseStack, renderTypeBuffer, 15728880)
    }

    private fun vertex(
        vertexConsumer: VertexConsumer,
        matrix4f: Matrix4f,
        diff: Vec3
    ) {
        vertexConsumer.vertex(matrix4f, diff.x.toFloat(), diff.y.toFloat(), diff.z.toFloat())
            .color(255, 255, 255, 255)
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

    override fun getTextureLocation(entity: RayEntity): ResourceLocation = RL_WHITE

}