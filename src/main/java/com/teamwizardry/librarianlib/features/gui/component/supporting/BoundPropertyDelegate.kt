package com.teamwizardry.librarianlib.features.gui.component.supporting

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

/**
 * TODO: Document file BoundPropertyDelegate
 *
 * Created by TheCodeWarrior
 */
class BoundPropertyDelegateReadOnly<in R, T>(val prop: KProperty0<T>) : ReadOnlyProperty<R, T> {
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        return prop.get()
    }
}

class BoundPropertyDelegateReadWrite<in R, T>(val prop: KMutableProperty0<T>) : ReadWriteProperty<R, T> {
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        return prop.get()
    }

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        prop.set(value)
    }
}

val <T> KProperty0<T>.delegate: ReadOnlyProperty<Any, T>
    get() = BoundPropertyDelegateReadOnly(this)
val <T> KMutableProperty0<T>.delegate: ReadWriteProperty<Any, T>
    get() = BoundPropertyDelegateReadWrite(this)
