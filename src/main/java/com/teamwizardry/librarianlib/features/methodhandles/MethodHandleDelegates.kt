package com.teamwizardry.librarianlib.features.methodhandles

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author WireSegal
 * Created at 6:49 PM on 8/14/16.
 */

data class ImmutableFieldDelegate<in T, out V> @JvmOverloads constructor(val getter: (T) -> Any?, val cache: Boolean = false) : ReadOnlyProperty<T, V> {
    private var cachedValue: V? = null
    private var initialized = false

    @JvmOverloads constructor(inst: T, getter: (T) -> Any?, cache: Boolean = false) : this(getter, cache) {
        this.inst = inst
    }

    private var inst: T? = null

    @Suppress("UNCHECKED_CAST")
    override operator fun getValue(thisRef: T, property: KProperty<*>): V {
        if (initialized) return cachedValue!!

        val gotten = getter(inst ?: thisRef) as V

        if (!initialized && cache) {
            cachedValue = gotten
            initialized = false
        }
        return gotten
    }
}

data class MutableFieldDelegate<in T, V>(val getter: (T) -> Any?, val setter: (T, Any?) -> Unit) : ReadWriteProperty<T, V> {

    constructor(inst: T, getter: (T) -> Any?, setter: (T, Any?) -> Unit) : this(getter, setter) {
        this.inst = inst
    }

    private var inst: T? = null

    @Suppress("UNCHECKED_CAST")
    override operator fun getValue(thisRef: T, property: KProperty<*>): V {
        return getter(inst ?: thisRef) as V
    }

    override operator fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        setter(inst ?: thisRef, value)
    }
}


data class ImmutableStaticFieldDelegate<in T, out V> @JvmOverloads constructor(val getter: () -> Any?, val cache: Boolean = false) : ReadOnlyProperty<T, V> {
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

data class MutableStaticFieldDelegate<in T, V>(val getter: () -> Any?, val setter: (Any?) -> Unit) : ReadWriteProperty<T, V> {
    @Suppress("UNCHECKED_CAST")
    override operator fun getValue(thisRef: T, property: KProperty<*>) = getter() as V

    override operator fun setValue(thisRef: T, property: KProperty<*>, value: V) = setter(value)
}
