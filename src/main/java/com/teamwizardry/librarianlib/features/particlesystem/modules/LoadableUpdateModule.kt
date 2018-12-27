package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding

class LoadableUpdateModule(val module: ParticleUpdateModule, vararg val bindings: ReadParticleBinding) : ParticleUpdateModule {

    override fun update(particle: DoubleArray) {
        for (binding in bindings) {
            binding.load(particle)
        }
        module.update(particle)
    }
}