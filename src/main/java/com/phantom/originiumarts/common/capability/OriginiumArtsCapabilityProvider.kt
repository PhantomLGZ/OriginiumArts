package com.phantom.originiumarts.common.capability

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityProvider
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.common.util.LazyOptional

class OriginiumArtsCapabilityProvider() :
    CapabilityProvider<OriginiumArtsCapabilityProvider>(OriginiumArtsCapabilityProvider::class.java),
    INBTSerializable<CompoundTag> {

    companion object {
        val ORIGINIUM_ARTS_CAPABILITY: Capability<OriginiumArtsCapability> =
            CapabilityManager.get(object : CapabilityToken<OriginiumArtsCapability>() {})
    }

    private var oac: OriginiumArtsCapability? = null
    private val oacOpt: LazyOptional<OriginiumArtsCapability> = LazyOptional.of { createOriginiumArtsCapability() }

    private fun createOriginiumArtsCapability(): OriginiumArtsCapability {
        if (oac == null) {
            oac = OriginiumArtsCapability()
        }
        return oac!!
    }

    override fun <T : Any> getCapability(cap: Capability<T>): LazyOptional<T> {
        if (cap == ORIGINIUM_ARTS_CAPABILITY) {
            return oacOpt.cast()
        }
        return LazyOptional.empty()
    }

    override fun <T : Any> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        return getCapability(cap)
    }

    override fun serializeNBT(): CompoundTag =
        createOriginiumArtsCapability().serializeNBT()

    override fun deserializeNBT(nbt: CompoundTag?) {
        createOriginiumArtsCapability().deserializeNBT(nbt)
    }
}