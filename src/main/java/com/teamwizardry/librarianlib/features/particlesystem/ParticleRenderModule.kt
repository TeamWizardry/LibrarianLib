package com.teamwizardry.librarianlib.features.particlesystem

import java.util.*

interface ParticleRenderModule {
    fun render(particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>)
}