package com.teamwizardry.librarianlib.math

import com.teamwizardry.librarianlib.core.util.lerp.Lerper

abstract class BasicAnimation<T>(
    /**
     * The duration of a single loop of the animation. The actual animation may last longer, depending on the value of
     * [repeatCount].
     */
    val duration: Float,
    /**
     * The number of times this animation should loop. [Int.MAX_VALUE] is interpreted as infinity, and non-positive
     * values will be interpreted as 1.
     */
    var repeatCount: Int = 1,
    /**
     * If true, the animation will "bounce", reversing direction on every loop
     */
    var reverseOnRepeat: Boolean = false,
    /**
     * A callback to run when the animation completes or is interrupted
     */
    var completionCallback: (() -> Unit)? = null
): Animation<T> {

    /**
     * Get the value of the animation at the passed fractional time, which has been adjusted based on the duration and
     * repeat settings to be in the range `[0, 1]`.
     */
    protected abstract fun getValueAt(time: Float): T

    override val end: Float
        get() = if(repeatCount == Int.MAX_VALUE)
            Float.POSITIVE_INFINITY
        else
            duration * repeatCount.clamp(1, Int.MAX_VALUE)

    override fun animate(time: Float): T {
        if(duration == 0f)
            return if(time == 0f) getValueAt(0f) else getValueAt(1f)

        var loopIndex = (time / duration).toInt()
        var loopTime = time % duration

        // If we don't do this check the animation will snap back to its initial value exactly at the end of its loop.
        if(time != 0f && loopTime == 0f) {
            loopTime = duration
            loopIndex--
        }

        if(reverseOnRepeat && loopIndex % 2 == 1) {
            loopTime = duration - loopTime
        }

        return getValueAt(loopTime / duration)
    }

    override fun onStarted() {
        // nop
    }

    override fun onStopped(time: Float): T {
        val finalValue = getValueAt(1f)
        completionCallback?.invoke()
        return finalValue
    }

    //region Builders
    fun repeat(repeatCount: Int): BasicAnimation<T> = build {
        this.repeatCount = repeatCount
    }

    fun repeatForever(): BasicAnimation<T> = build {
        this.repeatCount = Int.MAX_VALUE
    }

    fun reverseOnRepeat(): BasicAnimation<T> = build {
        this.reverseOnRepeat = true
    }

    fun reverseOnRepeat(reverseOnRepeat: Boolean): BasicAnimation<T> = build {
        this.reverseOnRepeat = reverseOnRepeat
    }

    fun onComplete(completionCallback: (() -> Unit)?): BasicAnimation<T> = build {
        this.completionCallback = completionCallback
    }

    private fun build(cb: () -> Unit): BasicAnimation<T> {
        cb()
        return this
    }
    //endregion
}