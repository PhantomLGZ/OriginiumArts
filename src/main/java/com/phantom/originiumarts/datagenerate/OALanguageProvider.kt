package com.phantom.originiumarts.datagenerate

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.block.BlockRegister
import com.phantom.originiumarts.client.KeyBinds
import com.phantom.originiumarts.client.TextKey
import com.phantom.originiumarts.common.arts.*
import com.phantom.originiumarts.common.EffectRegister
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.data.DataGenerator
import net.minecraftforge.common.data.LanguageProvider

class OALanguageProvider(gen: DataGenerator, locale: String) :
    LanguageProvider(gen, OriginiumArtsMod.MOD_ID, locale) {

    override fun addTranslations() {
        add("itemGroup." + OriginiumArtsMod.GROUP_NAME, "Originium Arts")

        add(ItemRegister.TEST_ITEM.get(), "Test")
        add(ItemRegister.ORIGINIUM.get(), "Originium")
        add(ItemRegister.ORIGINIUM_FRAGMENT.get(), "Originium Fragment")
        add(ItemRegister.ORIGINIUM_INGOT.get(), "Originium Ingot")
        add(ItemRegister.ETCHED_AMMO.get(), "Etched Ammo")
        add(ItemRegister.OA_STANDARD_PISTOL.get(), "Standard Pistol")
        add(ItemRegister.OA_STANDARD_STAFF.get(), "Standard Staff")
        add(ItemRegister.DRILL_BATTLE_RECORD.get(), "Drill Battle Record")
        add(ItemRegister.FRONTLINE_BATTLE_RECORD.get(), "Frontline Battle Record")
        add(ItemRegister.TACTICAL_BATTLE_RECORD.get(), "Tactical Battle Record")
        add(ItemRegister.STRATEGIC_BATTLE_RECORD.get(), "Strategic Battle Record")
        add(ItemRegister.ORIGINIUM_SLUG_EGG.get(), "Originium Slug Egg")
        add(ItemRegister.ACID_ORIGINIUM_SLUG_EGG.get(), "Acid Originium Slug Egg")
        add(ItemRegister.DRONE_MONSTER_EGG.get(), "\"Monster\" Egg")

        add(BlockRegister.ORIGINIUM_HOST_ROCK.get(), "Originium Host Rock")
        add(BlockRegister.ORIGINIUM_CLUSTER.get(), "Originium Cluster")
        add(BlockRegister.ORIGINIUM_LARGE_BUD.get(), "Originium Large Bud")
        add(BlockRegister.ORIGINIUM_MEDIUM_BUD.get(), "Originium Medium Bud")
        add(BlockRegister.ORIGINIUM_SMALL_BUD.get(), "Originium Small Bud")
        add(BlockRegister.OPERATING_BED_BLOCK.get(), "Operating Bed")
        add(EntityRegister.ORIGINIUM_SLUG.get(), "Originium Slug")
        add(EntityRegister.ACID_ORIGINIUM_SLUG.get(), "Acid Originium Slug")
        add(EntityRegister.DRONE_MONSTER.get(), "\"Monster\"")

        //EFFECT
        add(EffectRegister.ACUTE_ORIPATHY.get(), "Acute Oripathy")
        add(EffectRegister.HIGH_SPEED_CHANT.get(), "High Speed Chant")
        add(EffectRegister.CORROSION.get(), "Corrosion")

        //GUI
        add(TextKey.KEY_TEXT_FULL_NOW, "It's full now")
        add(TextKey.KEY_TEXT_NEED_EXPERIENCE, "Need experience")
        add(TextKey.KEY_TEXT_FLAWED, "Flawed")
        add(TextKey.KEY_TEXT_NORMAL, "Normal")
        add(TextKey.KEY_TEXT_STANDARD, "Standard")
        add(TextKey.KEY_TEXT_EXCELLENT, "Excellent")
        add(TextKey.KEY_TEXT_OUTSTANDING, "Outstanding")
        add(TextKey.KEY_TEXT_UNKNOWN, "■■")
        add(TextKey.KEY_TEXT_STRENGTH, "Strength")
        add(TextKey.KEY_TEXT_MOBILITY, "Mobility")
        add(TextKey.KEY_TEXT_ENDURANCE, "Endurance")
        add(TextKey.KEY_TEXT_TACTICAL_ACUMEN, "Tactical Acumen")
        add(TextKey.KEY_TEXT_COMBAT_SKILL, "Combat Skill")
        add(TextKey.KEY_TEXT_ARTS_ADAPTABILITY, "Arts Adaptability")
        add(TextKey.KEY_TEXT_CLEAR_LEARNED_ARTS, "Clear learned arts")

        //ARTS
        ArtIgnition.addArtsLang(
            this,
            "Ignition",
            "Fires a fireball that damages and ignites the target"
        )
        ArtSwirlingVortex.addArtsLang(
            this,
            "Swirling Vortex",
            "Fires a spell orb that momentarily generates an area that draws towards the center"
        )
        ArtDiffusionCurrent.addArtsLang(
            this,
            "Diffusion Current",
            "Fires an electric current that bounces to surrounding targets a limited number of times after hitting the target"
        )
        ArtMobileCombat.addArtsLang(
            this,
            "Mobile Combat",
            "Switch to Mobile Combat mode, increases movement speed based on Mobility's level"
        )
        ArtTrick.addArtsLang(
            this,
            "Trick",
            "Teleport forward under the cover of the card"
        )
        ArtFineBlending.addArtsLang(
            this,
            "Fine Blending",
            "Throws a bottle of potion, generating a health regeneration area for a few seconds at and around the hit location"
        )
        ArtEMP.addArtsLang(
            this,
            "EMP",
            "Releases an electromagnetic pulse, paralyzing surrounding targets"
        )
        ArtZeroPointBurst.addArtsLang(
            this,
            "Zero-Point Burst",
            "Unleash a low-temperature airflow, damaging and knocking back nearby targets"
        )
        ArtScorchedEarth.addArtsLang(
            this,
            "Scorched Earth",
            "Continue to release flames forward"
        )
        ArtHighSpeedChant.addArtsLang(
            this,
            "High Speed Chant",
            "Releases a buff for self, reducing the time it takes to release the skill"
        )
        ArtAntiGravity.addArtsLang(
            this,
            "Anti Gravity",
            "Continue to use continuously reduce the gravity, stop for a moment and then restore the gravity"
        )
        ArtGloriousShards.addArtsLang(
            this,
            "Glorious Shards",
            "Throws a shard, generates a thunderstorm area in the hit area, and continuously emits a current that spreads to random targets in the area"
        )
        add(ArtEmpty.getNameKey(), "Empty")

        add(KeyBinds.KEY_CATEGORY, "Originium Arts")
        add(KeyBinds.KEY_CHANGE_ARTS, "Change Arts")
        add(KeyBinds.KEY_OPEN_ARTS_SCREEN, "Open Arts Screen")
    }

}