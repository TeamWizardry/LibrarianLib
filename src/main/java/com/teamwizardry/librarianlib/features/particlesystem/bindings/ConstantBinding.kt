package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding

/**
 * A read-only binding backed by a constant array.
 */
class ConstantBinding(
        /**
         * The backing array of elements to use. Theoretically an array can be passed and that array modified
         * externally, however using a [VariableBinding] is the more orthodox way of achieving that effect.
         */
        private vararg val array: Double
): ReadParticleBinding {

    override fun getSize(): Int = array.size

    override fun getValue(index: Int): Double = array[index]

    override fun setValue(index: Int, value: Double) {
    }

    override fun load(particle: DoubleArray) {
    }

    override fun getValues(): DoubleArray = array

}