package com.teamwizardry.librarianlib.math

class CompoundEasingBuilder(private val initialValue: Float) {
    private val keyframes = mutableListOf<BuildingKeyframe>()

    /**
     * Add a keyframe. The given duration and easing are for the span between this keyframe and the previous keyframe.
     */
    fun add(duration: Float, easing: Easing, value: Float): CompoundEasingBuilder {
        keyframes.add(BuildingKeyframe(duration, value, easing))
        return this
    }

    /**
     * Instantly jump to the passed value.
     */
    fun jump(value: Float): CompoundEasingBuilder {
        keyframes.add(BuildingKeyframe(0f, value, Easing.linear))
        return this
    }

    /**
     * Hold the current value for the passed duration.
     */
    fun hold(duration: Float): CompoundEasingBuilder {
        val value = keyframes.lastOrNull()?.value ?: 0f
        keyframes.add(BuildingKeyframe(duration, value, Easing.linear))
        return this
    }

    fun build(): Easing {
        val built = mutableListOf<Keyframe>()
        val startKeyframe = Keyframe(null, 0f, initialValue, Easing.linear)
        built.add(startKeyframe)

        val totalDuration = keyframes.fold(0f) { acc, it -> acc + it.duration }

        if(totalDuration == 0f) {
            built.add(Keyframe(null, 1f, initialValue, Easing.linear))
        } else {
            var previous = startKeyframe
            keyframes.mapTo(built) { keyframe ->
                Keyframe(
                    previous, previous.time + keyframe.duration / totalDuration,
                    keyframe.value, keyframe.easingBefore
                ).also {
                    previous = it
                }
            }
        }
        return CompoundEasing(built)
    }

    private class BuildingKeyframe(val duration: Float, val value: Float, val easingBefore: Easing)

    private class Keyframe(val previous: Keyframe?, val time: Float, val value: Float, val easingBefore: Easing): Easing {
        val duration: Float = time - (previous?.time ?: 0f)

        operator fun contains(t: Float): Boolean {
            return (previous == null || t > previous.time) && t <= time
        }

        override fun ease(progress: Float): Float {
            if(previous == null)
                return value
            val adjustedProgress = (progress - previous.time) / duration
            return previous.value + (value - previous.value) * easingBefore.ease(adjustedProgress)
        }
    }

    /**
     * [keyframes] MUST contain a "start" keyframe at t=0 and the last keyframe must be at t=1
     */
    private class CompoundEasing(private val keyframes: List<Keyframe>): Easing {
        /**
         * Easings are often invoked incrementally, so we cache the last index. If the time falls within this keyframe's
         * range it's used, otherwise a normal search is performed.
         */
        private var index: Int = 0

        override fun ease(progress: Float): Float {
            if(progress <= 0)
                return keyframes.first().value
            if(progress >= 1)
                return keyframes.last().value

            if(progress !in keyframes[index]) {
                index = keyframes.indexOfFirst { it.duration != 0f && progress in it }
            }

            return keyframes[index].ease(progress)
        }
    }
}