package com.teamwizardry.librarianlib.prism.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import dev.thecodewarrior.prism.SerializationException
import dev.thecodewarrior.prism.annotation.RefractClass
import dev.thecodewarrior.prism.base.analysis.auto.ObjectAnalyzer
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.StringNBT

internal class EnumSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<Enum<*>>(), { type ->
    (type as? ClassMirror)?.enumType != null
}) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return EnumSerializer(prism, mirror.asClassMirror().enumType!!)
    }

    class EnumSerializer(prism: NBTPrism, type: TypeMirror): NBTSerializer<Enum<*>>(type) {
        private val cases: Map<String, Enum<*>> = type.asClassMirror().enumConstants!!.associateBy { it.name }

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(tag: INBT, existing: Enum<*>?): Enum<*> {
            val name = tag.expectType<StringNBT>("tag").string
            return cases[name] ?: throw DeserializationException("Unknown enum case name '$name'")
        }

        override fun serialize(value: Enum<*>): INBT {
            return StringNBT.valueOf(value.name)
        }
    }
}
