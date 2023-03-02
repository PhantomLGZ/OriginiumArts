package com.phantom.originiumarts.entity.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import com.phantom.originiumarts.entity.projectile.ItemProjectile
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation

class ItemProjectileRenderer(
    context: EntityRendererProvider.Context
) : EntityRenderer<ItemProjectile>(context) {

    private val itemRenderer = context.itemRenderer

    override fun render(
        entity: ItemProjectile,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        renderTypeBuffer: MultiBufferSource,
        light: Int
    ) {
        if (entity.tickCount >= 2 || entityRenderDispatcher.camera.entity.distanceToSqr(entity) >= 12.25) {
            poseStack.pushPose()
            poseStack.mulPose(entityRenderDispatcher.cameraOrientation())
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f))
            entity.poseStackInRender(poseStack)
            itemRenderer.renderStatic(
                entity.getItemStack(),
                ItemTransforms.TransformType.GUI,
                15728880,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                renderTypeBuffer,
                entity.id
            )
            poseStack.popPose()
            super.render(entity, entityYaw, partialTicks, poseStack, renderTypeBuffer, 15728880)
        }
    }

    override fun getTextureLocation(pEntity: ItemProjectile): ResourceLocation =
        pEntity.getTextureLocation()

}