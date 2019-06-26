/**
 * Miscellaneous ParticleBinding utilities
 */
@file:JvmName("ParticleBindings")
package com.teamwizardry.librarianlib.particles

import com.teamwizardry.librarianlib.particles.bindings.*
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
     * Loads the value into [contents].
     */
    fun load(particle: DoubleArray)
}

/**
 * A writable ParticleBinding
 *
 * @see ParticleBinding
 */
interface WriteParticleBinding: ParticleBinding {
    /**
     * Commits the current [contents] into storage
     */
    fun store(particle: DoubleArray)
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
 * @see InterpBinding
 * @see EaseBinding
 * @see PathVelocityBinding
 * @see VariableBinding
 */
interface ParticleBinding {
    val contents: DoubleArray

    @JvmDefault
    fun require(size: Int) {
        if (this.contents.size != size)
            throw IllegalArgumentException("Binding size is incorrect, required: $size, actual: ${this.contents.size}")
    }
}
