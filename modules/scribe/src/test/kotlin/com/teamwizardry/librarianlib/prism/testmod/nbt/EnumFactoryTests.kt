package com.teamwizardry.librarianlib.prism.testmod.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.TagBuilder
import com.teamwizardry.librarianlib.prism.nbt.EnumSerializerFactory
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.prism.DeserializationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class EnumFactoryTests: NBTPrismTest() {
    @Test
    fun `the serializer for an enum should be an EnumSerializer`() {
        val serializer = prism[Mirror.reflect<TestEnum>()].value
        assertEquals(EnumSerializerFactory.EnumSerializer::class.java, serializer.javaClass)
        assertSame(Mirror.reflect<TestEnum>(), serializer.type)
    }

    @Test
    fun `the serializer for an enum subclass should be the enclosing EnumSerializer`() {
        val serializer = prism[Mirror.reflect(TestEnum.THREE.javaClass)].value
        assertEquals(EnumSerializerFactory.EnumSerializer::class.java, serializer.javaClass)
        assertSame(Mirror.reflect<TestEnum>(), serializer.type)
    }

    @Test
    fun `read+write for enum should be symmetrical`() {
        simple<TestEnum, EnumSerializerFactory.EnumSerializer>(TestEnum.ONE, TagBuilder.string("ONE"))
    }

    @Test
    fun `reading a non-existent enum case name should throw`() {
        val serializer = prism[Mirror.reflect<TestEnum>()].value
        assertThrows<DeserializationException> {
            serializer.read(TagBuilder.string("OOPS"), null)
        }
    }

    enum class TestEnum {
        ONE,
        TWO,
        THREE {
            fun makeMeASubclass() {

            }
        };
    }
}

