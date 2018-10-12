package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.particlesystem.ReadWriteParticleBinding

/**
 * A binding backed by an internal array, designed as temporary storage for passing values between update modules.
 */
class VariableBinding(
        /**
         * The number of virtual elements this variable should have. Cannot be indefinite (-1)
         */
        override val size: Int
): ReadWriteParticleBinding {
    private val array = DoubleArray(size)

    override fun get(particle: DoubleArray, index: Int): Double {
        return array[index]
    }

    override fun set(particle: DoubleArray, index: Int, value: Double) {
        array[index] = value
    }
}