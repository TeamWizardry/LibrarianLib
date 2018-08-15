package com.teamwizardry.librarianlib.features.particlesystem

interface ParticleUpdateModule {
    fun update(particle: DoubleArray)
}

interface ParticleBatchUpdateModule {
    fun update(particles: MutableList<DoubleArray>)
}
