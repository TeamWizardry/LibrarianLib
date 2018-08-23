package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.WriteParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule

/**
 * Copies the elements from one binding to another.
 *
 * Performs a single operation, copies the each element from [source] into [target].
 */
class SetValueUpdateModule(
        /**
         * The target binding, where [source] will be copied into. The size of the target cannot be indefinite (-1)
         */
        @JvmField val target: WriteParticleBinding,
        /**
         * The source binding, what will be copied into [target]. The size of this binding must be equal to the size of
         * [target] or be indefinite.
         */
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