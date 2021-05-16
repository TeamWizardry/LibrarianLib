package com.teamwizardry.librarianlib.glitter.test

import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.ParticleSystemManager
import com.teamwizardry.librarianlib.glitter.test.systems.*
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.content.TestEntity
import com.teamwizardry.librarianlib.testcore.content.configure
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer

internal object LibLibGlitterTest {
    val logManager: ModLogManager = ModLogManager("liblib-glitter-test", "LibrarianLib Glitter Test")
    val manager: TestModContentManager = TestModContentManager("liblib-glitter-test", "Glitter", logManager)

    object CommonInitializer : ModInitializer {
        private val logger = logManager.makeLogger<CommonInitializer>()

        val systems = listOf(
            System(
                "flood",
                "Spray Physics Particles",
                """
                Sprays randomly colored particles with velocities in a general direction.
            """.trimIndent()
            ),
            System(
                "perfect_bouncy",
                "Perfectly Bouncy Physics Particles",
                """
                Spawns randomly colored particles with a specific velocity and 100% bounciness.
                
                Created to test collision bugs with 100% bouncy particles. As far as is known, these errors are impossible to resolve while maintaining performance.
            """.trimIndent()
            ),
            System(
                "physics",
                "Particle with Physics",
                """
                Spawns randomly colored particles with a specific velocity
            """.trimIndent()
            ),
            System(
                "static",
                "Static Particle",
                """
                Spawns randomly colored, motionless particles.
            """.trimIndent()
            ),
            System(
                "forward_facing",
                "Forward-Facing Particle",
                """
                Spawns randomly colored particles that face forward along their velocity vector
            """.trimIndent()
            ),
            System(
                "spritesheet",
                "Sprite Sheet Particle",
                """
                Spawns randomly colored, motionless particles with one of four sprites
            """.trimIndent()
            ),
            System(
                "depthsort",
                "Depth Sorted Particle",
                """
                Spawns randomly colored, motionless particles with depth sorting enabled
            """.trimIndent()
            ),
            System(
                "ignore_particle_setting",
                "Ignore Particle Setting Particle",
                """
                Spawns particles that ignore the particle setting
            """.trimIndent()
            ),
            System(
                "show_on_minimal",
                "Show On Minimal Particle",
                """
                Spawns particles that appear at a reduced rate on the minimal particle setting
            """.trimIndent()
            ),
            System(
                "spawn_count_adjustment",
                "Spawn Count Adjusted Particle",
                """
                Spawns particles that have their spawn count adjusted by the current particle setting
            """.trimIndent()
            ),
            System(
                "partial_tick_lerp",
                "Partial Tick Lerp Test Particle",
                """
                Spawns particles at a high, static velocity in order to test lerping
            """.trimIndent()
            ),
            System(
                "size_axes",
                "Size Axes Test Particle",
                """
                Spawns particles with different widths and heights in order to test per-axis sizes
            """.trimIndent()
            ),
            System(
                "up_vector",
                "Up Vector Test Particle",
                """
                Spawns billboarded particles with custom up vectors 
            """.trimIndent()
            ),
            System(
                "up_facing_vector",
                "Up Facing Vector Test Particle",
                """
                Spawns particles with custom up and facing vectors 
            """.trimIndent()
            ),
            System(
                "custom_uv",
                "Custom UV coordinates Test Particle",
                """
                Spawns particles with custom UV sizes and offsets
            """.trimIndent()
            ),
            System(
                "world_lit",
                "World lighting Test Particle",
                """
                Spawns world lit particles
            """.trimIndent()
            ),
            System(
                "diffuse_lit",
                "Diffuse lighting Test Particle",
                """
                Spawns diffuse lit particles
            """.trimIndent()
            ),
            System(
                "physics_only",
                "Physics only Test Particle",
                """
                Identical to flood but only renders when this item is held
            """.trimIndent()
            ),
        )

        override fun onInitialize() {
            for(system in systems) {
                manager.create<TestEntity>(system.id) {
                    name = system.name
                    description = system.description

                    spawnerItem.configure {
                        rightClick.clear()
                        rightClick.server {
                            sneaking {
                                this@create.spawn(player)
                            }
                        }
                    }
                }
            }
            manager.registerCommon()
        }

        data class System(val id: String, val name: String, val description: String)
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = logManager.makeLogger<ClientInitializer>()

        val systems = listOf(
            System("static", StaticSystem),
            System("physics", PhysicsSystem),
            System("flood", FloodSystem),
            System("perfect_bouncy", PerfectBouncySystem),
            System("forward_facing", ForwardFacingSystem),
            System("spritesheet", SpriteSheetSystem),
            System("depthsort", DepthSortSystem),
            System("ignore_particle_setting", IgnoreParticleSettingSystem),
            System("show_on_minimal", ShowOnMinimalSystem),
            System("spawn_count_adjustment", SpawnCountAdjustmentSystem),
            System("partial_tick_lerp", PartialTickLerpSystem),
            System("size_axes", SizeAxesSystem),
            System("up_vector", UpVectorSystem),
            System("up_facing_vector", UpFacingVectorSystem),
            System("custom_uv", CustomUvSystem),
            System("world_lit", WorldLitSystem),
            System("diffuse_lit", DiffuseLitSystem),
            System("physics_only", PhysicsOnlySystem),
        )

        override fun onInitializeClient() {
            val commonIds = CommonInitializer.systems.map { it.id }.toSet()
            val clientIds = systems.map { it.id }.toSet()
            if(commonIds != clientIds) {
                throw IllegalStateException("Mismatched common and client IDs. " +
                        "Common: [${commonIds.joinToString(", ")}], client: [${clientIds.joinToString(", ")}]")
            }

            for(system in systems) {
                manager.named<TestEntity>(system.id) {
                    tick.client {
                        system.system.spawn(target)
                    }
                    spawnerItem.rightClickHold.client {
                        notSneaking {
                            system.system.spawn(player)
                        }
                    }
                }
                ParticleSystemManager.add(system.system)
            }
            manager.registerClient()
        }

        data class System(val id: String, val system: TestSystem)
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = logManager.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
            manager.registerServer()
        }
    }
}
