package com.phantom.originiumarts.item

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraftforge.common.ToolAction
import net.minecraftforge.common.ToolActions

class OAStandardSword : ArtsUnitItem(
    basicSetting().stacksTo(1).durability(OAStandardStaff.tier.uses),
    tier
), ISpecialModelItem {

    override fun canPerformAction(stack: ItemStack?, toolAction: ToolAction?): Boolean {
        return ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction)
    }

    companion object {
        val tier = object : ArtUnitTier {
            override fun getUses(): Int = 400

            override fun getSpeed(): Float = 4f

            override fun getAttackDamageBonus(): Float = 2f

            override fun getLevel(): Int = 2

            override fun getEnchantmentValue(): Int = 22

            override fun getRepairIngredient(): Ingredient = Ingredient.of(Items.IRON_INGOT)

            override fun getSlotSize(): Int = 2

            override fun getEffectFactor(): Float = 0.8f

            override fun getUseFactor(): Double = 0.4
        }
    }

}