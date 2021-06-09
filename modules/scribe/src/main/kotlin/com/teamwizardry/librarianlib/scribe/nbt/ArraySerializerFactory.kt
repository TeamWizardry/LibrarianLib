package com.teamwizardry.librarianlib.scribe.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ArrayMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import dev.thecodewarrior.prism.SerializationException
import dev.thecodewarrior.prism.base.analysis.ArrayAnalyzer
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList

internal class ArraySerializerFactory(prism: NbtPrism): NBTSerializerFactory(prism, Mirror.reflect<Array<*>>()) {
    override fun create(mirror: TypeMirror): NbtSerializer<*> {
        return ArraySerializer(prism, mirror as ArrayMirror)
    }

    class ArraySerializer(prism: NbtPrism, type: ArrayMirror): NbtSerializer<Array<Any?>>(type) {
        private val analyzer = ArrayAnalyzer<Any?, NbtSerializer<*>>(prism, type)

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(tag: NbtElement, existing: Array<Any?>?): Array<Any?> {
            analyzer.getReader(existing).use { state ->
                @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtList>("tag")

                state.reserve(tag.size)
                tag.forEachIndexed { i, it ->
                    try {
                        val entry = it.expectType<NbtCompound>("element $i")
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

        override fun serialize(value: Array<Any?>): NbtElement {
            analyzer.getWriter(value).use { state ->
                val tag = NbtList()
                state.elements.forEachIndexed { i, v ->
                    try {
                        val entry = NbtCompound()
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
