package com.teamwizardry.librarianlib.prism.testmod.nbt

import com.teamwizardry.librarianlib.prism.nbt.NBTPrism
import com.teamwizardry.librarianlib.prism.NBTPrism
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.TypeMirror
import net.minecraft.nbt.INBT
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import java.lang.RuntimeException

abstract class NBTPrismTest {
    val prism: NBTPrism = NBTPrism

    /**
     * Does some simple assertions for reading/writing:
     * - the serializer's type is [S]
     * - [value], when serialized, results in [tag]
     * - [tag], when deserialized, results in [value]
     */
    inline fun<reified S> simple(type: TypeMirror, value: Any, tag: INBT) {
        val serializer = prism[type].value
        assertEquals(S::class.java, serializer.javaClass)
        val serialized = serializer.write(value)
        assertEquals(serialized, tag)
        val deserialized = serializer.read(serialized, null)
        assertEqualsOrArrayEquals(deserialized, value)
    }

    /**
     * Does some simple assertions for reading/writing:
     * - the serializer's type is [S]
     * - [value], when serialized, results in [tag]
     * - [tag], when deserialized, results in [value]
     */
    inline fun<reified T: Any, reified S> simple(value: T, tag: INBT) {
        val serializer = prism[Mirror.reflect<T>()].value
        assertEquals(S::class.java, serializer.javaClass)
        val serialized = serializer.write(value)
        assertEquals(tag, serialized)
        val deserialized = serializer.read(serialized, null)
        assertEqualsOrArrayEquals(value, deserialized)
    }

    /**
     * Does some simple assertions for reading:
     * - the serializer's type is [S]
     * - [tag], when deserialized, results in [value]
     */
    inline fun<reified S> simpleRead(type: TypeMirror, value: Any, tag: INBT) {
        val serializer = prism[type].value
        assertEquals(S::class.java, serializer.javaClass)
        val deserialized = serializer.read(tag, null)
        assertEqualsOrArrayEquals(value, deserialized)
    }

    /**
     * Does some simple assertions for reading:
     * - the serializer's type is [S]
     * - [tag], when deserialized, results in [value]
     */
    inline fun<reified T: Any, reified S> simpleRead(value: T, tag: INBT) {
        val serializer = prism[Mirror.reflect<T>()].value
        assertEquals(S::class.java, serializer.javaClass)
        val deserialized = serializer.read(tag, null)
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