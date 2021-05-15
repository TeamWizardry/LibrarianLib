package com.teamwizardry.librarianlib.scribe.test.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.TagBuilder
import com.teamwizardry.librarianlib.scribe.nbt.ArraySerializerFactory
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.prism.DeserializationException
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ArrayFactoryTests: NbtPrismTest() {
    @Test
    fun `the serializer for an array type should be an ArraySerializer`() {
        val serializer = prism[Mirror.reflect<Array<String>>()].value
        assertEquals(ArraySerializerFactory.ArraySerializer::class.java, serializer.javaClass)
        assertSame(Mirror.reflect<Array<String>>(), serializer.type)
    }

    @Test
    fun `read+write for an array with nulls should be symmetrical`() {
        simple<Array<String?>, ArraySerializerFactory.ArraySerializer>(
            arrayOf("first", "second", null, "fourth"),
            TagBuilder.list {
                +compound { "V" *= string("first") }
                +compound { "V" *= string("second") }
                +compound {}
                +compound { "V" *= string("fourth") }
            }
        )
    }

    @Test
    fun `reading an array with an existing value with the same size should fill the existing array`() {
        val targetArray = arrayOf("value")

        val theArray = arrayOf<String?>(null)
        val theNode = TagBuilder.list {
            +compound { "V" *= string("value") }
        }
        val deserialized = prism[Mirror.reflect<Array<String?>>()].value.read(theNode, theArray)


        assertSame(theArray, deserialized)
        assertEquals(Array<String>::class.java, deserialized.javaClass)
        @Suppress("UNCHECKED_CAST")
        assertArrayEquals(targetArray, deserialized as Array<String>)
    }

    @Test
    fun `reading an array with an existing value with the wrong size should create a new array`() {
        val targetArray = arrayOf("value")

        val theArray = arrayOf<String?>(null, null)
        val theNode = TagBuilder.list {
            +compound { "V" *= string("value") }
        }
        val deserialized = prism[Mirror.reflect<Array<String?>>()].value.read(theNode, theArray)

        assertNotSame(theArray, deserialized)
        assertEquals(Array<String>::class.java, deserialized.javaClass)
        @Suppress("UNCHECKED_CAST")
        assertArrayEquals(targetArray, deserialized as Array<String?>)
    }

    @Test
    fun `reading an array with no existing value should create a new array`() {
        val targetArray = arrayOf("value")

        val theNode = TagBuilder.list {
            +compound { "V" *= string("value") }
        }
        val deserialized = prism[Mirror.reflect<Array<String?>>()].value.read(theNode, null)

        assertEquals(Array<String>::class.java, deserialized.javaClass)
        @Suppress("UNCHECKED_CAST")
        assertArrayEquals(targetArray, deserialized as Array<String?>)
    }

    @Test
    fun `reading an array with the wrong NBT type should throw`() {
        assertThrows<DeserializationException> {
            prism[Mirror.reflect<Array<String?>>()].value.read(TagBuilder.string("oops!"), null)
        }
    }

    @Test
    fun `reading an array with the wrong ListNBT element type should throw`() {
        assertThrows<DeserializationException> {
            prism[Mirror.reflect<Array<String?>>()].value.read(TagBuilder.list { +string("oops!") }, null)
        }
    }
}