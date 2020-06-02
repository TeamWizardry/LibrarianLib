package com.teamwizardry.librarianlib.facade.value

import com.teamwizardry.librarianlib.core.util.lerp.Lerper
import com.teamwizardry.librarianlib.facade.layer.AnimationTimeListener
import com.teamwizardry.librarianlib.math.Animation
import com.teamwizardry.librarianlib.math.BasicAnimation
import com.teamwizardry.librarianlib.math.Easing
import java.util.PriorityQueue

/**
 * The common functionality for both primitive and generic GuiValues
 */
abstract class GuiValue<T>: AnimationTimeListener {
    private var _animationValue: T? = null
    private var currentTime: Float = 0f
    private val animations = PriorityQueue<ScheduledAnimation<T>>()
    private var animation: ScheduledAnimation<T>? = null

    //region API
    @JvmOverloads
    fun animate(from: T, to: T, duration: Float, easing: Easing = Easing.linear, delay: Float = 0f): BasicAnimation<T> {
        if(!hasLerper)
            throw IllegalStateException("Can not animate a GuiValue that has no lerper")
        val animation = SimpleAnimation(from, to, duration, easing, false)
        scheduleAnimation(delay, animation)
        return animation
    }

    @JvmOverloads
    fun animate(to: T, duration: Float, easing: Easing = Easing.linear, delay: Float = 0f): BasicAnimation<T> {
        if(!hasLerper)
            throw IllegalStateException("Can not animate a GuiValue that has no lerper")
        val animation = SimpleAnimation(currentValue, to, duration, easing, true)
        scheduleAnimation(delay, animation)
        return animation
    }

    /**
     * Schedule an animation to run in [delay] ticks
     */
    fun scheduleAnimation(delay: Float, animation: Animation<T>) {
        animations.add(ScheduledAnimation(currentTime + delay, animation))
    }
    //endregion

    //region Subclass API
    /**
     * Whether this GuiValue has a lerper, and thus can be animated
     */
    protected abstract val hasLerper: Boolean

    /**
     * The current value, used when animating from the current value, and when animations are detecting changes
     */
    protected abstract val currentValue: T

    /**
     * Lerp the value
     */
    protected abstract fun lerp(from: T, to: T, fraction: Float): T

    /**
     * Called when the animation value changes
     */
    protected abstract fun animationChange(from: T, to: T)

    /**
     * Called to persist the final value of an animation when it completes. This is called _before_ the animation is
     * removed.
     */
    protected abstract fun persistAnimation(value: T)

    /**
     * When this is true, [_animationValue]
     */
    protected val useAnimationValue: Boolean
        get() = animation != null

    /**
     * The current animation's value
     */
    protected val animationValue: T
        @Suppress("UNCHECKED_CAST")
        get() = _animationValue as T
    //endregion

    override fun updateTime(time: Float) {
        currentTime = time
        if(animation == null && animations.isEmpty())
            return

        val oldValue = currentValue
        var didAnimate = false
        var newValue = oldValue
        var animation = animation

        fun finishAnimations() {
            animation?.also {
                newValue = it.finish(time)
                didAnimate = true
                animation = null
            }
        }
        fun startAnimation(newAnimation: ScheduledAnimation<T>) {
            animation = newAnimation
            newAnimation.start()

        }

        while(animations.isNotEmpty() && animations.peek().start <= time) {
            finishAnimations()
            startAnimation(animations.poll())
        }

        // if we're past the end of the animation, wrap it up
        if(animation != null && time >= animation!!.end) {
            finishAnimations()
        }

        // if we survived this far, this is the current animation, and `time` is within its range
        animation?.also {
            newValue = it.update(time)
            didAnimate = true
        }

        if(didAnimate) {
            _animationValue = newValue
            if (oldValue != newValue) {
                animationChange(oldValue, newValue)
            }
            if (animation == null) {
                persistAnimation(newValue)
                _animationValue = null
            }
            this.animation = animation
        }
    }

    val innerLerper = object: Lerper<T>() {
        override fun lerp(from: T, to: T, fraction: Float): T {
            return this@GuiValue.lerp(from, to, fraction)
        }
    }

    protected class ScheduledAnimation<T>(val start: Float, val animation: Animation<T>): Comparable<ScheduledAnimation<T>> {
        val end: Float get() = start + animation.end

        fun update(time: Float): T {
            return animation.animate(time - start)
        }

        fun start() {
            animation.onStarted()
        }

        fun finish(time: Float): T {
            return animation.onStopped(time - start)
        }

        override fun compareTo(other: ScheduledAnimation<T>): Int {
            return start.compareTo(other.start)
        }
    }

    protected inner class SimpleAnimation(
        var from: T,
        val to: T,
        duration: Float,
        val easing: Easing,
        /**
         * If true, this animation will load the current value into [from] when it starts. This is used for animations
         * with implicit start values.
         */
        val fromCurrentValue: Boolean
    ): BasicAnimation<T>(duration) {
        override fun onStarted() {
            super.onStarted()
            if(fromCurrentValue)
                from = currentValue
        }

        override fun getValueAt(time: Float): T {
            return lerp(from, to, easing.ease(time))
        }
    }
}