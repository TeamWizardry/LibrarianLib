package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding

class ConstantBinding(vararg val values: Double): ReadParticleBinding {
    override val size = values.size

    override fun get(particle: DoubleArray, index: Int): Double {
        return values[index]
    }
}