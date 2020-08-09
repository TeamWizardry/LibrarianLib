package com.teamwizardry.librarianlib.etcetera.testmod

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.etcetera.Raycaster
import com.teamwizardry.librarianlib.math.times
import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestEntityConfig
import com.teamwizardry.librarianlib.testbase.objects.TestItem
import net.minecraft.entity.Entity
import net.minecraft.particles.BasicParticleType
import net.minecraft.particles.BlockParticleData
import net.minecraft.particles.IParticleData
import net.minecraft.particles.ParticleType
import net.minecraft.particles.ParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager
import java.util.function.Predicate

@Mod("librarianlib-etcetera-test")
object LibrarianLibEtceteraTestMod: TestMod("etcetera", "Etcetera", logger) {

    init {
        +raycaster("raycast_collision", "Collision",
            "Block mode: COLLISION\nFluid mode: NONE\nEntity filter: null",
            Raycaster.BlockMode.COLLISION, Raycaster.FluidMode.NONE, null
        )
        +raycaster("raycast_visual", "Visual",
            "Block mode: VISUAL\nFluid mode: NONE\nEntity filter: null",
            Raycaster.BlockMode.VISUAL, Raycaster.FluidMode.NONE, null
        )
        +raycaster("raycast_fluids", "Fluids",
            "Block mode: NONE\nFluid mode: ANY\nEntity filter: null",
            Raycaster.BlockMode.NONE, Raycaster.FluidMode.ANY, null
        )
        +raycaster("raycast_source", "Fluid Source",
            "Block mode: NONE\nFluid mode: ANY\nEntity filter: null",
            Raycaster.BlockMode.NONE, Raycaster.FluidMode.SOURCE, null
        )
        +raycaster("raycast_entities", "Entities",
            "Block mode: NONE\nFluid mode: NONE\nEntity filter: { true }",
            Raycaster.BlockMode.NONE, Raycaster.FluidMode.NONE, Predicate { true }
        )
        +raycaster("raycast_all", "All",
            "Block mode: COLLISION\nFluid mode: ANY\nEntity filter: { true }",
            Raycaster.BlockMode.COLLISION, Raycaster.FluidMode.ANY, Predicate { true }
        )

        +TestItem(TestItemConfig("raycast_types", "Raycaster: Hit Types") {
            description = "Spawns a different particle type for each hit type"
            val serverRaycaster = Raycaster()

            rightClickHold.server {
                notSneaking {
                    val eyePos = player.getEyePosition(0f)
                    val look = player.lookVec * 100
                    serverRaycaster.cast(
                        player.world, Raycaster.BlockMode.COLLISION, Raycaster.FluidMode.ANY, Predicate { true },
                        eyePos.x, eyePos.y, eyePos.z,
                        eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                    )
                    var count = 1
                    val particleData: IParticleData = when (serverRaycaster.hitType) {
                        Raycaster.HitType.NONE -> {
                            return@notSneaking
                        }
                        Raycaster.HitType.BLOCK -> {
                            val state = player.world.getBlockState(BlockPos(serverRaycaster.blockX, serverRaycaster.blockY, serverRaycaster.blockZ))
                            BlockParticleData(ParticleTypes.BLOCK, state)
                        }
                        Raycaster.HitType.FLUID -> {
                            count = 3
                            ParticleTypes.SPLASH
                        }
                        Raycaster.HitType.ENTITY -> {
                            ParticleTypes.FLAME
                        }
                    }
                    (player.world as ServerWorld).spawnParticle(particleData,
                        eyePos.x + look.x * serverRaycaster.fraction,
                        eyePos.y + look.y * serverRaycaster.fraction,
                        eyePos.z + look.z * serverRaycaster.fraction,
                        count,
                        0.0, 0.0, 0.0, 0.0
                    )
                    serverRaycaster.reset()
                }
            }
        })
    }

    fun raycaster(id: String, name: String, desc: String, blockMode: Raycaster.BlockMode,
        fluidMode: Raycaster.FluidMode, entityFilter: Predicate<Entity>?): TestEntityConfig {
        return TestEntityConfig(id, "Raycaster: $name") {
            description = desc
            val clientRaycaster = Raycaster()
            val serverRaycaster = Raycaster()

            client {
                tick {
                    val eyePos = target.getEyePosition(0f)
                    val look = target.lookVec * 100
                    clientRaycaster.cast(
                        world, blockMode, fluidMode, entityFilter,
                        eyePos.x, eyePos.y, eyePos.z,
                        eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                    )
                    world.addParticle(Particles.TARGET_BLUE, true,
                        eyePos.x + look.x * clientRaycaster.fraction,
                        eyePos.y + look.y * clientRaycaster.fraction,
                        eyePos.z + look.z * clientRaycaster.fraction,
                        0.0, 0.0, 0.0
                    )
                    clientRaycaster.reset()
                }
            }

            server {
                tick {
                    val eyePos = target.getEyePosition(0f)
                    val look = target.lookVec * 100
                    serverRaycaster.cast(
                        world, blockMode, fluidMode, entityFilter,
                        eyePos.x, eyePos.y, eyePos.z,
                        eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                    )
                    (world as ServerWorld).spawnParticle(Particles.TARGET_RED,
                        eyePos.x + look.x * serverRaycaster.fraction,
                        eyePos.y + look.y * serverRaycaster.fraction,
                        eyePos.z + look.z * serverRaycaster.fraction,
                        1,
                        0.0, 0.0, 0.0, 0.0
                    )
                    serverRaycaster.reset()
                }
            }

            spawnerItem.config {
                rightClick.clear()

                rightClick.server {
                    sneaking {
                        spawn(player)
                    }
                }

                rightClickHold.server {
                    notSneaking {
                        val eyePos = player.getEyePosition(0f)
                        val look = player.lookVec * 20
                        serverRaycaster.cast(
                            player.world, blockMode, fluidMode, entityFilter,
                            eyePos.x, eyePos.y, eyePos.z,
                            eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                        )
                        (player.world as ServerWorld).spawnParticle(Particles.TARGET_RED,
                            eyePos.x + look.x * serverRaycaster.fraction,
                            eyePos.y + look.y * serverRaycaster.fraction,
                            eyePos.z + look.z * serverRaycaster.fraction,
                            1,
                            0.0, 0.0, 0.0, 0.0
                        )
                        serverRaycaster.reset()
                    }
                }

                rightClickHold.client {
                    val eyePos = player.getEyePosition(0f)
                    val look = player.lookVec * 20
                    clientRaycaster.cast(
                        player.world, blockMode, fluidMode, entityFilter,
                        eyePos.x, eyePos.y, eyePos.z,
                        eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                    )
                    player.world.addParticle(Particles.TARGET_BLUE, true,
                        eyePos.x + look.x * clientRaycaster.fraction,
                        eyePos.y + look.y * clientRaycaster.fraction,
                        eyePos.z + look.z * clientRaycaster.fraction,
                        0.0, 0.0, 0.0
                    )
                    clientRaycaster.reset()
                }
            }
        }
    }

    @SubscribeEvent
    fun registerParticles(e: RegistryEvent.Register<ParticleType<*>>) {
        e.registry.register(Particles.TARGET_RED)
        e.registry.register(Particles.TARGET_BLUE)
    }

    override fun clientSetup(event: FMLClientSetupEvent) {
        super.clientSetup(event)
        Client.minecraft.particles.registerFactory(Particles.TARGET_RED, HitParticle::Factory)
        Client.minecraft.particles.registerFactory(Particles.TARGET_BLUE, HitParticle::Factory)
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Etcetera Test")
