package com.teamwizardry.librarianlib.client.fx.particle.functions

import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction

/**
 * Created by TheCodeWarrior
 */
class InterpFadeInOut(val fadeInTime: Float, val fadeOutTime: Float) : InterpFunction<Float> {
    override fun get(i: Float): Float {
        var alpha = if (i < 1 && i > 0) 1f else 0f

        if (i <= fadeInTime && fadeInTime != 0f) {
            alpha = (i / fadeInTime)
        }
        if (i >= 1 - fadeOutTime && fadeOutTime != 0f) {
            alpha = 1 - (i - (1 - fadeOutTime)) / fadeOutTime
        }
        return if (alpha < 0) 0f else alpha
    }
}
