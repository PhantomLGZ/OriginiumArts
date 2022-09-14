package com.phantom.originiumarts.entity.randerer

import com.phantom.originiumarts.entity.OriginiumSlug
import com.phantom.originiumarts.entity.model.OriginiumSlugModel
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import kotlin.math.max

class OriginiumSlugRenderer(context: EntityRendererProvider.Context) :
    LivingEntityRenderer<OriginiumSlug, OriginiumSlugModel>(
        context,
        OriginiumSlugModel(context.bakeLayer(OriginiumSlugModel.LAYER)),
        0.4f
    ) {

    override fun shouldShowName(entity: OriginiumSlug): Boolean {
        return false
    }

    override fun getBlockLightLevel(originiumSlug: OriginiumSlug, blockPos: BlockPos): Int {
        return max(super.getBlockLightLevel(originiumSlug, blockPos), 8)
    }

    override fun getTextureLocation(entity: OriginiumSlug): ResourceLocation {
        return entity.getTextureLocation()
    }
}