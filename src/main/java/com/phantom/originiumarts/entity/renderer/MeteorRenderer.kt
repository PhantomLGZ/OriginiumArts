package com.phantom.originiumarts.entity.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Matrix4f
import com.mojang.math.Vector3f
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.model.MeteorModel
import com.phantom.originiumarts.entity.projectile.Meteor
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3

class MeteorRenderer(context: EntityRendererProvider.Context) :
    EntityRenderer<Meteor>(context) {

    private val model: MeteorModel

    init {
        model = MeteorModel((context.bakeLayer(MeteorModel.LAYER)))
    }

    companion object {
        val RL_METEOR = ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/meteor.png")
    }

    override fun render(
        entity: Meteor,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        renderTypeBuffer: MultiBufferSource,
        light: Int
    ) {

        poseStack.pushPose()
        poseStack.mulPose(Vector3f.YP.rotationDegrees(entity.yRot + 180))
        poseStack.mulPose(Vector3f.XP.rotationDegrees(entity.xRot))

        val size = entity.getSize() / 5.0
        val length = size * 32
        val color0 = intArrayOf(230, 204, 160, 255)
        val color1 = intArrayOf(230, 204, 160, 0)

        val m4 = poseStack.last().pose()
        val vertexConsumer = renderTypeBuffer.getBuffer(RenderType.lightning())

        vertex(vertexConsumer, m4, color0, Vec3(-size, size, 0.0))
        vertex(vertexConsumer, m4, color1, Vec3(-size / 4, size / 4, length))
        vertex(vertexConsumer, m4, color1, Vec3(size / 4, size / 4, length))
        vertex(vertexConsumer, m4, color0, Vec3(size, size, 0.0))

        vertex(vertexConsumer, m4, color0, Vec3(-size, -size, 0.0))
        vertex(vertexConsumer, m4, color0, Vec3(size, -size, 0.0))
        vertex(vertexConsumer, m4, color1, Vec3(size / 4, -size / 4, length))
        vertex(vertexConsumer, m4, color1, Vec3(-size / 4, -size / 4, length))

        vertex(vertexConsumer, m4, color0, Vec3(size, -size, 0.0))
        vertex(vertexConsumer, m4, color0, Vec3(size, size, 0.0))
        vertex(vertexConsumer, m4, color1, Vec3(size / 4, size / 4, length))
        vertex(vertexConsumer, m4, color1, Vec3(size / 4, -size / 4, length))

        vertex(vertexConsumer, m4, color0, Vec3(-size, -size, 0.0))
        vertex(vertexConsumer, m4, color1, Vec3(-size / 4, -size / 4, length))
        vertex(vertexConsumer, m4, color1, Vec3(-size / 4, size / 4, length))
        vertex(vertexConsumer, m4, color0, Vec3(-size, size, 0.0))
        poseStack.popPose()

        poseStack.pushPose()
        entity.getSize().let {
            poseStack.translate(0.0, -it * 1.5, 0.0)
            poseStack.scale(it, it, it)
        }
        model.renderToBuffer(
            poseStack,
            renderTypeBuffer.getBuffer(model.renderType(getTextureLocation(entity))),
            light,
            OverlayTexture.NO_OVERLAY,
            1.0f,
            1.0f,
            1.0f,
            1.0f
        )
        poseStack.popPose()
        super.render(entity, entityYaw, partialTicks, poseStack, renderTypeBuffer, light)
    }

    private fun vertex(
        vertexConsumer: VertexConsumer,
        matrix4f: Matrix4f,
        color: IntArray,
        diff: Vec3
    ) {
        vertexConsumer.vertex(matrix4f, diff.x.toFloat(), diff.y.toFloat(), diff.z.toFloat())
            .color(color[0], color[1], color[2], color[3])
            .endVertex()
    }

    override fun shouldRender(
        pLivingEntity: Meteor,
        pCamera: Frustum,
        pCamX: Double,
        pCamY: Double,
        pCamZ: Double
    ): Boolean {
        return true
    }

    override fun getTextureLocation(pEntity: Meteor): ResourceLocation = RL_METEOR

    override fun getBlockLightLevel(pEntity: Meteor, pPos: BlockPos): Int {
        return 15
    }
}