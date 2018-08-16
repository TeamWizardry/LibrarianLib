package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ReadWriteParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.WriteParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule

class VelocityUpdateModule(
        val position: ReadWriteParticleBinding,
        val velocity: ReadParticleBinding,
        val previousPosition: WriteParticleBinding? = null
): ParticleUpdateModule {
    override fun update(particle: DoubleArray) {
        update(particle, 0)
        update(particle, 1)
        update(particle, 2)
    }

    private fun update(particle: DoubleArray, index: Int) {
        val pos = position[particle, index]
        previousPosition?.set(particle, index, pos)
        position[particle, index] = pos + velocity[particle, index]
    }
}