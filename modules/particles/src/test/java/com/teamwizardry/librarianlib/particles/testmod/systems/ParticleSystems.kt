package com.teamwizardry.librarianlib.particles.testmod.systems

import com.teamwizardry.librarianlib.particles.ParticleSystemManager
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity

object ParticleSystems {
    val systems = mapOf(
        "static" to StaticSystem,
        "physics" to PhysicsSystem
    )

    init {
        systems.values.forEach {
            ParticleSystemManager.add(it)
        }
    }

    fun spawn(name: String, player: Entity) {
        systems[name]?.spawn(player)
    }
}