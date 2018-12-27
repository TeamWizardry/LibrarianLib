package com.teamwizardry.librarianlib.features.particlesystem.paths

import com.teamwizardry.librarianlib.features.particlesystem.ParticlePath
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import net.minecraft.util.math.MathHelper

/**
 * A path that traces the edge of an ellipse.
 *
 * The ellipse is defined in terms of the two axes and radii, as this means total control and that no normal has to be
 * transformed into X/Y unit vectors every time a array is requested, instead those are provided and presumably stored.
 */
class EllipsePath(
        /**
         * The major axis normal. Though this need not be the axis with the longest radius (the strict definition of
         * the (semi)major axis), the distinction is made to easily differentiate the two axes.
         *
         * The output is this point (* radius) at t=0 and t=1, and is the opposite point at t=0.5
         */
        @JvmField val majorAxis: ReadParticleBinding,
        /**
         * The minor axis normal. Though this need not be the axis with the shortest radius (the strict definition of
         * the (semi)minor axis), the distinction is made to easily differentiate the two axes.
         *
         * The output is this point (* radius) at t=0.25, and is the opposite point at t=0.75
         */
        @JvmField val minorAxis: ReadParticleBinding,
        /**
         * The multiplier for the [majorAxis] normal. This is the major axis radius if the normal has a length of 1.
         */
        @JvmField val majorRadius: ReadParticleBinding,
        /**
         * The multiplier for the [minorAxis] normal. This is the minor axis radius if the normal has a length of 1.
         */
        @JvmField val minorRadius: ReadParticleBinding
): ParticlePath {

    override val value: DoubleArray = DoubleArray(3)

    init {
        majorAxis.require(3)
        minorAxis.require(3)
        majorRadius.require(1)
        minorRadius.require(1)
    }

    override fun computePosition(particle: DoubleArray, t: Double) {
        val c = MathHelper.cos((t * 2 * Math.PI).toFloat())
        val s = MathHelper.sin((t * 2 * Math.PI).toFloat())

        for(i in 0 until 3) {
            value[i] = 0.0 +
                    majorAxis.getValue(i) * c * majorRadius.getValue(0) +
                    minorAxis.getValue(i) * s * minorRadius.getValue(0)
        }
    }

    override fun computeTangent(particle: DoubleArray, t: Double) {
        val c = MathHelper.cos((t * 2 * Math.PI).toFloat())
        val s = MathHelper.sin((t * 2 * Math.PI).toFloat())

        for(i in 0 until 3) {
            value[i] = 0.0 +
                    majorAxis.getValue(i) * s * majorRadius.getValue(0) +
                    minorAxis.getValue(i) * c * minorRadius.getValue(0)
        }
    }
}