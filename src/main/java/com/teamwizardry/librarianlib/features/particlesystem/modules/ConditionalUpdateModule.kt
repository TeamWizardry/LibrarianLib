package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule

class ConditionalUpdateModule(val module: ParticleUpdateModule, val condition: (particle: DoubleArray) -> Boolean) : ParticleUpdateModule {

    override fun update(particle: DoubleArray) {
        if (condition(particle)) {
            module.update(particle)
        }
    }
}