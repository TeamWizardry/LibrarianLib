package com.teamwizardry.librarianlib.scribe.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import dev.thecodewarrior.prism.base.analysis.ListAnalyzer
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.ListTag

internal class ListSerializerFactory(prism: NbtPrism): NBTSerializerFactory(prism, Mirror.reflect<List<*>>()) {
    override fun create(mirror: TypeMirror): NbtSerializer<*> {
        return ListSerializer(prism, mirror as ClassMirror)
    }

    class ListSerializer(prism: NbtPrism, type: ClassMirror): NbtSerializer<MutableList<Any?>>(type) {
        private val analyzer = ListAnalyzer<Any?, NbtSerializer<*>>(prism, type)

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(tag: Tag, existing: MutableList<Any?>?): MutableList<Any?> {
            analyzer.getReader(existing).use { state ->
                @Suppress("NAME_SHADOWING") val tag = tag.expectType<ListTag>("tag")
                state.reserve(tag.size)
                tag.forEachIndexed { i, it ->
                    try {
                        val entry = it.expectType<CompoundTag>("element $i")
                        if (entry.contains("V"))
                            state.add(state.serializer.read(entry.expect("V"), existing?.getOrNull(i)))
                        else
                            state.add(null)
                    } catch (e: Exception) {
                        throw DeserializationException("Deserializing element $i", e)
                    }
                }
                return state.apply()
            }
        }

        override fun serialize(value: MutableList<Any?>): Tag {
            analyzer.getWriter(value).use { state ->
                val tag = ListTag()
                state.elements.forEach { v ->
                    val entry = CompoundTag()
                    if (v != null)
                        entry.put("V", state.serializer.write(v))
                    tag.add(entry)
                }
                return tag
            }
        }
    }
}
