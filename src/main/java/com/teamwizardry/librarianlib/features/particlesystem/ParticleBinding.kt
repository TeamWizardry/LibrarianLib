package com.teamwizardry.librarianlib.features.particlesystem

interface ReadWriteParticleBinding: ReadParticleBinding, WriteParticleBinding

interface ReadParticleBinding {
    /**
     * The number of components in this binding, or -1 if it is unbounded
     */
    val size: Int
    operator fun get(particle: DoubleArray, index: Int): Double
}

interface WriteParticleBinding {
    /**
     * The number of components in this binding, or -1 if it is unbounded
     */
    val size: Int
    operator fun set(particle: DoubleArray, index: Int, value: Double)
}
