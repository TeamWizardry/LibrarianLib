package com.teamwizardry.librarianlib.math

import com.teamwizardry.librarianlib.core.LibrarianLibCoreModule
import com.teamwizardry.librarianlib.core.util.lerp.Lerper
import java.lang.IllegalStateException

public class KeyframeAnimation<T>(initialValue: T, private val lerper: Lerper<T>): BasicAnimation<T>(0f) {
    override var duration: Float = 0f
        private set
    private val keyframes = mutableListOf<Keyframe>()

    private var hasCallbacks: Boolean = false
    /**
     * Animations are often invoked incrementally, so we cache the last index. If the time falls within this
     * keyframe's range it's used, otherwise a normal search is performed.
     */
    private var index: Int = 0

    init {
        keyframes.add(Keyframe(null, 0f, initialValue, Easing.linear, null))
    }

    /**
     * Add a keyframe. The given duration and easing are for the span between this keyframe and the previous keyframe.
     */
    public fun add(duration: Float, easing: Easing, value: T): KeyframeAnimation<T> {
        this.duration += duration
        keyframes.add(Keyframe(keyframes.last(), this.duration, value, easing, null))
        return this
    }

    /**
     * Instantly jump to the passed value.
     */
    public fun jump(value: T): KeyframeAnimation<T> {
        add(0f, Easing.linear, value)
        return this
    }

    /**
     * Hold the current value for the passed duration.
     */
    public fun hold(duration: Float): KeyframeAnimation<T> {
        add(duration, Easing.linear, keyframes.last().value)
        return this
    }

    /**
     * Specifies a function to run when a keyframe is reached. NOTE: this makes the animation incompatible with
     * [reverseOnRepeat].
     */
    public fun onKeyframe(callback: Runnable?): KeyframeAnimation<T> {
        keyframes.last().onKeyframe = callback
        hasCallbacks = keyframes.any { it.onKeyframe != null }
        return this
    }

    override fun onStopped(time: Float): T {
        val value = super.onStopped(time)
        index = 0
        return value
    }

    override fun getValueAt(time: Float, loopCount: Int): T {

        // run callbacks in a loop
        if(loopCount != 0 && hasCallbacks) {
            for (i in 0 until loopCount) {
                for (j in index until keyframes.size) {
                    keyframes[j].onKeyframe?.run()
                }
                index = 0
            }
        }

        if(time <= 0)
            return keyframes.first().value
        if(time >= 1) {
            for(i in index until keyframes.size) {
                keyframes[i].onKeyframe?.run()
            }
            index = keyframes.lastIndex
            return keyframes.last().value
        }
        if(keyframes.size == 1)
            return keyframes[0].value

        val absTime = time * duration
        if(absTime !in keyframes[index]) {
            val newIndex = keyframes.indexOfFirst { it.duration != 0f && absTime in it }
            if(newIndex < 0) {
                throw IllegalStateException("No keyframe found for time $absTime (duration is $duration)")
            } else if(newIndex < index && hasCallbacks) {
                throw IllegalStateException("Keyframe animations with callbacks can't be run backward")
            } else {
                for(i in index until newIndex) {
                    keyframes[i].onKeyframe?.run()
                }
            }
            index = newIndex
        }

        return keyframes[index].animate(absTime)
    }

    private inner class Keyframe(val previous: Keyframe?, val time: Float, val value: T, val easingBefore: Easing, var onKeyframe: Runnable?) {
        val duration: Float = time - (previous?.time ?: 0f)

        operator fun contains(t: Float): Boolean {
            return (previous == null || t > previous.time) && t <= time
        }

        fun animate(t: Float): T {
            if (previous == null)
                return value
            val adjustedProgress = (t - previous.time) / duration
            return lerper.lerp(previous.value, value, easingBefore.ease(adjustedProgress))
        }
    }

    private companion object {
        val logger = LibrarianLibCoreModule.makeLogger<KeyframeAnimation<*>>()
    }
}
