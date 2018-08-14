package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.particlesystem.ParticleBinding

class ConstantBinding(vararg val values: Double): ParticleBinding {
    override fun getSize(): Int = values.size

    override fun get(particle: DoubleArray, index: Int): Double {
        return values[index]
    }

    override fun set(particle: DoubleArray, index: Int, value: Double) {
        // NO-OP
    }
}