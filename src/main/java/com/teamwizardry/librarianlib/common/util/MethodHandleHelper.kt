package com.teamwizardry.librarianlib.common.util

import net.minecraftforge.fml.relauncher.ReflectionHelper
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles.publicLookup
import java.lang.invoke.MethodType

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

    @JvmStatic
    fun <T> wrapperForGetter(clazz: Class<T>, vararg fieldNames: String): (T) -> Any? {
        val handle = handleForField(clazz, true, *fieldNames)
        return wrapperForGetter(handle)
    }

    @JvmStatic
    fun <T> wrapperForGetter(handle: MethodHandle): (T) -> Any? {
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(1)))
        return { wrapper(it) }
    }

    @JvmStatic
    fun wrapperForStaticGetter(clazz: Class<*>, vararg fieldNames: String): () -> Any? {
        val handle = handleForField(clazz, true, *fieldNames)
        return wrapperForStaticGetter(handle)
    }


    @JvmStatic
    fun wrapperForStaticGetter(handle: MethodHandle): () -> Any? {
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(0)))
        return { wrapper() }
    }

    @JvmStatic
    fun <T> wrapperForSetter(clazz: Class<T>, vararg fieldNames: String): (T, Any?) -> Unit {
        val handle = handleForField(clazz, false, *fieldNames)
        return wrapperForSetter(handle)
    }

    @JvmStatic
    fun <T> wrapperForSetter(handle: MethodHandle): (T, Any?) -> Unit {
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(2)))
        return { obj, value -> wrapper(obj, value) }
    }

    @JvmStatic
    fun wrapperForStaticSetter(clazz: Class<*>, vararg fieldNames: String): (Any?) -> Unit {
        val handle = handleForField(clazz, false, *fieldNames)
        return wrapperForStaticSetter(handle)
    }

    @JvmStatic
    fun wrapperForStaticSetter(handle: MethodHandle): (Any?) -> Unit {
        val wrapper = InvocationWrapper(handle.asType(MethodType.genericMethodType(1)))
        return { wrapper(it) }
    }
}
