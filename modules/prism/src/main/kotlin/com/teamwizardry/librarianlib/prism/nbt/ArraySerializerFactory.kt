package com.teamwizardry.librarianlib.prism.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ArrayMirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import dev.thecodewarrior.prism.base.analysis.ArrayAnalyzer
import dev.thecodewarrior.prism.base.analysis.ListAnalyzer
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.ListNBT
import net.minecraft.nbt.NumberNBT

open class ArraySerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<Array<*>>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return ArraySerializer(prism, mirror as ArrayMirror)
    }

    class ArraySerializer(prism: NBTPrism, type: ArrayMirror): NBTSerializer<Array<Any?>>(type) {
        val analyzer = ArrayAnalyzer<Any?, NBTSerializer<*>>(prism, type)

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(tag: INBT, existing: Array<Any?>?): Array<Any?> {
            val state = analyzer.getState()
            @Suppress("NAME_SHADOWING") val tag = tag.expectType<ListNBT>("tag")

            state.reserve(tag.size)
            tag.forEachIndexed { i, it ->
                try {
                    val entry = it.expectType<CompoundNBT>("element $i")
                    if(entry.contains("V"))
                        state.add(analyzer.elementSerializer.read(entry.expect("V"), existing?.getOrNull(i)))
                    else
                        state.add(null)
                } catch(e: Exception) {
                    throw DeserializationException("Error deserializing element $i", e)
                }
            }
            val newValue = state.apply(existing)
            analyzer.releaseState(state)
            return newValue
        }

        override fun serialize(value: Array<Any?>): INBT {
            val state = analyzer.getState()
            val tag = ListNBT()
            state.populate(value)
            state.buffer.forEachIndexed { index, v ->
                val entry = CompoundNBT()
                if(v != null)
                    entry.put("V", analyzer.elementSerializer.write(v))
                tag.add(entry)
            }
            analyzer.releaseState(state)
            return tag
        }
    }
}
