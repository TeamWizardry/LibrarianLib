package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding

class LifetimeInterpBinding(
        private val lifetime: ReadParticleBinding,
        private val age: ReadParticleBinding,
        private val interp: InterpFunction<Float>
): ReadParticleBinding {
    override val size = 1

    override fun get(particle: DoubleArray, index: Int): Double {
        val fraction = age[particle, 0] / lifetime[particle, 0]
        return interp.get(fraction.toFloat()).toDouble()
    }
}