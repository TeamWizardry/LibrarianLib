package com.teamwizardry.librarianlib.features.animator

import com.teamwizardry.librarianlib.features.kotlin.clamp

/**
 * An animation applied to a specific object and property of that object
 */
abstract class Animation<T: Any>(val target: T, val property: AnimatableProperty<T>) {
    /**
     * The start time of the animation in ticks. If this is 0 the start time will be set to the current time of the
     * animator (at the time it is added to the animator)
     */
    var start: Float = 0f

    /**
     * The duration in ticks of the animation. This is the duration before reverses or loops
     */
    var duration: Float = 0f

    /**
     * The end time of the animation in ticks, taking into account whether the animation loops, or reverses.
     * Returns [Float.POSITIVE_INFINITY] if the animation will loop forever
     */
    val end: Float
        get() {
            if(repeatCount < 0) return Float.POSITIVE_INFINITY

            var duration = duration
            if(shouldReverse) duration *= 2
            if(repeatCount > 0) duration *= repeatCount

            return start + duration
        }

    /**
     * If this value is set to true the animation will reverse before finishing.
     *
     * This is applied before [repeatCount]. This means that a 5s animation that reverses and repeats three times
     * will take `5 seconds * 2 * 3 = 30 seconds`.
     */
    var shouldReverse = false

    /**
     * The number of times the animation should loop.
     * If this value is less than zero the animation will loop indefinitely
     * If this value is set to 0 or 1 the animation will not loop.
     */
    var repeatCount = 0

    /**
     * @param time The current time of the containing Animator
     * @returns The current progress of the animation from 0-1 inclusive, taking into account [shouldReverse]
     *          and clamping the values outside of [start] and [end]
     */
    protected fun timeFraction(time: Float): Float {
        if(time < start) return 0f
        if(time > end) return if(shouldReverse) 0f else 1f
        if(shouldReverse) {
            val f = ((time-start)/duration).rem(2)
            if(f > 1)
                return (2-f).clamp(0f,1f) // make it turn down after it reaches 1
            return f.clamp(0f,1f)
        } else {
            var f = (time-start)/duration
            if(repeatCount != 0 && repeatCount != 1) f = f.rem(1)
            return f.clamp(0f,1f)
        }
    }

    /**
     * Updates the value of this animation's property based on the passed time
     * @param time The current time of the containing Animator
     */
    abstract fun update(time: Float)

    internal fun onAddedToAnimator(animator: Animator) {
        if(start == 0f) {
            start = animator.time
        }
    }

    companion object {
        /**
         * Because animations only get their start reset to the Animator's current time if their start is exactly equal
         * to zero, using a small value such as this will prevent that reset while still making it effectively zero
         */
        const val kTimeStartAtZero = 1e-15
    }
}

