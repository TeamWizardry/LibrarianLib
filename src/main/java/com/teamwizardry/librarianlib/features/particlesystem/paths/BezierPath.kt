package com.teamwizardry.librarianlib.features.particlesystem.paths

import com.teamwizardry.librarianlib.features.particlesystem.ParticlePath
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding

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

    override val value: DoubleArray = DoubleArray(3)

    init {
        start.require(3)
        end.require(3)
        startControl?.require(3)
        endControl?.require(3)
    }

    override fun computePosition(particle: DoubleArray, t: Double) {
        start.load(particle)
        end.load(particle)
        startControl?.load(particle)
        endControl?.load(particle)

        for(i in 0 until 3) {
            val startControlValue =
                if (startControl == null) {
                    if (i == 1) 0.0
                    else ((end.value[i] - start.value[i]) / 2.0)
                } else {
                    startControl.value[i]
                }
            val endControlValue =
                if (endControl == null) {
                    if (i == 1) 0.0
                    else ((start.value[i] - end.value[i]) / 2.0)
                } else {
                    endControl.value[i]
                }
            value[i] = getBezierComponent(t, start.value[i], end.value[i],
                start.value[i] + startControlValue,
                start.value[i] + endControlValue
            )
        }
    }

    override fun computeTangent(particle: DoubleArray, t: Double) {
        start.load(particle)
        end.load(particle)
        startControl?.load(particle)
        endControl?.load(particle)

        for(i in 0 until 3) {
            val startControlValue =
                if (startControl == null) {
                    if (i == 1) 0.0
                    else ((end.value[i] - start.value[i]) / 2.0)
                } else {
                    startControl.value[i]
                }
            val endControlValue =
                if (endControl == null) {
                    if (i == 1) 0.0
                    else ((start.value[i] - end.value[i]) / 2.0)
                } else {
                    endControl.value[i]
                }
            value[i] = getBezierComponent(
                t,
                start.value[i],
                end.value[i],
                start.value[i] + startControlValue,
                start.value[i] + endControlValue
            )
        }
    }

    private fun getBezierComponent(t: Double, s: Double, e: Double, sc: Double, ec: Double): Double {
        val T = 1 - t
        return T * T * T * s + 3 * T * T * t * sc + 3 * T * t * t * ec + t * t * t * e
    }
}