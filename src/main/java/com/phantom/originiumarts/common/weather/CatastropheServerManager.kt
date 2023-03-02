package com.phantom.originiumarts.common.weather

import com.mojang.logging.LogUtils
import com.phantom.originiumarts.common.network.OANetworking
import com.phantom.originiumarts.common.network.sendpack.OACatastropheSyncSendPack
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import net.minecraftforge.common.BiomeDictionary
import javax.annotation.Nonnull
import kotlin.math.*
import kotlin.random.Random
import kotlin.random.nextInt


class CatastropheServerManager : SavedData() {

    init {
        setDirty()
    }

    val catastropheList = mutableListOf<CatastropheServerData>()
    var catastropheIntensity = 0
    private var nextCheckTime = 1000
    private val log = LogUtils.getLogger()

    fun tick(level: ServerLevel) {
        level.server.playerList.players.forEach { player ->
            OANetworking.sendToPlayer(
                player,
                OACatastropheSyncSendPack(catastropheList.map { it.toClientData() })
            )
        }
        catastropheList.removeIf { it.needRemove() }
        catastropheList.forEach { it.tick(level) }
        if (nextCheckTime-- < 0) {
            setDirty()
            val r1 = Random.nextDouble()
            catastropheIntensity = when (catastropheIntensity) {
                0 -> when {
                    r1 < 0.9 -> catastropheIntensity
                    else -> catastropheIntensity + 1
                }
                1, 2 -> when {
                    r1 < 0.1 -> catastropheIntensity - 1
                    r1 < 0.8 -> catastropheIntensity
                    else -> catastropheIntensity + 1
                }
                3, 4 -> when {
                    r1 < 0.2 -> catastropheIntensity - 1
                    r1 < 0.8 -> catastropheIntensity
                    else -> catastropheIntensity + 1
                }
                5, 6 -> when {
                    r1 < 0.3 -> catastropheIntensity - 1
                    r1 < 0.8 -> catastropheIntensity
                    else -> catastropheIntensity + 1
                }
                7 -> when {
                    r1 < 0.3 -> catastropheIntensity - 1
                    else -> catastropheIntensity
                }
                else -> catastropheIntensity
            }
            nextCheckTime = Random.nextInt(1000..10000)
            val r2 = Random.nextDouble()
            when (catastropheIntensity) {
                1, 2 -> if (r2 < 0.05) disasterOccurs(level)
                3, 4, 5 -> if (r2 < 0.1) disasterOccurs(level)
                6, 7 -> if (r2 < 0.2) disasterOccurs(level)
            }
        }
    }

    private fun disasterOccurs(level: ServerLevel) {
        val player = level.server.playerList.players.filter {
            BiomeDictionary.hasType(
                level.getBiome(it.onPos).unwrapKey().get(),
                BiomeDictionary.Type.OVERWORLD
            )
        }.takeIf { it.isNotEmpty() }?.let { it[it.indices.random()] }
        log.info("OAM: can addCatastrophe player list: $player")
        player?.let {
            addCatastrophe(player, catastropheIntensity)
            catastropheIntensity = 0
        }
    }

    fun addCatastrophe(player: Player, intensity: Int) {
        val pa = Random.nextDouble(2 * PI)
        val pr = Random.nextInt(200..500)
        val va = Random.nextDouble(2 * PI)
        val vr = Random.nextDouble(0.01 + 0.01 * intensity / 7, 0.02 + 0.02 * intensity / 7)
        val catastrophe = CatastropheServerData(
            posX = player.x + pr * cos(pa),
            posY = 128.0,
            posZ = player.z + pr * sin(pa),
            vX = vr * cos(va),
            vZ = vr * sin(va),
            lifetime = 20000 - (intensity - 5).let { it * it } * 800,
            intensity = intensity
        )
        log.info("OAM: add Catastrophe at (${catastrophe.posX}, ${catastrophe.posY}, ${catastrophe.posZ})")
        catastropheList.add(catastrophe)
        setDirty()
    }

    fun setIntensity(intensity: Int) {
        catastropheIntensity = intensity
    }

    override fun save(pCompoundTag: CompoundTag): CompoundTag {
        pCompoundTag.putInt("catastropheLevel", catastropheIntensity)
        val catastropheListNBT = CompoundTag()
        catastropheList.forEachIndexed { index, it ->
            val catastropheNBT = CompoundTag()
            catastropheNBT.putDouble("posX", it.posX)
            catastropheNBT.putDouble("posY", it.posY)
            catastropheNBT.putDouble("posZ", it.posZ)
            catastropheNBT.putDouble("vX", it.vX)
            catastropheNBT.putDouble("vZ", it.vZ)
            catastropheNBT.putInt("tickCount", it.tickCount)
            catastropheNBT.putInt("lifetime", it.lifetime)
            catastropheNBT.putInt("intensity", it.intensity)

            catastropheListNBT.put("catastrophe_${index}", catastropheNBT)
        }
        pCompoundTag.put("catastropheListNBT", catastropheListNBT)
        println("TEST save size = ${catastropheList.size}  $catastropheList")

        return pCompoundTag
    }

    companion object {

        private fun load(pCompoundTag: CompoundTag, level: ServerLevel): CatastropheServerManager {
            val manager = CatastropheServerManager()
            manager.catastropheIntensity = pCompoundTag.getInt("catastropheLevel")
            pCompoundTag.getCompound("catastropheListNBT").let {
                it.allKeys.forEach { key ->
                    val tag = it.getCompound(key)
                    manager.catastropheList.add(
                        CatastropheServerData(
                            posX = tag.getDouble("posX"),
                            posY = tag.getDouble("posY"),
                            posZ = tag.getDouble("posZ"),
                            vX = tag.getDouble("vX"),
                            vZ = tag.getDouble("vZ"),
                            tickCount = tag.getInt("tickCount"),
                            lifetime = tag.getInt("lifetime"),
                            intensity = tag.getInt("intensity")
                        )
                    )
                }
            }
            println("TEST size = ${manager.catastropheList.size} load ${manager.catastropheList}")
            return manager
        }

        @Nonnull
        fun get(level: Level): CatastropheServerManager? {
            if (level is ServerLevel) {
                val storage = level.server.overworld().dataStorage
                return storage.computeIfAbsent(
                    { load(it, level) },
                    { CatastropheServerManager() },
                    "CatastropheManager"
                )
            } else {
                throw RuntimeException("Don't access this client-side!")
            }
        }

        fun set(level: Level, data: CatastropheServerManager) {
            if (level is ServerLevel) {
                val storage = level.server.overworld().dataStorage
                storage.set("CatastropheManager", data)
            } else {
                throw RuntimeException("Don't access this client-side!")
            }
        }
    }
}