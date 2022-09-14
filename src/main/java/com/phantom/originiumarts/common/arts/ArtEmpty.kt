package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

object ArtEmpty : AbstractArts(
    uniqueName = "empty"
) {

    override fun onUse(player: Player) {}

    private val itemStack = ItemStack(ItemRegister.EMPTY_ART.get())

    override fun getItemStack(): ItemStack {
        return itemStack
    }

    override fun consumeExperienceValue(): Int = 0

    override fun learningUnmetConditions(player: Player): List<Component> = listOf()
}