package com.teamwizardry.librarianlib.features.animator

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.saving.ArrayReflect
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.staticProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.javaSetter

/**
 * Please, don't look. It's horrifying.
 *
 * best regards,
 * - everyone who has seen this code
 *
 * Demoniaque: HE'S SERIOUS AND I DIDN'T BELIEVE HIM. I TRIED. PLEASE DON'T LOOK AT IT.
 */

data class KeyPathAccessor(val clazz: Class<Any>, val getter: (target: Any) -> Any?, val setter: (target: Any, value: Any?) -> Unit, val involvement: (target: Any, check: Any) -> Boolean)

data class StaticKeyPathAccessor(val clazz: Class<Any>, val getter: () -> Any?, val setter: (value: Any?) -> Unit, val involvement: (check: Any) -> Boolean)


/**
 * Foo.someField.anArrayField[3]
 * list:
 *  - class = foo, accessing = someField
 *  - class = someField, accessing = anArrayField,
 *  - class = anArrayField, accessing = [3]
 */
private fun getFieldList(target: Class<*>, keyPath: Array<String>): FieldListItem? {
    var firstItem: FieldListItem? = null
    var currentTarget = target
    var lastItem: FieldListItem? = null
    keyPath.forEach { elem ->
        val item = FieldListItem(currentTarget, elem)
        if (firstItem == null) firstItem = item
        lastItem?.child = item
        lastItem = item
        currentTarget = item.fieldClass
    }
    return firstItem
}

private fun getStaticFieldList(target: Class<*>, keyPath: Array<String>): StaticFieldListItem? {
    if (keyPath.isEmpty())
        return null
    val host = StaticFieldListItem(target, keyPath.first())

    var currentTarget = host.fieldClass
    var lastItem: FieldListItem? = null
    keyPath.drop(1).forEach { elem ->
        val item = FieldListItem(currentTarget, elem)
        if (host.child == null)
            host.child = item
        lastItem?.child = item
        lastItem = item
        currentTarget = item.fieldClass
    }

    return host
}

/**
 * returns: getter, setter, final field type
 */
fun generateGetterAndSetterForKeyPath(target: Class<*>, keyPath: Array<String>): KeyPathAccessor {
    val item = getFieldList(target, keyPath) ?: return KeyPathAccessor(
            Any::class.java,
            { _ -> null },
            { _, _ -> },
            { _, _ -> false }
    )
    val getter = item.createRootGetter()
    val setter = item.createRootSetter()
    val involvement = item.createInvolvementChecker()
    val type = item.getRootType()

    return KeyPathAccessor(
            clazz = type,
            getter = getter,
            setter = { holder, value -> setter(holder, value) },
            involvement = involvement
    )
}

fun generateGetterAndSetterForStaticKeyPath(target: Class<*>, keyPath: Array<String>): StaticKeyPathAccessor {
    val item = getStaticFieldList(target, keyPath) ?: return StaticKeyPathAccessor(
            Any::class.java,
            { null },
            { },
            { false }
    )

    val getter = item.rootGetter
    val setter = item.rootSetter
    val involvement = item.involvementChecker
    val type = item.rootType

    return StaticKeyPathAccessor(
            clazz = type,
            getter = getter,
            setter = { value -> setter(value) },
            involvement = involvement
    )
}

private fun KClass<*>.getStaticPropertyRecursive(name: String): KProperty<*>? {
    var cls: KClass<*>? = this
    var prop: KProperty<*>? = null
    while (cls != null && prop == null) {
        val props = cls.staticProperties
        prop = props.firstOrNull { it.name == name }
        val supers = cls.superclasses
        cls = supers.firstOrNull { !it.java.isInterface }
    }
    return prop
}

private fun Class<*>.getDeclaredFieldRecursive(name: String): Field? {
    var cls: Class<*>? = this
    var field: Field? = null
    while (cls != null && field == null) {
        try {
            field = cls.getDeclaredField(name)
        } catch (e: NoSuchFieldException) {
            //noop
        }
        cls = cls.superclass
    }
    return field
}

private fun KClass<*>.getDeclaredPropertyRecursive(name: String): KProperty<*>? {
    var cls: KClass<*>? = this
    var prop: KProperty<*>? = null
    while (cls != null && prop == null) {
        val props = cls.declaredMemberProperties
        prop = props.firstOrNull { it.name == name }
        val supers = cls.superclasses
        cls = supers.firstOrNull { !it.java.isInterface }
    }
    return prop
}

private val subscriptRegex = "\\[(\\d+)]".toRegex()

private class FieldListItem(val target: Class<*>, val name: String) {
    val fieldClass: Class<*>
    var child: FieldListItem? = null

    private val accessorOfChoice: Any

    init {
        if (target.isArray) {
            // if (name.startsWith("[") && name.endsWith("]"))
            //     accessorOfChoice = StringUtils.substringBetween(name, "[", "]").toIntOrNull()
            //             ?: throw IllegalArgumentException("Name `$name` not a valid subscript string! (valid format: `\\[\\d+]`)")
            // else throw IllegalArgumentException("Name `$name` not a valid subscript string! (valid format: `\\[\\d+]`)")
            accessorOfChoice = subscriptRegex.find(name)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: throw IllegalArgumentException("Name `$name` not a valid subscript string! (valid format: `\\[\\d+]`)")
            fieldClass = target.componentType
        } else {
            val property = target.kotlin.getDeclaredPropertyRecursive(name)
                    ?: throw IllegalArgumentException("Couldn't find a property `$name` in class `${target.canonicalName}` or any of its superclasses")
            fieldClass = (property.returnType.classifier as KClass<*>).java

            accessorOfChoice = if (property.javaGetter != null) {
                property
            } else {
                property.javaField
                        ?: throw IllegalArgumentException("Property `$name` in class `${target.canonicalName}` has no getter and no backing field")
            }
        }
    }

    fun getRootType(): Class<Any> {
        @Suppress("UNCHECKED_CAST")
        return child?.getRootType() ?: fieldClass as Class<Any>
    }

    fun createRootGetter(): (target: Any) -> Any? {
        val getter: (target: Any) -> Any?

        when (accessorOfChoice) {
            is Field -> {
                if (!Modifier.isPublic(accessorOfChoice.modifiers)) {
                    throw IllegalAccessException("Could not access field `$name` in class `${target.canonicalName}`")
                }
                getter = MethodHandleHelper.wrapperForGetter(accessorOfChoice)
            }
            is KProperty<*> -> {
                if (!Modifier.isPublic(accessorOfChoice.javaGetter!!.modifiers)) {
                    throw IllegalAccessException("Could not access property getter for `$name` in class `${target.canonicalName}`")
                }
                val m = MethodHandleHelper.wrapperForMethod<Any>(accessorOfChoice.javaGetter!!)
                getter = { t -> m(t, emptyArray()) }
            }
            is Int -> getter = { t -> ArrayReflect.get(t, accessorOfChoice) }
            else -> throw IllegalStateException("accessorOfChoice was neither a Field nor a KProperty, it was `${(accessorOfChoice as Any?)?.javaClass?.canonicalName
                    ?: "null"}`")
        }

        val childGetter = child?.createRootGetter()

        if (childGetter == null) {
            return getter
        } else {
            return { t -> childGetter(getter(t)!!) }
        }
    }

    fun createRootSetter(): (target: Any, finalValue: Any?) -> Any? {
        val getter: (target: Any) -> Any?

        when (accessorOfChoice) {
            is Field -> {
                if (!Modifier.isPublic(accessorOfChoice.modifiers)) {
                    throw IllegalAccessException("Could not access field `$name` in class `${target.canonicalName}`")
                }
                getter = MethodHandleHelper.wrapperForGetter(accessorOfChoice)
            }
            is KProperty<*> -> {
                if (!Modifier.isPublic(accessorOfChoice.javaGetter!!.modifiers)) {
                    throw IllegalAccessException("Could not access property getter for `$name` in class `${target.canonicalName}`")
                }
                val m = MethodHandleHelper.wrapperForMethod<Any>(accessorOfChoice.javaGetter!!)
                getter = { t -> m(t, emptyArray()) }
            }
            is Int -> getter = { t -> ArrayReflect.get(t, accessorOfChoice) }
            else -> throw IllegalStateException("accessorOfChoice was neither a Field nor a KProperty, it was `${(accessorOfChoice as Any?)?.javaClass?.canonicalName
                    ?: "null"}`")
        }

        val childSetter = child?.createRootSetter() ?: { _, finalValue -> finalValue }

        val setter by lazy<(target: Any, value: Any?) -> Any?> {
            if (accessorOfChoice is Int) {
                return@lazy m@{ t, v ->
                    ArrayReflect.set(t, accessorOfChoice, v)
                    return@m NULL_OBJECT
                }
            } else if (
                    (accessorOfChoice as? KMutableProperty<*>)?.javaSetter?.modifiers?.let { Modifier.isPublic(it) } == true ||
                    (accessorOfChoice is Field && !Modifier.isFinal(accessorOfChoice.modifiers))
            ) {
                when (accessorOfChoice) {
                    is Field -> {
                        val m = MethodHandleHelper.wrapperForSetter<Any>(accessorOfChoice)
                        return@lazy m@{ t, v ->
                            m(t, v)
                            return@m NULL_OBJECT
                        }
                    }
                    is KMutableProperty<*> -> {
                        val m = MethodHandleHelper.wrapperForMethod<Any>(accessorOfChoice.javaSetter!!)
                        return@lazy m@{ t, v ->
                            m(t, arrayOf(v))
                            return@m NULL_OBJECT
                        }
                    }
                    else -> throw IllegalStateException("accessorOfChoice was neither a Field nor a KProperty, it was `${(accessorOfChoice as Any?)?.javaClass?.canonicalName
                            ?: "null"}`")
                }
            } else {
                val mutator = ImmutableFieldMutatorHandler.getMutator(target, name)
                        ?: throw IllegalAccessException("Cannot set the immutable field `$name` without a mutator")

                return@lazy m@{ t, v ->
                    return@m mutator.mutate(t, v)
                }
            }

        }

        return ret@{ target, finalValue ->
            val fieldValue = getter(target)!!
            val childValue = childSetter(fieldValue, finalValue)

            if (childValue !== NULL_OBJECT) {
                return@ret setter(target, childValue)
            }

            return@ret NULL_OBJECT
        }

    }

    fun createInvolvementChecker(): (target: Any, check: Any) -> Boolean {
        val getter: (target: Any) -> Any?

        when (accessorOfChoice) {
            is Field -> {
                if (!Modifier.isPublic(accessorOfChoice.modifiers)) {
                    throw IllegalAccessException("Could not access field `$name` in class `${target.canonicalName}`")
                }
                getter = MethodHandleHelper.wrapperForGetter(accessorOfChoice)
            }
            is KProperty<*> -> {
                if (!Modifier.isPublic(accessorOfChoice.javaGetter!!.modifiers)) {
                    throw IllegalAccessException("Could not access property getter for `$name` in class `${target.canonicalName}`")
                }
                val m = MethodHandleHelper.wrapperForMethod<Any>(accessorOfChoice.javaGetter!!)
                getter = { t -> m(t, emptyArray()) }
            }
            is Int -> getter = { t -> ArrayReflect.get(t, accessorOfChoice) }
            else -> throw IllegalStateException("accessorOfChoice was neither a Field nor a KProperty, it was `${(accessorOfChoice as Any?)?.javaClass?.canonicalName
                    ?: "null"}`")
        }

        val childGetter = child?.createInvolvementChecker()

        if (childGetter == null) {
            return { t, c -> getter(t) === c }
        } else {
            return ret@{ t, c ->
                val us = getter(t) ?: return@ret false
                us === c || childGetter(us, c)
            }
        }
    }
}

private class StaticFieldListItem(val target: Class<*>, val name: String) {
    val fieldClass: Class<*>
    var child: FieldListItem? = null

    private val accessorOfChoice: Any

    init {
        val property = target.kotlin.getStaticPropertyRecursive(name)
                ?: throw IllegalArgumentException("Couldn't find a static property `$name` in class `${target.canonicalName}` or any of its superclasses")
        fieldClass = (property.returnType.classifier as KClass<*>).java

        accessorOfChoice = if (property.javaGetter != null) {
            property
        } else {
            property.javaField
                    ?: throw IllegalArgumentException("Static property `$name` in class `${target.canonicalName}` has no getter and no backing field")
        }
    }

    @Suppress("UNCHECKED_CAST")
    val rootType: Class<Any> by lazy {
        child?.getRootType() ?: fieldClass as Class<Any>
    }

    val rootGetter: () -> Any? by lazy {
        when (accessorOfChoice) {
            is Field -> {
                if (!Modifier.isPublic(accessorOfChoice.modifiers)) {
                    throw IllegalAccessException("Could not access static field `$name` in class `${target.canonicalName}`")
                }
                MethodHandleHelper.wrapperForStaticGetter(accessorOfChoice)
            }
            is KProperty<*> -> {
                if (!Modifier.isPublic(accessorOfChoice.javaGetter!!.modifiers)) {
                    throw IllegalAccessException("Could not access static property getter for `$name` in class `${target.canonicalName}`")
                }
                val m = MethodHandleHelper.wrapperForStaticMethod(accessorOfChoice.javaGetter!!)
                val x = { m(emptyArray()) }; x
            }
            else -> throw IllegalStateException("accessorOfChoice was neither a Field nor a KProperty, it was `${(accessorOfChoice as Any?)?.javaClass?.canonicalName
                    ?: "null"}`")
        }
    }

    private val rootMutator: (value: Any?) -> Any? by lazy {
        if ((accessorOfChoice as? KMutableProperty<*>)?.javaSetter?.modifiers?.let { Modifier.isPublic(it) } == true ||
                (accessorOfChoice is Field && !Modifier.isFinal(accessorOfChoice.modifiers)))
            when (accessorOfChoice) {
                is Field -> {
                    val m = MethodHandleHelper.wrapperForStaticSetter(accessorOfChoice)

                    val x = { it: Any? ->
                        m(it)
                        NULL_OBJECT
                    }; x
                }

                is KMutableProperty<*> -> {
                    val m = MethodHandleHelper.wrapperForStaticMethod(accessorOfChoice.javaSetter!!)

                    val x = { it: Any? ->
                        m(arrayOf(it))
                        NULL_OBJECT
                    }; x
                }

                else ->
                    throw IllegalStateException("accessorOfChoice was neither a Field nor a KProperty, it was `${(
                            accessorOfChoice as Any?)?.javaClass?.canonicalName ?: "null"}`")
            }
        else
            throw IllegalAccessException("Static immutable field `$name` cannot have a mutator")
    }

    val rootSetter: (finalValue: Any?) -> Any? by lazy {
        val childSetter = child?.createRootSetter() ?: { _, finalValue -> finalValue }

        { finalValue: Any? ->
            val fieldValue = rootGetter()!!
            val childValue = childSetter(fieldValue, finalValue)

            if (childValue !== NULL_OBJECT) {
                rootMutator(childValue)
            } else NULL_OBJECT
        }
    }

    val involvementChecker: (check: Any) -> Boolean by lazy {
        { c: Any ->
            val us = rootGetter()
            us !== null && us === c
        }
    }
}

private val NULL_OBJECT = Any()
