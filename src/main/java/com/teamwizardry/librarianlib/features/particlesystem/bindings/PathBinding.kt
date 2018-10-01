package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.particlesystem.ParticlePath
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.require

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

        @JvmField var origin: ReadParticleBinding? = null,

        @JvmField var target: ReadParticleBinding? = null,
        /**
         * The easing to use when generating values for the binding
         */
        override val easing: Easing = Easing.linear
) : AbstractTimeBinding(lifetime, age, timescale, offset, easing) {

    init {
        lifetime.require(1)
        age.require(1)
        timescale?.require(1)
        offset?.require(1)

        if (origin == null) {
            origin = ConstantBinding(*DoubleArray(path.getSize()) { 0.0 })
        }
        if (target == null)
            target = ConstantBinding(*DoubleArray(path.getSize()) { 1.0 })
    }

    override val size: Int = path.getSize()

    override fun get(particle: DoubleArray, index: Int): Double {
        val time = getTime(particle)
        return origin!![particle, index] + (target!![particle, index] * path.getPosition(particle, getTime(particle) * easing(time.toFloat()), index))
    }
}