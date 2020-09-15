package com.teamwizardry.librarianlib.math

import com.teamwizardry.librarianlib.core.util.lerp.Lerper

public class SimpleAnimation<T>(
    private val lerper: Lerper<T>,
    public val from: T,
    public val to: T,
    duration: Float,
    public val easing: Easing
): BasicAnimation<T>(duration) {
    override fun getValueAt(time: Float): T {
        return lerper.lerp(from, to, easing.ease(time))
    }
}