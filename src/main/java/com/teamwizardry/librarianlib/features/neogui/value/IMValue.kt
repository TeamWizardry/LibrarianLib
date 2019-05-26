package com.teamwizardry.librarianlib.features.neogui.value

import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.animator.LerperHandler
import com.teamwizardry.librarianlib.features.animator.NullAnimatable
import java.util.function.Supplier
import kotlin.reflect.KProperty

/**
 * A kotlin delegate that can be set to fixed values or be told to generate values using a callback
 * (a la immediate mode GUIs, the namesake of this Immediate Mode Value class).
 *
 * The convention until Bluexin yells at me or approves is as follows:
 *
 * ```kotlin
 * val yourProperty_im = IMValue<SomeType>(initialValue)
 * var yourProperty by yourProperty_im
 * ```
 */
@Suppress("Duplicates")
class IMValue<T> private constructor(private var storage: Storage<T>): GuiAnimatable<IMValue<T>> {
    constructor(initialValue: T): this(Storage.Fixed(initialValue))
    constructor(initialCallback: Supplier<T>): this(Storage.Callback(initialCallback))
    constructor(initialCallback: () -> T): this(Storage.Callback(Supplier(initialCallback)))

    /**
     * Gets the current value
     */
    fun get(): T {
        return storage.get()
    }

    /**
     * Sets the callback, unsetting the fixed value in the process
     */
    fun set(f: Supplier<T>) {
        GuiAnimator.current.add(this)
        storage = (this.storage as? Storage.Callback<T>)?.also { it.callback = f } ?: Storage.Callback(f)
    }

    /**
     * Gets the callback or null if this IMValue is storing a fixed value
     */
    fun getCallback(): Supplier<T>? {
        return (this.storage as? Storage.Callback<T>)?.callback
    }

    /**
     * Sets the fixed callback. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call into `somePropery_im` for its value)
     */
    fun setValue(value: T) {
        GuiAnimator.current.add(this)
        storage = (this.storage as? Storage.Fixed<T>)?.also { it.value = value } ?: Storage.Fixed(value)
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return storage.get()
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        setValue(value)
    }

    /**
     * A kotlin helper to allow cleanly specifying the callback (`something.theValue_im { return someValue }`)
     */
    operator fun invoke(f: () -> T) {
        set(Supplier(f))
    }

    private sealed class Storage<T> {
        abstract fun get(): T

        class Fixed<T>(var value: T): Storage<T>() {
            override fun get() = value
        }

        class Callback<T>(var callback: Supplier<T>): Storage<T>() {
            override fun get() = callback.get()
        }
    }

    override fun getAnimatableValue(): Any? {
        return this.get()
    }

    @Suppress("UNCHECKED_CAST")
    override fun setAnimatableValue(value: Any?) {
        this.setValue(value as T)
    }

    override fun getAnimatableCallback(): Any? {
        return this.getCallback()
    }

    @Suppress("UNCHECKED_CAST")
    override fun setAnimatableCallback(supplier: Any) {
        this.set(supplier as Supplier<T>)
    }

    @JvmOverloads
    fun animate(from: T, to: T, duration: Float, easing: Easing = Easing.linear, delay: Float = 0f): Animation<IMValue<T>> {
        val animation = AnimationImpl(from, to, this)
        animation.duration = duration
        animation.easing = easing
        animation.start = delay
        Animator.global.add(animation)
        return animation
    }

    @JvmOverloads
    fun animate(to: T, duration: Float, easing: Easing = Easing.linear, delay: Float = 0f): Animation<IMValue<T>> {
        val anim = animate(get(), to, duration, easing, delay) as AnimationImpl<T>
        anim.implicitStart = true
        return anim
    }

    private class AnimationImpl<T: Any?>(var from: T, var to: T, target: IMValue<T>): Animation<IMValue<T>>(target, NullAnimatable()) {
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
            target.setValue(new)
        }
    }

    @JvmOverloads
    fun animateKeyframes(initialValue: T, delay: Float = 0f): KeyframeAnimationBuilder<T> {
        return KeyframeAnimationBuilder(initialValue, delay, this)
    }

    class KeyframeAnimationBuilder<T>(initialValue: T, private val delay: Float, private val target: IMValue<T>) {
        private val keyframes = mutableListOf<Keyframe>()

        init {
            keyframes.add(Keyframe(0f, initialValue as Any))
        }
        /**
         * Add a keyframe [time] ticks after the previous one
         */
        @JvmOverloads
        fun add(time: Float, value: T, easing: Easing = Easing.linear): KeyframeAnimationBuilder<T> {
            keyframes.add(Keyframe(time, value as Any, easing))
            return this
        }

        fun finish(): Animation<IMValue<T>> {
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
    private class KeyframeAnimation<T>(target: IMValue<T>, private val keyframes: List<Keyframe>): Animation<IMValue<T>>(target, NullAnimatable()) {
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
                    target.setValue(next.value as T)
                } else {
                    val partialProgress = next.easing((progress - prev.time) / (next.time - prev.time))
                    target.setValue(lerper.lerp(prev.value as T, next.value as T, partialProgress))
                }
            } else if (next != null) {
                target.setValue(next.value as T)
            } else if (prev != null) {
                target.setValue(prev.value as T)
            }

        }
    }


    companion object {
        /**
         * Initializes an instance of IMValue that initially contains `null`. This is not a constructor because of the
         * requirement that the type be nullable in kotlin, and is required because passing `null` to the constructor
         * causes ambiguity unless one explicitly specifies the type arguments for the constructor. (Nobody wants to
         * double up on type arguments, so just do `IMValue()`)
         */
        operator fun <T> invoke(): IMValue<T?> {
            return IMValue<T?>(null)
        }
    }

}

