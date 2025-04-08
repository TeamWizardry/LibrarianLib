package com.teamwizardry.librarianlib.math

public abstract class BasicAnimation<T>(
    /**
     * The duration of a single loop of the animation. The actual animation may last longer, depending on the value of
     * [repeatCount].
     */
    public open val duration: Float
): Animation<T> {
    /**
     * The number of times this animation should loop. [Int.MAX_VALUE] is interpreted as infinity, and non-positive
     * values will be interpreted as 1.
     */
    public var repeatCount: Int = 1
    /**
     * If true, the animation will "bounce", reversing direction on every loop
     */
    public var reverseOnRepeat: Boolean = false
    /**
     * A callback to run when the animation completes or is interrupted
     */
    private var completionCallback: Runnable? = null

    /**
     * Get the value of the animation at the passed fractional time, which has been adjusted based on the duration and
     * repeat settings to be in the range `[0, 1]`.
     *
     * @param time The time to sample in the range `[0, 1]`
     * @param loopCount The number of times the animation wrapped around between the previous sample and this sample
     */
    protected abstract fun getValueAt(time: Float, loopCount: Int): T

    override val end: Float
        get() = if(repeatCount == Int.MAX_VALUE)
            Float.POSITIVE_INFINITY
        else
            duration * repeatCount.clamp(1, Int.MAX_VALUE)

    /**
     * Get the value at the specified [time]. Time should increase monotonically for each instance. Some animations may
     * behave erratically if [time] ever decreases.
     */
    override fun animate(time: Float): T {
        if(duration == 0f)
            return if(time == 0f) getValueAt(0f, 0) else getValueAt(1f, 0)

        var loopCount = (time / duration).toInt()
        var loopedTime = time % duration

        // If we don't do this check the animation will snap back to its initial value exactly at the end of its loop.
        if(time != 0f && loopedTime == 0f) {
            loopedTime = duration
            loopCount--
        }

        if(reverseOnRepeat && loopCount % 2 == 1) {
            loopedTime = duration - loopedTime
        }

        return getValueAt(loopedTime / duration, loopCount)
    }

    override fun onStarted() {
        // nop
    }

    override fun onStopped(time: Float): T {
        val finalValue = getValueAt(1f, 0)
        completionCallback?.run()
        return finalValue
    }

    //region Builders
    public fun repeat(repeatCount: Int): BasicAnimation<T> = build {
        this.repeatCount = repeatCount
    }

    public fun repeat(): BasicAnimation<T> = build {
        this.repeatCount = Int.MAX_VALUE
    }

    public fun reverseOnRepeat(): BasicAnimation<T> = build {
        this.reverseOnRepeat = true
    }

    public fun reverseOnRepeat(reverseOnRepeat: Boolean): BasicAnimation<T> = build {
        this.reverseOnRepeat = reverseOnRepeat
    }

    public fun onComplete(completionCallback: Runnable?): BasicAnimation<T> = build {
        this.completionCallback = completionCallback
    }

    private fun build(cb: () -> Unit): BasicAnimation<T> {
        cb()
        return this
    }
    //endregion
}