package com.phantom.originiumarts.entity.randerer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.projectile.OriginiumAcid
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

class OriginiumAcidRenderer(
    context: EntityRendererProvider.Context
) : EntityRenderer<OriginiumAcid>(context) {

    private val itemRenderer = context.itemRenderer

    override fun render(
        entity: OriginiumAcid,
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
            itemRenderer.renderStatic(
                itemStack,
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

    companion object {
        private val itemStack = ItemStack(ItemRegister.ORIGINIUM_ACID.get())
    }

    override fun getTextureLocation(pEntity: OriginiumAcid): ResourceLocation {
        return ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/item/originium_acid.png")
    }

}