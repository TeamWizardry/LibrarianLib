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
         * The multiplier for the normalized age. If this array is > 1 the movement will loop, and if this array is < 1
         * the movement will end before the end of the path.
         */
        open val timescale: ReadParticleBinding? = ConstantBinding(1.0),
        /**
         * The time offset for the normalized age. Applied before the [timescale], so regardless of [timescale]'s array,
         * if the offset is 0.5, the animation will begin halfway along the path
         */
        open val offset: ReadParticleBinding? = ConstantBinding(0.0),
        /**
         * The easing to use if you want one to manipulate the binding.
         */
        open val easing: Easing = Easing.linear
) : ReadParticleBinding {
    protected var time = 0.0

    protected open val array: DoubleArray = DoubleArray(0)

    override fun load(particle: DoubleArray) {
        age.load(particle)
        lifetime.load(particle)
        var t = age.getValue(0) / lifetime.getValue(0)

        if (easing != Easing.linear) t = easing(t.toFloat()).toDouble()
        if (offset != null) t += offset!!.getValue(0)
        if (timescale != null) t *= timescale!!.getValue(0)
        if(t != 0.0) {
            t %= 1
            // because 1 % 1 = 0, but it should go up to 1 inclusive. If t is really 0 the above if won't pass
            if(t == 0.0) t = 1.0
        }
        time = t
    }

    override fun getSize(): Int = array.size

    override fun getValue(index: Int): Double = array[index]

    override fun setValue(index: Int, value: Double) {
        array[index] = value
    }

    override fun getValues(): DoubleArray = array
}