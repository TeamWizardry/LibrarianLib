package com.teamwizardry.librarianlib.features.particlesystem

interface ParticlePath {
    fun getPosition(particle: DoubleArray, t: Double, component: Int): Double
    fun getTangent(particle: DoubleArray, t: Double, component: Int): Double
}