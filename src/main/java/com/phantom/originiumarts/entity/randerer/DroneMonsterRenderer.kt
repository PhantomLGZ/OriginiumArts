package com.phantom.originiumarts.entity.randerer

import com.phantom.originiumarts.entity.DroneMonster
import com.phantom.originiumarts.entity.model.DroneMonsterModel
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import kotlin.math.max

class DroneMonsterRenderer (context: EntityRendererProvider.Context) :
    LivingEntityRenderer<DroneMonster, DroneMonsterModel>(
        context,
        DroneMonsterModel(context.bakeLayer(DroneMonsterModel.LAYER)),
        0.4f
    ) {

    override fun getRenderType(
        pLivingEntity: DroneMonster,
        pBodyVisible: Boolean,
        pTranslucent: Boolean,
        pGlowing: Boolean
    ): RenderType {
        return RenderType.entityTranslucent(getTextureLocation(pLivingEntity))
    }

    override fun shouldShowName(entity: DroneMonster): Boolean {
        return false
    }

    override fun getBlockLightLevel(entity: DroneMonster, blockPos: BlockPos): Int {
        return max(super.getBlockLightLevel(entity, blockPos), 8)
    }

    override fun getTextureLocation(entity: DroneMonster): ResourceLocation {
        return entity.getTextureLocation()
    }
}