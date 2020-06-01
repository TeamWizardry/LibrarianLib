package com.teamwizardry.librarianlib.facade.value

import com.teamwizardry.librarianlib.core.util.lerp.Lerper
import com.teamwizardry.librarianlib.math.Easing

interface ValueAnimation<T> {
    /**
     * The animation duration in ticks
     */
    val duration: Float

    fun animate(fraction: Float): T
}

class SimpleValueAnimation<T>(
    private val lerper: Lerper<T>,
    private val from: T,
    private val to: T,
    override val duration: Float,
    private val easing: Easing
): ValueAnimation<T> {
    override fun animate(fraction: Float): T {
        return lerper.lerp(from, to, easing.ease(fraction))
    }
}

class KeyframeValueAnimationBuilder<T>(val initialValue: T, val lerper: Lerper<T>) {
    private val keyframes = mutableListOf<BuildingKeyframe<T>>()

    /**
     * Add a keyframe. The given duration and easing are for the span between this keyframe and the previous keyframe.
     */
    fun add(duration: Float, easing: Easing, value: T): KeyframeValueAnimationBuilder<T> {
        keyframes.add(BuildingKeyframe(duration, value, easing))
        return this
    }

    /**
     * Instantly jump to the passed value.
     */
    fun jump(value: T): KeyframeValueAnimationBuilder<T> {
        keyframes.add(BuildingKeyframe(0f, value, Easing.linear))
        return this
    }

    /**
     * Hold the current value for the passed duration.
     */
    fun hold(duration: Float): KeyframeValueAnimationBuilder<T> {
        val value = keyframes.lastOrNull()?.value ?: initialValue
        keyframes.add(BuildingKeyframe(duration, value, Easing.linear))
        return this
    }

    fun build(): ValueAnimation<T> {
        val built = mutableListOf<Keyframe<T>>()
        val startKeyframe = Keyframe(null, 0f, initialValue, Easing.linear, lerper)
        built.add(startKeyframe)

        val totalDuration = keyframes.fold(0f) { acc, it -> acc + it.duration }

        if(totalDuration == 0f) {
            built.add(Keyframe(null, 1f, initialValue, Easing.linear, lerper))
        } else {
            var previous = startKeyframe
            keyframes.mapTo(built) { keyframe ->
                Keyframe(
                    previous, previous.time + keyframe.duration / totalDuration,
                    keyframe.value, keyframe.easingBefore, lerper
                ).also {
                    previous = it
                }
            }
        }
        return KeyframeValueAnimation(built, totalDuration)
    }

    private class BuildingKeyframe<T>(val duration: Float, val value: T, val easingBefore: Easing)

    class Keyframe<T>(val previous: Keyframe<T>?, val time: Float, val value: T, val easingBefore: Easing, val lerper: Lerper<T>) {
        val duration: Float = time - (previous?.time ?: 0f)

        operator fun contains(t: Float): Boolean {
            return (previous == null || t > previous.time) && t <= time
        }

        fun animate(progress: Float): T {
            if (previous == null)
                return value
            val adjustedProgress = (progress - previous.time) / duration
            return lerper.lerp(previous.value, value, easingBefore.ease(adjustedProgress))
        }
    }

    private class KeyframeValueAnimation<T>(val keyframes: List<Keyframe<T>>, override val duration: Float): ValueAnimation<T> {
        /**
         * Animations are often invoked incrementally, so we cache the last index. If the time falls within this
         * keyframe's range it's used, otherwise a normal search is performed.
         */
        private var index: Int = 0

        override fun animate(fraction: Float): T {
            if(fraction <= 0)
                return keyframes.first().value
            if(fraction >= 1)
                return keyframes.last().value

            if(fraction !in keyframes[index]) {
                index = keyframes.indexOfFirst { it.duration != 0f && fraction in it }
            }

            return keyframes[index].animate(fraction)
        }
    }
}