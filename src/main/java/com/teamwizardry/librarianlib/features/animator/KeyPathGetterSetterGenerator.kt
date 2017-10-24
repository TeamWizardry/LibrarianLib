package com.teamwizardry.librarianlib.features.animator

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.javaSetter
import kotlin.reflect.jvm.kotlinProperty

/**
 * Please, don't look. It's horrifying.
 *
 * best regards,
 * - everyone who has seen this code
 */

private fun getFieldList(target: Class<*>, keyPath: Array<String>): FieldListItem? {
    var firstItem: FieldListItem? = null
    var currentTarget = target
    var lastItem: FieldListItem? = null
    keyPath.forEach { elem ->
        val item = FieldListItem(currentTarget, elem)
        if(firstItem == null) firstItem = item
        lastItem?.child = item
        lastItem = item
        currentTarget = item.fieldClass
    }
    return firstItem
}

/**
 * returns: getter, setter, final field type
 */
fun generateGetterAndSetterForKeyPath(target: Class<*>, keyPath: Array<String>): Triple<(target: Any) -> Any?, (target: Any, value: Any?) -> Unit, Class<*>> {
    val item = getFieldList(target, keyPath)
    if(item == null) {
        return Triple(
                { _ -> null },
                { _, _ -> },
                Any::class.java
        )
    }
    val getter = item.createRootGetter()
    val setter = item.createRootSetter()
    val type = item.getRootType()

    return Triple(getter, { target, value -> setter(target, value) }, type)
}

private fun Class<*>.getDeclaredFieldRecursive(name: String): Field? {
    var cls: Class<*>? = this
    var field: Field? = null
    while(cls != null && field == null) {
        try {
            field = cls.getDeclaredField(name)
        } catch(e: NoSuchFieldException) {
            //noop
        }
        cls = cls.superclass
    }
    return field
}

private class FieldListItem(val target: Class<*>, val name: String) {
    val fieldClass: Class<*>
    var child: FieldListItem? = null

    private val accessorOfChoice: Any

    init {
        val field = target.getDeclaredFieldRecursive(name) ?:
                throw IllegalArgumentException("Couldn't find field `$name` in class `${target.canonicalName}` or any of its superclasses")
        fieldClass = field.type
        val property = field.kotlinProperty

        if(property?.javaGetter != null) {
            accessorOfChoice = property
        } else {
            accessorOfChoice = field
        }
    }

    fun getRootType(): Class<*> {
        return child?.getRootType() ?: fieldClass
    }

    fun createRootGetter(): (target: Any) -> Any? {
        val getter: (target: Any) -> Any?

        if(accessorOfChoice is Field) {
            if(!Modifier.isPublic(accessorOfChoice.modifiers)) {
                throw IllegalAccessException("Could not access field `$name` in class `${target.canonicalName}`")
            }
            getter = MethodHandleHelper.wrapperForGetter<Any>(accessorOfChoice)
        } else if(accessorOfChoice is KProperty<*>) {
            if(!Modifier.isPublic(accessorOfChoice.javaGetter!!.modifiers)) {
                throw IllegalAccessException("Could not access property getter for `$name` in class `${target.canonicalName}`")
            }
            val m = MethodHandleHelper.wrapperForMethod<Any>(accessorOfChoice.javaGetter!!)
            getter = { t -> m(t, emptyArray()) }
        } else {
            throw IllegalStateException("accessorOfChoice was neither a Field nor a KProperty, it was `${(accessorOfChoice as Any?)?.javaClass?.canonicalName ?: "null"}`")
        }

        val childGetter = child?.createRootGetter()

        if(childGetter == null) {
            return getter
        } else {
            return { t -> childGetter(getter(t)!!) }
        }
    }

    fun createRootSetter(superTarget: Class<*>? = null): (target: Any, finalValue: Any?) -> Any? {
        val getter: (target: Any) -> Any?

        if (accessorOfChoice is Field) {
            if (!Modifier.isPublic(accessorOfChoice.modifiers)) {
                throw IllegalAccessException("Could not access field `$name` in class `${target.canonicalName}`")
            }
            getter = MethodHandleHelper.wrapperForGetter<Any>(accessorOfChoice)
        } else if (accessorOfChoice is KProperty<*>) {
            if (!Modifier.isPublic(accessorOfChoice.javaGetter!!.modifiers)) {
                throw IllegalAccessException("Could not access property getter for `$name` in class `${target.canonicalName}`")
            }
            val m = MethodHandleHelper.wrapperForMethod<Any>(accessorOfChoice.javaGetter!!)
            getter = { t -> m(t, emptyArray()) }
        } else {
            throw IllegalStateException("accessorOfChoice was neither a Field nor a KProperty, it was `${(accessorOfChoice as Any?)?.javaClass?.canonicalName ?: "null"}`")
        }

        val childSetter = child?.createRootSetter(target) ?: { _, finalValue -> finalValue }

        // if mutable
        if ((accessorOfChoice as? KMutableProperty<*>)?.javaSetter?.modifiers?.let { Modifier.isPublic(it) } ?: false ||
                (accessorOfChoice is Field && !Modifier.isFinal(accessorOfChoice.modifiers))
                ) {
            val setter: (target: Any, value: Any?) -> Unit
            if(accessorOfChoice is Field) {
                setter = MethodHandleHelper.wrapperForSetter(accessorOfChoice)
            } else if(accessorOfChoice is KMutableProperty<*>) {
                val m = MethodHandleHelper.wrapperForMethod<Any>(accessorOfChoice.javaSetter!!)
                setter = { t, v -> m(t, arrayOf(v)) }
            } else {
                throw IllegalStateException("accessorOfChoice was neither a Field nor a KProperty, it was `${(accessorOfChoice as Any?)?.javaClass?.canonicalName ?: "null"}`")
            }

            return ret@ { target, finalValue ->
                val fieldValue = getter(target)!!
                val childValue = childSetter(fieldValue, finalValue)

                if(childValue !== NULL_OBJECT) {
                    setter(target, childValue)
                }

                return@ret NULL_OBJECT
            }

        } else {
            val mutator = ImmutableFieldMutatorHandler.getMutator(target, name)
                    ?: throw IllegalAccessException("Cannot set the immutable field `$name` without a mutator")

            return ret@ { target, finalValue ->
                val fieldValue = getter(target)!!
                val childValue = childSetter(fieldValue, finalValue)

                if(childValue !== NULL_OBJECT) {
                    val newSuper = mutator.mutate(target, childValue)
                    return@ret newSuper
                }

                return@ret NULL_OBJECT
            }
        }
    }
}

private val NULL_OBJECT = Any()


//private val getter: (T) -> Any?
//private val setter: (T, Any?) -> Unit
//
//init {
//    val steps = keyPath.split(".")
//
//    val (getLast, lastType) = getFinalTarget(steps, 0, target)
//
//    val field = lastType.getDeclaredField(steps.last())
//    val property = field.kotlinProperty
//    if(property == null && !field.isAccessible) {
//        throw IllegalAccessException("Could not access field $keyPath")
//    } else if(property != null) {
//        if(property !is KMutableProperty<*>) {
//            throw IllegalAccessException("Property $keyPath is immutable")
//        }
//        if(!property.javaGetter!!.isAccessible) {
//            throw IllegalAccessException("Getter for property $keyPath is inaccessable")
//        }
//        if(!property.javaSetter!!.isAccessible) {
//            throw IllegalAccessException("Setter for property $keyPath is inaccessable")
//        }
//    }
//
//    if(property != null) {
//        val m = MethodHandleHelper.wrapperForMethod<Any>(property.javaGetter!!)
//        getter = { t ->
//            val current = getLast(t)
//            m(current, emptyArray())
//        }
//    } else {
//        getter = MethodHandleHelper.wrapperForGetter<Any>(field)
//    }
//
//    if(property != null) {
//        property as KMutableProperty<*>
//        val m = MethodHandleHelper.wrapperForMethod<Any>(property.javaSetter!!)
//        setter = { t, v ->
//            val current = getLast(t)
//            m(current, arrayOf(v))
//        }
//    } else {
//        setter = MethodHandleHelper.wrapperForSetter<Any>(field)
//    }
//}
//
//private fun getFinalTarget(steps: List<String>, index: Int, type: Class<*>, runningName: String = steps.first()): Pair<(Any) -> Any, Class<*>> {
//    if(index >= steps.size-2) {
//        return Pair({ v -> v }, type)
//    }
//    val name = steps[index]
//    val field = type.getDeclaredField(name)
//    val property = field.kotlinProperty
//    if(property == null && !field.isAccessible) {
//        throw IllegalAccessException("Could not access field $runningName")
//    } else if(property != null && !property.javaGetter!!.isAccessible) {
//        throw IllegalAccessException("Could not access property $runningName")
//    }
//
//    val getterThisStep: (Any) -> Any?
//    if(property != null) {
//        val g = MethodHandleHelper.wrapperForMethod<Any>(property.javaGetter!!)
//        getterThisStep = { v -> g(v, emptyArray())}
//    } else {
//        getterThisStep = MethodHandleHelper.wrapperForGetter<Any>(field)
//    }
//
//    val (next, nextType) = getFinalTarget(steps, index+1, field.type, runningName + "." + steps[index+1])
//
//    return Pair({ v ->
//        val current = getterThisStep(v)!!
//        next(current)
//    }, nextType)
//}
