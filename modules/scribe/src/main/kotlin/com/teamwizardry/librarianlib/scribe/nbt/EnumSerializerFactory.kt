package com.teamwizardry.librarianlib.scribe.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString

internal class EnumSerializerFactory(prism: NbtPrism): NBTSerializerFactory(prism, Mirror.reflect<Enum<*>>(), { type ->
    (type as? ClassMirror)?.enumType != null
}) {
    override fun create(mirror: TypeMirror): NbtSerializer<*> {
        return EnumSerializer(prism, mirror.asClassMirror().enumType!!)
    }

    class EnumSerializer(prism: NbtPrism, type: TypeMirror): NbtSerializer<Enum<*>>(type) {
        private val cases: Map<String, Enum<*>> = type.asClassMirror().enumConstants!!.associateBy { it.name }

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(tag: NbtElement, existing: Enum<*>?): Enum<*> {
            val name = tag.expectType<NbtString>("tag").asString()
            return cases[name] ?: throw DeserializationException("Unknown enum case name '$name'")
        }

        override fun serialize(value: Enum<*>): NbtElement {
            return NbtString.of(value.name)
        }
    }
}
