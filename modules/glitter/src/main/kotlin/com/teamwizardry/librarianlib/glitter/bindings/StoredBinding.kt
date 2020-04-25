package com.teamwizardry.librarianlib.glitter.bindings

import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.ReadWriteParticleBinding

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
        val size: Int
): ReadWriteParticleBinding {
    override val contents: DoubleArray = DoubleArray(size)

    override fun load(particle: DoubleArray) {
        particle.copyInto(contents, 0, index, index+size)
    }

    override fun store(particle: DoubleArray) {
        contents.copyInto(particle, index)
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
        values.copyInto(particle, index)
    }
}