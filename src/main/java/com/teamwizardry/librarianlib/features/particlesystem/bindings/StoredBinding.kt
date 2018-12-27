package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.ReadWriteParticleBinding

/**
 * A binding backed directly by some range of values in the particle array. Created by [ParticleSystem.bind]
 */
class StoredBinding internal constructor(
        /**
         * The index in the particle array of the first element of this binding
         */
        val index: Int,
        /**
         * The number of elements this binding has allocated
         */
        size: Int
) : ReadWriteParticleBinding {

    protected val array: DoubleArray = DoubleArray(size)

    override fun getSize(): Int = array.size

    override fun getValue(index: Int): Double = array[index]

    override fun setValue(index: Int, value: Double) {
        array[index] = value
    }

    override fun load(particle: DoubleArray) {
        particle.copyInto(array, 0, index, index + getSize())
    }

    override fun store(particle: DoubleArray) {
        array.copyInto(particle, index)
    }

    /**
     * Set all the values of this binding in the passed particle at once using the passed [values]
     *
     * @throws IllegalArgumentException if [values] has a different number elements than this component's [size]
     */
    fun set(particle: DoubleArray, vararg values: Double) {
        if (values.size != getSize())
            throw IllegalArgumentException("Mismatched sizes. Bindings size is ${getSize()}, " +
                    "parameter size is ${values.size}")
        values.copyInto(particle, index)
    }

    override fun getValues(): DoubleArray = array

}