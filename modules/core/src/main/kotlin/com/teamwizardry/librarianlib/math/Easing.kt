package com.teamwizardry.librarianlib.math

import net.minecraft.util.math.MathHelper

/**
 * An easing function. Used to make more natural transitions.
 */
interface Easing {
    /**
     * @param progress A progress value from 0-1
     * @return A float value for interpolation. This value **is not** guaranteed to be between 0 and 1. If your
     *           interpolation algorithm requires a 0-1 value you **must** clamp this to that range.
     */
    fun ease(progress: Float): Float

    @JvmDefault
    val reversed: Easing
        get() = object : Easing {
            override fun ease(progress: Float): Float {
                return this@Easing.ease(1 - progress)
            }

            override val reversed: Easing
                get() = this@Easing
        }

    private abstract class PenningEasing : Easing {
        // variables used in the penning formulas
        val b = 0f
        val c = 1f
        val d = 1f
    }

    @Suppress("unused")
    companion object {
        @JvmField
        val linear = object : Easing {
            override fun ease(progress: Float): Float {
                return progress
            }
        }

        /** http://easings.net/#easeInSine */
        @JvmField
        val easeInSine: Easing = BezierEasing(0.47, 0, 0.745, 0.715)
        /** http://easings.net/#easeOutSine */
        @JvmField
        val easeOutSine: Easing = BezierEasing(0.39, 0.575, 0.565, 1)
        /** http://easings.net/#easeInOutSine */
        @JvmField
        val easeInOutSine: Easing = BezierEasing(0.445, 0.05, 0.55, 0.95)
        /** http://easings.net/#easeInQuad */
        @JvmField
        val easeInQuad: Easing = BezierEasing(0.55, 0.085, 0.68, 0.53)
        /** http://easings.net/#easeOutQuad */
        @JvmField
        val easeOutQuad: Easing = BezierEasing(0.25, 0.46, 0.45, 0.94)
        /** http://easings.net/#easeInOutQuad */
        @JvmField
        val easeInOutQuad: Easing = BezierEasing(0.455, 0.03, 0.515, 0.955)
        /** http://easings.net/#easeInCubic */
        @JvmField
        val easeInCubic: Easing = BezierEasing(0.55, 0.055, 0.675, 0.19)
        /** http://easings.net/#easeOutCubic */
        @JvmField
        val easeOutCubic: Easing = BezierEasing(0.215, 0.61, 0.355, 1)
        /** http://easings.net/#easeInOutCubic */
        @JvmField
        val easeInOutCubic: Easing = BezierEasing(0.645, 0.045, 0.355, 1)
        /** http://easings.net/#easeInQuart */
        @JvmField
        val easeInQuart: Easing = BezierEasing(0.895, 0.03, 0.685, 0.22)
        /** http://easings.net/#easeOutQuart */
        @JvmField
        val easeOutQuart: Easing = BezierEasing(0.165, 0.84, 0.44, 1)
        /** http://easings.net/#easeInOutQuart */
        @JvmField
        val easeInOutQuart: Easing = BezierEasing(0.77, 0, 0.175, 1)
        /** http://easings.net/#easeInQuint */
        @JvmField
        val easeInQuint: Easing = BezierEasing(0.755, 0.05, 0.855, 0.06)
        /** http://easings.net/#easeOutQuint */
        @JvmField
        val easeOutQuint: Easing = BezierEasing(0.23, 1, 0.32, 1)
        /** http://easings.net/#easeInOutQuint */
        @JvmField
        val easeInOutQuint: Easing = BezierEasing(0.86, 0, 0.07, 1)
        /** http://easings.net/#easeInExpo */
        @JvmField
        val easeInExpo: Easing = BezierEasing(0.95, 0.05, 0.795, 0.035)
        /** http://easings.net/#easeOutExpo */
        @JvmField
        val easeOutExpo: Easing = BezierEasing(0.19, 1, 0.22, 1)
        /** http://easings.net/#easeInOutExpo */
        @JvmField
        val easeInOutExpo: Easing = BezierEasing(1, 0, 0, 1)
        /** http://easings.net/#easeInCirc */
        @JvmField
        val easeInCirc: Easing = BezierEasing(0.6, 0.04, 0.98, 0.335)
        /** http://easings.net/#easeOutCirc */
        @JvmField
        val easeOutCirc: Easing = BezierEasing(0.075, 0.82, 0.165, 1)
        /** http://easings.net/#easeInOutCirc */
        @JvmField
        val easeInOutCirc: Easing = BezierEasing(0.785, 0.135, 0.15, 0.86)
        /** http://easings.net/#easeInBack */
        @JvmField
        val easeInBack: Easing = BezierEasing(0.6, -0.28, 0.735, 0.045)
        /** http://easings.net/#easeOutBack */
        @JvmField
        val easeOutBack: Easing = BezierEasing(0.175, 0.885, 0.32, 1.275)
        /** http://easings.net/#easeInOutBack */
        @JvmField
        val easeInOutBack: Easing = BezierEasing(0.68, -0.55, 0.265, 1.55)
        /** http://easings.net/#easeInElastic */
        @JvmField
        val easeInElastic: Easing = object : PenningEasing() {
            override fun ease(progress: Float): Float {
                var t = progress / d
                if (t == 0f) return b
                if (t == 1f) return b + c
                val p = d * .3f
                val a = c
                val s = p / 4
                t -= 1
                return -(a * Math.pow(2.0, (10 * t).toDouble()).toFloat() * MathHelper.sin((t * d - s) * (2 * Math.PI.toFloat()) / p)) + b
            }
        }
        /** http://easings.net/#easeOutElastic */
        @JvmField
        val easeOutElastic: Easing = object : PenningEasing() {
            override fun ease(progress: Float): Float {
                val t = progress / d
                if (t == 0f) return b
                if (t == 1f) return b + c
                val p = d * .3f
                val a = c
                val s = p / 4
                return a * Math.pow(2.0, (-10 * t).toDouble()).toFloat() * MathHelper.sin((t * d - s) * (2 * Math.PI.toFloat()) / p) + c + b
            }
        }
        /** http://easings.net/#easeInOutElastic */
        @JvmField
        val easeInOutElastic: Easing = object : PenningEasing() {
            override fun ease(progress: Float): Float {
                var t = progress / (d / 2)
                if (t == 0f) return b
                if (t == 2f) return b + c
                val p = d * (.3f * 1.5f)
                val a = c
                val s = p / 4
                t -= 1
                if (t < 1) return -.5f * (a * Math.pow(2.0, (10 * t).toDouble()).toFloat() * MathHelper.sin((t * d - s) * (2 * Math.PI.toFloat()) / p)) + b
                t -= 1
                return a * Math.pow(2.0, (-10 * t).toDouble()).toFloat() * MathHelper.sin((t * d - s) * (2 * Math.PI.toFloat()) / p) * .5f + c + b
            }
        }
        /** http://easings.net/#easeInBounce */
        @JvmField
        val easeInBounce: Easing = object : PenningEasing() {
            @Suppress("RemoveRedundantQualifierName") // Without the `Easing.` the compiler wigs out
            override fun ease(progress: Float): Float {
                val t = progress / d
                return c - Easing.easeOutBounce.ease(d - t) + b
            }
        }
        /** http://easings.net/#easeOutBounce */
        @JvmField
        val easeOutBounce: Easing = object : PenningEasing() {
            override fun ease(progress: Float): Float {
                var t = progress / d
                if (t < (1 / 2.75f)) {
                    return c * (7.5625f * t * t) + b
                } else if (t < (2 / 2.75f)) {
                    t -= (1.5f / 2.75f)
                    return c * (7.5625f * t * t + .75f) + b
                } else if (t < (2.5 / 2.75)) {
                    t -= (2.25f / 2.75f)
                    return c * (7.5625f * t * t + .9375f) + b
                } else {
                    t -= (2.625f / 2.75f)
                    return c * (7.5625f * t * t + .984375f) + b
                }
            }
        }
        /** http://easings.net/#easeInOutBounce */
        @JvmField
        val easeInOutBounce: Easing = object : PenningEasing() {
            @Suppress("RemoveRedundantQualifierName") // Without the `Easing.` the compiler wigs out
            override fun ease(progress: Float): Float {
                val t = progress
                return if (t < d / 2) Easing.easeInBounce.ease(t * 2) * .5f + b
                else Easing.easeOutBounce.ease(t * 2 - d) * .5f + c * .5f + b
            }
        }

        /**
         * Creates an easing for a basic linear fade in, hold, fade out progression. Proportionally the easing will
         * linearly fade from 0 to 1 over [fadeIn] units of time, hold 1 for [hold] units of time, then linearly
         * fade out over [fadeOut] units of time.
         */
        @JvmStatic
        fun easeInOutLinear(fadeIn: Float, hold: Float, fadeOut: Float): Easing
            = easeInOut(fadeIn, hold, fadeOut, linear, linear)

        /**
         * Creates an easing for a fade in, hold, fade out progression. Proportionally the easing will go through
         * [fadeInEasing] over [fadeIn] units of time, hold 1 for [hold] units of time, then go through [fadeOutEasing]
         * over [fadeOut] units of time (`fadeOutEasing` is inverted so it goes from 1 to 0).
         */
        @JvmStatic
        fun easeInOut(fadeIn: Float, hold: Float, fadeOut: Float, fadeInEasing: Easing, fadeOutEasing: Easing): Easing {
            val totalUnits = fadeIn + hold + fadeOut
            val fadeInFraction = fadeIn / totalUnits
            val holdFraction = hold / totalUnits
            val fadeOutStart = fadeInFraction + holdFraction
            val fadeOutFraction = fadeOut / totalUnits

            return object: Easing {
                override fun ease(progress: Float): Float {
                    return when (progress) {
                        in 0f .. fadeInFraction -> fadeInEasing.ease(progress / fadeInFraction)
                        in fadeInFraction .. fadeOutStart -> 1f
                        in fadeOutStart .. 1f -> 1f-fadeOutEasing.ease((progress-fadeOutStart) / fadeOutFraction)
                        else -> 0f
                    }
                }
            }
        }

        @JvmStatic
        fun compound(initialValue: Float): CompoundEasingBuilder {
            return CompoundEasingBuilder(initialValue)
        }
    }

}
