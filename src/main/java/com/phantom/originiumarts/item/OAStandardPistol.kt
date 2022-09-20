package com.phantom.originiumarts.item

import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.entity.projectile.EtchedAmmo
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.Level

class OAStandardPistol : ArtsUnitItem(
    basicSetting().stacksTo(1).durability(tier.uses),
    tier
), IOverrideAttack, ISpecialModelItem {

    override fun attack(player: ServerPlayer) {
        val level = player.level
        val ammo = player.consumeAmmo(level)
        if (ammo != null) {
            level.addFreshEntity(ammo)
            player.getItemInHand(InteractionHand.MAIN_HAND)
                .hurtAndBreak(1, player) {
                    it.broadcastBreakEvent(it.usedItemHand)
                }
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

    companion object {
        val tier = object : ArtUnitTier {
            override fun getUses(): Int = 250

            override fun getSpeed(): Float = 1f

            override fun getAttackDamageBonus(): Float = 0.2f

            override fun getLevel(): Int = 2

            override fun getEnchantmentValue(): Int = 14

            override fun getRepairIngredient(): Ingredient = Ingredient.of(Items.IRON_INGOT)

            override fun getSlotSize(): Int = 1

            override fun getEffectFactor(): Float = 0.5f

            override fun getUseFactor(): Double = 0.8
        }
    }

}