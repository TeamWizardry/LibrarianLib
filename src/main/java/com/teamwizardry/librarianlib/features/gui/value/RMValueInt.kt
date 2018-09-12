package com.teamwizardry.librarianlib.features.gui.value

import kotlin.reflect.KProperty

/**
 * A kotlin delegate that behaves almost identically to a plain `var`, except that it supports implicit animations. It
 * represents a Retained Mode Value, in contrast to [IMValue], which represents an Immediate Mode Value
 */
class RMValueInt @JvmOverloads constructor(private var value: Int, private val change: (Int) -> Unit = {}) : GuiAnimatable {
    /**
     * Gets the current value
     */
    fun get(): Int {
        return value
    }

    /**
     * Sets a new value
     */
    fun set(value: Int) {
        GuiAnimator.current.add(this)
        val oldValue = this.value
        this.value = value
        if(oldValue != value) {
            change(oldValue)
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValueInt()`)
     */
    operator fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return this.get()
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValueInt()`)
     */
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        this.set(value)
    }

    override fun getAnimatableValue(): Any? {
        return value
    }

    @Suppress("UNCHECKED_CAST")
    override fun setAnimatableValue(value: Any?) {
        this.value = value as Int
    }

    override fun getAnimatableCallback(): Any? {
        return null
    }

    override fun setAnimatableCallback(supplier: Any) {
        // NO-OP
    }
}

