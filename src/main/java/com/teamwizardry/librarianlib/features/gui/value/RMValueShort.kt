package com.teamwizardry.librarianlib.features.gui.value

import kotlin.reflect.KProperty

/**
 * A kotlin delegate that behaves almost identically to a plain `var`, except that it supports implicit animations. It
 * represents a Retained Mode Value, in contrast to [IMValue], which represents an Immediate Mode Value
 */
class RMValueShort @JvmOverloads constructor(private var value: Short, private val change: (Short) -> Unit = {}) : GuiAnimatable {
    /**
     * Gets the current value
     */
    fun get(): Short {
        return value
    }

    /**
     * Sets a new value
     */
    fun set(value: Short) {
        GuiAnimator.current.add(this)
        val oldValue = this.value
        this.value = value
        if(oldValue != value) {
            change(oldValue)
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValueShort()`)
     */
    operator fun getValue(thisRef: Any, property: KProperty<*>): Short {
        return this.get()
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValueShort()`)
     */
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: Short) {
        this.set(value)
    }

    override fun getAnimatableValue(): Any? {
        return value
    }

    @Suppress("UNCHECKED_CAST")
    override fun setAnimatableValue(value: Any?) {
        this.value = value as Short
    }

    override fun getAnimatableCallback(): Any? {
        return null
    }

    override fun setAnimatableCallback(supplier: Any) {
        // NO-OP
    }
}

