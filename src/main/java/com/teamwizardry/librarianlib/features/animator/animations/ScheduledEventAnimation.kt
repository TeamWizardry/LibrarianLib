package com.teamwizardry.librarianlib.features.animator.animations

import com.teamwizardry.librarianlib.features.animator.AnimatableProperty
import com.teamwizardry.librarianlib.features.animator.Animation

/**
 * Created by TheCodeWarrior
 */
class ScheduledEventAnimation(time: Float, callback: Runnable) : Animation<Any>(Any()) {
    constructor(time: Float, callback: () -> Unit) : this(time, Runnable(callback))

    init {
        start = time
        duration = 0f
        completion = callback
    }

    override fun update(time: Float) {}
}
