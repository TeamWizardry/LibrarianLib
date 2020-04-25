package com.teamwizardry.librarianlib.glitter.bindings

import com.teamwizardry.librarianlib.math.Easing
import com.teamwizardry.librarianlib.glitter.ParticlePath
import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.ReadParticleBinding

class PathBinding @JvmOverloads constructor(
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
         * The path object to use for the positioning.
         */
        @JvmField val path: ParticlePath,
        /**
         * The start value to interpolate from.
         */
        @JvmField var origin: ReadParticleBinding = ConstantBinding(*DoubleArray(path.value.size) { 0.0 }),
        /**
         * The end value to interpolate to.
         */
        @JvmField var target: ReadParticleBinding = ConstantBinding(*DoubleArray(path.value.size) { 1.0 }),
        /**
         * The easing to use when generating values for the binding.
         */
        override val easing: Easing = Easing.linear
) : AbstractTimeBinding(lifetime, age, timescale, offset, easing) {

    override val contents: DoubleArray = DoubleArray(path.value.size)

    init {
        lifetime.require(1)
        age.require(1)
        timescale?.require(1)
        offset?.require(1)
    }

    override fun load(particle: DoubleArray) {
        super.load(particle)
        path.computePosition(particle, time * easing(time.toFloat()))
        for(i in 0 until contents.size) {
            contents[i] = origin.contents[i] + (target.contents[i] * path.value[i])
        }
    }
}