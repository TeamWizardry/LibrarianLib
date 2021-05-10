package com.teamwizardry.librarianlib.etcetera.test

import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.etcetera.Raycaster
import com.teamwizardry.librarianlib.math.times
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.content.TestEntity
import com.teamwizardry.librarianlib.testcore.content.TestItem
import com.teamwizardry.librarianlib.testcore.content.configure
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.entity.Entity
import net.minecraft.particle.BlockStateParticleEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import java.util.function.Predicate

internal object LibLibEtceteraTest {
    val logManager: ModLogManager = ModLogManager("liblib-etcetera-test", "LibrarianLib Etcetera Test")
    val manager: TestModContentManager = TestModContentManager("liblib-etcetera-test", "Etcetera", logManager)

    object CommonInitializer : ModInitializer {
        private val logger = logManager.makeLogger<CommonInitializer>()

        override fun onInitialize() {
            Particles.register()

            raycaster(
                "raycast_collision", "Collision",
                "Block mode: COLLISION\nFluid mode: NONE\nEntity filter: null",
                Raycaster.BlockMode.COLLISION, Raycaster.FluidMode.NONE, null
            )
            raycaster(
                "raycast_visual", "Visual",
                "Block mode: VISUAL\nFluid mode: NONE\nEntity filter: null",
                Raycaster.BlockMode.VISUAL, Raycaster.FluidMode.NONE, null
            )
            raycaster(
                "raycast_fluids", "Fluids",
                "Block mode: NONE\nFluid mode: ANY\nEntity filter: null",
                Raycaster.BlockMode.NONE, Raycaster.FluidMode.ANY, null
            )
            raycaster(
                "raycast_source", "Fluid Source",
                "Block mode: NONE\nFluid mode: ANY\nEntity filter: null",
                Raycaster.BlockMode.NONE, Raycaster.FluidMode.SOURCE, null
            )
            raycaster("raycast_entities", "Entities",
                "Block mode: NONE\nFluid mode: NONE\nEntity filter: { true }",
                Raycaster.BlockMode.NONE, Raycaster.FluidMode.NONE, Predicate { true }
            )
            raycaster("raycast_all", "All",
                "Block mode: COLLISION\nFluid mode: ANY\nEntity filter: { true }",
                Raycaster.BlockMode.COLLISION, Raycaster.FluidMode.ANY, Predicate { true }
            )


            manager.create<TestItem>("raycast_types") {
                name = "Raycaster: Hit Types"
                description = "Spawns a different particle type for each hit type"
                val serverRaycaster = Raycaster()

                rightClickHold.server {
                    notSneaking {
                        val eyePos = player.getCameraPosVec(0f)
                        val look = player.rotationVector * 100
                        serverRaycaster.cast(
                            player.world, Raycaster.BlockMode.COLLISION, Raycaster.FluidMode.ANY, Predicate { true },
                            eyePos.x, eyePos.y, eyePos.z,
                            eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                        )
                        var count = 1
                        val particleData: ParticleEffect = when (serverRaycaster.hitType) {
                            Raycaster.HitType.NONE -> {
                                return@notSneaking
                            }
                            Raycaster.HitType.BLOCK -> {
                                val state = player.world.getBlockState(
                                    BlockPos(
                                        serverRaycaster.blockX,
                                        serverRaycaster.blockY,
                                        serverRaycaster.blockZ
                                    )
                                )
                                BlockStateParticleEffect(ParticleTypes.BLOCK, state)
                            }
                            Raycaster.HitType.FLUID -> {
                                count = 3
                                ParticleTypes.SPLASH
                            }
                            Raycaster.HitType.ENTITY -> {
                                ParticleTypes.FLAME
                            }
                        }
                        (player.world as ServerWorld).spawnParticles(
                            particleData,
                            eyePos.x + look.x * serverRaycaster.fraction,
                            eyePos.y + look.y * serverRaycaster.fraction,
                            eyePos.z + look.z * serverRaycaster.fraction,
                            count,
                            0.0, 0.0, 0.0, 0.0
                        )
                        serverRaycaster.reset()
                    }
                }
            }

            manager.registerCommon()
        }

        private fun raycaster(
            id: String, name: String, desc: String, blockMode: Raycaster.BlockMode,
            fluidMode: Raycaster.FluidMode, entityFilter: Predicate<Entity>?
        ) {
            manager.create<TestEntity>(id) {
                description = desc
                val clientRaycaster = Raycaster()
                val serverRaycaster = Raycaster()

                client {
                    tick {
                        val eyePos = target.getCameraPosVec(0f)
                        val look = target.rotationVector * 100
                        clientRaycaster.cast(
                            world, blockMode, fluidMode, entityFilter,
                            eyePos.x, eyePos.y, eyePos.z,
                            eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                        )
                        world.addParticle(
                            Particles.TARGET_BLUE, true,
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
                        val eyePos = target.getCameraPosVec(0f)
                        val look = target.rotationVector * 100
                        serverRaycaster.cast(
                            world, blockMode, fluidMode, entityFilter,
                            eyePos.x, eyePos.y, eyePos.z,
                            eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                        )
                        (world as ServerWorld).spawnParticles(
                            Particles.TARGET_RED,
                            eyePos.x + look.x * serverRaycaster.fraction,
                            eyePos.y + look.y * serverRaycaster.fraction,
                            eyePos.z + look.z * serverRaycaster.fraction,
                            1,
                            0.0, 0.0, 0.0, 0.0
                        )
                        serverRaycaster.reset()
                    }
                }

                spawnerItem.configure {
                    this.name = name
                    rightClick.clear()

                    rightClick.server {
                        sneaking {
                            spawn(player)
                        }
                    }

                    rightClickHold.server {
                        notSneaking {
                            val eyePos = player.getCameraPosVec(0f)
                            val look = player.rotationVector * 20
                            serverRaycaster.cast(
                                player.world, blockMode, fluidMode, entityFilter,
                                eyePos.x, eyePos.y, eyePos.z,
                                eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                            )
                            (player.world as ServerWorld).spawnParticles(
                                Particles.TARGET_RED,
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
                        val eyePos = player.getCameraPosVec(0f)
                        val look = player.rotationVector * 20
                        clientRaycaster.cast(
                            player.world, blockMode, fluidMode, entityFilter,
                            eyePos.x, eyePos.y, eyePos.z,
                            eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                        )
                        player.world.addParticle(
                            Particles.TARGET_BLUE, true,
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
    }


    internal object ClientInitializer : ClientModInitializer {
        private val logger = logManager.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
            manager.registerClient()
            Particles.registerClient()
        }
    }

    internal object ServerInitializer : DedicatedServerModInitializer {
        private val logger = logManager.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
            manager.registerServer()
        }
    }
}
