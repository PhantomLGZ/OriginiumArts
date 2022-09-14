package com.phantom.originiumarts

import com.phantom.originiumarts.block.BlockRegister
import com.phantom.originiumarts.client.ClientSetup
import com.phantom.originiumarts.client.ParticleRegister
import com.phantom.originiumarts.common.CommonSetup
import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.network.OANetworking
import com.phantom.originiumarts.common.EffectRegister
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod("originiumarts")
class OriginiumArtsMod {

    init {
        MOD_BUS.let {
            ItemRegister.ITEMS.register(it)
            BlockRegister.BLOCKS.register(it)
            EffectRegister.EFFECTS.register(it)
            EntityRegister.ENTITY_TYPE.register(it)
            SoundRegister.SOUNDS.register(it)
            ParticleRegister.PARTICLE_TYPES.register(it)
            it.addListener(this::onClientSetup)
            it.addListener(this::onCommonSetup)
        }
        OANetworking.registerMessage()
        OAConfig.register()
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork(ClientSetup::setup)
    }

    private fun onCommonSetup(event: FMLCommonSetupEvent) {
        event.enqueueWork(CommonSetup::setup)
    }

    companion object {
        const val MOD_ID = "originiumarts"
        const val GROUP_NAME = MOD_ID
        const val maxUseDuration = 72000

        @JvmField
        val ITEM_GROUP: CreativeModeTab = object : CreativeModeTab(GROUP_NAME) {
            override fun makeIcon(): ItemStack {
                return ItemStack(ItemRegister.ORIGINIUM.get())
            }
        }
    }
}