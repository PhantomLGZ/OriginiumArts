package com.phantom.originiumarts.entity.randerer

import com.phantom.originiumarts.entity.Rioter
import com.phantom.originiumarts.entity.model.RioterModel
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.HumanoidMobRenderer
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import kotlin.math.max

class RioterRenderer(context: EntityRendererProvider.Context) :
    HumanoidMobRenderer<Rioter, RioterModel>(
        context,
        RioterModel(context.bakeLayer(RioterModel.LAYER)),
        0.4f
    ) {

    override fun getRenderType(
        pLivingEntity: Rioter,
        pBodyVisible: Boolean,
        pTranslucent: Boolean,
        pGlowing: Boolean
    ): RenderType {
        return RenderType.entityTranslucent(getTextureLocation(pLivingEntity))
    }

    override fun shouldShowName(entity: Rioter): Boolean {
        return false
    }

    override fun getBlockLightLevel(entity: Rioter, blockPos: BlockPos): Int {
        return max(super.getBlockLightLevel(entity, blockPos), 8)
    }

    override fun getTextureLocation(entity: Rioter): ResourceLocation {
        return entity.getTextureLocation()
    }
}