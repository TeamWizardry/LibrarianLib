package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.particlesystem.ParticleBinding

class VariableBinding(private val size: Int): ParticleBinding {
    private val array = DoubleArray(size)

    override fun getSize(): Int = size

    override fun get(particle: DoubleArray, index: Int): Double {
        return array[index]
    }

    override fun set(particle: DoubleArray, index: Int, value: Double) {
        array[index] = value
    }
}