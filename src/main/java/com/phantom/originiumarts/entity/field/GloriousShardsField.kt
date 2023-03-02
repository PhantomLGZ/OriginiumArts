package com.phantom.originiumarts.entity.field

import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.arts.ArtGloriousShards
import com.phantom.originiumarts.common.capability.OriginiumArtsCapability
import com.phantom.originiumarts.common.capability.getOACapability
import com.phantom.originiumarts.entity.EntityRegister
import com.phantom.originiumarts.entity.RayEntity
import com.phantom.originiumarts.entity.getEntitiesAround
import com.phantom.originiumarts.entity.getNearestEntity
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.IndirectEntityDamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class GloriousShardsField(
    entityType: EntityType<GloriousShardsField>,
    level: Level
) : BaseFieldEntity(entityType, level) {

    private var damage = 2.5

    init {
        setCycle(20)
    }

    override fun setOwner(entity: Entity?) {
        super.setOwner(entity)
        if (entity is Player) {
            setup(entity)
        }
    }

    private fun setup(player: Player?) {
        player?.getOACapability()?.let {
            setLifetime(
                when (it.artsAdaptability.getLevel()) {
                    OriginiumArtsCapability.ValueLevel.FLAWED -> 40
                    OriginiumArtsCapability.ValueLevel.NORMAL -> 40
                    OriginiumArtsCapability.ValueLevel.STANDARD -> 60
                    OriginiumArtsCapability.ValueLevel.EXCELLENT -> 80
                    OriginiumArtsCapability.ValueLevel.OUTSTANDING -> 100
                    OriginiumArtsCapability.ValueLevel.UNKNOWN -> 200
                }
            )
            damage = 2.5 * it.artDamageFactor
            setDiameter(
                when (it.artsAdaptability.getLevel()) {
                    OriginiumArtsCapability.ValueLevel.FLAWED -> 6f
                    OriginiumArtsCapability.ValueLevel.NORMAL -> 6f
                    OriginiumArtsCapability.ValueLevel.STANDARD -> 6f
                    OriginiumArtsCapability.ValueLevel.EXCELLENT -> 6f
                    OriginiumArtsCapability.ValueLevel.OUTSTANDING -> 8f
                    OriginiumArtsCapability.ValueLevel.UNKNOWN -> 12f
                }
            )
        }
    }

    override fun onEffect() {
        if (!level.isClientSide) {
            getEntitiesAround(getDiameter().toDouble(), Mob::class.java).let {
                val entity = it.randomOrNull()
                if (entity != null) {
                    level.playSound(
                        null,
                        entity,
                        SoundRegister.MAGIC_N.get(),
                        SoundSource.PLAYERS,
                        1.0f,
                        1.0f
                    )
                    val hitList = mutableListOf(entity)
                    for (i in 0..2) {
                        val last = hitList.last()
                        last.hurt(
                            IndirectEntityDamageSource(ArtGloriousShards.getNameKey(), this, owner)
                                .setMagic(),
                            damage.toFloat()
                        )
                        val hit = last.getEntitiesAround(4.0, Mob::class.java)
                            .toMutableList().apply { removeAll(hitList.toSet()) }
                            .getNearestEntity(last) as? Mob ?: break
                        hitList.add(hit)
                    }
                    val posList = hitList.map { item -> item.eyePosition }
                        .toMutableList().apply {
                            if (isNotEmpty()) {
                                add(0, Vec3(hitList.first().x, y + 8, hitList.first().z))
                            }
                        }
                    for (i in 0..posList.size - 2) {
                        val first = posList[i]
                        val second = posList[i + 1]
                        level.addFreshEntity(RayEntity(EntityRegister.RAY_ENTITY.get(), level).apply {
                            setPos(first.subtract(0.0, 0.05, 0.0))
                            setLookDirection(second.subtract(first).normalize())
                            setLength(first.distanceTo(second).toFloat())
                            setLifetime(5)
                        })
                    }
                }
            }
        }
    }

    override fun makeParticle() {
        if (random.nextDouble() > 0.5) {
            ArtGloriousShards.makeParticle(
                Vec3(
                    x + (random.nextDouble() * 2 - 1) * getDiameter() / 2,
                    y + random.nextDouble() * 0.05 + 0.05,
                    z + (random.nextDouble() * 2 - 1) * getDiameter() / 2
                ),
                level
            )
        }
    }

}