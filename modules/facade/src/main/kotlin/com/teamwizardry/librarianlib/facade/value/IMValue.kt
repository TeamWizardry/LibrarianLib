package com.teamwizardry.librarianlib.facade.value

import com.teamwizardry.librarianlib.core.util.lerp.Lerper
import com.teamwizardry.librarianlib.facade.component.GuiLayer
import java.util.function.Supplier
import kotlin.reflect.KProperty

/**
 * A kotlin delegate that can be set to fixed values or be told to generate values using a callback
 * (a la immediate mode GUIs, the namesake of this Immediate Mode Value class).
 *
 * **Note!** Animating an IMValue will remove its callback once it completes.
 *
 * The naming convention is as follows:
 * ```kotlin
 * val yourProperty_im = IMValue<SomeType>(initialValue)
 * var yourProperty by yourProperty_im
 * ```
 *
 * For [GuiLayers][GuiLayer], use the [GuiLayer.imValue] method, since that will
 */
@Suppress("Duplicates")
class IMValue<T> private constructor(private var storage: Storage<T>, private val lerper: Lerper<T>?): GuiValue<T>() {
    constructor(initialValue: T, lerper: Lerper<T>?): this(Storage.Fixed(initialValue), lerper)
    constructor(initialCallback: Supplier<T>, lerper: Lerper<T>?): this(Storage.Callback(initialCallback), lerper)
    constructor(initialCallback: () -> T, lerper: Lerper<T>?): this(Storage.Callback(Supplier(initialCallback)), lerper)

    /**
     * Gets the current value
     */
    fun get(): T {
        return if(useAnimationValue) animationValue else storage.get()
    }

    /**
     * Sets the callback, unsetting the fixed value in the process
     */
    fun set(f: Supplier<T>) {
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
        storage = (this.storage as? Storage.Fixed<T>)?.also { it.value = value } ?: Storage.Fixed(value)
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    @JvmSynthetic
    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return get()
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_im`)
     */
    @JvmSynthetic
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        setValue(value)
    }

    /**
     * (SAM interfaces in kotlin are a pain)
     */
    @JvmSynthetic
    inline fun set(crossinline f: () -> T) {
        set(Supplier { f() })
    }

    override val hasLerper: Boolean
        get() = lerper != null
    override val currentValue: T
        get() = get()

    override fun lerp(from: T, to: T, fraction: Float): T {
        if(lerper == null)
            throw IllegalStateException("Can not lerp an IMValue that has no lerper")
        return lerper.lerp(from, to, fraction)
    }

    override fun animationChange(from: T, to: T) {
        // nop
    }

    override fun persistAnimation(value: T) {
        setValue(value)
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
}

