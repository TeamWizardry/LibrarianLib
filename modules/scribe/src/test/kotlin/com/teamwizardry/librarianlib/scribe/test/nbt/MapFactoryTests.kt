package com.teamwizardry.librarianlib.scribe.test.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.NbtBuilder
import com.teamwizardry.librarianlib.scribe.nbt.ListSerializerFactory
import com.teamwizardry.librarianlib.scribe.nbt.MapSerializerFactory
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.prism.DeserializationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MapFactoryTests: NbtPrismTest() {
    @Test
    fun `the serializer for a HashMap should be a MapSerializer`() {
        val serializer = prism[Mirror.reflect<HashMap<String, String>>()].value
        assertEquals(MapSerializerFactory.MapSerializer::class.java, serializer.javaClass)
        assertSame(Mirror.reflect<HashMap<String, String>>(), serializer.type)
    }

    @Test
    fun `read+write for HashMap should be symmetrical`() {
        simple<LinkedHashMap<String?, Int?>, MapSerializerFactory.MapSerializer>(
            linkedMapOf(
                null to 0,
                "first" to 1,
                "second" to 2,
                "third" to null,
            ),
            NbtBuilder.list {
                +compound { "V" *= int(0) }
                +compound { "K" *= string("first"); "V" *= int(1) }
                +compound { "K" *= string("second"); "V" *= int(2) }
                +compound { "K" *= string("third") }
            }
        )
    }

    @Test
    fun `reading a HashMap with the wrong NBT type should throw`() {
        assertThrows<DeserializationException> {
            prism[Mirror.reflect<HashMap<String, String>>()].value.read(NbtBuilder.string("oops!"))
        }
    }

    @Test
    fun `read+write nested maps with the same serializer should be symmetrical`() {
        @Suppress("NestedLambdaShadowedImplicitParameter")
        val list = Foo().also {
            it["first"] = Foo().also {
                it["a"] = Foo()
                it["b"] = Foo()
            }
            it["second"] = Foo().also {
                it["a"] = Foo()
                it["b"] = Foo()
                it["c"] = Foo()
            }
        }

        val targetTag = NbtBuilder.list {
            +compound {
                "K" *= string("first")
                "V" *= list {
                    +compound { "K" *= string("a"); "V" *= list {} }
                    +compound { "K" *= string("b"); "V" *= list {} }
                }
            }
            +compound {
                "K" *= string("second")
                "V" *= list {
                    +compound { "K" *= string("a"); "V" *= list {} }
                    +compound { "K" *= string("b"); "V" *= list {} }
                    +compound { "K" *= string("c"); "V" *= list {} }
                }
            }
        }

        simple<Foo, MapSerializerFactory.MapSerializer>(list, targetTag)
    }

    class Foo: LinkedHashMap<String, Foo>()
}