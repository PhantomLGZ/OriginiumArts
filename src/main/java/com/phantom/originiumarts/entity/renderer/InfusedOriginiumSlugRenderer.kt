package com.phantom.originiumarts.entity.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.phantom.originiumarts.entity.InfusedOriginiumSlug
import com.phantom.originiumarts.entity.model.InfusedOriginiumSlugModel
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import kotlin.math.max

class InfusedOriginiumSlugRenderer(context: EntityRendererProvider.Context) :
    LivingEntityRenderer<InfusedOriginiumSlug, InfusedOriginiumSlugModel>(
        context,
        InfusedOriginiumSlugModel(context.bakeLayer(InfusedOriginiumSlugModel.LAYER)),
        0.4f
    ) {

    override fun scale(pLivingEntity: InfusedOriginiumSlug, pMatrixStack: PoseStack, pPartialTickTime: Float) {
        var f = pLivingEntity.iosDeathTime / 30f
        val f1 = 1.0f + Mth.sin(f * 100.0f) * f * 0.01f
        f = Mth.clamp(f, 0.0f, 1.0f)
        f *= f
        f *= f
        val f2 = (1.0f + f * 0.4f) * f1
        val f3 = (1.0f + f * 0.1f) / f1
        pMatrixStack.scale(f2, f3, f2)
    }

    override fun shouldShowName(entity: InfusedOriginiumSlug): Boolean {
        return false
    }

    override fun getBlockLightLevel(entity: InfusedOriginiumSlug, blockPos: BlockPos): Int {
        return max(super.getBlockLightLevel(entity, blockPos), 8)
    }

    override fun getTextureLocation(entity: InfusedOriginiumSlug): ResourceLocation {
        return entity.getTextureLocation()
    }
}