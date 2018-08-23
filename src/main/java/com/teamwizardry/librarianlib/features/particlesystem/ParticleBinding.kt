/**
 * Miscellaneous ParticleBinding utilities
 */
@file:JvmName("ParticleBindings")
package com.teamwizardry.librarianlib.features.particlesystem

import com.teamwizardry.librarianlib.features.particlesystem.bindings.*
import net.minecraft.util.math.Vec3d
import java.awt.Color

/**
 * A read/write particle binding
 *
 * @see ParticleBinding
 * @see ReadParticleBinding
 * @see WriteParticleBinding
 */
interface ReadWriteParticleBinding: ParticleBinding, ReadParticleBinding, WriteParticleBinding

/**
 * A readable ParticleBinding
 *
 * @see ParticleBinding
 */
interface ReadParticleBinding: ParticleBinding {
    /**
     * Returns the value at [index] given the passed [particle].
     *
     * @param particle The particle array
     * @param index The index to be read
     */
    operator fun get(particle: DoubleArray, index: Int): Double
}

/**
 * A writable ParticleBinding
 *
 * @see ParticleBinding
 */
interface WriteParticleBinding: ParticleBinding {
    /**
     * Sets the value at [index] to [value] given the passed [particle].
     *
     * @param particle The particle array
     * @param index The index to be written to
     * @param value The value to be written
     */
    operator fun set(particle: DoubleArray, index: Int, value: Double)
}

/**
 * Bindings are essentially accessors for some abstract data, whether that is stored persistently in the particle,
 * returned from a constant value, or computed on the fly.
 *
 * While it would be easy to store information about particles in fields as [Vec3d]s, [Color]s, and any number of other
 * objects, doing so is slow and incurs a major memory burden, especially the immutable [Vec3d], which must be
 * reallocated every time it is modified, causing an astronomical amount of memory churn. One solution to this problem
 * is to store the state of particles in a raw double array, which eliminates the performance penalty of constantly
 * allocating [Vec3d]s and other short-lived objects, as well as the JVM overhead of simply storing those objects.
 * While one method of using this array would be to simply pass indices around, [ParticleBinding]s provide a much more
 * flexible interface.
 *
 * One of the major upsides of bindings is that they don't necessarily have to be tied directly to the particle array,
 * or have anything to do with the specific particle at all. While the most common binding, [StoredBinding], is directly
 * tied into the particle array, other binding implementations dynamically compute their values or even return a static
 * value that has nothing to do with the particle in question.
 *
 * @see StoredBinding
 * @see ConstantBinding
 * @see LifetimeInterpBinding
 * @see PathPositionBinding
 * @see PathVelocityBinding
 * @see VariableBinding
 */
interface ParticleBinding {
    /**
     * The number of valid indices in this binding (0 <= i < size), or -1 if it is unbounded.
     */
    val size: Int
}

/**
 * Asserts that the binding's size must be at least [size], or it must be indefinite.
 *
 * @param size the minimum size required
 * @throws IllegalArgumentException if the binding is too small
 */
fun ParticleBinding.require(size: Int) {
    if (this.size != -1 && this.size < size)
        throw IllegalArgumentException("Binding size is too small, required: $size, passed: ${this.size}")
}
