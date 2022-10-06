package com.phantom.originiumarts.entity

import com.phantom.originiumarts.OriginiumArtsMod
import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.item.ItemRegister
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.raid.Raider
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraftforge.common.BiomeDictionary

class Rioter(type: EntityType<Rioter>, worldLevel: Level) :
    Raider(type, worldLevel) {

    override fun registerGoals() {
        goalSelector.let {
            it.addGoal(1, MeleeAttackGoal(this, 1.0, true))
            it.addGoal(0, FloatGoal(this))
            it.addGoal(5, WaterAvoidingRandomStrollGoal(this, 0.5))
            it.addGoal(6, LookAtPlayerGoal(this, Player::class.java, 8f))
            it.addGoal(6, RandomLookAroundGoal(this))
        }
        targetSelector.let {
            it.addGoal(1, HurtByTargetGoal(this).setAlertOthers())
            it.addGoal(2, NearestAttackableTargetGoal(this, Player::class.java, true))
        }
    }

    override fun finalizeSpawn(
        pLevel: ServerLevelAccessor,
        pDifficulty: DifficultyInstance,
        pReason: MobSpawnType,
        pSpawnData: SpawnGroupData?,
        pDataTag: CompoundTag?
    ): SpawnGroupData? {
        val spawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag)
        populateDefaultEquipmentSlots(pDifficulty)
        return spawnData
    }

    override fun populateDefaultEquipmentSlots(pDifficulty: DifficultyInstance) {
        setItemSlot(EquipmentSlot.MAINHAND, ItemStack(ItemRegister.RIOTER_WATER_PIPE.get()))
    }

    override fun applyRaidBuffs(pWave: Int, pUnusedFalse: Boolean) {
    }

    override fun getCelebrateSound(): SoundEvent =
        SoundRegister.E_CHEER_STREET.get()

    fun getTextureLocation(): ResourceLocation =
        TEXTURE_LIST[0]

    companion object {

        fun checkSpawnRules(
            entityType: EntityType<Rioter>,
            levelAccessor: LevelAccessor,
            mobSpawnType: MobSpawnType,
            blockPos: BlockPos,
            random: java.util.Random
        ): Boolean {
            val biome = levelAccessor.biomeManager.getBiome(blockPos).unwrapKey()
            return BiomeDictionary.hasType(biome.get(), BiomeDictionary.Type.OVERWORLD)
                    && !BiomeDictionary.hasType(biome.get(), BiomeDictionary.Type.OCEAN)
        }

        fun prepareAttributes(): AttributeSupplier.Builder {
            return createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.1)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
        }

        private val TEXTURE_LIST = listOf(
            ResourceLocation(OriginiumArtsMod.MOD_ID, "textures/entity/rioter.png")
        )
    }

}