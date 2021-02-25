package com.teamwizardry.librarianlib.glitter.testmod.systems

object SystemNames {
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
    )

    data class System(val id: String, val name: String, val description: String)
}