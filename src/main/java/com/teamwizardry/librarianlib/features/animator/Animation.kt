package com.teamwizardry.librarianlib.features.animator

import com.teamwizardry.librarianlib.features.kotlin.clamp

/**
 * An animation applied to a specific object and property of that object
 */
abstract class Animation<T : Any>(val target: T) {

    /**
     * Default: true
     *
     * If this flag is set the start time will be treated as relative to the [Animator] time it is being added to. If it
     * is false, the start time will be unaffected by the Animator's time.
     */
    var isTimeRelative = true

    /**
     * The start time of the animation in ticks.
     *
     * Once this animation has been added to an animator this field cannot be changed.
     */
    var start: Float = 0f
        set(value) {
            if (isInAnimator)
                throw IllegalStateException("Cannot change the start time of an animation once it has been added to an animator")
            field = value
        }

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
            if (repeatCount < 0) return Float.POSITIVE_INFINITY

            var duration = duration
            if (shouldReverse) duration *= 2
            if (repeatCount > 0) duration *= repeatCount

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
     * A callback that is called when this animation completes
     */
    var completion: Runnable = Runnable {}

    /**
     * @param time The current time of the containing Animator
     * @returns The current progress of the animation from 0-1 inclusive, taking into account [shouldReverse]
     *          and clamping the values outside of [start] and [end]
     */
    protected fun timeFraction(time: Float): Float {
        if (time < start) return 0f
        if (time > end) return if (shouldReverse) 0f else 1f
        if (shouldReverse) {
            val f = ((time - start) / duration).rem(2)
            if (f > 1)
                return (2 - f).clamp(0f, 1f) // make it turn down after it reaches 1
            return f.clamp(0f, 1f)
        } else {
            var f = (time - start) / duration
            if (repeatCount != 0 && repeatCount != 1) f = f.rem(1)
            return f.clamp(0f, 1f)
        }
    }

    /**
     * True if this animation has been added to an animator already.
     */
    val isInAnimator: Boolean
        get() = _id != -1

    /**
     * Updates the value of this animation's property based on the passed time
     * @param time The current time of the containing Animator
     */
    abstract fun update(time: Float)

    /**
     * runs the completion callback
     */
    open fun complete() {
        completion.run()
        finished = true
    }

    /**
     * Terminates this animation.
     * No further updates will be applied, and the [completion] callback will not be called.
     */
    var terminated = false

    /**
     * Whether this animation is over.
     * This value is set after the [completion] callback is called.
     */
    var finished = false
        internal set

    internal fun onAddedToAnimator(animator: Animator) {
        if (isTimeRelative) {
            start += animator.time
        }
        this._id = animator.nextID++
    }

    internal var _id: Int = -1
        private set
}

