package com.phantom.originiumarts.entity

import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class Rioter(type: EntityType<AbstractRioter>, worldLevel: Level) : AbstractRioter(type, worldLevel) {

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.let {
            it.addGoal(1, MeleeAttackGoal(this, 1.0, true))
        }
    }

    override fun populateDefaultEquipmentSlots(pDifficulty: DifficultyInstance) {
        setItemSlot(EquipmentSlot.MAINHAND, ItemStack(ItemRegister.RIOTER_WATER_PIPE.get()))
    }

}