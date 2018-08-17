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

fun ReadParticleBinding.require(size: Int) {
    if(this.size != -1 && this.size < size)
        throw IllegalArgumentException("Binding size is too small, required: $size, passed: ${this.size}")
}

fun WriteParticleBinding.require(size: Int) {
    if(this.size != -1 && this.size < size)
        throw IllegalArgumentException("Binding size is too small, required: $size, passed: ${this.size}")
}

fun ReadWriteParticleBinding.require(size: Int) {
    if(this.size != -1 && this.size < size)
        throw IllegalArgumentException("Binding size is too small, required: $size, passed: ${this.size}")
}
