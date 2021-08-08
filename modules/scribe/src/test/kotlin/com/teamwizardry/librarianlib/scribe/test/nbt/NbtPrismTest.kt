package com.teamwizardry.librarianlib.scribe.test.nbt

import com.teamwizardry.librarianlib.scribe.nbt.NbtPrism
import com.teamwizardry.librarianlib.scribe.Scribe
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.TypeMirror
import net.minecraft.nbt.NbtElement
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals

abstract class NbtPrismTest {
    val prism: NbtPrism = Scribe.nbt

    /**
     * Does some simple assertions for reading/writing:
     * - the serializer's type is [S]
     * - [value], when serialized, results in [tag]
     * - [tag], when deserialized, results in [value]
     */
    inline fun<reified S> simple(type: TypeMirror, value: Any, tag: NbtElement, noinline equality: ((Any, Any) -> Boolean)? = null) {
        val serializer = prism[type].value
        assertEquals(S::class.java, serializer.javaClass)
        val serialized = serializer.write(value)
        assertEquals(serialized, tag)
        val deserialized = serializer.read(serialized, null)
        // if an equality callback isn't provided, use assertEquals for the equals check
        // if an equality callback is provided, use assertEquals for the error message
        if(equality == null || !equality(value, deserialized))
            assertEqualsOrArrayEquals(value, deserialized)
    }

    /**
     * Does some simple assertions for reading/writing:
     * - the serializer's type is [S]
     * - [value], when serialized, results in [tag]
     * - [tag], when deserialized, results in [value]
     */
    inline fun<reified T: Any, reified S> simple(value: T, tag: NbtElement, noinline equality: ((T, T) -> Boolean)? = null) {
        val serializer = prism[Mirror.reflect<T>()].value
        assertEquals(S::class.java, serializer.javaClass)
        val serialized = serializer.write(value)
        assertEquals(tag, serialized)
        val deserialized = serializer.read(serialized, null)
        // if an equality callback isn't provided, use assertEquals for the equals check
        // if an equality callback is provided, use assertEquals for the error message
        if(equality == null || !(deserialized is T && equality(value, deserialized)))
            assertEqualsOrArrayEquals(value, deserialized)
    }

    /**
     * Does some simple assertions for reading:
     * - the serializer's type is [S]
     * - [tag], when deserialized, results in [value]
     */
    inline fun<reified S> simpleRead(type: TypeMirror, value: Any, tag: NbtElement, noinline equality: ((Any, Any) -> Boolean)? = null) {
        val serializer = prism[type].value
        assertEquals(S::class.java, serializer.javaClass)
        val deserialized = serializer.read(tag, null)
        // if an equality callback isn't provided, use assertEquals for the equals check
        // if an equality callback is provided, use assertEquals for the error message
        if(equality == null || !equality(value, deserialized))
            assertEqualsOrArrayEquals(value, deserialized)
    }

    /**
     * Does some simple assertions for reading:
     * - the serializer's type is [S]
     * - [tag], when deserialized, results in [value]
     */
    inline fun<reified T: Any, reified S> simpleRead(value: T, tag: NbtElement, noinline equality: ((T, T) -> Boolean)? = null) {
        val serializer = prism[Mirror.reflect<T>()].value
        assertEquals(S::class.java, serializer.javaClass)
        val deserialized = serializer.read(tag, null)
        // if an equality callback isn't provided, use assertEquals for the equals check
        // if an equality callback is provided, use assertEquals for the error message
        if(equality == null || !(deserialized is T && equality(value, deserialized)))
            assertEqualsOrArrayEquals(value, deserialized)
    }

    fun assertEqualsOrArrayEquals(expected: Any?, actual: Any?) {
        if(expected != null && actual != null && expected.javaClass.isAssignableFrom(actual.javaClass))
            when(expected) {
                is Array<*> -> assertArrayEquals(expected, actual as Array<*>)
                is DoubleArray -> assertArrayEquals(expected, actual as DoubleArray)
                is FloatArray -> assertArrayEquals(expected, actual as FloatArray)
                is LongArray -> assertArrayEquals(expected, actual as LongArray)
                is IntArray -> assertArrayEquals(expected, actual as IntArray)
                is ShortArray -> assertArrayEquals(expected, actual as ShortArray)
                is ByteArray -> assertArrayEquals(expected, actual as ByteArray)
                is CharArray -> assertArrayEquals(expected, actual as CharArray)
                is BooleanArray -> assertArrayEquals(expected, actual as BooleanArray)
                else -> assertEquals(expected, actual)
            }
        else
            assertEquals(expected, actual)
    }
}