package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.WriteParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule

class SetValueUpdateModule(
        @JvmField val target: WriteParticleBinding,
        @JvmField val source: ReadParticleBinding
): ParticleUpdateModule {
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
            target[particle, i] = source[particle, i]
        }
    }
}