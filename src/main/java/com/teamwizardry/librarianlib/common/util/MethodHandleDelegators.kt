package com.teamwizardry.librarianlib.common.util

import kotlin.reflect.KProperty

/**
 * @author WireSegal
 * Created at 6:49 PM on 8/14/16.
 */

data class ImmutableFieldDelegator<T, out V>(val inst: T, val getter: (T) -> Any?) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): V {
        return getter(inst) as V
    }
}

data class MutableFieldDelegator<T, V>(val inst: T, val getter: (T) -> Any?, val setter: (T, Any?) -> Unit) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): V {
        return getter(inst) as V
    }

    @Suppress("UNCHECKED_CAST")
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        setter(inst, value)
    }
}

data class ImmutableStaticFieldDelegator<out V>(val getter: () -> Any?) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): V {
        return getter() as V
    }
}

data class MutableStaticFieldDelegator<V>(val getter: () -> Any?, val setter: (Any?) -> Unit) {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): V {
        return getter() as V
    }

    @Suppress("UNCHECKED_CAST")
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        setter(value)
    }
}
