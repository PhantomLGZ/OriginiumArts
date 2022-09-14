package com.phantom.originiumarts.entity.randerer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.field.BaseFieldEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

class BaseFieldRenderer<T : BaseFieldEntity<T>>(
    context: EntityRendererProvider.Context,
) : EntityRenderer<T>(context) {

    private val itemRenderer = context.itemRenderer

    override fun render(
        field: T,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        renderTypeBuffer: MultiBufferSource,
        light: Int
    ) {
        poseStack.pushPose()
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation())
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f))
        itemRenderer.renderStatic(
            ItemStack.EMPTY,
            ItemTransforms.TransformType.GUI,
            15728880,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            renderTypeBuffer,
            field.id
        )
        poseStack.popPose()
        super.render(field, entityYaw, partialTicks, poseStack, renderTypeBuffer, 15728880)
    }

    override fun getTextureLocation(field: T): ResourceLocation {
        return ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/item/transparent.png")
    }

}