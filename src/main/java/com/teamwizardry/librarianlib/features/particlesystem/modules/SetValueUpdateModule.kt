package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule

class SetValueUpdateModule(private val target: ParticleBinding, private val source: ParticleBinding): ParticleUpdateModule {
    init {
        if(target.size == -1) {
            throw IllegalArgumentException("Target binding cannot have an indefinite size")
        }
        if(source.size != -1 && source.size != target.size) {
            throw IllegalArgumentException("Source binding has a non-indefinite size which is different from the " +
                    "target size: source: ${source.size} target: ${target.size}")
        }
    }
    override fun update(particle: DoubleArray) {
        for(i in 0 until target.size) {
            target.set(particle, i, source.get(particle, i))
        }
    }
}