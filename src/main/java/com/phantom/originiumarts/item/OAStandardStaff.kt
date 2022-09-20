package com.phantom.originiumarts.item

import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient

class OAStandardStaff : ArtsUnitItem(
    basicSetting().stacksTo(1).durability(tier.uses),
    tier
), ISpecialModelItem {

    companion object {
        val tier = object : ArtUnitTier {
            override fun getUses(): Int = 300

            override fun getSpeed(): Float = 2f

            override fun getAttackDamageBonus(): Float = 0.5f

            override fun getLevel(): Int = 2

            override fun getEnchantmentValue(): Int = 22

            override fun getRepairIngredient(): Ingredient = Ingredient.of(Items.IRON_INGOT)

            override fun getSlotSize(): Int = 3

            override fun getEffectFactor(): Float = 1f

            override fun getUseFactor(): Double = 1.0
        }
    }

}