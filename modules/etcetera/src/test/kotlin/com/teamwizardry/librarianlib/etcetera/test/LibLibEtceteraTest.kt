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
import net.minecraft.block.Blocks
import net.minecraft.fluid.Fluids
import net.minecraft.particle.BlockStateParticleEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShapes
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
                "Block mode: COLLISION",
            ) {
                it.withBlockMode(Raycaster.BlockMode.COLLISION)
            }
            raycaster(
                "raycast_visual", "Visual",
                "Block mode: VISUAL",
            ) {
                it.withBlockMode(Raycaster.BlockMode.VISUAL)
            }
            raycaster(
                "raycast_fluids", "Fluids",
                "Fluid mode: ANY",
            ) {
                it.withFluidMode(Raycaster.FluidMode.ANY)
            }
            raycaster(
                "raycast_source", "Fluid Source",
                "Fluid mode: SOURCE",
            ) {
                it.withFluidMode(Raycaster.FluidMode.SOURCE)
            }
            raycaster("raycast_entities", "Entities",
                "Entities: <all>",
            ) {
                it.withEntities(null, null)
            }
            raycaster("raycast_all", "All",
                "Block mode: COLLISION\nFluid mode: ANY\nEntities: <all>",
            ) {
                it.withBlockMode(Raycaster.BlockMode.COLLISION)
                    .withFluidMode(Raycaster.FluidMode.ANY)
                    .withEntities(null, null)
            }
            raycaster("raycast_scaffolding", "Collision + Scaffolding",
                "Block mode: COLLISION\nBlock override: scaffolding=full block",
            ) {
                it.withBlockMode(Raycaster.BlockMode.COLLISION)
                    .withBlockOverride { state, _, _ ->
                        when(state.block) {
                            Blocks.SCAFFOLDING -> VoxelShapes.fullCube()
                            else -> null
                        }
                    }
            }
            raycaster("raycast_fluid_no_lava", "Fluid + No Lava",
                "Fluid mode: ANY\nFluid override: lava=empty, flowing_lava=empty",
            ) {
                it.withFluidMode(Raycaster.FluidMode.ANY)
                    .withFluidOverride { state, _, _ ->
                        when(state.fluid) {
                            Fluids.LAVA -> VoxelShapes.empty()
                            Fluids.FLOWING_LAVA -> VoxelShapes.empty()
                            else -> null
                        }
                    }
            }

            manager.create<TestItem>("raycast_types") {
                name = "Raycaster: Hit Types"
                description = "Spawns a different particle type for each hit type"
                val serverRaycaster = Raycaster()

                rightClickHold.server {
                    notSneaking {
                        val eyePos = player.getCameraPosVec(0f)
                        val look = player.rotationVector * 100
                        serverRaycaster.cast(
                            Raycaster.RaycastRequest(
                                player.world,
                                eyePos.x, eyePos.y, eyePos.z,
                                eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                            )
                                .withEntityContext(this.player)
                                .withBlockMode(Raycaster.BlockMode.COLLISION)
                                .withFluidMode(Raycaster.FluidMode.ANY)
                                .withEntities(null, null)
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
            id: String, name: String, desc: String,
            configure: (Raycaster.RaycastRequest) -> Unit
        ) {
            manager.create<TestEntity>(id) {
                description = desc
                val clientRaycaster = Raycaster()
                val serverRaycaster = Raycaster()

                client {
                    tick {
                        val eyePos = target.getCameraPosVec(0f)
                        val look = target.rotationVector * 100
                        val request = Raycaster.RaycastRequest(
                            world,
                            eyePos.x, eyePos.y, eyePos.z,
                            eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                        )
                        configure(request)
                        clientRaycaster.cast(request)
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
                        val request = Raycaster.RaycastRequest(
                            world,
                            eyePos.x, eyePos.y, eyePos.z,
                            eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                        )
                        configure(request)
                        serverRaycaster.cast(request)
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
                            val request = Raycaster.RaycastRequest(
                                player.world,
                                eyePos.x, eyePos.y, eyePos.z,
                                eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                            )
                                .withEntityContext(player)
                            configure(request)
                            serverRaycaster.cast(request)
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
                        val request = Raycaster.RaycastRequest(
                            player.world,
                            eyePos.x, eyePos.y, eyePos.z,
                            eyePos.x + look.x, eyePos.y + look.y, eyePos.z + look.z
                        )
                            .withEntityContext(player)
                        configure(request)
                        clientRaycaster.cast(request)
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
