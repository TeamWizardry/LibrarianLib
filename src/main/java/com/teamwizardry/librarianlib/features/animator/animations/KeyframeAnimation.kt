package com.teamwizardry.librarianlib.features.animator.animations

import com.teamwizardry.librarianlib.features.animator.AnimatableProperty
import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.IAnimatable
import com.teamwizardry.librarianlib.features.animator.LerperHandler
import java.util.*

/**
 * A keyframe animation. Not much more to say.
 */
class KeyframeAnimation<T : Any>(target: T, property: IAnimatable<T>) : Animation<T>(target, property) {
    constructor(target: T, property: String) : this(target, AnimatableProperty.get(target.javaClass, property))
    @PublishedApi internal constructor(target: T, property: AnimatableProperty<T>) : this(target, property as IAnimatable<T>)

    /**
     * The list of keyframes for this animation. Getting and setting this value copy the array, and it cannot be
     * modified after the animation has been added to an animator. The array is also sorted by keyframe time before
     * being used.
     *
     * If two keyframes have the same time, it will instantly transition between the two upon passing that frame.
     */
    var keyframes: Array<Keyframe>
        set(value) {
            if (isInAnimator) {
                throw IllegalStateException("Cannot set keyframes after the animation has been added to an animator")
            }
            _keyframes = value.copyOf()
            _keyframes.sortBy { it.time }
        }
        get() = _keyframes.copyOf()

    private var _keyframes: Array<Keyframe> = arrayOf()
    private var lerper = LerperHandler.getLerperOrError(property.type)

    override fun update(time: Float) {
        val progress = timeFraction(time)
        val prev = try {
            _keyframes.last { it.time <= progress }
        } catch (e: NoSuchElementException) {
            null
        }
        val next = try {
            _keyframes.first { it.time >= progress }
        } catch (e: NoSuchElementException) {
            null
        }
        if (prev != null && next != null) {
            if (next.time == prev.time) { // this can only happen with single-keyframe animations or when we are on top of a keyframe
                property.set(target, next.value)
            } else {
                val partialProgress = next.easing((progress - prev.time) / (next.time - prev.time))
                property.set(target, lerper.lerp(prev.value, next.value, partialProgress))
            }
        } else if (next != null) {
            property.set(target, next.value)
        } else if (prev != null) {
            property.set(target, prev.value)
        }

    }
}
