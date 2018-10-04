package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.require

/**
 * A 1D binding that generates its value by passing its normalized age (0–1) into an InterpFunction<Float>
 */
class EaseBinding @JvmOverloads constructor(
        /**
         * The lifetime binding for the particle. Generally [ParticleSystem.lifetime]
         */
        override val lifetime: ReadParticleBinding,
        /**
         * The age binding for the particle. Generally [ParticleSystem.age]
         */
        override val age: ReadParticleBinding,
        /**
         * The multiplier for the normalized age. If this value is > 1 the movement will loop, and if this value is < 1
         * the movement will end before the end of the path.
         */
        override val timescale: ReadParticleBinding? = null,
        /**
         * The time offset for the normalized age. Applied before the [timescale], so regardless of [timescale]'s value,
         * if the offset is 0.5, the animation will begin halfway along the path
         */
        override val offset: ReadParticleBinding? = null,
        /**
         * The easing to use when generating values for the binding.
         */
        override val easing: Easing,
        /**
         * If working with a single number, set to 1, if a vector, set to 3 for x, y, z, if a color,
         * set to 4 for R, G, B, and A
         *
         * This exists to allow flexibility so you can ease whatever object you want no matter how many parameters it
         * may have.
         */
        @JvmField val bindingSize: Int,
        /**
         * The start value to interpolate from.
         */
        @JvmField var origin: ReadParticleBinding? = null,
        /**
         * The end value to interpolate to.
         */
        @JvmField var target: ReadParticleBinding? = null
) : AbstractTimeBinding(lifetime, age, timescale, offset, easing) {

    init {
        lifetime.require(1)
        age.require(1)
        if (origin == null) {
            origin = ConstantBinding(*DoubleArray(bindingSize) { 0.0 })
        }
        if (target == null)
            target = ConstantBinding(*DoubleArray(bindingSize) { 1.0 })
    }

    override val size = bindingSize

    override fun get(particle: DoubleArray, index: Int): Double {
        return origin!![particle, index] + (target!![particle, index] * getTime(particle).toFloat())
    }
}