package com.teamwizardry.librarianlib.features.particlesystem.paths

import com.teamwizardry.librarianlib.features.particlesystem.ParticlePath
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.require
import kotlin.math.cos
import kotlin.math.sin

class EllipsePath(
        /**
         * Though not necessarily the longest axis, the 0 and 1 points lie at the end of this vector
         */
        private val majorAxis: ReadParticleBinding,
        private val minorAxis: ReadParticleBinding,
        private val majorRadius: ReadParticleBinding,
        private val minorRadius: ReadParticleBinding
): ParticlePath {
    init {
        majorAxis.require(3)
        minorAxis.require(3)
        majorRadius.require(1)
        minorRadius.require(1)
    }

    override fun getPosition(particle: DoubleArray, t: Double, component: Int): Double {
        val c = cos(t*2*Math.PI)
        val s = sin(t*2*Math.PI)

        return 0.0 +
                majorAxis[particle, component] * c * majorRadius[particle, 0] +
                minorAxis[particle, component] * s * minorRadius[particle, 0]
    }

    override fun getTangent(particle: DoubleArray, t: Double, component: Int): Double {
        val c = cos(t*2*Math.PI)
        val s = sin(t*2*Math.PI)

        return 0.0 +
                majorAxis[particle, component] * s +
                minorAxis[particle, component] * c
    }
}