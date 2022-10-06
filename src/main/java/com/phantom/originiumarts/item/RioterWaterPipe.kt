package com.phantom.originiumarts.item

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TieredItem
import net.minecraft.world.item.crafting.Ingredient
import net.minecraftforge.common.ToolAction
import net.minecraftforge.common.ToolActions

class RioterWaterPipe : TieredItem(
    tier,
    basicSetting().stacksTo(1).durability(OAStandardStaff.tier.uses)
), ISpecialModelItem {

    override fun canPerformAction(stack: ItemStack?, toolAction: ToolAction?): Boolean {
        return ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction)
    }

    companion object {
        val tier = object : ArtsUnitItem.ArtUnitTier {
            override fun getUses(): Int = 200

            override fun getSpeed(): Float = 2f

            override fun getAttackDamageBonus(): Float = 1f

            override fun getLevel(): Int = 2

            override fun getEnchantmentValue(): Int = 22

            override fun getRepairIngredient(): Ingredient = Ingredient.of(Items.IRON_INGOT)

            override fun getSlotSize(): Int = 0

            override fun getEffectFactor(): Float = 0.1f

            override fun getUseFactor(): Double = 0.1
        }
    }

}
