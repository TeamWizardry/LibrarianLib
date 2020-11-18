package com.teamwizardry.librarianlib.prism.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ArrayMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import dev.thecodewarrior.prism.SerializationException
import dev.thecodewarrior.prism.base.analysis.ArrayAnalyzer
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.ListNBT

internal class ArraySerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<Array<*>>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return ArraySerializer(prism, mirror as ArrayMirror)
    }

    class ArraySerializer(prism: NBTPrism, type: ArrayMirror): NBTSerializer<Array<Any?>>(type) {
        private val analyzer = ArrayAnalyzer<Any?, NBTSerializer<*>>(prism, type)

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(tag: INBT, existing: Array<Any?>?): Array<Any?> {
            analyzer.getReader(existing).use { state ->
                @Suppress("NAME_SHADOWING") val tag = tag.expectType<ListNBT>("tag")

                state.reserve(tag.size)
                tag.forEachIndexed { i, it ->
                    try {
                        val entry = it.expectType<CompoundNBT>("element $i")
                        if (entry.contains("V"))
                            state.add(state.serializer.read(entry.expect("V"), existing?.getOrNull(i)))
                        else
                            state.add(null)
                    } catch (e: Exception) {
                        throw DeserializationException("Error deserializing element $i", e)
                    }
                }
                return state.apply()
            }
        }

        override fun serialize(value: Array<Any?>): INBT {
            analyzer.getWriter(value).use { state ->
                val tag = ListNBT()
                state.elements.forEachIndexed { i, v ->
                    try {
                        val entry = CompoundNBT()
                        if (v != null)
                            entry.put("V", state.serializer.write(v))
                        tag.add(entry)
                    } catch (e: Exception) {
                        throw SerializationException("Error serializing element $i", e)
                    }
                }
                return tag
            }
        }
    }
}
