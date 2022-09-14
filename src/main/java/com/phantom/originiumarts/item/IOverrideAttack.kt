package com.phantom.originiumarts.item

import net.minecraft.server.level.ServerPlayer

interface IOverrideAttack {

    fun onClick(player: ServerPlayer) {
        if (player.getAttackStrengthScale(0f) > 0.5f) {
            attack(player)
        } else {
            player.resetAttackStrengthTicker()
        }
    }

    fun attack(player: ServerPlayer)
}