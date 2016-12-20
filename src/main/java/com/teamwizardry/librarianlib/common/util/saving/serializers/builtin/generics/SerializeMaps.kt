package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.generics

import com.teamwizardry.librarianlib.common.util.forEachIndexed
import com.teamwizardry.librarianlib.common.util.readVarInt
import com.teamwizardry.librarianlib.common.util.safeCast
import com.teamwizardry.librarianlib.common.util.saving.FieldTypeGeneric
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.Targets
import com.teamwizardry.librarianlib.common.util.writeVarInt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import java.util.*
import kotlin.collections.set

/**
 * Created by TheCodeWarrior
 */
object SerializeMaps {
    // check for map interface and 0 arg constructor
    init {
        SerializerRegistry.register("java:generator.map", Serializer(HashMap::class.java))

        SerializerRegistry["java:generator.map"]?.register(Targets.NBT, { type ->
            type as FieldTypeGeneric
            val keyParam = type.generic(0)!!
            val valueParam = type.generic(1)!!
            val keySerializer = SerializerRegistry.lazyImpl(Targets.NBT, keyParam)
            val valueSerializer = SerializerRegistry.lazyImpl(Targets.NBT, valueParam)

            Targets.NBT.impl<HashMap<*,*>>({ nbt, existing, syncing ->
                val list = nbt.safeCast(NBTTagList::class.java)
                val map = HashMap<Any?, Any?>()

                list.forEachIndexed<NBTTagCompound> { i, container ->
                    val keyTag = container.getTag("k")
                    val valTag = container.getTag("v")
                    val k = keySerializer().read(keyTag, null, syncing)
                    val v = if (valTag == null) null else valueSerializer().read(valTag, existing?.get(k), syncing)
                    map.set(k, v)
                }

                map
            }, { value, syncing ->
                val list = NBTTagList()

                for (i in value.keys) {
                    val container = NBTTagCompound()
                    list.appendTag(container)
                    val v = value[i]
                    container.setTag("k", keySerializer().write(i, syncing))
                    if (v != null) {
                        container.setTag("v", valueSerializer().write(v, syncing))
                    }
                }

                list
            })
        })

        SerializerRegistry["java:generator.map"]?.register(Targets.BYTES, { type ->
            type as FieldTypeGeneric
            val keyParam = type.generic(0)!!
            val valueParam = type.generic(1)!!
            val keySerializer = SerializerRegistry.lazyImpl(Targets.BYTES, keyParam)
            val valueSerializer = SerializerRegistry.lazyImpl(Targets.BYTES, valueParam)

            Targets.BYTES.impl<HashMap<*,*>>({ buf, existing, syncing ->
                val map = HashMap<Any?, Any?>()

                val nullCount = buf.readVarInt()
                for(i in 0..nullCount-1) {
                    val k = keySerializer().read(buf, null, syncing)
                    map[k] = null
                }

                var nonNullCount = buf.readVarInt()
                for(i in 0..nonNullCount-1) {
                    val k = keySerializer().read(buf, null, syncing)
                    val v = valueSerializer().read(buf, existing?.get(k), syncing)
                    map[k] = v
                }

                map
            }, { buf, value, syncing ->
                val nulls = value.filter { it.value == null }
                buf.writeVarInt(nulls.size)
                nulls.forEach { keySerializer().write(buf, it.key, syncing) }

                val nonNulls = value.filter { it.value != null}
                buf.writeVarInt(nonNulls.size)
                nonNulls.forEach {
                    keySerializer().write(buf, it.key, syncing)
                    valueSerializer().write(buf, it.value, syncing)
                }
            })
        })
    }
}
