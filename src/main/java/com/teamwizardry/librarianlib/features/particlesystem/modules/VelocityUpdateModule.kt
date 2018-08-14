package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule

class VelocityUpdateModule(
        val position: ParticleBinding,
        val velocity: ParticleBinding,
        val previousPosition: ParticleBinding? = null
): ParticleUpdateModule {
    override fun update(particle: DoubleArray) {
        update(particle, 0)
        update(particle, 1)
        update(particle, 2)
    }

    private fun update(particle: DoubleArray, index: Int) {
        val pos = position.get(particle, index)
        previousPosition?.set(particle, index, pos)
        position.set(particle, index, pos + velocity.get(particle, index))
    }
}