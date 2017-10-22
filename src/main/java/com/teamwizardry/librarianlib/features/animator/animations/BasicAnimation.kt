package com.teamwizardry.librarianlib.features.animator.animations

import com.teamwizardry.librarianlib.features.animator.AnimatableProperty
import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.LerperHandler

/**
 * TODO: Document file BasicAnimation
 *
 * Created by TheCodeWarrior
 */
class BasicAnimation<T : Any>(target: T, property: AnimatableProperty<T>) : Animation<T>(target, property) {
    var from: Any = property.get(target)
    var to: Any = property.get(target)

    private var lerper = LerperHandler.getLerperOrError(property.type)

    override fun update(time: Float) {
        val new = lerper.lerp(from, to, timeFraction(time))
        property.set(target, new)
    }
}
