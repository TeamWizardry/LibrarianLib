package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding

abstract class AbstractTimeBinding(
        /**
         * The lifetime binding for the particle. Generally [ParticleSystem.lifetime]
         */
        open val lifetime: ReadParticleBinding,
        /**
         * The age binding for the particle. Generally [ParticleSystem.age]
         */
        open val age: ReadParticleBinding,
        /**
         * The multiplier for the normalized age. If this value is > 1 the movement will loop, and if this value is < 1
         * the movement will end before the end of the path.
         */
        open val timescale: ReadParticleBinding? = ConstantBinding(1.0),
        /**
         * The time offset for the normalized age. Applied before the [timescale], so regardless of [timescale]'s value,
         * if the offset is 0.5, the animation will begin halfway along the path
         */
        open val offset: ReadParticleBinding? = ConstantBinding(0.0),

        open val easing: Easing = Easing.linear
) : ReadParticleBinding {

    fun getTime(particle: DoubleArray): Double {
        var t = age[particle, 0] / lifetime[particle, 0]

        if (easing != Easing.linear) t = easing(t.toFloat()).toDouble()
        if (offset != null) t += offset!![particle, 0]
        if (timescale != null) t *= timescale!![particle, 0]
        t %= 1

        return t
    }
}