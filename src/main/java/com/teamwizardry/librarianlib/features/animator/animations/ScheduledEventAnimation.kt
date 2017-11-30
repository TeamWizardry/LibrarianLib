package com.teamwizardry.librarianlib.features.animator.animations

import com.teamwizardry.librarianlib.features.animator.AnimatableProperty
import com.teamwizardry.librarianlib.features.animator.Animation

/**
 * TODO: Document file ScheduledEventAnimation
 *
 * Created by TheCodeWarrior
 */
class ScheduledEventAnimation(time: Float, callback: Runnable) :
        Animation<ScheduledEventAnimation.PointlessAnimatableObject>(PointlessAnimatableObject,
                AnimatableProperty.get(PointlessAnimatableObject::class.java, "field"), callback) {
    constructor(time: Float, callback: () -> Unit) : this(time, Runnable(callback))
    init {
        start = time
    }

    override fun update(time: Float) {}

    object PointlessAnimatableObject {
        var field = 0
    }
}
