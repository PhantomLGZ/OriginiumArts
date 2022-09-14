package com.phantom.originiumarts.common.capability

import com.mojang.logging.LogUtils
import com.phantom.originiumarts.common.arts.ArtEmpty
import com.phantom.originiumarts.common.EffectRegister
import com.phantom.originiumarts.common.network.OANetworking
import com.phantom.originiumarts.common.network.sendpack.OAAcuteOripathySendPack
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraftforge.common.ForgeMod
import java.util.*

fun Player.setStrength(value: Int) {
    doInOACapabilityWithPlayer {
        strength.modifyValue({ setValue(value) }, { setStrengthEffect(it) })
    }
}

fun Player.changeStrength(value: Int) {
    doInOACapabilityWithPlayer {
        strength.modifyValue({ change(value) }, { setStrengthEffect(it) })
    }
}

fun Player.setMobility(value: Int) {
    doInOACapabilityWithPlayer {
        mobility.modifyValue({ setValue(value) }, { setMobilityEffect(it) })
    }
}

fun Player.changeMobility(value: Int) {
    doInOACapabilityWithPlayer {
        mobility.modifyValue({ change(value) }, { setMobilityEffect(it) })
    }
}

fun Player.setEndurance(value: Int) {
    doInOACapabilityWithPlayer {
        endurance.modifyValue({ setValue(value) }, { setEnduranceEffect(it) })
    }
}

fun Player.changeEndurance(value: Int) {
    doInOACapabilityWithPlayer {
        endurance.modifyValue({ change(value) }, { setEnduranceEffect(it) })
    }
}

fun Player.setTacticalAcumen(value: Int) {
    doInOACapabilityWithPlayer {
        tacticalAcumen.modifyValue({ setValue(value) }, { setTacticalAcumenEffect(it) })
    }
}

fun Player.changeTacticalAcumen(value: Int) {
    doInOACapabilityWithPlayer {
        tacticalAcumen.modifyValue({ change(value) }, { setTacticalAcumenEffect(it) })
    }
}

fun Player.setCombatSkill(value: Int) {
    doInOACapabilityWithPlayer {
        combatSkill.modifyValue({ setValue(value) }, { setCombatSkillEffect(it) })
    }
}

fun Player.changeCombatSkill(value: Int) {
    doInOACapabilityWithPlayer {
        combatSkill.modifyValue({ change(value) }, { setCombatSkillEffect(it) })
    }
}

fun Player.setArtsAdaptability(value: Int) {
    doInOACapabilityWithPlayer {
        artsAdaptability.modifyValue({ setValue(value) }, { setArtsAdaptabilityEffect(it) })
    }
}

fun Player.changeArtsAdaptability(value: Int) {
    doInOACapabilityWithPlayer {
        artsAdaptability.modifyValue({ change(value) }, { setArtsAdaptabilityEffect(it) })
    }
}

private fun Player.doInOACapabilityWithPlayer(func: OriginiumArtsCapability.(player: Player) -> Unit) {
    getCapability(OriginiumArtsCapabilityProvider.ORIGINIUM_ARTS_CAPABILITY).ifPresent {
        it.func(this)
    }
}

fun Player.getOACapability(): OriginiumArtsCapability? {
    var cap: OriginiumArtsCapability? = null
    getCapability(OriginiumArtsCapabilityProvider.ORIGINIUM_ARTS_CAPABILITY).ifPresent {
        cap = it
    }
    return cap
}

fun Player.getUseDurationAmplifier(): Double {
    var amplifier = getEffect(EffectRegister.HIGH_SPEED_CHANT.get())?.amplifier?.toDouble() ?: 1.0
    if (getEffect(EffectRegister.ACUTE_ORIPATHY.get()) != null) {
        amplifier = amplifier * 10 / (getOACapability()?.getBurden()?.takeIf { it > 10.0 } ?: 10.0)
    }
    return amplifier
}

fun Player.updateAllEffect() {
    doInOACapabilityWithPlayer {
        setStrengthEffect(it)
        setMobilityEffect(it)
        setEnduranceEffect(it)
        setTacticalAcumenEffect(it)
        setCombatSkillEffect(it)
        setArtsAdaptabilityEffect(it)
        LogUtils.getLogger().debug("${it.displayName.string}:: \n${testInfo()}")
    }
}

fun Player.burdenEffect() {
    doInOACapabilityWithPlayer {
        val randomValue = Random().nextDouble()
        when (getBurden()) {
            in 0.0..50.0 -> {}
            in 50.0..80.0 -> {
                if (randomValue < 0.05) {
                    it.addEffect(
                        MobEffectInstance(
                            EffectRegister.ACUTE_ORIPATHY.get(),
                            5 * 20,
                            0
                        )
                    )
                    OANetworking.INSTANCE
                        ?.sendToServer(OAAcuteOripathySendPack(5 * 20, 0))
                }
            }
            in 80.0..100.0 -> {
                if (randomValue < 0.2) {
                    it.addEffect(
                        MobEffectInstance(
                            EffectRegister.ACUTE_ORIPATHY.get(),
                            30 * 20,
                            1
                        )
                    )
                    OANetworking.INSTANCE
                        ?.sendToServer(OAAcuteOripathySendPack(30 * 20, 1))
                }
            }
            in 100.0..Double.MAX_VALUE -> {
                if (randomValue < 0.5) {
                    it.addEffect(
                        MobEffectInstance(
                            EffectRegister.ACUTE_ORIPATHY.get(),
                            120 * 20,
                            2
                        )
                    )
                    OANetworking.INSTANCE
                        ?.sendToServer(OAAcuteOripathySendPack(120 * 20, 2))
                }
            }
        }
    }
}

private fun OriginiumArtsCapability.setStrengthEffect(player: Player) {
    player.getAttribute(Attributes.ATTACK_DAMAGE)?.setOAModifier(
        "cap_strength_attack_damage",
        strength,
        doubleArrayOf(-0.5, -0.2, 0.0, 0.4, 1.0, 3.0),
        AttributeModifier.Operation.MULTIPLY_BASE
    )
}

private fun OriginiumArtsCapability.setMobilityEffect(player: Player) {
    player.getAttribute(Attributes.MOVEMENT_SPEED)?.setOAModifier(
        "cap_mobility_movement_speed",
        mobility,
        doubleArrayOf(-0.2, 0.0, 0.0, 0.0, 0.0, 0.0),
        AttributeModifier.Operation.MULTIPLY_BASE
    )
    player.getAttribute(Attributes.FLYING_SPEED)?.setOAModifier(
        "cap_mobility_flying_speed",
        mobility,
        doubleArrayOf(-0.2, 0.0, 0.0, 0.0, 0.0, 0.0),
        AttributeModifier.Operation.MULTIPLY_BASE
    )
    player.getAttribute(ForgeMod.SWIM_SPEED.get())?.setOAModifier(
        "cap_mobility_swim_speed",
        mobility,
        doubleArrayOf(-0.2, 0.0, 0.0, 0.0, 0.0, 0.0),
        AttributeModifier.Operation.MULTIPLY_BASE
    )
}

private fun OriginiumArtsCapability.setEnduranceEffect(player: Player) {
    player.getAttribute(Attributes.MAX_HEALTH)?.setOAModifier(
        "cap_endurance_max_health",
        endurance,
        doubleArrayOf(-14.0, -8.0, 0.0, 10.0, 20.0, 40.0),
        AttributeModifier.Operation.ADDITION
    )
    player.getAttribute(Attributes.ARMOR)?.setOAModifier(
        "cap_endurance_armor",
        endurance,
        doubleArrayOf(0.0, 0.0, 0.0, 2.0, 4.0, 10.0),
        AttributeModifier.Operation.ADDITION
    )
    player.getAttribute(Attributes.MAX_HEALTH)?.let {
        if (player.health > it.value) {
            player.health = it.value.toFloat()
        }
        if (healthOnDisconnect != 0f) {
            player.health = healthOnDisconnect
            healthOnDisconnect = 0f
        }
    }
}

private fun OriginiumArtsCapability.setTacticalAcumenEffect(player: Player) {
    val artSlotCount = when (tacticalAcumen.getLevel()) {
        OriginiumArtsCapability.ValueLevel.FLAWED -> 0
        OriginiumArtsCapability.ValueLevel.NORMAL -> 1
        OriginiumArtsCapability.ValueLevel.STANDARD -> 2
        OriginiumArtsCapability.ValueLevel.EXCELLENT -> 3
        OriginiumArtsCapability.ValueLevel.OUTSTANDING -> 4
        OriginiumArtsCapability.ValueLevel.UNKNOWN -> 6
    }
    while (installedArtIds.size > artSlotCount) {
        installedArtIds.removeLast()
    }
    while (installedArtIds.size < artSlotCount) {
        installedArtIds.add(ArtEmpty.uniqueID)
    }
}

private fun OriginiumArtsCapability.setCombatSkillEffect(player: Player) {
    player.getAttribute(Attributes.ATTACK_SPEED)?.setOAModifier(
        "cap_combat_skill_attack_speed",
        combatSkill,
        doubleArrayOf(-0.5, -0.2, 0.0, 0.4, 1.0, 2.0),
        AttributeModifier.Operation.MULTIPLY_BASE
    )
}

private fun OriginiumArtsCapability.setArtsAdaptabilityEffect(player: Player) {
    artDamageFactor = when (artsAdaptability.getLevel()) {
        OriginiumArtsCapability.ValueLevel.FLAWED -> 0.5f
        OriginiumArtsCapability.ValueLevel.NORMAL -> 1f
        OriginiumArtsCapability.ValueLevel.STANDARD -> 1.2f
        OriginiumArtsCapability.ValueLevel.EXCELLENT -> 1.5f
        OriginiumArtsCapability.ValueLevel.OUTSTANDING -> 2f
        OriginiumArtsCapability.ValueLevel.UNKNOWN -> 4f
    }
}

private fun AttributeInstance.setOAModifier(
    key: String,
    capability: OriginiumArtsCapability.LimitedRangeNum,
    changeValue: DoubleArray,
    operation: AttributeModifier.Operation
) {
    val uuid = UUID.nameUUIDFromBytes(key.toByteArray())
    removeModifier(uuid)
    addTransientModifier(
        AttributeModifier(
            uuid, key, when (capability.getLevel()) {
                OriginiumArtsCapability.ValueLevel.FLAWED -> changeValue[0]
                OriginiumArtsCapability.ValueLevel.NORMAL -> changeValue[1]
                OriginiumArtsCapability.ValueLevel.STANDARD -> changeValue[2]
                OriginiumArtsCapability.ValueLevel.EXCELLENT -> changeValue[3]
                OriginiumArtsCapability.ValueLevel.OUTSTANDING -> changeValue[4]
                OriginiumArtsCapability.ValueLevel.UNKNOWN -> changeValue[5]
            }, operation
        )
    )
}

private fun AttributeInstance.hasModifier(key: String): Boolean {
    val uuid = UUID.nameUUIDFromBytes(key.toByteArray())
    return getModifier(uuid) != null
}

private fun OriginiumArtsCapability.LimitedRangeNum.modifyValue(
    modifyValueFun: OriginiumArtsCapability.LimitedRangeNum.() -> Unit,
    setEffectFun: () -> Unit
) {
    val oldLevel = this.getLevel()
    this.modifyValueFun()
    val newLevel = this.getLevel()
    if (oldLevel != newLevel) {
        setEffectFun()
    }
}

const val TARGET_KEY_STRENGTH = "strength"
const val TARGET_KEY_MOBILITY = "mobility"
const val TARGET_KEY_ENDURANCE = "endurance"
const val TARGET_KEY_TACTICAL_ACUMEN = "tacticalAcumen"
const val TARGET_KEY_COMBAT_SKILL = "combatSkill"
const val TARGET_KEY_ARTS_ADAPTABILITY = "artsAdaptability"