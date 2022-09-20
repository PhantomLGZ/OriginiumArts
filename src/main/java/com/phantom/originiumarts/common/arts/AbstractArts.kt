package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.client.TextKey
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability.*
import com.phantom.originiumarts.common.capability.burdenEffect
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.item.ArtsUnitItem
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3

abstract class AbstractArts(
    uniqueName: String,
    private val icon: ResourceLocation,
    private val nameKey: String,
    val descriptionKey: String,
    private val checkCapInfo: CheckCapInfo = CheckCapInfo(),
    val parentArts: List<AbstractArts> = listOf()
) {

    val uniqueID = uniqueName.hashCode()
    var burden: Double = 10.0
    var needUseTick: Int = 25
    var gravity: Double = 0.0

    constructor(
        uniqueName: String,
        checkCapInfo: CheckCapInfo = CheckCapInfo(),
        parentArts: List<AbstractArts> = listOf(),
    ) : this(
        uniqueName,
        ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/art/art_${uniqueName}.png"),
        "arts.originiumarts.${uniqueName}",
        "arts.originiumarts.description.${uniqueName}",
        checkCapInfo,
        parentArts
    )

    private val description = TranslatableComponent(descriptionKey)

    val name: TranslatableComponent = TranslatableComponent(nameKey)

    open fun onUse(player: Player, artsUnitItem: ArtsUnitItem) {
        if (player.level.isClientSide) {
            player.burdenEffect()
            player.getOACapability()?.addBurden(burden)
        }
    }

    open fun onHitEntity(fromEntity: LivingEntity?, projectile: Entity, entityHitResult: EntityHitResult) {}

    open fun onHitBlock(fromEntity: LivingEntity?, projectile: Entity, blockHitResult: BlockHitResult) {}

    open fun onTimeOut(fromEntity: LivingEntity?, projectile: Entity, level: Level) {}

    open fun makeParticle(pos: Vec3, level: Level) {}

    open fun makeParticle(pos: Vec3, level: Level, dir: Vec3) {}

    abstract fun consumeExperienceValue(): Int

    open fun getItemStack(): ItemStack = ItemStack.EMPTY

    open fun learningUnmetConditions(player: Player): List<Component> {
        val list = mutableListOf<Component>()
        player.getOACapability()
            ?.takeUnless { it.learnedArtIds.contains(uniqueID) }
            ?.let { cap ->
                if (consumeExperienceValue() > player.totalExperience) {
                    list.add(TranslatableComponent(TextKey.KEY_TEXT_NEED_EXPERIENCE).append(": ${consumeExperienceValue()}"))
                }
                list.addAll(parentArts
                    .filter { needArt -> !cap.learnedArtIds.contains(needArt.uniqueID) }
                    .map { it.name })
                list.checkInfo(checkCapInfo.strength, cap.strength, TextKey.KEY_TEXT_STRENGTH)
                list.checkInfo(checkCapInfo.mobility, cap.mobility, TextKey.KEY_TEXT_MOBILITY)
                list.checkInfo(checkCapInfo.endurance, cap.endurance, TextKey.KEY_TEXT_ENDURANCE)
                list.checkInfo(checkCapInfo.tacticalAcumen, cap.tacticalAcumen, TextKey.KEY_TEXT_TACTICAL_ACUMEN)
                list.checkInfo(checkCapInfo.combatSkill, cap.combatSkill, TextKey.KEY_TEXT_COMBAT_SKILL)
                list.checkInfo(checkCapInfo.artsAdaptability, cap.artsAdaptability, TextKey.KEY_TEXT_ARTS_ADAPTABILITY)
            }
        return list
    }

    private fun MutableList<Component>.checkInfo(infoCheck: ValueLevel, cap: LimitedRangeNum, key: String) {
        infoCheck.takeIf { it != ValueLevel.FLAWED && cap.getValue() < it.baseValue() }
            ?.let { add(TranslatableComponent(key).append(": ").append(it.getComponent())) }
    }

    fun getIconResource(): ResourceLocation = icon

    fun getNameKey(): String = nameKey

    fun getDescription(): TranslatableComponent = description

    fun onLearning(player: ServerPlayer) {
        player.giveExperiencePoints(-consumeExperienceValue())
    }

    companion object {
        val LOCKED_ART_SLOT_RESOURCE = ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/art/art_locked.png")
    }

    data class CheckCapInfo(
        val strength: ValueLevel = ValueLevel.FLAWED,
        val mobility: ValueLevel = ValueLevel.FLAWED,
        val endurance: ValueLevel = ValueLevel.FLAWED,
        val tacticalAcumen: ValueLevel = ValueLevel.FLAWED,
        val combatSkill: ValueLevel = ValueLevel.FLAWED,
        val artsAdaptability: ValueLevel = ValueLevel.FLAWED
    )
}