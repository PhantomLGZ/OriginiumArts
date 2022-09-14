package com.phantom.originiumarts.block

import com.phantom.originiumarts.OriginiumArtsMod
import net.minecraft.world.level.block.Block
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object BlockRegister {
    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, OriginiumArtsMod.MOD_ID)

    val ORIGINIUM_HOST_ROCK: RegistryObject<OriginiumHostRock> =
        BLOCKS.register("originium_host_rock") { OriginiumHostRock() }
    val ORIGINIUM_SMALL_BUD: RegistryObject<OriginiumClusterBlock> =
        BLOCKS.register("originium_small_bud") { OriginiumClusterBlock(3,4) }
    val ORIGINIUM_MEDIUM_BUD: RegistryObject<OriginiumClusterBlock> =
        BLOCKS.register("originium_medium_bud") { OriginiumClusterBlock(4,3) }
    val ORIGINIUM_LARGE_BUD: RegistryObject<OriginiumClusterBlock> =
        BLOCKS.register("originium_large_bud") { OriginiumClusterBlock(5,3) }
    val ORIGINIUM_CLUSTER: RegistryObject<OriginiumClusterBlock> =
        BLOCKS.register("originium_cluster") { OriginiumClusterBlock(7,3) }
    val OPERATING_BED_BLOCK: RegistryObject<OperatingBedBlock> =
        BLOCKS.register("operating_bed") { OperatingBedBlock() }

}