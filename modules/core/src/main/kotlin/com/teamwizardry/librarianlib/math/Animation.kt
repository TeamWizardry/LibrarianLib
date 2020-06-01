package com.teamwizardry.librarianlib.math

/**
 * The base interface for animations.
 */
interface Animation<T> {
    /**
     * The end time of the animation. May be [Float.POSITIVE_INFINITY] for animations that loop indefinitely or use some
     * other function that never ends.
     */
    val end: Float

    /**
     * Get the value of the animation at the specified time. The time should be expressed in ticks, with the animation
     * starting at 0.
     */
    fun animate(time: Float): T

    /**
     * Can be called when the animation is started.
     *
     * While calling this method is recommended, as it can facilitate richer animation functionality, it is not strictly
     * required, so animation implementations should not rely on this method being called.
     */
    fun onStarted()

    /**
     * Can be called when the animation is completed or interrupted, returning the final value of the animation. This is
     * used to ensure animations land _precisely_ at their final value, as opposed to a frame or two before the final
     * value. The passed time will likely be after [end], and may be considerably so.
     *
     * While calling this method is recommended, as it facilitates richer animation functionality, it is not strictly
     * required, so animation implementations should not rely on this method being called.
     */
    fun onStopped(time: Float): T
}