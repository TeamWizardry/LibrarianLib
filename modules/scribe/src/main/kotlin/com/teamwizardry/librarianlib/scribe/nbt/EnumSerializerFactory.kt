package com.teamwizardry.librarianlib.scribe.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.nbt.Tag
import net.minecraft.nbt.StringTag

internal class EnumSerializerFactory(prism: NbtPrism): NBTSerializerFactory(prism, Mirror.reflect<Enum<*>>(), { type ->
    (type as? ClassMirror)?.enumType != null
}) {
    override fun create(mirror: TypeMirror): NbtSerializer<*> {
        return EnumSerializer(prism, mirror.asClassMirror().enumType!!)
    }

    class EnumSerializer(prism: NbtPrism, type: TypeMirror): NbtSerializer<Enum<*>>(type) {
        private val cases: Map<String, Enum<*>> = type.asClassMirror().enumConstants!!.associateBy { it.name }

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(tag: Tag, existing: Enum<*>?): Enum<*> {
            val name = tag.expectType<StringTag>("tag").asString()
            return cases[name] ?: throw DeserializationException("Unknown enum case name '$name'")
        }

        override fun serialize(value: Enum<*>): Tag {
            return StringTag.of(value.name)
        }
    }
}
