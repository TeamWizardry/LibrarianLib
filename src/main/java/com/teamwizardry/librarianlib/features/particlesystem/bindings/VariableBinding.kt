package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.particlesystem.ReadWriteParticleBinding

/**
 * A binding backed by an internal array, designed as TEMPORARY storage for passing values between update modules.
 */
class VariableBinding(
        /**
         * The number of virtual elements this variable should have. Cannot be indefinite (-1)
         */
        size: Int
): ReadWriteParticleBinding {

    protected val array: DoubleArray = DoubleArray(size)

    override fun getSize(): Int = array.size

    override fun getValue(index: Int): Double = array[index]

    override fun setValue(index: Int, value: Double) {
        this.array[index] = value
    }

    override fun load(particle: DoubleArray) {
    }

    override fun store(particle: DoubleArray) {
    }

    override fun getValues(): DoubleArray = array
}