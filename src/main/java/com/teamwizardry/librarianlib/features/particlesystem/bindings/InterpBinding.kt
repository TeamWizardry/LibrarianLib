package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding

/**
 * A 1D binding that generates its value by passing its normalized age (0–1) into an InterpFunction<Float>
 */
class InterpBinding @JvmOverloads constructor(
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
         * if the offset is 0.5, the animation will begin halfway along the path.
         */
        override val offset: ReadParticleBinding? = null,
        /**
         * The interp to use when generating values for the binding.
         */
        @JvmField val interp: InterpFunction<Float>,
        /**
         * The start value to interpolate from.
         */
        @JvmField val origin: ReadParticleBinding = ConstantBinding(0.0),
        /**
         * The end value to interpolate to.
         */
        @JvmField val target: ReadParticleBinding = ConstantBinding(1.0),
        /**
         * The easing to use when generating values for the binding.
         */
        override val easing: Easing = Easing.linear
) : AbstractTimeBinding(lifetime, age, timescale, offset, easing) {

    override val value: DoubleArray = DoubleArray(origin.value.size)

    init {
        lifetime.require(1)
        age.require(1)
        target.require(origin.value.size)
    }

    override fun load(particle: DoubleArray) {
        super.load(particle)
        val multiplier = interp.get(time.toFloat()).toDouble()
        for(i in 0 until origin.value.size) {
            value[i] = origin.value[i] + (target.value[i] * multiplier)
        }
    }
}