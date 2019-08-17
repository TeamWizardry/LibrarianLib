package com.teamwizardry.librarianlib.features.facade.value

import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.animator.LerperHandler
import com.teamwizardry.librarianlib.features.animator.NullAnimatable
import kotlin.reflect.KProperty

/**
 * A kotlin delegate that behaves almost identically to a plain `var`, except that it supports implicit animations. It
 * represents a Retained Mode Value, in contrast to [IMValue], which represents an Immediate Mode Value
 */
@Suppress("Duplicates")
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
    inner class Animatable: GuiAnimatable<Animatable> {
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

    private class AnimationImpl<T: Any?>(var from: T, var to: T, target: RMValue<T>): Animation<RMValue<T>>(target, NullAnimatable()) {
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

    @JvmOverloads
    fun animateKeyframes(initialValue: T = value, delay: Float = 0f): KeyframeAnimationBuilder<T> {
        return KeyframeAnimationBuilder(initialValue, delay, this)
    }

    class KeyframeAnimationBuilder<T>(initialValue: T, private val delay: Float, private val target: RMValue<T>) {
        private val keyframes = mutableListOf<Keyframe>()
        init {
            keyframes.add(Keyframe(0f, initialValue as Any, Easing.linear))
        }

        /**
         * Add a keyframe [time] ticks after the previous one
         */
        @JvmOverloads
        fun add(time: Float, value: T, easing: Easing = Easing.linear): KeyframeAnimationBuilder<T> {
            keyframes.add(Keyframe(time, value as Any, easing))
            return this
        }

        fun finish(): Animation<RMValue<T>> {
            if(keyframes.isEmpty()) throw IllegalStateException("Cannot create an empty keyframe animation")

            val duration = keyframes.fold(0f) { s, it -> s + it.time }
            var total = 0f
            keyframes.map {
                total += it.time
                it.time = total / duration
            }
            val animation = KeyframeAnimation(target, keyframes)
            animation.duration = duration
            animation.start = delay
            Animator.global.add(animation)
            return animation
        }
    }

    private data class Keyframe(var time: Float, val value: Any, val easing: Easing = Easing.linear)
    private class KeyframeAnimation<T>(target: RMValue<T>, private val keyframes: List<Keyframe>): Animation<RMValue<T>>(target, NullAnimatable()) {
        @Suppress("UNCHECKED_CAST")
        private var lerper = LerperHandler.getLerperOrError(
            keyframes.first().value.javaClass as Class<T>
        )

        @Suppress("UNCHECKED_CAST")
        override fun update(time: Float) {
            val progress = timeFraction(time)
            val prev = keyframes.lastOrNull { it.time <= progress }
            val next = keyframes.firstOrNull { it.time >= progress }
            if (prev != null && next != null) {
                if (next.time == prev.time) { // this can only happen with single-keyframe animations or when we are on top of a keyframe
                    target.set(next.value as T)
                } else {
                    val partialProgress = next.easing((progress - prev.time) / (next.time - prev.time))
                    target.set(lerper.lerp(prev.value as T, next.value as T, partialProgress))
                }
            } else if (next != null) {
                target.set(next.value as T)
            } else if (prev != null) {
                target.set(prev.value as T)
            }

        }
    }
}

