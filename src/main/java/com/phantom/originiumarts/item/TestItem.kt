package com.phantom.originiumarts.item

import com.phantom.originiumarts.common.capability.OriginiumArtsCapabilityProvider
import com.phantom.originiumarts.client.gui.TestItemGui
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import kotlin.random.Random


class TestItem : Item(basicSetting()), IOverrideAttack {

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        if (level.isClientSide) {
            TestItemGui.openGui()
            return InteractionResultHolder.consume(player.getItemInHand(interactionHand))
        }

        player.getCapability(OriginiumArtsCapabilityProvider.ORIGINIUM_ARTS_CAPABILITY).ifPresent {
            player.displayClientMessage(TextComponent(it.testInfo()), false)
        }

        return super.use(level, player, interactionHand)
    }

    override fun attack(player: ServerPlayer) {

    }

    override fun onClickInClient(player: Player) {
        player.level.addParticle(
            ParticleTypes.SMOKE,
            player.eyePosition.x + player.lookAngle.x,
            player.eyePosition.y + player.lookAngle.y,
            player.eyePosition.z + player.lookAngle.z,
            Random.nextDouble() * 0.2,
            Random.nextDouble() * 0.4,
            Random.nextDouble() * 0.2
        )
    }
}