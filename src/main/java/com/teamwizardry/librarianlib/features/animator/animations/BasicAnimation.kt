package com.teamwizardry.librarianlib.features.animator.animations

import com.teamwizardry.librarianlib.features.animator.AnimatableProperty
import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.animator.LerperHandler

/**
 * A basic animation from [from] to [to]. Both values default to the current value of this animation's property
 */
class BasicAnimation<T : Any>(target: T, val property: AnimatableProperty<T>) : Animation<T>(target) {
    constructor(target: T, property: String) : this(target, AnimatableProperty.get(target.javaClass, property))

    /**
     * The value of the property at [start]
     */
    var from: Any = property.get(target)

    /**
     * The value of the property at [end]
     */
    var to: Any = property.get(target)

    /**
     * The easing function to use to animate between [start] and [end]
     */
    var easing: Easing = Easing.linear

    private var lerper = LerperHandler.getLerperOrError(property.type)

    override fun update(time: Float) {
        val progress = easing(timeFraction(time))
        val new = lerper.lerp(from, to, progress)
        property.set(target, new)
    }
}
