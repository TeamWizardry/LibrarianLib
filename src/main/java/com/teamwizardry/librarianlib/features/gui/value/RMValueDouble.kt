package com.teamwizardry.librarianlib.features.gui.value

import kotlin.reflect.KProperty

/**
 * A kotlin delegate that behaves almost identically to a plain `var`, except that it supports implicit animations. It
 * represents a Retained Mode Value, in contrast to [IMValue], which represents an Immediate Mode Value
 */
class RMValueDouble @JvmOverloads constructor(private var value: Double, private val change: (Double) -> Unit = {}) : GuiAnimatable {
    /**
     * Gets the current value
     */
    fun get(): Double {
        return value
    }

    /**
     * Sets a new value
     */
    fun set(value: Double) {
        GuiAnimator.current.add(this)
        val oldValue = this.value
        this.value = value
        if(oldValue != value) {
            change(oldValue)
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValueDouble()`)
     */
    operator fun getValue(thisRef: Any, property: KProperty<*>): Double {
        return this.get()
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValueDouble()`)
     */
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: Double) {
        this.set(value)
    }

    override fun getAnimatableValue(): Any? {
        return value
    }

    @Suppress("UNCHECKED_CAST")
    override fun setAnimatableValue(value: Any?) {
        this.value = value as Double
    }

    override fun getAnimatableCallback(): Any? {
        return null
    }

    override fun setAnimatableCallback(supplier: Any) {
        // NO-OP
    }
}

