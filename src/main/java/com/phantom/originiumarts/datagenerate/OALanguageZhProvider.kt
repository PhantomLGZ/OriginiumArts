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

class OALanguageZhProvider(gen: DataGenerator, locale: String) :
    LanguageProvider(gen, OriginiumArtsMod.MOD_ID, locale) {

    override fun addTranslations() {
        add("itemGroup." + OriginiumArtsMod.GROUP_NAME, "源石技艺")

        add(ItemRegister.TEST_ITEM.get(), "测试")
        add(ItemRegister.ORIGINIUM.get(), "源石")
        add(ItemRegister.ORIGINIUM_FRAGMENT.get(), "源石碎片")
        add(ItemRegister.ORIGINIUM_INGOT.get(), "源石锭")
        add(ItemRegister.ETCHED_AMMO.get(), "蚀刻弹药")
        add(ItemRegister.OA_STANDARD_PISTOL.get(), "制式手枪")
        add(ItemRegister.OA_STANDARD_STAFF.get(), "制式法杖")
        add(ItemRegister.OA_STANDARD_SWORD.get(), "制式剑")
        add(ItemRegister.RIOTER_WATER_PIPE.get(), "水管")
        add(ItemRegister.COCKTAIL.get(), "燃烧瓶")
        add(ItemRegister.DRILL_BATTLE_RECORD.get(), "基础作战记录")
        add(ItemRegister.FRONTLINE_BATTLE_RECORD.get(), "初级作战记录")
        add(ItemRegister.TACTICAL_BATTLE_RECORD.get(), "中级作战记录")
        add(ItemRegister.STRATEGIC_BATTLE_RECORD.get(), "高级作战记录")
        add(ItemRegister.ORIGINIUM_SLUG_EGG.get(), "源石虫刷怪蛋")
        add(ItemRegister.ACID_ORIGINIUM_SLUG_EGG.get(), "酸液源石虫刷怪蛋")
        add(ItemRegister.DRONE_MONSTER_EGG.get(), "\"怪物\"刷怪蛋")
        add(ItemRegister.RIOTER_EGG.get(), "暴徒刷怪蛋")
        add(ItemRegister.COCKTAIL_THROWER_EGG.get(), "鸡尾酒投掷者刷怪蛋")

        add(BlockRegister.ORIGINIUM_HOST_ROCK.get(), "源石母岩")
        add(BlockRegister.ORIGINIUM_CLUSTER.get(), "源石晶簇")
        add(BlockRegister.ORIGINIUM_LARGE_BUD.get(), "大型源石芽")
        add(BlockRegister.ORIGINIUM_MEDIUM_BUD.get(), "中型源石芽")
        add(BlockRegister.ORIGINIUM_SMALL_BUD.get(), "小型源石芽")
        add(BlockRegister.ORIGINIUM_DUST.get(), "源石尘")
        add(BlockRegister.OPERATING_BED_BLOCK.get(), "手术床")
        add(EntityRegister.ORIGINIUM_SLUG.get(), "源石虫")
        add(EntityRegister.ACID_ORIGINIUM_SLUG.get(), "酸液源石虫")
        add(EntityRegister.DRONE_MONSTER.get(), "\"怪物\"")
        add(EntityRegister.RIOTER.get(), "暴徒")
        add(EntityRegister.COCKTAIL_THROWER.get(), "鸡尾酒投掷者")

        //EFFECT
        add(EffectRegister.ACUTE_ORIPATHY.get(), "急性矿石病")
        add(EffectRegister.HIGH_SPEED_CHANT.get(), "高速咏唱")
        add(EffectRegister.CORROSION.get(), "腐蚀")

        //GUI
        add(TextKey.KEY_TEXT_FULL_NOW, "已经满级了")
        add(TextKey.KEY_TEXT_NEED_EXPERIENCE, "需要经验值")
        add(TextKey.KEY_TEXT_FLAWED, "缺陷")
        add(TextKey.KEY_TEXT_NORMAL, "普通")
        add(TextKey.KEY_TEXT_STANDARD, "标准")
        add(TextKey.KEY_TEXT_EXCELLENT, "优良")
        add(TextKey.KEY_TEXT_OUTSTANDING, "卓越")
        add(TextKey.KEY_TEXT_UNKNOWN, "■■")
        add(TextKey.KEY_TEXT_STRENGTH, "物理强度")
        add(TextKey.KEY_TEXT_MOBILITY, "战场机动")
        add(TextKey.KEY_TEXT_ENDURANCE, "生理耐受")
        add(TextKey.KEY_TEXT_TACTICAL_ACUMEN, "战术规划")
        add(TextKey.KEY_TEXT_COMBAT_SKILL, "战斗技巧")
        add(TextKey.KEY_TEXT_ARTS_ADAPTABILITY, "源石技艺适应性")
        add(TextKey.KEY_TEXT_CLEAR_LEARNED_ARTS, "清除已学习技艺")
        add(TextKey.KEY_TEXT_SET_CATASTROPHE_INTENSITY, "设置天灾强度")
        add(TextKey.KEY_TEXT_ADD_CATASTROPHE, "生成天灾")

        //ARTS
        ArtIgnition.addArtsLang(
            this,
            "点燃",
            "发射一个火球，伤害并点燃命中目标"
        )
        ArtSwirlingVortex.addArtsLang(
            this,
            "聚能涡旋",
            "发射一个法术球，片刻后生成一个向中心吸引的区域"
        )
        ArtDiffusionCurrent.addArtsLang(
            this,
            "扩散电流",
            "发射一道电流，命中目标后有限次地向周围目标弹射"
        )
        ArtMobileCombat.addArtsLang(
            this,
            "机动作战",
            "切换机动作战模式，根据'战场机动'等级增加移动速度"
        )
        ArtTrick.addArtsLang(
            this,
            "把戏",
            "在卡牌的掩护下向前方瞬移"
        )
        ArtFineBlending.addArtsLang(
            this,
            "精调",
            "丢出一瓶药剂，在命中位置及周围生成数秒生命恢复区域"
        )
        ArtEMP.addArtsLang(
            this,
            "电磁脉冲",
            "释放电磁脉冲，麻痹周围目标"
        )
        ArtZeroPointBurst.addArtsLang(
            this,
            "零度爆发",
            "释放低温气流，伤害并击退周围目标"
        )
        ArtScorchedEarth.addArtsLang(
            this,
            "灼地",
            "向前方持续释放火焰"
        )
        ArtHighSpeedChant.addArtsLang(
            this,
            "高速咏唱",
            "为自身释放一个增益状态，减少技艺释放所需时间"
        )
        ArtAntiGravity.addArtsLang(
            this,
            "反重力",
            "持续使用不断减少重力，停止片刻后重力恢复"
        )
        ArtGloriousShards.addArtsLang(
            this,
            "辉煌裂片",
            "丢出一个碎片，在命中区域生成雷暴区域，不断对区域内随机目标发射会扩散的电流"
        )
        add(ArtEmpty.getNameKey(), "空")

        add(KeyBinds.KEY_CATEGORY, "源石技艺")
        add(KeyBinds.KEY_CHANGE_ARTS, "变更技艺")
        add(KeyBinds.KEY_OPEN_ARTS_SCREEN, "打开技艺界面")
    }

}