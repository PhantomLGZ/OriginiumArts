package com.phantom.originiumarts.entity.renderer

import com.phantom.originiumarts.entity.AbstractRioter
import com.phantom.originiumarts.entity.model.RioterModel
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.HumanoidMobRenderer
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import kotlin.math.max

class RioterRenderer(context: EntityRendererProvider.Context) :
    HumanoidMobRenderer<AbstractRioter, RioterModel>(
        context,
        RioterModel(context.bakeLayer(RioterModel.LAYER)),
        0.4f
    ) {

    override fun getRenderType(
        pLivingEntity: AbstractRioter,
        pBodyVisible: Boolean,
        pTranslucent: Boolean,
        pGlowing: Boolean
    ): RenderType {
        return RenderType.entityTranslucent(getTextureLocation(pLivingEntity))
    }

    override fun shouldShowName(entity: AbstractRioter): Boolean {
        return false
    }

    override fun getBlockLightLevel(entity: AbstractRioter, blockPos: BlockPos): Int {
        return max(super.getBlockLightLevel(entity, blockPos), 8)
    }

    override fun getTextureLocation(entity: AbstractRioter): ResourceLocation {
        return entity.getTextureLocation()
    }
}