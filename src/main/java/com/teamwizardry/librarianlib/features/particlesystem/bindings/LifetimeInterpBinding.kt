package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem

/**
 * A 1D binding that generates its value by passing its normalized age (0–1) into an InterpFunction<Float>
 */
class LifetimeInterpBinding(
        /**
         * The lifetime binding for the particle. Generally [ParticleSystem.lifetime]
         */
        @JvmField val lifetime: ReadParticleBinding,
        /**
         * The age binding for the particle. Generally [ParticleSystem.age]
         */
        @JvmField val age: ReadParticleBinding,
        /**
         * The interp to use when generating values for the binding
         */
        @JvmField val interp: InterpFunction<Float>
): ReadParticleBinding {
    override val size = 1

    override fun get(particle: DoubleArray, index: Int): Double {
        val fraction = age[particle, 0] / lifetime[particle, 0]
        return interp.get(fraction.toFloat()).toDouble()
    }
}