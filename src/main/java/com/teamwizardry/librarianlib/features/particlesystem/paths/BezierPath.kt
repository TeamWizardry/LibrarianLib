package com.teamwizardry.librarianlib.features.particlesystem.paths

import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.withY
import com.teamwizardry.librarianlib.features.particlesystem.ParticlePath
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.ConstantBinding
import com.teamwizardry.librarianlib.features.particlesystem.require
import net.minecraft.util.math.MathHelper

/**
 * Create a Bézier curve from [start] to [end] with [startControl] and [endControl] as relative positions of handles for the curvature
 *
 * ![Bézier curve image example](http://imgur.com/bkIoyyR) |
 * P0 is [start], P1 relative to P0 is [startControl], P2 relative to P3 is [endControl], and P3 is [end]
 */
class BezierPath @JvmOverloads constructor(
        /**
         * The start value to interpolate from.
         */
        @JvmField val start: ReadParticleBinding,
        /**
         * The end value to interpolate to.
         */
        @JvmField val end: ReadParticleBinding,
        /**
         * Controls the bending of the start value.
         */
        @JvmField val startControl: ReadParticleBinding? = null,
        /**
         * Controls the bending of the end value.
         */
        @JvmField val endControl: ReadParticleBinding? = null
) : ParticlePath {

    override fun getSize(): Int = 3

    init {
        start.require(3)
        end.require(3)
        startControl?.require(3)
        endControl?.require(3)
    }

    override fun getPosition(particle: DoubleArray, t: Double, index: Int): Double {
        val iStart = start[particle, index]
        val iEnd = end[particle, index]
        return getBezierComponent(
                t,
                iStart,
                iEnd,
                iStart + (if (startControl == null) {
                    if (index == 1) 0.0
                    else ((iEnd - iStart) / 2.0)
                } else startControl[particle, index]),
                iStart + (if (endControl == null) {
                    if (index == 1) 0.0
                    else ((iStart - iEnd) / 2.0)
                } else endControl[particle, index]))
    }

    override fun getTangent(particle: DoubleArray, t: Double, index: Int): Double {
        val iStart = start[particle, index]
        val iEnd = end[particle, index]
        return getBezierComponent(
                t,
                iStart,
                iEnd,
                iStart + (if (startControl == null) {
                    if (index == 1) 0.0
                    else ((iEnd - iStart) / 2.0)
                } else startControl[particle, index]),
                iStart + (if (endControl == null) {
                    if (index == 1) 0.0
                    else ((iStart - iEnd) / 2.0)
                } else endControl[particle, index]))
    }

    private fun getBezierComponent(t: Double, s: Double, e: Double, sc: Double, ec: Double): Double {
        val T = 1 - t
        return T * T * T * s + 3 * T * T * t * sc + 3 * T * t * t * ec + t * t * t * e
    }
}