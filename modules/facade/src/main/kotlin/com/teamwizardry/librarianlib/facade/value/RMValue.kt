package com.teamwizardry.librarianlib.facade.value

import com.teamwizardry.librarianlib.core.util.lerp.Lerper
import java.lang.IllegalStateException
import kotlin.reflect.KProperty

/**
 * A kotlin delegate that behaves almost identically to a plain `var`, except that it supports implicit animations. It
 * represents a Retained Mode Value, in contrast to [IMValue], which represents an Immediate Mode Value
 */
@Suppress("Duplicates")
public class RMValue<T> @JvmOverloads constructor(
    private var value: T, private val lerper: Lerper<T>?, private val change: ChangeListener<T>? = null
): GuiValue<T>() {
    /**
     * Gets the current value
     */
    public fun get(): T {
        @Suppress("UNCHECKED_CAST")
        return if(useAnimationValue) animationValue else value
    }

    /**
     * Sets a new value
     */
    public fun set(value: T) {
        val oldValue = this.value
        this.value = value
        if(oldValue != value && !useAnimationValue) {
            change?.report(oldValue, value)
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValue()`)
     */
    public operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return this.get()
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValue()`)
     */
    public operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.set(value)
    }

    override val hasLerper: Boolean
        get() = lerper != null

    override val currentValue: T
        get() = get()

    override fun lerp(from: T, to: T, fraction: Float): T {
        if(lerper == null)
            throw IllegalStateException("Can not lerp an RMValue that has no lerper")
        return lerper.lerp(from, to, fraction)
    }

    override fun animationChange(from: T, to: T) {
        change?.report(from, to)
    }

    override fun persistAnimation(value: T) {
        this.value = value
    }
}

