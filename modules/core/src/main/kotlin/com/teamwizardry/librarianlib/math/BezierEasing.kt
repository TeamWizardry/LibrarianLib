package com.teamwizardry.librarianlib.math

/**
 * Source: http://greweb.me/2012/02/bezier-curve-based-easing-functions-from-concept-to-implementation/
 *
 * To create your own, visit http://cubic-bezier.com/
 */
@Suppress("FunctionName")
public class BezierEasing(private val mX1: Float, private val mY1: Float, private val mX2: Float, private val mY2: Float): Easing {
    public constructor(mX1: Number, mY1: Number, mX2: Number, mY2: Number): this(mX1.toFloat(), mY1.toFloat(), mX2.toFloat(), mY2.toFloat())

    override fun ease(progress: Float): Float {
        if (mX1 == mY1 && mX2 == mY2) return progress // linear
        return CalcBezier(GetTForX(progress), mY1, mY2)
    }

    private fun A(aA1: Float, aA2: Float): Float {
        return 1f - 3f * aA2 + 3f * aA1
    }

    private fun B(aA1: Float, aA2: Float): Float {
        return 3f * aA2 - 6f * aA1
    }

    private fun C(aA1: Float): Float {
        return 3f * aA1
    }

    // Returns x(t) given t, x1, and x2, or y(t) given t, y1, and y2.
    private fun CalcBezier(aT: Float, aA1: Float, aA2: Float): Float {
        return ((A(aA1, aA2) * aT + B(aA1, aA2)) * aT + C(aA1)) * aT
    }

    // Returns dx/dt given t, x1, and x2, or dy/dt given t, y1, and y2.
    private fun GetSlope(aT: Float, aA1: Float, aA2: Float): Float {
        return 3f * A(aA1, aA2) * aT * aT + 2f * B(aA1, aA2) * aT + C(aA1)
    }

    private fun GetTForX(aX: Float): Float {
        // Newton raphson iteration
        var aGuessT = aX
        for (i in 0..3) {
            val currentSlope = GetSlope(aGuessT, mX1, mX2)
            if (currentSlope == 0f) return aGuessT
            val currentX = CalcBezier(aGuessT, mX1, mX2) - aX
            aGuessT -= currentX / currentSlope
        }
        return aGuessT
    }
}
