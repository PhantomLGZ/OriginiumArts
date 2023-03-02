package com.phantom.originiumarts.item

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.block.BlockRegister
import com.phantom.originiumarts.entity.EntityRegister
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraftforge.common.ForgeSpawnEggItem
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ItemRegister {

    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, OriginiumArtsMod.MOD_ID)

    val TEST_ITEM: RegistryObject<Item> = ITEMS.register("test_item") { TestItem() }

    val ORIGINIUM: RegistryObject<Item> = ITEMS.register("originium") { Item(basicSetting()) }
    val ORIGINIUM_INGOT: RegistryObject<Item> = ITEMS.register("originium_ingot") { Item(basicSetting()) }
    val ORIGINIUM_FRAGMENT: RegistryObject<Item> = ITEMS.register("originium_fragment") { Item(basicSetting()) }
    val COCKTAIL: RegistryObject<Item> = ITEMS.register("cocktail") { Cocktail() }

    //FOOD
    val DRILL_BATTLE_RECORD: RegistryObject<Item> =
        ITEMS.register("drill_battle_record") { BattleRecord(BattleRecord.BattleRecordLevel.DRILL) }
    val FRONTLINE_BATTLE_RECORD: RegistryObject<Item> =
        ITEMS.register("frontline_battle_record") { BattleRecord(BattleRecord.BattleRecordLevel.FRONTLINE) }
    val TACTICAL_BATTLE_RECORD: RegistryObject<Item> =
        ITEMS.register("tactical_battle_record") { BattleRecord(BattleRecord.BattleRecordLevel.TACTICAL) }
    val STRATEGIC_BATTLE_RECORD: RegistryObject<Item> =
        ITEMS.register("strategic_battle_record") { BattleRecord(BattleRecord.BattleRecordLevel.STRATEGIC) }

    //Special ITEM
    val OA_STANDARD_PISTOL: RegistryObject<OAStandardPistol> =
        ITEMS.register("oa_standard_pistol") { OAStandardPistol() }
    val OA_STANDARD_STAFF: RegistryObject<OAStandardStaff> =
        ITEMS.register("oa_standard_staff") { OAStandardStaff() }
    val OA_STANDARD_SWORD: RegistryObject<OAStandardSword> =
        ITEMS.register("oa_standard_sword") { OAStandardSword() }
    val RIOTER_WATER_PIPE: RegistryObject<RioterWaterPipe> =
        ITEMS.register("rioter_water_pipe") { RioterWaterPipe() }
    val ETCHED_AMMO: RegistryObject<EtchedAmmoItem> =
        ITEMS.register("etched_ammo_item") { EtchedAmmoItem() }

    //BlockItem
    val ORIGINIUM_HOST_ROCK_ITEM: RegistryObject<BlockItem> =
        ITEMS.register("originium_host_rock") { BlockItem(BlockRegister.ORIGINIUM_HOST_ROCK.get(), basicSetting()) }
    val ORIGINIUM_CLUSTER_ITEM: RegistryObject<BlockItem> =
        ITEMS.register("originium_cluster") { BlockItem(BlockRegister.ORIGINIUM_CLUSTER.get(), basicSetting()) }
    val ORIGINIUM_LARGE_BUD_ITEM: RegistryObject<BlockItem> =
        ITEMS.register("originium_large_bud") { BlockItem(BlockRegister.ORIGINIUM_LARGE_BUD.get(), basicSetting()) }
    val ORIGINIUM_MEDIUM_BUD_ITEM: RegistryObject<BlockItem> =
        ITEMS.register("originium_medium_bud") { BlockItem(BlockRegister.ORIGINIUM_MEDIUM_BUD.get(), basicSetting()) }
    val ORIGINIUM_SMALL_BUD_ITEM: RegistryObject<BlockItem> =
        ITEMS.register("originium_small_bud") { BlockItem(BlockRegister.ORIGINIUM_SMALL_BUD.get(), basicSetting()) }
    val ORIGINIUM_DUST_ITEM: RegistryObject<BlockItem> =
        ITEMS.register("originium_dust") { BlockItem(BlockRegister.ORIGINIUM_DUST.get(), basicSetting()) }
    val OPERATING_BED_ITEM: RegistryObject<BlockItem> =
        ITEMS.register("operating_bed") { BlockItem(BlockRegister.OPERATING_BED_BLOCK.get(), basicSetting()) }

    //EGG
    val ORIGINIUM_SLUG_EGG: RegistryObject<ForgeSpawnEggItem> =
        ITEMS.register("originium_slug") {
            ForgeSpawnEggItem(EntityRegister.ORIGINIUM_SLUG, 0x97876E, 0xFFFF08, basicSetting())
        }

    val ACID_ORIGINIUM_SLUG_EGG: RegistryObject<ForgeSpawnEggItem> =
        ITEMS.register("acid_originium_slug") {
            ForgeSpawnEggItem(EntityRegister.ACID_ORIGINIUM_SLUG, 0x97876E, 0xFF4208, basicSetting())
        }

    val DRONE_MONSTER_EGG: RegistryObject<ForgeSpawnEggItem> =
        ITEMS.register("drone_monster_egg") {
            ForgeSpawnEggItem(EntityRegister.DRONE_MONSTER, 0x373737, 0x7E7E7E, basicSetting())
        }

    val RIOTER_EGG: RegistryObject<ForgeSpawnEggItem> =
        ITEMS.register("rioter_egg") {
            ForgeSpawnEggItem(EntityRegister.RIOTER, 0x383840, 0xD0D0D0, basicSetting())
        }

    val COCKTAIL_THROWER_EGG: RegistryObject<ForgeSpawnEggItem> =
        ITEMS.register("cocktail_thrower_egg") {
            ForgeSpawnEggItem(EntityRegister.COCKTAIL_THROWER, 0x383840, 0x6A332B, basicSetting())
        }

    //ART
    val EMPTY_ART: RegistryObject<Item> = ITEMS.register("art_empty") {
        Item(Item.Properties())
    }
    val IGNITION_ART: RegistryObject<Item> = ITEMS.register("art_ignition") {
        Item(Item.Properties())
    }
    val FINE_BLENDING_ART: RegistryObject<Item> = ITEMS.register("art_fine_blending") {
        Item(Item.Properties())
    }
    val SWIRLING_VORTEX_ART: RegistryObject<Item> = ITEMS.register("art_swirling_vortex") {
        Item(Item.Properties())
    }
    val SCORCHED_EARTH_ART: RegistryObject<Item> = ITEMS.register("art_scorched_earth") {
        Item(Item.Properties())
    }
    val DIFFUSION_CURRENT_ART: RegistryObject<Item> = ITEMS.register("art_diffusion_current") {
        Item(Item.Properties())
    }
    val GLORIOUS_SHARDS_ART: RegistryObject<Item> = ITEMS.register("art_glorious_shards") {
        Item(Item.Properties())
    }
    val ORIGINIUM_ACID: RegistryObject<Item> = ITEMS.register("originium_acid") {
        Item(Item.Properties())
    }

}