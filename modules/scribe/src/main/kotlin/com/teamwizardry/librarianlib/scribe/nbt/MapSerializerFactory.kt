package com.teamwizardry.librarianlib.scribe.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import dev.thecodewarrior.prism.base.analysis.ListAnalyzer
import dev.thecodewarrior.prism.base.analysis.MapAnalyzer
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList

internal class MapSerializerFactory(prism: NbtPrism): NBTSerializerFactory(prism, Mirror.reflect<Map<*, *>>()) {
    override fun create(mirror: TypeMirror): NbtSerializer<*> {
        return MapSerializer(prism, mirror as ClassMirror)
    }

    class MapSerializer(prism: NbtPrism, type: ClassMirror): NbtSerializer<MutableMap<Any?, Any?>>(type) {
        private val analyzer = MapAnalyzer<Any?, Any?, NbtSerializer<*>>(prism, type)

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(tag: NbtElement): MutableMap<Any?, Any?> {
            analyzer.getReader().use { state ->
                @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtList>("tag")
                tag.forEachIndexed { i, it ->
                    try {
                        val entry = it.expectType<NbtCompound>("entry $i")
                        state.put(
                            if(entry.contains("K")) state.keySerializer.read(entry.expect("K")) else null,
                            if(entry.contains("V")) state.valueSerializer.read(entry.expect("V")) else null
                        )
                    } catch (e: Exception) {
                        throw DeserializationException("Deserializing entry $i", e)
                    }
                }
                return state.build() as MutableMap<Any?, Any?>
            }
        }

        override fun serialize(value: MutableMap<Any?, Any?>): NbtElement {
            analyzer.getWriter(value).use { state ->
                val tag = NbtList()
                for(k in state.keys) {
                    val v = state.get(k)
                    val entry = NbtCompound()
                    if(k != null)
                        entry.put("K", state.keySerializer.write(k))
                    if(v != null)
                        entry.put("V", state.valueSerializer.write(v))
                    tag.add(entry)
                }
                return tag
            }
        }
    }
}
