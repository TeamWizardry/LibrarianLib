package com.teamwizardry.librarianlib.common.util

import net.minecraftforge.fml.relauncher.ReflectionHelper
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles.publicLookup
import java.lang.invoke.MethodType
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * @author WireSegal
 * Created at 6:49 PM on 8/14/16.
 */
object MethodHandleHelper {

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

    /**
     * Reflects a setter from a class, and provides a wrapper for it.
     */
    @JvmStatic
    fun <T> wrapperForSetter(clazz: Class<T>, vararg fieldNames: String): (T, Any?) -> Unit {
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
}
