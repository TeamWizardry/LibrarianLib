package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding

/**
 * A read-only binding backed by a constant value.
 */
class ConstantBinding(
        /**
         * The backing array of elements to use. Theoretically an array can be passed and that array modified
         * externally, however using a [VariableBinding] is the more orthodox way of achieving that effect.
         */
        vararg val values: Double
): ReadParticleBinding {
    override val size = values.size

    override fun get(particle: DoubleArray, index: Int): Double {
        return values[index]
    }
}