package com.phantom.originiumarts.datagenerate

import com.phantom.originiumarts.common.arts.AbstractArts
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.data.LanguageProvider
import net.minecraftforge.registries.RegistryObject

fun RegistryObject<out Block>.blockName(): String =
    this.get().registryName?.path ?: ""

fun RegistryObject<out Item>.itemName(): String =
    this.get().registryName?.path ?: ""

fun AbstractArts.addArtsLang(provider: LanguageProvider, name: String, description: String) {
    provider.add(getNameKey(), name)
    descriptionKey.let { provider.add(it, description) }
}


