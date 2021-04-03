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

internal class ObjectSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<Any>(), { type ->
    (type as? ClassMirror)?.annotations?.any { it is RefractClass } == true
}) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return ObjectSerializer(prism, mirror)
    }

    class ObjectSerializer(prism: NBTPrism, type: TypeMirror): NBTSerializer<Any>(type) {
        private val analyzer = ObjectAnalyzer<Any, NBTSerializer<*>>(prism, type.asClassMirror())

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(tag: INBT, existing: Any?): Any {
            analyzer.getReader(existing).use { state ->
                @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
                state.properties.forEach { property ->
                    try {
                        val valueTag = tag[property.name]
                        if (valueTag != null) {
                            property.value = property.serializer.read(valueTag, property.existing)
                        } else {
                            property.value = null
                        }
                    } catch (e: Exception) {
                        // TODO: if `setValue` fails it'll throw an exception with the property name already. Write a test
                        //   to fail this
                        throw DeserializationException("Property ${property.name}", e)
                    }
                }
                return state.apply()
            }
        }

        override fun serialize(value: Any): INBT {
            analyzer.getWriter(value).use { state ->
                val tag = CompoundNBT()
                state.properties.forEach { property ->
                    val v = property.value
                    if (v != null) {
                        try {
                            tag.put(property.name, property.serializer.write(v))
                        } catch (e: Exception) {
                            throw SerializationException("Property ${property.name}", e)
                        }
                    }
                }
                return tag
            }
        }
    }
}
