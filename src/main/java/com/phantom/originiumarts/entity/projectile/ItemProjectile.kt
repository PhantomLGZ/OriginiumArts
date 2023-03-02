package com.phantom.originiumarts.entity.projectile

import com.mojang.blaze3d.vertex.PoseStack
import com.phantom.originiumarts.OriginiumArtsMod
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult

abstract class ItemProjectile(
    entityType: EntityType<out ItemProjectile>,
    level: Level
) : AbstractProjectile(entityType, level) {

    fun getTextureLocation(): ResourceLocation =
        ResourceLocation(
            OriginiumArtsMod.MOD_ID,
            "textures/item/${getItemStack().item.registryName}.png"
        )

    override fun tick() {
        super.tick()
        if (level.isClientSide) {
            makeParticle()
        }
    }

    override fun onHitEntity(pResult: EntityHitResult) {
        super.onHitEntity(pResult)
        remove(RemovalReason.KILLED)
    }

    override fun onHitBlock(pResult: BlockHitResult) {
        super.onHitBlock(pResult)
        remove(RemovalReason.KILLED)
    }

    abstract fun getItemStack(): ItemStack

    open fun poseStackInRender(poseStack: PoseStack) {}

    open fun makeParticle() {}

}