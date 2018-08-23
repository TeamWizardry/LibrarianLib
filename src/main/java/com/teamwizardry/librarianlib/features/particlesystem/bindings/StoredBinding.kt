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
        override val size: Int
): ReadWriteParticleBinding {
    override fun get(particle: DoubleArray, index: Int): Double {
        return particle[this.index + index]
    }

    override fun set(particle: DoubleArray, index: Int, value: Double) {
        particle[this.index + index] = value
    }

    /**
     * Set all the values of this binding in the passed particle at once using the passed [values]
     *
     * @throws IllegalArgumentException if [values] has a different number elements than this component's [size]
     */
    fun set(particle: DoubleArray, vararg values: Double) {
        if(values.size != this.size)
            throw IllegalArgumentException("Mismatched sizes. Bindings size is ${this.size}, " +
                    "parameter size is ${values.size}")
        (0 until size).forEach {
            particle[this.index + it] = values[it]
        }
    }
}