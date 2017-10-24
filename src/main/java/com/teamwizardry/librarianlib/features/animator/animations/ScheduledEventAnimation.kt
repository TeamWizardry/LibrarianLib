package com.teamwizardry.librarianlib.features.animator.animations

import com.teamwizardry.librarianlib.features.animator.AnimatableProperty
import com.teamwizardry.librarianlib.features.animator.Animation

/**
 * TODO: Document file ScheduledEventAnimation
 *
 * Created by TheCodeWarrior
 */
class ScheduledEventAnimation(time: Float, private val callback: () -> Unit) :
        Animation<ScheduledEventAnimation.PointlessAnimatableObject>(PointlessAnimatableObject,
                AnimatableProperty.get(PointlessAnimatableObject::class.java, "field")) {
    constructor(time: Float, callback_runnable: Runnable) : this(time, { callback_runnable.run() })
    init {
        start = time
    }

    override fun update(time: Float) {
        if(this.timeFraction(time) == 1f)
            callback()
    }

    object PointlessAnimatableObject {
        var field = 0
    }
}
