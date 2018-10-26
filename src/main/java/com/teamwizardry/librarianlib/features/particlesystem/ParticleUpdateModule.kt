package com.teamwizardry.librarianlib.features.particlesystem

import com.teamwizardry.librarianlib.features.particlesystem.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SetValueUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.AccelerationUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.VelocityUpdateModule

/**
 * An object that runs code on a particle.
 *
 * Particle update modules are the simplest modules, the others being the [ParticleGlobalUpdateModule] and
 * [ParticleRenderModule]. The purpose of a particle update module is to run code on a per-particle basis, whether it
 * be during the world tick ([ParticleSystem.updateModules]) or in preparation for a particle's rendering
 * ([ParticleSystem.renderPrepModules]).
 *
 * Modules can perform almost any task, from simply setting a value ([SetValueUpdateModule]) to performing basic
 * position/velocity caluclations ([VelocityUpdateModule], [AccelerationUpdateModule]) to providing an all-in-one and
 * performant physics module ([BasicPhysicsUpdateModule]).
 */
interface ParticleUpdateModule {
    /**
     * Run on the passed particle. This module should do no further processing for the particle after this method
     * completes.
     */
    fun update(particle: DoubleArray)

    fun init(particle: DoubleArray) {}
}

/**
 * An object that runs code on the full list of particles.
 *
 * Particle batch update modules are called at the end of each tick and are passed the full particle list. They were
 * designed to do depth sorting but they are general enough to do much more.
 */
interface ParticleGlobalUpdateModule {
    /**
     * Run on the passed particle list.
     */
    fun update(particles: MutableList<DoubleArray>)
}
