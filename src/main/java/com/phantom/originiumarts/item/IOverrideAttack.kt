package com.phantom.originiumarts.item

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

interface IOverrideAttack {

    fun onClick(player: ServerPlayer) {
        if (player.getAttackStrengthScale(0f) > 0.5f) {
            attack(player)
        } else {
            player.resetAttackStrengthTicker()
        }
    }

    fun onClickInClient(player: Player) {
    }

    fun attack(player: ServerPlayer)
}