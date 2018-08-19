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
        @JvmField val majorAxis: ReadParticleBinding,
        @JvmField val minorAxis: ReadParticleBinding,
        @JvmField val majorRadius: ReadParticleBinding,
        @JvmField val minorRadius: ReadParticleBinding
): ParticlePath {
    init {
        majorAxis.require(3)
        minorAxis.require(3)
        majorRadius.require(1)
        minorRadius.require(1)
    }

    override fun getPosition(particle: DoubleArray, t: Double, index: Int): Double {
        val c = cos(t*2*Math.PI)
        val s = sin(t*2*Math.PI)

        return 0.0 +
                majorAxis[particle, index] * c * majorRadius[particle, 0] +
                minorAxis[particle, index] * s * minorRadius[particle, 0]
    }

    override fun getTangent(particle: DoubleArray, t: Double, index: Int): Double {
        val c = cos(t*2*Math.PI)
        val s = sin(t*2*Math.PI)

        return 0.0 +
                majorAxis[particle, index] * s +
                minorAxis[particle, index] * c
    }
}