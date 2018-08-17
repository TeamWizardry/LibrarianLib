package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.ReadWriteParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.require

class AccelerationUpdateModule(
        private val velocity: ReadWriteParticleBinding,
        private val acceleration: ReadParticleBinding
): ParticleUpdateModule {
    init {
        velocity.require(3)
        velocity.require(3)
    }
    override fun update(particle: DoubleArray) {
        update(particle, 0)
        update(particle, 1)
        update(particle, 2)
    }

    private fun update(particle: DoubleArray, index: Int) {
        val vel = velocity.get(particle, index)
        velocity.set(particle, index, vel + acceleration.get(particle, index))
    }
}
