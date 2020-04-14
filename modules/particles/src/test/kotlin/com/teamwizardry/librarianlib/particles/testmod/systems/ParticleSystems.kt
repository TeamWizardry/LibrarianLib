package com.teamwizardry.librarianlib.particles.testmod.systems

import com.teamwizardry.librarianlib.particles.ParticleSystemManager
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity

object ParticleSystems {
    val systems = listOf(
        StaticSystem,
        PhysicsSystem,
        FloodSystem,
        PerfectBouncySystem,
        ForwardFacingSystem,
        SpriteSheetSystem,
        DepthSortSystem
    )
    private val systemMap = systems.associateBy { it.id }

    init {
        systems.forEach {
            ParticleSystemManager.add(it)
        }
    }

    fun spawn(name: String, player: Entity) {
        systemMap[name]?.spawn(player)
    }
}