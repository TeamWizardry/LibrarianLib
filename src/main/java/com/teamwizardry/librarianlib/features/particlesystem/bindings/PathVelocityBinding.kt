package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.particlesystem.ParticlePath
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.require

class PathVelocityBinding(
        private val lifetime: ReadParticleBinding,
        private val age: ReadParticleBinding,
        private val speed: ReadParticleBinding,
        private val path: ParticlePath
): ReadParticleBinding {
    init {
        lifetime.require(1)
        age.require(1)
        speed.require(1)
    }

    override val size: Int = 3

    override fun get(particle: DoubleArray, index: Int): Double {
        return path.getTangent(particle, age[particle, 0]/lifetime[particle, 0], index) * speed[particle, 0]
    }
}
