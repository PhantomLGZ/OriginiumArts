package com.phantom.originiumarts.entity

import com.phantom.originiumarts.entity.projectile.CocktailEntity
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.monster.RangedAttackMob
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class CocktailThrower(type: EntityType<AbstractRioter>, worldLevel: Level) :
    AbstractRioter(type, worldLevel), RangedAttackMob {

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.let {
            it.addGoal(1, RangedAttackGoal(this, 1.0, 40, 6.0F))
        }
    }

    override fun performRangedAttack(pTarget: LivingEntity, pVelocity: Float) {
        if (!level.isClientSide) {
            val lookDir = pTarget.eyePosition.subtract(eyePosition)
            val dh = lookDir.horizontalDistance()
            val dy = pTarget.eyePosition.y - eyePosition.y
            val dir = lookDir.subtract(0.0, lookDir.y, 0.0).normalize()
                .add(0.0, dy / dh + 0.03 * dh, 0.0)
            // TODO 音效
            level.addFreshEntity(CocktailEntity(EntityRegister.COCKTAIL.get(), level).apply {
                owner = this@CocktailThrower
                setPos(this@CocktailThrower.eyePosition)
                deltaMovement = dir
            })
        }
    }

    override fun populateDefaultEquipmentSlots(pDifficulty: DifficultyInstance) {
        setItemSlot(EquipmentSlot.MAINHAND, ItemStack(ItemRegister.COCKTAIL.get()))
    }

}