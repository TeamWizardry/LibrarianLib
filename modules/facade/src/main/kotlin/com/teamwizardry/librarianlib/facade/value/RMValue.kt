package com.teamwizardry.librarianlib.facade.value

import com.teamwizardry.librarianlib.core.util.lerp.Lerper
import com.teamwizardry.librarianlib.facade.component.AnimationTimeListener
import com.teamwizardry.librarianlib.math.Animation
import com.teamwizardry.librarianlib.math.BasicAnimation
import com.teamwizardry.librarianlib.math.Easing
import java.lang.IllegalStateException
import java.util.PriorityQueue
import kotlin.reflect.KProperty

/**
 * A kotlin delegate that behaves almost identically to a plain `var`, except that it supports implicit animations. It
 * represents a Retained Mode Value, in contrast to [IMValue], which represents an Immediate Mode Value
 */
@Suppress("Duplicates")
class RMValue<T> @JvmOverloads constructor(
    private var value: T, private val lerper: Lerper<T>?, private val change: (oldValue: T, newValue: T) -> Unit = { _, _ -> }
): AnimationTimeListener {
    private var currentTime: Float = 0f
    private val animations = PriorityQueue<ScheduledAnimation<T>>()
    private var animation: ScheduledAnimation<T>? = null
    private var animationValue: T? = null

    /**
     * Gets the current value
     */
    fun get(): T {
        @Suppress("UNCHECKED_CAST")
        return if(animation != null) animationValue as T else value
    }

    /**
     * Sets a new value
     */
    fun set(value: T) {
        val oldValue = this.value
        this.value = value
        if(oldValue != value) {
            change(oldValue, value)
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValue()`)
     */
    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return this.get()
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValue()`)
     */
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.set(value)
    }

    @JvmOverloads
    fun animate(from: T, to: T, duration: Float, easing: Easing = Easing.linear, delay: Float = 0f): BasicAnimation<T> {
        if(lerper == null)
            throw IllegalStateException("Can not animate an RMValue that has no lerper")
        val animation = SimpleAnimation(lerper, from, to, duration, easing, false)
        scheduleAnimation(delay, animation)
        return animation
    }

    @JvmOverloads
    fun animate(to: T, duration: Float, easing: Easing = Easing.linear, delay: Float = 0f): BasicAnimation<T> {
        if(lerper == null)
            throw IllegalStateException("Can not animate an RMValue that has no lerper")
        val animation = SimpleAnimation(lerper, value, to, duration, easing, true)
        scheduleAnimation(delay, animation)
        return animation
    }

    /**
     * Schedule an animation to run in [delay] ticks
     */
    fun scheduleAnimation(delay: Float, animation: Animation<T>) {
        animations.add(ScheduledAnimation(currentTime + delay, animation))
    }

    override fun updateTime(time: Float) {
        currentTime = time
        if(animation == null && animations.isEmpty())
            return

        val oldValue = get()
        var didFinish = false
        var newValue = oldValue
        var animation = animation

        while(animations.isNotEmpty() && animations.peek().start <= time) {
            // wrap up any in-progress animation
            if(animation != null) {
                newValue = animation.finish(time)
                didFinish = true
                animation = null
            }
            // pop and start the new animation
            animation = animations.poll()
            animation.start()
        }
        // if we're past the end of the animation, wrap it up
        if(animation != null && time >= animation.end) {
            newValue = animation.finish(time)
            didFinish = true
            animation = null
        }
        // if we survived this far, this is the current animation, and `time` is within its range
        if(animation != null) {
            newValue = animation.update(time)
        }

        this.animation = animation
        if(animation != null) {
            animationValue = newValue
            if(oldValue != newValue) {
                change(oldValue, newValue)
            }
        } else if(didFinish) {
            set(newValue) // persist the last value
        }
    }

    private class ScheduledAnimation<T>(val start: Float, val animation: Animation<T>): Comparable<ScheduledAnimation<T>> {
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

    private inner class SimpleAnimation(
        val lerper: Lerper<T>,
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
                from = value
        }

        override fun getValueAt(time: Float): T {
            return lerper.lerp(from, to, easing.ease(time))
        }
    }
}

