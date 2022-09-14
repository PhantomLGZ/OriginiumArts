package com.phantom.originiumarts.item

import com.phantom.originiumarts.common.EffectRegister
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item

open class BattleRecord(private val level: BattleRecordLevel = BattleRecordLevel.DRILL) : Item(
    basicSetting()
        .food(
            FoodProperties.Builder()
                .nutrition(0)
                .saturationMod(0f)
                .alwaysEat()
                .fast()
                .effect({
                    MobEffectInstance(
                        EffectRegister.GAIN_EXPERIENCE.get(), 1, when (level) {
                            BattleRecordLevel.DRILL -> 1
                            BattleRecordLevel.FRONTLINE -> 2
                            BattleRecordLevel.TACTICAL -> 3
                            BattleRecordLevel.STRATEGIC -> 4
                        }
                    )
                }, 1.0f)
                .build()
        )
) {

    enum class BattleRecordLevel {
        DRILL, FRONTLINE, TACTICAL, STRATEGIC
    }

}