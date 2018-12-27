package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.WriteParticleBinding

/**
 * Copies the elements from one binding to another.
 *
 * Performs a single operation, copies the each element from [source] into [target].
 */
open class SetValueUpdateModule(
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
        if (source.getSize() != target.getSize()) {
            throw IllegalArgumentException("Source binding has a non-indefinite size which is different from the " +
                    "target size. source: ${source.getSize()} target: ${target.getSize()}")
        }
    }

    override fun update(particle: DoubleArray) {
        source.load(particle)
        source.copyInto(target)
        target.store(particle)
    }
}