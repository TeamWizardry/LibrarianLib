package com.teamwizardry.librarianlib.test.particlesystem

import com.teamwizardry.librarianlib.features.particlesystem.ParticleRenderManager
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.ReadOnlyParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.ConstantBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.VariableBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.*
import net.minecraft.util.math.Vec3d
import java.awt.Color

object FountainParticleSystem {
    private val system = ParticleSystem()

    private val position = system.bind(3)
    private val previousPosition = system.bind(3)
    private val velocity = system.bind(3)
    private val color = system.bind(4)
    private val size = system.bind(1)

    init {
        reloadSystem()
        ParticleRenderManager.emitters.add(system)
        ParticleRenderManager.reloadHandlers.add(Runnable { reloadSystem() })
    }

    fun spawn(lifetime: Double, position: Vec3d, velocity: Vec3d, color: Color, size: Double) {
        system.addParticle(lifetime,
                position.x, position.y, position.z,
                position.x, position.y, position.z,
                velocity.x, velocity.y, velocity.z,
                color.red/255.0, color.green/255.0, color.blue/255.0, color.alpha/255.0,
                size
        )
    }

    fun reloadSystem() {
        system.updateModules.clear()
        system.renderModules.clear()

        system.updateModules.add(BasicPhysicsUpdateModule(
                position = position,
                previousPosition = previousPosition,
                velocity = velocity,
                gravity = 0.04,
                bounciness = 0.4,
                friction = 0.2,
                damping = 0.0
        ))
        system.poolSize = 3000

//        system.renderModules.add(SpriteRenderModule(
//                previousPosition = previousPosition,
//                position = position,
//                color = color,
//                size = size
//        ))
    }
}