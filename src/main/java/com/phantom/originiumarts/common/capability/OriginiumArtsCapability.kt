package com.phantom.originiumarts.common.capability


import com.phantom.originiumarts.OAConfig
import com.phantom.originiumarts.client.TextKey.KEY_TEXT_ARTS_ADAPTABILITY
import com.phantom.originiumarts.client.TextKey.KEY_TEXT_COMBAT_SKILL
import com.phantom.originiumarts.client.TextKey.KEY_TEXT_ENDURANCE
import com.phantom.originiumarts.client.TextKey.KEY_TEXT_EXCELLENT
import com.phantom.originiumarts.client.TextKey.KEY_TEXT_FLAWED
import com.phantom.originiumarts.client.TextKey.KEY_TEXT_MOBILITY
import com.phantom.originiumarts.client.TextKey.KEY_TEXT_NORMAL
import com.phantom.originiumarts.client.TextKey.KEY_TEXT_OUTSTANDING
import com.phantom.originiumarts.client.TextKey.KEY_TEXT_STANDARD
import com.phantom.originiumarts.client.TextKey.KEY_TEXT_STRENGTH
import com.phantom.originiumarts.client.TextKey.KEY_TEXT_TACTICAL_ACUMEN
import com.phantom.originiumarts.client.TextKey.KEY_TEXT_UNKNOWN
import com.phantom.originiumarts.common.arts.ArtEmpty
import com.phantom.originiumarts.common.ArtsManager.getArtById
import com.phantom.originiumarts.common.network.OANetworking
import com.phantom.originiumarts.common.network.sendpack.OAAntiGravitySendPack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.common.util.INBTSerializable

class OriginiumArtsCapability : INBTSerializable<CompoundTag> {

    val strength = LimitedRangeNum()
    val mobility = LimitedRangeNum()
    val endurance = LimitedRangeNum()
    val tacticalAcumen = LimitedRangeNum()
    val combatSkill = LimitedRangeNum()
    val artsAdaptability = LimitedRangeNum()

    val learnedArtIds = mutableListOf<Int>()
    val installedArtIds = mutableListOf<Int>()
    var selectedArtId: Int = ArtEmpty.uniqueID
    var artDamageFactor = 1.0f

    var healthOnDisconnect = 0f

    // ***********************************
    // only handle in client
    private var burden = 0.0
    var burdenRecovery = 0.2
    private val burdenMaxCount = 40
    private var burdenCount = burdenMaxCount

    var gravityCount = 0
    var gravityChange = 0.0

    // ***********************************

    var cellOriginiumAssimilation = 0.0 // TODO
    var bloodOriginiumCrystalDensity = 0.0 // TODO

    fun tick() {
        if (burdenCount >= 0) {
            burdenCount--
        }
        if (burdenCount < 0) {
            decBurden(burdenRecovery)
        }
        if (gravityCount > 0) {
            gravityCount--
            if (gravityCount <= 0) {
                gravityChange = 0.0
                OANetworking.sendToServer(OAAntiGravitySendPack(gravityChange))
            }
        }
    }

    fun getBurden(): Double = burden

    fun addBurden(value: Double) {
        resetCount()
        burden += value
    }

    private fun decBurden(value: Double) {
        if (burden > value) {
            burden -= value
        } else if (burden > 0) {
            burden = 0.0
        }
    }

    private fun resetCount() {
        burdenCount = burdenMaxCount
    }

    fun getAllInfo(): String {
        return """
            ${TranslatableComponent(KEY_TEXT_STRENGTH).string}: ${strength.getDescription()}
            ${TranslatableComponent(KEY_TEXT_MOBILITY).string}: ${mobility.getDescription()}
            ${TranslatableComponent(KEY_TEXT_ENDURANCE).string}: ${endurance.getDescription()}
            ${TranslatableComponent(KEY_TEXT_TACTICAL_ACUMEN).string}: ${tacticalAcumen.getDescription()}
            ${TranslatableComponent(KEY_TEXT_COMBAT_SKILL).string}: ${combatSkill.getDescription()}
            ${TranslatableComponent(KEY_TEXT_ARTS_ADAPTABILITY).string}: ${artsAdaptability.getDescription()}
            hasArts: ${
            learnedArtIds.map { it.getArtById() }
                .map { it.name }
                .joinToString { it.string ?: "" }
        }
            installedArts: ${
            installedArtIds.map { it.getArtById() }
                .map { it.name }
                .joinToString { it.string ?: "" }
        }
            artDamageFactor: $artDamageFactor
        """.trimIndent()
    }

    fun testInfo(): String {
        return """
            strength: ${strength.getValue()}
            mobility: ${mobility.getValue()}
            endurance: ${endurance.getValue()}
            tactical acumen: ${tacticalAcumen.getValue()}
            combat skill: ${combatSkill.getValue()}
            arts adaptability: ${artsAdaptability.getValue()}
        """.trimIndent()
    }

    override fun serializeNBT(): CompoundTag =
        CompoundTag().apply {
            putInt("strength", strength.getValue())
            putInt("mobility", mobility.getValue())
            putInt("endurance", endurance.getValue())
            putInt("tacticalAcumen", tacticalAcumen.getValue())
            putInt("combatSkill", combatSkill.getValue())
            putInt("artsAdaptability", artsAdaptability.getValue())
            putIntArray("learnedArtIds", learnedArtIds.toIntArray())
            putIntArray("installedArtIds", installedArtIds.toIntArray())
            putInt("selectedArtId", selectedArtId)
            putDouble("burden", burden)
//            putDouble("cellOriginiumAssimilation", cellOriginiumAssimilation)
//            putDouble("bloodOriginiumCrystalDensity", bloodOriginiumCrystalDensity)
            putFloat("healthOnDisconnect", healthOnDisconnect)
        }

    override fun deserializeNBT(nbt: CompoundTag?) {
        if (nbt != null) {
            nbt.getInt("strength").let { strength.setValue(it) }
            nbt.getInt("mobility").let { mobility.setValue(it) }
            nbt.getInt("endurance").let { endurance.setValue(it) }
            nbt.getInt("tacticalAcumen").let { tacticalAcumen.setValue(it) }
            nbt.getInt("combatSkill").let { combatSkill.setValue(it) }
            nbt.getInt("artsAdaptability").let { artsAdaptability.setValue(it) }
            nbt.getIntArray("learnedArtIds").let {
                learnedArtIds.clear()
                learnedArtIds.addAll(it.toList())
            }
            nbt.getIntArray("installedArtIds").let {
                installedArtIds.clear()
                installedArtIds.addAll(it.toList())
            }
            nbt.getInt("selectedArtId").let { selectedArtId = it }
            nbt.getDouble("burden").let { burden = it }
//            nbt.getDouble("cellOriginiumAssimilation").let { cellOriginiumAssimilation = it }
//            nbt.getDouble("bloodOriginiumCrystalDensity").let { bloodOriginiumCrystalDensity = it }
            nbt.getFloat("healthOnDisconnect").let { healthOnDisconnect = it }
        }
    }

    class LimitedRangeNum {

        private var mValue: Int

        init {
            mValue = (OAConfig.SIX_DIM_ATTR_MIN.get()..OAConfig.SIX_DIM_ATTR_MAX.get()).random()
        }

        fun change(value: Int) {
            setValue(mValue + value)
        }

        fun setValue(value: Int) {
            mValue = value.coerceIn(minValue..maxValue)
        }

        fun getValue(): Int {
            return mValue
        }

        fun getLevel(): ValueLevel {
            return when (mValue) {
                in Int.MIN_VALUE..19 -> ValueLevel.FLAWED
                in 20..39 -> ValueLevel.NORMAL
                in 40..59 -> ValueLevel.STANDARD
                in 60..79 -> ValueLevel.EXCELLENT
                in 80..99 -> ValueLevel.OUTSTANDING
                else -> ValueLevel.UNKNOWN
            }
        }

        fun getDescription(): String = getLevel().toString()

        companion object {
            const val maxValue: Int = 100
            const val minValue: Int = 0
        }

    }

    enum class ValueLevel {
        FLAWED, NORMAL, STANDARD, EXCELLENT, OUTSTANDING, UNKNOWN;

        fun getTestString(): String = when (this) {
            FLAWED -> "flawed"
            NORMAL -> "normal"
            STANDARD -> "standard"
            EXCELLENT -> "excellent"
            OUTSTANDING -> "outstanding"
            UNKNOWN -> "unknown"
        }

        fun baseValue(): Int = when (this) {
            FLAWED -> 0
            NORMAL -> 20
            STANDARD -> 40
            EXCELLENT -> 60
            OUTSTANDING -> 80
            UNKNOWN -> 100
        }

        fun costExp(): Int = when (this) {
            FLAWED -> 10
            NORMAL -> 100
            STANDARD -> 500
            EXCELLENT -> 2000
            OUTSTANDING -> 10000
            UNKNOWN -> 100000
        }

        fun getComponent(): Component = when (this) {
            FLAWED -> TranslatableComponent(KEY_TEXT_FLAWED)
            NORMAL -> TranslatableComponent(KEY_TEXT_NORMAL)
            STANDARD -> TranslatableComponent(KEY_TEXT_STANDARD)
            EXCELLENT -> TranslatableComponent(KEY_TEXT_EXCELLENT)
            OUTSTANDING -> TranslatableComponent(KEY_TEXT_OUTSTANDING)
            UNKNOWN -> TranslatableComponent(KEY_TEXT_UNKNOWN)
        }

        override fun toString(): String = when (this) {
            FLAWED -> TranslatableComponent(KEY_TEXT_FLAWED).string
            NORMAL -> TranslatableComponent(KEY_TEXT_NORMAL).string
            STANDARD -> TranslatableComponent(KEY_TEXT_STANDARD).string
            EXCELLENT -> TranslatableComponent(KEY_TEXT_EXCELLENT).string
            OUTSTANDING -> TranslatableComponent(KEY_TEXT_OUTSTANDING).string
            UNKNOWN -> TranslatableComponent(KEY_TEXT_UNKNOWN).string
        }
    }
}