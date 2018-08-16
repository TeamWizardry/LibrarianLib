package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.particlesystem.ReadWriteParticleBinding

class VariableBinding(override val size: Int): ReadWriteParticleBinding {
    private val array = DoubleArray(size)

    override fun get(particle: DoubleArray, index: Int): Double {
        return array[index]
    }

    override fun set(particle: DoubleArray, index: Int, value: Double) {
        array[index] = value
    }
}