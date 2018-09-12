package com.teamwizardry.librarianlib.features.gui.value

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
class IMValue<T> private constructor(private var storage: Storage<T>): GuiAnimatable {
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

