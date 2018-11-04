package com.teamwizardry.librarianlib.features.gui.value

import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.animator.LerperHandler
import kotlin.reflect.KProperty

/**
 * A kotlin delegate that behaves almost identically to a plain `var`, except that it supports implicit animations. It
 * represents a Retained Mode Value, in contrast to [IMValue], which represents an Immediate Mode Value
 */
class RMValue<T> @JvmOverloads constructor(
    private var value: T, private val change: (oldValue: T, newValue: T) -> Unit = { _, _ -> }
) {

    /**
     * Gets the current value
     */
    fun get(): T {
        return value
    }

    /**
     * Sets a new value
     */
    fun set(value: T) {
        GuiAnimator.current.add(animatable)
        val oldValue = this.value
        this.value = value
        if(oldValue != value) {
            change(oldValue, value)
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValue()`)
     */
    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return this.get()
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValue()`)
     */
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.set(value)
    }

    private val animatable = Animatable()
    inner class Animatable: GuiAnimatable {
        override fun getAnimatableValue(): Any? {
            return value
        }

        @Suppress("UNCHECKED_CAST")
        override fun setAnimatableValue(value: Any?) {
            this@RMValue.value = value as T
        }

        override fun getAnimatableCallback(): Any? {
            return null
        }

        override fun setAnimatableCallback(supplier: Any) {
            // NO-OP
        }
    }

    @JvmOverloads
    fun animate(from: T, to: T, duration: Float, easing: Easing = Easing.linear, delay: Float = 0f): Animation<RMValue<T>> {
        val animation = AnimationImpl(from, to, this)
        animation.duration = duration
        animation.easing = easing
        animation.start = delay
        Animator.global.add(animation)
        return animation
    }

    @JvmOverloads
    fun animate(to: T, duration: Float, easing: Easing = Easing.linear, delay: Float = 0f): Animation<RMValue<T>> {
        val anim = animate(value, to, duration, easing, delay) as AnimationImpl<T>
        anim.implicitStart = true
        return anim
    }

    class AnimationImpl<T: Any?>(var from: T, var to: T, target: RMValue<T>): Animation<RMValue<T>>(target) {
        var easing: Easing = Easing.linear
        var implicitStart: Boolean = false

        @Suppress("UNCHECKED_CAST")
        private var lerper = LerperHandler.getLerperOrError(((from as Any?)?.javaClass ?: (to as Any?)?.javaClass) as Class<T>)
        override fun update(time: Float) {
            if(implicitStart) {
                from = target.get()
                implicitStart = false
            }
            val progress = easing(timeFraction(time))
            val new = lerper.lerp(from, to, progress)
            target.set(new)
        }
    }
}

