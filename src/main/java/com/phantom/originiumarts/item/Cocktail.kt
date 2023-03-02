package com.phantom.originiumarts.item

import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.entity.projectile.CocktailEntity
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class Cocktail : Item(basicSetting()) {

    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (!pLevel.isClientSide) {
            val itemStack = pPlayer.getItemInHand(pUsedHand)
            itemStack.shrink(1)
            if (itemStack.isEmpty) {
                pPlayer.inventory.removeItem(itemStack)
            }
            pLevel.addFreshEntity(CocktailEntity(EntityRegister.COCKTAIL.get(), pLevel).apply {
                setPos(pPlayer.eyePosition)
                deltaMovement = pPlayer.lookAngle
                owner = pPlayer
            })
            return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide())
        }
        return super.use(pLevel, pPlayer, pUsedHand)
    }
}