package com.teamwizardry.librarianlib.scribe.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import dev.thecodewarrior.prism.base.analysis.ListAnalyzer
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList

internal class ListSerializerFactory(prism: NbtPrism): NBTSerializerFactory(prism, Mirror.reflect<List<*>>()) {
    override fun create(mirror: TypeMirror): NbtSerializer<*> {
        return ListSerializer(prism, mirror as ClassMirror)
    }

    class ListSerializer(prism: NbtPrism, type: ClassMirror): NbtSerializer<MutableList<Any?>>(type) {
        private val analyzer = ListAnalyzer<Any?, NbtSerializer<*>>(prism, type)

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(tag: NbtElement): MutableList<Any?> {
            analyzer.getReader().use { state ->
                @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtList>("tag")
                state.reserve(tag.size)
                tag.forEachIndexed { i, it ->
                    try {
                        val entry = it.expectType<NbtCompound>("element $i")
                        if (entry.contains("V"))
                            state.add(state.serializer.read(entry.expect("V")))
                        else
                            state.add(null)
                    } catch (e: Exception) {
                        throw DeserializationException("Deserializing element $i", e)
                    }
                }
                return state.build() as MutableList<Any?>
            }
        }

        override fun serialize(value: MutableList<Any?>): NbtElement {
            analyzer.getWriter(value).use { state ->
                val tag = NbtList()
                for(i in 0 until state.size) {
                    val v = state.get(i)
                    val entry = NbtCompound()
                    if (v != null)
                        entry.put("V", state.serializer.write(v))
                    tag.add(entry)
                }
                return tag
            }
        }
    }
}
