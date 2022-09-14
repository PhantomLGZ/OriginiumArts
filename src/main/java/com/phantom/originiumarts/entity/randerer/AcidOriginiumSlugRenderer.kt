package com.phantom.originiumarts.entity.randerer

import com.phantom.originiumarts.entity.AcidOriginiumSlug
import com.phantom.originiumarts.entity.model.AcidOriginiumSlugModel
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import kotlin.math.max

class AcidOriginiumSlugRenderer(context: EntityRendererProvider.Context) :
    LivingEntityRenderer<AcidOriginiumSlug, AcidOriginiumSlugModel>(
        context,
        AcidOriginiumSlugModel(context.bakeLayer(AcidOriginiumSlugModel.LAYER)),
        0.4f
    ) {

    override fun shouldShowName(entity: AcidOriginiumSlug): Boolean {
        return false
    }

    override fun getBlockLightLevel(originiumSlug: AcidOriginiumSlug, blockPos: BlockPos): Int {
        return max(super.getBlockLightLevel(originiumSlug, blockPos), 8)
    }

    override fun getTextureLocation(entity: AcidOriginiumSlug): ResourceLocation {
        return entity.getTextureLocation()
    }
}