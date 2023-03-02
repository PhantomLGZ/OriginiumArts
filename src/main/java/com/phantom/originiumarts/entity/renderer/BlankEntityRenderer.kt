package com.phantom.originiumarts.entity.renderer

import com.phantom.originiumarts.client.RL_WHITE
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

open class BlankEntityRenderer(
    context: EntityRendererProvider.Context,
) : EntityRenderer<Entity>(context) {

    override fun getTextureLocation(field: Entity): ResourceLocation {
        return RL_WHITE
    }

}