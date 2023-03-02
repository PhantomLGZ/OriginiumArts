package com.phantom.originiumarts.entity.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.entity.projectile.EtchedAmmo
import com.phantom.originiumarts.entity.model.EtchedAmmoModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class EtchedAmmoRenderer(context: EntityRendererProvider.Context) :
    EntityRenderer<EtchedAmmo>(context) {

    private val model: EtchedAmmoModel

    init {
        model = EtchedAmmoModel(context.bakeLayer(EtchedAmmoModel.LAYER))
    }

    override fun render(
        entity: EtchedAmmo,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        renderTypeBuffer: MultiBufferSource,
        light: Int
    ) {
        poseStack.pushPose()
        0.125f.let {
            poseStack.scale(it, it, it)
        }
        poseStack.mulPose(
            Vector3f.YP.rotationDegrees(
                (Mth.lerp(partialTicks, entity.yRotO, entity.yRot)-180)
            )
        )
        poseStack.mulPose(
            Vector3f.ZP.rotationDegrees(
                Mth.lerp(partialTicks, entity.xRotO, entity.xRot)
            )
        )

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

    override fun getTextureLocation(entity: EtchedAmmo): ResourceLocation {
        return ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/etched_ammo.png")
    }

}