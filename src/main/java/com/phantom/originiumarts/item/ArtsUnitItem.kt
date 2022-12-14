package com.phantom.originiumarts.item

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.common.ArtsManager.getArtById
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.common.capability.getUseDurationAmplifier
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tier
import net.minecraft.world.item.TieredItem
import net.minecraft.world.item.Vanishable
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

abstract class ArtsUnitItem(
    properties: Properties,
    tier: ArtUnitTier
) : TieredItem(tier, properties), Vanishable {

    val slotSize: Int
    val effectFactor: Float
    val useFactor: Double

    private val defaultModifiers: Multimap<Attribute, AttributeModifier>

    init {
        slotSize = tier.getSlotSize()
        effectFactor = tier.getEffectFactor()
        useFactor = tier.getUseFactor()

        val builder = ImmutableMultimap.builder<Attribute, AttributeModifier>()
        builder.put(
            Attributes.ATTACK_DAMAGE, AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID,
                "ArtsUnit modifier",
                3.0 + tier.attackDamageBonus,
                AttributeModifier.Operation.ADDITION
            )
        )
        builder.put(
            Attributes.ATTACK_SPEED, AttributeModifier(
                BASE_ATTACK_SPEED_UUID,
                "ArtsUnit modifier",
                -3.0 + 0.2 * tier.speed,
                AttributeModifier.Operation.ADDITION
            )
        )
        this.defaultModifiers = builder.build()
    }

    override fun releaseUsing(itemStack: ItemStack, level: Level, livingEntity: LivingEntity, usingDuration: Int) {
        if (livingEntity is Player) {
            livingEntity.getOACapability()?.selectedArtId?.getArtById()?.let { art ->
                if ((OriginiumArtsMod.maxUseDuration - usingDuration) * livingEntity.getUseDurationAmplifier() > art.needUseTick) {
                    art.onUse(livingEntity, this)
                    itemStack.hurtAndBreak(1, livingEntity) {
                        it.broadcastBreakEvent(it.usedItemHand)
                    }
                } else {
                    art.makeParticle(livingEntity.eyePosition, level)
                }
            }
        }
        super.releaseUsing(itemStack, level, livingEntity, usingDuration)
    }

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(interactionHand)
        val art = player.getOACapability()?.selectedArtId?.getArtById()
        return if (player.isUsingItem) {
            InteractionResultHolder.fail(itemStack)
        } else if (art?.needUseTick == 0) {
            art.onUse(player, this)
            itemStack.hurtAndBreak(1, player) {
                it.broadcastBreakEvent(it.usedItemHand)
            }
            InteractionResultHolder.consume(itemStack)
        } else {
            player.startUsingItem(interactionHand)
            InteractionResultHolder.consume(itemStack)
        }
    }

    override fun getUseDuration(itemStack: ItemStack): Int {
        return OriginiumArtsMod.maxUseDuration
    }

    override fun hurtEnemy(pStack: ItemStack, pTarget: LivingEntity, pAttacker: LivingEntity): Boolean {
        pStack.hurtAndBreak(1, pAttacker) {
            it.broadcastBreakEvent(EquipmentSlot.MAINHAND)
        }
        return true
    }

    override fun mineBlock(
        pStack: ItemStack,
        pLevel: Level,
        pState: BlockState,
        pPos: BlockPos,
        pMiningEntity: LivingEntity
    ): Boolean {
        if (pState.getDestroySpeed(pLevel, pPos) != 0.0f) {
            pStack.hurtAndBreak(2, pMiningEntity) {
                it.broadcastBreakEvent(EquipmentSlot.MAINHAND)
            }
        }
        return true
    }

    override fun getDefaultAttributeModifiers(pEquipmentSlot: EquipmentSlot): Multimap<Attribute, AttributeModifier> {
        return if (pEquipmentSlot == EquipmentSlot.MAINHAND)
            defaultModifiers
        else
            super.getDefaultAttributeModifiers(pEquipmentSlot)
    }

    interface ArtUnitTier : Tier {

        fun getSlotSize(): Int = 0

        fun getEffectFactor(): Float = 1f

        fun getUseFactor(): Double = 1.0

    }

}