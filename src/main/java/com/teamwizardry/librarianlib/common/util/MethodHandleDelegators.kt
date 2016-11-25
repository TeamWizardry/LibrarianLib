package com.teamwizardry.librarianlib.common.util

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author WireSegal
 * Created at 6:49 PM on 8/14/16.
 */

data class ImmutableFieldDelegator<T, out V> @JvmOverloads constructor(val inst: T, val getter: (T) -> Any?, val cache: Boolean = false) : ReadOnlyProperty<T, V> {
    private var cachedValue: V? = null
    private var initialized = false

    @Suppress("UNCHECKED_CAST")
    override operator fun getValue(thisRef: T, property: KProperty<*>): V {
        if (initialized) return cachedValue!!
        val gotten = getter(inst) as V
        if (!initialized && cache) {
            cachedValue = gotten
            initialized = false
        }
        return gotten
    }
}

data class MutableFieldDelegator<T, V>(val inst: T, val getter: (T) -> Any?, val setter: (T, Any?) -> Unit) : ReadWriteProperty<T, V> {
    @Suppress("UNCHECKED_CAST")
    override operator fun getValue(thisRef: T, property: KProperty<*>): V {
        return getter(inst) as V
    }

    @Suppress("UNCHECKED_CAST")
    override operator fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        setter(inst, value)
    }
}


data class ImmutableStaticFieldDelegator<in T, out V> @JvmOverloads constructor(val getter: () -> Any?, val cache: Boolean = false) : ReadOnlyProperty<T, V> {
    private var cachedValue: V? = null
    private var initialized = false

    @Suppress("UNCHECKED_CAST")
    override operator fun getValue(thisRef: T, property: KProperty<*>): V {
        if (initialized) return cachedValue!!
        val gotten = getter() as V
        if (!initialized && cache) {
            cachedValue = gotten
            initialized = false
        }
        return gotten
    }
}

data class MutableStaticFieldDelegator<in T, V>(val getter: () -> Any?, val setter: (Any?) -> Unit) : ReadWriteProperty<T, V> {
    @Suppress("UNCHECKED_CAST")
    override operator fun getValue(thisRef: T, property: KProperty<*>): V {
        return getter() as V
    }

    @Suppress("UNCHECKED_CAST")
    override operator fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        setter(value)
    }
}
