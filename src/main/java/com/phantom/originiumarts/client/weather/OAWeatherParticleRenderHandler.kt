package com.phantom.originiumarts.client.weather

import com.phantom.originiumarts.client.ParticleRegister
import com.phantom.originiumarts.common.SoundRegister
import com.phantom.originiumarts.common.weather.CatastropheClientManager
import com.phantom.originiumarts.common.weather.CatastropheType
import com.phantom.originiumarts.common.weather.getAffectingCatastropheType
import com.phantom.originiumarts.common.weather.getCatastropheColorInClient
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.ParticleStatus
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CampfireBlock
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.Vec3
import net.minecraftforge.client.IWeatherParticleRenderHandler
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

object OAWeatherParticleRenderHandler : IWeatherParticleRenderHandler {

    private var rainSoundTime = 0
    private var soundTime = 0

    override fun render(ticks: Int, level: ClientLevel, minecraft: Minecraft, camera: Camera) {
        val rainLevel = minecraft.level!!.getRainLevel(1.0f)
        val catastropheEffect = CatastropheClientManager.catastropheEffect.toFloat()
        val f = rainLevel / if (Minecraft.useFancyGraphics()) 1.0f else 2.0f
        val isRain = f > 0f
        if (isRain || catastropheEffect > 0) {
            val random = Random(ticks.toLong() * 312987231L)
            val levelReader = minecraft.level!!
            val blockpos = BlockPos(camera.position)
            var blockpos1: BlockPos? = null
            val mixF = max(f, catastropheEffect)
            val i =
                (100.0f * mixF * mixF).toInt() / if (minecraft.options.particles == ParticleStatus.DECREASED) 2 else 1

            for (j in 0 until i) {
                val k = random.nextInt(21) - 10
                val l = random.nextInt(21) - 10
                val blockpos2 = levelReader.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockpos.offset(k, 0, l))
                val biome = levelReader.getBiome(blockpos2).value()
                if ((blockpos2.y > levelReader.minBuildHeight
                            && blockpos2.y <= blockpos.y + 10
                            && blockpos2.y >= blockpos.y - 10
                            && biome.precipitation == Biome.Precipitation.RAIN
                            && biome.warmEnoughToRain(blockpos2))
                    && (isRain
                            || getAffectingCatastropheType(level, camera.position) == CatastropheType.RAINSTORM)
                ) {
                    blockpos1 = blockpos2.below()
                    if (minecraft.options.particles == ParticleStatus.MINIMAL) {
                        break
                    }
                    val d0 = random.nextDouble()
                    val d1 = random.nextDouble()
                    val blockState = levelReader.getBlockState(blockpos1!!)
                    val fluidState = levelReader.getFluidState(blockpos1)
                    val voxelShape = blockState.getCollisionShape(levelReader, blockpos1)
                    val d2 = voxelShape.max(Direction.Axis.Y, d0, d1)
                    val d3 = fluidState.getHeight(levelReader, blockpos1).toDouble()
                    val d4 = d2.coerceAtLeast(d3)
                    val particleOptions: ParticleOptions =
                        if (!fluidState.`is`(FluidTags.LAVA)
                            && !blockState.`is`(Blocks.MAGMA_BLOCK)
                            && !CampfireBlock.isLitCampfire(blockState)
                        ) ParticleRegister.RAIN_SPLASH.get() else ParticleTypes.SMOKE
                    val color = CatastropheClientManager.nearestCatastropheInfo?.let {
                        getCatastropheColorInClient(
                            Vec3(
                                blockpos1.x.toDouble(),
                                blockpos1.y.toDouble(),
                                blockpos1.z.toDouble()
                            ), it
                        )
                    } ?: Vec3(1.0, 1.0, 1.0)
                    level.addParticle(
                        particleOptions,
                        blockpos1.x.toDouble() + d0,
                        blockpos1.y.toDouble() + d4,
                        blockpos1.z.toDouble() + d1,
                        color.x,
                        color.y,
                        color.z
                    )
                }
            }

            if (arrayOf(CatastropheType.SANDSTORM, CatastropheType.BLIZZARD).contains(
                    getAffectingCatastropheType(level, camera.position)
                )
            ) {
                if (random.nextInt(20) < soundTime++) {
                    soundTime = 0
                    var distance = Double.MAX_VALUE
                    for (px in -4..4) {
                        for (pz in -4..4) {
                            val bPos = blockpos.offset(Vec3i(px, 0, pz))
                            if (levelReader.getHeightmapPos(
                                    Heightmap.Types.MOTION_BLOCKING,
                                    bPos
                                ).y <= bPos.y
                            ) {
                                distance = min(distance, sqrt(blockpos.distSqr(bPos)))
                            }
                        }
                    }
                    val factor = catastropheEffect * max(0f, 1 - distance.toFloat() / 4)
                    level.playLocalSound(
                        blockpos,
                        SoundRegister.BLIZZARD.get(),
                        SoundSource.WEATHER,
                        0.4f * factor,
                        0.5f + 0.5f * factor,
                        false
                    )
                }
            }

            if (blockpos1 != null && random.nextInt(3) < rainSoundTime++) {
                rainSoundTime = 0
                if (blockpos1.y > blockpos.y + 1 && levelReader.getHeightmapPos(
                        Heightmap.Types.MOTION_BLOCKING,
                        blockpos
                    ).y > Mth.floor(blockpos.y.toFloat())
                ) {
                    level.playLocalSound(
                        blockpos1,
                        SoundEvents.WEATHER_RAIN_ABOVE,
                        SoundSource.WEATHER,
                        0.1f,
                        0.5f,
                        false
                    )
                } else {
                    level.playLocalSound(
                        blockpos1,
                        SoundEvents.WEATHER_RAIN,
                        SoundSource.WEATHER,
                        0.2f,
                        1.0f,
                        false
                    )
                }
            }
        }
    }
}