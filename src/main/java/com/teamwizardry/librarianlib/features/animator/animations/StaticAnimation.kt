package com.teamwizardry.librarianlib.features.animator.animations

import com.teamwizardry.librarianlib.features.animator.*

/**
 * A basic animation from [from] to [to]. Both values default to the current value of this animation's property
 */
class StaticAnimation<T : Any>(property: IAnimatable<Nothing?>) : Animation<Nothing?>(null, property) {
    constructor(target: Class<T>, property: String) : this(StaticAnimatableProperty.get(target, property))

    /**
     * The value of the property at [start]
     */
    var from: Any = property.get(null)

    /**
     * The value of the property at [end]
     */
    var to: Any = property.get(null)

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
