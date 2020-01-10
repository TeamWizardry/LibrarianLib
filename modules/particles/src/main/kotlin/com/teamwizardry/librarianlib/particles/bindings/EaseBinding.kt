package com.teamwizardry.librarianlib.particles.bindings

import com.teamwizardry.librarianlib.math.Easing
import com.teamwizardry.librarianlib.particles.ParticleSystem
import com.teamwizardry.librarianlib.particles.ReadParticleBinding

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
        @JvmField val origin: ReadParticleBinding = ConstantBinding(*DoubleArray(bindingSize) { 1.0 }),
        /**
         * The end value to interpolate to.
         */
        @JvmField var target: ReadParticleBinding = ConstantBinding(*DoubleArray(bindingSize) { 1.0 })
) : AbstractTimeBinding(lifetime, age, timescale, offset, easing) {

    override val contents: DoubleArray = DoubleArray(bindingSize)

    init {
        lifetime.require(1)
        age.require(1)
        origin.require(bindingSize)
        target.require(bindingSize)
    }

    override fun load(particle: DoubleArray) {
        super.load(particle)
        origin.load(particle)
        target.load(particle)
        for(i in 0 until bindingSize) {
            contents[i] = (origin.contents[i] * (1 - time)) + (target.contents[i] * time)
        }
    }
}