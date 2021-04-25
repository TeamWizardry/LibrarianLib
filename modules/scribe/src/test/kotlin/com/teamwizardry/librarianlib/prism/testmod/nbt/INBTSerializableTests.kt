package com.teamwizardry.librarianlib.prism.testmod.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.TagBuilder
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.nbt.StringNBT
import net.minecraftforge.common.util.INBTSerializable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class INBTSerializableTests: NBTPrismTest() {
    @Test
    fun `writing an INBTSerializable should return the serialized NBT`() {
        assertEquals(
            TagBuilder.string("value"),
            prism[Mirror.reflect<SerializableThing>()].value.write(SerializableThing("value"))
        )
    }

    @Test
    fun `reading an existing INBTSerializable from a tag should deserialize`() {
        val thing = SerializableThing("")
        prism[Mirror.reflect<SerializableThing>()].value.read(TagBuilder.string("value"), thing)
        assertEquals("value", thing.value)
    }

    @Test
    fun `reading an INBTSerializable from a tag with no existing value should throw`() {
        assertThrows<DeserializationException> {
            prism[Mirror.reflect<SerializableThing>()].value.read(TagBuilder.string("value"), null)
        }
    }

    class SerializableThing(var value: String): INBTSerializable<StringNBT> {
        override fun serializeNBT(): StringNBT {
            return TagBuilder.string(value)
        }

        override fun deserializeNBT(nbt: StringNBT) {
            value = nbt.string
        }
    }
}