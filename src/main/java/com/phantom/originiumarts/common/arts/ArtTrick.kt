package com.phantom.originiumarts.common.arts

import com.phantom.originiumarts.common.capability.OriginiumArtsCapability
import com.phantom.originiumarts.client.ParticleRegister
import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.capability.getArtEffectFactor
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.entity.field.ParticleGeneratorField
import com.phantom.originiumarts.item.ArtsUnitItem
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.random.Random

object ArtTrick : AbstractArts(
    uniqueName = "trick",
    checkCapInfo = CheckCapInfo(
        artsAdaptability = OriginiumArtsCapability.ValueLevel.STANDARD
    )
) {

    init {
        burden = 2.0
        needUseTick = 5
    }

    override fun onUse(player: Player, artsUnitItem: ArtsUnitItem) {
        super.onUse(player, artsUnitItem)
        val level = player.level
        if (!level.isClientSide) {
            level.playSound(
                null,
                player.x,
                player.y,
                player.z,
                SoundRegister.SNAPPING.get(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )
            level.addFreshEntity(
                ParticleGeneratorField(
                    EntityRegister.PARTICLE_GENERATOR_FIELD.get(),
                    level,
                    player.position(),
                    this,
                    10,
                    0.1f,
                    25
                )
            )
        }
        val distance = 5.0 * player.getArtEffectFactor(artsUnitItem)
        player.move(MoverType.SELF, player.lookAngle.multiply(distance, distance, distance))
    }

    override fun makeParticle(pos: Vec3, level: Level) {
        level.addParticle(
            ParticleRegister.POKER_CARD.get(),
            pos.x,
            pos.y,
            pos.z,
            Random.nextDouble(-0.5, 0.5),
            Random.nextDouble(2.0, 8.0),
            Random.nextDouble(-0.5, 0.5)
        )
    }

    override fun consumeExperienceValue(): Int = 1500

    override fun getItemStack(): ItemStack = ItemStack.EMPTY

}