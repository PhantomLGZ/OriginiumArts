package com.phantom.originiumarts.item

import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.entity.projectile.EtchedAmmo
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class OAStandardPistol : ArtsUnitItem(basicSetting().stacksTo(1), 1), IOverrideAttack, I3DItem {

    override fun attack(player: ServerPlayer) {
        val level = player.level
        val ammo = player.consumeAmmo(level)
        if (ammo != null) {
            level.addFreshEntity(ammo)
            level.playSound(
                null,
                player.x,
                player.y,
                player.z,
                SoundRegister.PISTOL_N.get(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )
        }
    }

    private fun Player.consumeAmmo(level: Level): EtchedAmmo? {
        val ammoStack = inventory.items.find {
            it.`is`(ItemRegister.ETCHED_AMMO.get())
        }
        return if (ammoStack?.isEmpty != false) {
            null
        } else {
            val ammo = (ammoStack.item as EtchedAmmoItem).createAmmo(level, this)
            ammo.damageModifyList["attackStrengthScale"] = getAttackStrengthScale(0f)
            if (!abilities.instabuild) {
                ammoStack.shrink(1)
                if (ammoStack.isEmpty) {
                    inventory.removeItem(ammoStack)
                }
            }
            ammo
        }
    }

    override fun onEntitySwing(stack: ItemStack, entity: LivingEntity): Boolean {
        return true
    }
}