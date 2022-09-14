package com.phantom.originiumarts.item

import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.entity.projectile.EtchedAmmo
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class EtchedAmmoItem : Item(basicSetting()), I3DItem {

    fun createAmmo(level: Level, entity: LivingEntity): EtchedAmmo {
        return EtchedAmmo(EntityRegister.ETCHED_AMMO.get(), level, entity)
    }

}