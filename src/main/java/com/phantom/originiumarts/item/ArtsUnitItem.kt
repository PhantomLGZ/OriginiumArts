package com.phantom.originiumarts.item

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.common.ArtsManager.getArtById
import com.phantom.originiumarts.common.capability.getUseDurationAmplifier
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level


abstract class ArtsUnitItem(properties: Properties, val slotSize: Int = 4) : Item(properties) {

    override fun releaseUsing(itemStack: ItemStack, level: Level, livingEntity: LivingEntity, usingDuration: Int) {
        if (livingEntity is Player) {
            livingEntity.getOACapability()?.selectedArtId?.getArtById()?.let { art ->
                if ((OriginiumArtsMod.maxUseDuration - usingDuration) * livingEntity.getUseDurationAmplifier() > art.needUseTick) {
                    art.onUse(livingEntity)
                } else {
                    art.makeParticle(livingEntity.eyePosition, level)
                }
            }
        }
        super.releaseUsing(itemStack, level, livingEntity, usingDuration)
    }

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(interactionHand)
        val art = player.getOACapability()?.selectedArtId?.getArtById()
        return if (player.isUsingItem) {
            InteractionResultHolder.fail(itemStack)
        } else if (art?.needUseTick == 0) {
            art.onUse(player)
            InteractionResultHolder.consume(itemStack)
        } else {
            player.startUsingItem(interactionHand)
            InteractionResultHolder.consume(itemStack)
        }
    }

    override fun getUseDuration(itemStack: ItemStack): Int {
        return OriginiumArtsMod.maxUseDuration
    }

}