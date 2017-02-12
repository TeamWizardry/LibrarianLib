package com.teamwizardry.librarianlib.common.util

import net.minecraftforge.fml.relauncher.ReflectionHelper
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles.publicLookup
import java.lang.invoke.MethodType
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * @author WireSegal
 * Created at 6:49 PM on 8/14/16.
 */
object MethodHandleHelper {

    //region base

    /**
     * Reflects a method from a class, and provides a MethodHandle for it.
     * Methodhandles MUST be invoked from java code, due to the way [@PolymorphicSignature] works.
     */
    @JvmStatic
    fun <T> handleForMethod(clazz: Class<T>, methodNames: Array<String>, vararg methodClasses: Class<*>): MethodHandle {
        val m = ReflectionHelper.findMethod<T>(clazz, null, methodNames, *methodClasses)
        return publicLookup().unreflect(m)
    }

    /**
     * Reflects a field from a class, and provides a MethodHandle for it.
     * MethodHandles MUST be invoked from java code, due to the way [@PolymorphicSignature] works.
     */
    @JvmStatic
    fun <T> handleForField(clazz: Class<T>, getter: Boolean, vararg fieldNames: String): MethodHandle {
        val f = ReflectionHelper.findField(clazz, *fieldNames)
        return if (getter) publicLookup().unreflectGetter(f) else publicLookup().unreflectSetter(f)
    }

    /**
     * Reflects a constructor from a class, and provides a MethodHandle for it.
     * MethodHandles MUST be invoked from java code, due to the way [@PolymorphicSignature] works.
     */
    @JvmStatic
    fun <T> handleForConstructor(clazz: Class<T>, vararg constructorArgs: Class<*>): MethodHandle {
        val c = try {
            val m = clazz.getDeclaredConstructor(*constructorArgs)
            m.isAccessible = true
            m
        } catch (e: Exception) {
            throw ReflectionHelper.UnableToFindMethodException(arrayOf("<init>"), e)
        }

        return publicLookup().unreflectConstructor(c)
    }

    //endregion

    //region getter

    /**
     * Reflects a getter from a class, and provides a wrapper for it.
     * No casts are required to use this, although they are recommended.
     */
    @JvmStatic
    fun <T> wrapperForGetter(clazz: Class<T>, vararg fieldNames: String): (T) -> Any? {
        val handle = handleForField(clazz, true, *fieldNames)
        return wrapperForGetter(handle)
    }

    /**
     * Provides a wrapper for an existing MethodHandle getter.
     * No casts are required to use this, although they are recommended.
     */
    @JvmStatic
    fun <T> wrapperForGetter(handle: MethodHandle): (T) -> Any? {
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(1)))
        return { wrapper(it) }
    }

    @JvmStatic fun <T> wrapperForGetter(field: Field): (T) -> Any? = wrapperForGetter(publicLookup().unreflectGetter(field))

    /**
     * Reflects a static getter from a class, and provides a wrapper for it.
     * No casts are required to use this, although they are recommended.
     */
    @JvmStatic
    fun wrapperForStaticGetter(clazz: Class<*>, vararg fieldNames: String): () -> Any? {
        val handle = handleForField(clazz, true, *fieldNames)
        return wrapperForStaticGetter(handle)
    }

    /**
     * Provides a wrapper for an existing static MethodHandle getter.
     * No casts are required to use this, although they are recommended.
     */
    @JvmStatic
    fun wrapperForStaticGetter(handle: MethodHandle): () -> Any? {
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(0)))
        return { wrapper() }
    }

    @JvmStatic fun wrapperForStaticGetter(field: Field): () -> Any? = wrapperForStaticGetter(publicLookup().unreflectGetter(field))

    //endregion

    //region setter

    /**
     * Reflects a setter from a class, and provides a wrapper for it.
     */
    @JvmStatic
    fun <T : Any> wrapperForSetter(clazz: Class<T>, vararg fieldNames: String): (T, Any?) -> Unit {
        val handle = handleForField(clazz, false, *fieldNames)
        return wrapperForSetter(handle)
    }

    /**
     * Provides a wrapper for an existing MethodHandle setter.
     */
    @JvmStatic
    fun <T> wrapperForSetter(handle: MethodHandle): (T, Any?) -> Unit {
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(2)))
        return { obj, value -> wrapper(obj, value) }
    }

    @JvmStatic fun <T> wrapperForSetter(field: Field): (T, Any?) -> Unit = wrapperForSetter(publicLookup().unreflectSetter(field))

    /**
     * Reflects a static setter from a class, and provides a wrapper for it.
     */
    @JvmStatic
    fun wrapperForStaticSetter(clazz: Class<*>, vararg fieldNames: String): (Any?) -> Unit {
        val handle = handleForField(clazz, false, *fieldNames)
        return wrapperForStaticSetter(handle)
    }

    /**
     * Provides a wrapper for an existing static MethodHandle setter.
     */
    @JvmStatic
    fun wrapperForStaticSetter(handle: MethodHandle): (Any?) -> Unit {
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(1)))
        return { wrapper(it) }
    }

    @JvmStatic fun wrapperForStaticSetter(field: Field): (Any?) -> Unit = wrapperForStaticSetter(publicLookup().unreflectSetter(field))

    //endregion

    //region methods

    /**
     * Reflects a method from a class, and provides a wrapper for it.
     */
    @JvmStatic
    fun <T> wrapperForMethod(clazz: Class<T>, methodNames: Array<String>, vararg methodClasses: Class<*>): (T, Array<Any?>) -> Any? {
        val handle = handleForMethod(clazz, methodNames, *methodClasses)
        return wrapperForMethod(handle)
    }

    /**
     * Provides a wrapper for an existing MethodHandle method wrapper.
     */
    @JvmStatic
    fun <T> wrapperForMethod(handle: MethodHandle): (T, Array<Any?>) -> Any? {
        val type = handle.type()
        val count = type.parameterCount()
        var remapped = handle.asType(MethodType.genericMethodType(count))

        if (count > 1)
            remapped = remapped.asSpreader(Array<Any>::class.java, count)

        val wrapper = InvocationWrapper(remapped)
        if (count == 1)
            return { obj, args -> wrapper(obj) }

        return { obj, args -> wrapper.invokeArity(arrayOf(obj, *args)) }
    }

    @JvmStatic fun <T> wrapperForMethod(method: Method): (T, Array<Any?>) -> Any? = wrapperForMethod(publicLookup().unreflect(method))

    /**
     * Reflects a static method from a class, and provides a wrapper for it.
     */
    @JvmStatic
    fun wrapperForStaticMethod(clazz: Class<*>, methodNames: Array<String>, vararg methodClasses: Class<*>): (Array<Any?>) -> Any? {
        val handle = handleForMethod(clazz, methodNames, *methodClasses)
        return wrapperForStaticMethod(handle)
    }

    /**
     * Provides a wrapper for an existing MethodHandle method wrapper.
     */
    @JvmStatic
    fun wrapperForStaticMethod(handle: MethodHandle): (Array<Any?>) -> Any? {
        val type = handle.type()
        val count = type.parameterCount()
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(count)).asSpreader(Array<Any>::class.java, count))
        return { wrapper.invokeArity(it) }
    }

    @JvmStatic fun wrapperForStaticMethod(method: Method): (Array<Any?>) -> Any? = wrapperForStaticMethod(publicLookup().unreflect(method))

    //endregion

    //region constructors

    /**
     * Reflects a constructor from a class, and provides a wrapper for it.
     */
    @JvmStatic
    fun <T> wrapperForConstructor(clazz: Class<*>, vararg constructorArgs: Class<*>): (Array<Any?>) -> T {
        val handle = handleForConstructor(clazz, *constructorArgs)
        return wrapperForConstructor(handle)
    }

    /**
     * Provides a wrapper for an existing MethodHandle constructor wrapper.
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <T> wrapperForConstructor(handle: MethodHandle): (Array<Any?>) -> T {
        val type = handle.type()
        val count = type.parameterCount()
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(count)).asSpreader(Array<Any>::class.java, count))
        return { wrapper.invokeArity(it) as T }
    }

    @JvmStatic fun <T> wrapperForConstructor(constructor: Constructor<T>): (Array<Any?>) -> T = wrapperForConstructor(publicLookup().unreflectConstructor(constructor))

    //endregion

    //region delegates

    @JvmStatic
    fun <T, V> delegateForReadOnly(clazz: Class<T>, vararg fieldNames: String): ImmutableFieldDelegate<T, V> {
        val getter = wrapperForGetter(clazz, *fieldNames)
        return ImmutableFieldDelegate(getter)
    }

    @JvmStatic
    fun <T, V> delegateForReadWrite(clazz: Class<T>, vararg fieldNames: String): MutableFieldDelegate<T, V> {
        val getter = wrapperForGetter(clazz, *fieldNames)
        val setter = wrapperForSetter(clazz, *fieldNames)
        return MutableFieldDelegate(getter, setter)
    }

    @JvmStatic
    fun <T, V> delegateForStaticReadOnly(clazz: Class<*>, vararg fieldNames: String): ImmutableStaticFieldDelegate<T, V> {
        val getter = wrapperForStaticGetter(clazz, *fieldNames)
        return ImmutableStaticFieldDelegate(getter)
    }

    @JvmStatic
    fun <T, V> delegateForStaticReadWrite(clazz: Class<*>, vararg fieldNames: String): MutableStaticFieldDelegate<T, V> {
        val getter = wrapperForStaticGetter(clazz, *fieldNames)
        val setter = wrapperForStaticSetter(clazz, *fieldNames)
        return MutableStaticFieldDelegate(getter, setter)
    }

    //endregion
}

//region extensions

@Suppress("UNCHECKED_CAST") fun <T, V> Class<T>.mhGetter(vararg names: String): (T) -> V = MethodHandleHelper.wrapperForGetter(this, *names) as (T) -> V
@Suppress("UNCHECKED_CAST") fun <T, V> Class<T>.mhSetter(vararg names: String): (T, V) -> Unit = MethodHandleHelper.wrapperForSetter(this, *names)

@Suppress("UNCHECKED_CAST") fun <V> Class<*>.mhStaticGetter(vararg names: String): () -> V = MethodHandleHelper.wrapperForStaticGetter(this, *names) as () -> V
@Suppress("UNCHECKED_CAST") fun <V> Class<*>.mhStaticSetter(vararg names: String): (V) -> Unit = MethodHandleHelper.wrapperForStaticSetter(this, *names)

fun <T, V> Class<T>.mhValDelegate(vararg names: String) = MethodHandleHelper.delegateForReadOnly<T, V>(this, *names)
fun <T, V> Class<T>.mhVarDelegate(vararg names: String) = MethodHandleHelper.delegateForReadWrite<T, V>(this, *names)

fun <T, V> Class<T>.mhStaticValDelegate(vararg names: String) = MethodHandleHelper.delegateForStaticReadOnly<T, V>(this, *names)
fun <T, V> Class<T>.mhStaticVarDelegate(vararg names: String) = MethodHandleHelper.delegateForStaticReadWrite<T, V>(this, *names)

fun <T> Class<T>.mhMethod(names: Array<String>, vararg params: Class<*>) = MethodHandleHelper.wrapperForMethod(this, names, *params)
fun <T> Class<T>.mhStaticMethod(names: Array<String>, vararg params: Class<*>) = MethodHandleHelper.wrapperForStaticMethod(this, names, *params)


//endregion
