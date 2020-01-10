package com.teamwizardry.librarianlib.particles.bindings

import com.teamwizardry.librarianlib.particles.ReadWriteParticleBinding

/**
 * A binding backed by an internal array, designed as temporary storage for passing values between update modules.
 */
class VariableBinding(
        /**
         * The number of virtual elements this variable should have. Cannot be indefinite (-1)
         */
        val size: Int
): ReadWriteParticleBinding {
    override val contents: DoubleArray = DoubleArray(size)

    override fun load(particle: DoubleArray) {
        //nop
    }

    override fun store(particle: DoubleArray) {
        //nop
    }
}