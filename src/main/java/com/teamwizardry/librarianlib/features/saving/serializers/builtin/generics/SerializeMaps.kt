package com.teamwizardry.librarianlib.features.saving.serializers.builtin.generics

import com.teamwizardry.librarianlib.features.kotlin.forEachIndexed
import com.teamwizardry.librarianlib.features.kotlin.readVarInt
import com.teamwizardry.librarianlib.features.kotlin.safeCast
import com.teamwizardry.librarianlib.features.kotlin.writeVarInt
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.saving.FieldTypeGeneric
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.features.saving.serializers.builtin.Targets
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
        SerializerRegistry.register("java:generator.map", Serializer(HashMap::class.java, LinkedHashMap::class.java))

        SerializerRegistry["java:generator.map"]?.register(Targets.NBT, { type ->
            type as FieldTypeGeneric
            val keyParam = type.generic(0)!!
            val valueParam = type.generic(1)!!
            val keySerializer = SerializerRegistry.lazyImpl(Targets.NBT, keyParam)
            val valueSerializer = SerializerRegistry.lazyImpl(Targets.NBT, valueParam)

            val constructorMH = MethodHandleHelper.wrapperForConstructor<MutableMap<Any?, Any?>>(type.clazz)

            Targets.NBT.impl<MutableMap<*, *>>({ nbt, existing, syncing ->
                val list = nbt.safeCast(NBTTagList::class.java)
                val map = constructorMH(arrayOf())

                list.forEachIndexed<NBTTagCompound> { i, container ->
                    val keyTag = container.getTag("k")
                    val valTag = container.getTag("v")
                    val k = if (keyTag == null) null else keySerializer().read(keyTag, null, syncing)
                    val v = if (valTag == null) null else valueSerializer().read(valTag, existing?.get(k), syncing)
                    map[k] = v
                }

                map
            }, { value, syncing ->
                val list = NBTTagList()

                for (k in value.keys) {
                    val container = NBTTagCompound()
                    list.appendTag(container)
                    val v = value[k]

                    if (k != null) {
                        container.setTag("k", keySerializer().write(k, syncing))
                    }
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

            val constructorMH = MethodHandleHelper.wrapperForConstructor<MutableMap<Any?, Any?>>(type.clazz)

            Targets.BYTES.impl<MutableMap<*, *>>({ buf, existing, syncing ->
                val map = constructorMH(arrayOf())

                val nullCount = buf.readVarInt()
                for (i in 0..nullCount - 1) {
                    val k = keySerializer().read(buf, null, syncing)
                    map[k] = null
                }

                val hasNullKey = buf.readBoolean()
                if (hasNullKey) {
                    val isNullValue = buf.readBoolean()
                    map[null] = if (isNullValue) null else valueSerializer().read(buf, existing?.get(null), syncing)
                }

                val nonNullCount = buf.readVarInt()
                for (i in 0..nonNullCount - 1) {
                    val k = keySerializer().read(buf, null, syncing)
                    val v = valueSerializer().read(buf, existing?.get(k), syncing)
                    map[k] = v
                }

                map
            }, { buf, value, syncing ->
                val nulls = value.filter { it.value == null && it.key != null }
                buf.writeVarInt(nulls.count { it.key != null })
                nulls.forEach { keySerializer().write(buf, it.key!!, syncing) }

                buf.writeBoolean(value.containsKey(null))
                if (value.containsKey(null)) {
                    buf.writeBoolean(value[null] == null)
                    if (value[null] != null)
                        valueSerializer().write(buf, value[null]!!, syncing)
                }

                val nonNulls = value.filter { it.value != null && it.key != null }
                buf.writeVarInt(nonNulls.size)
                nonNulls.forEach {
                    keySerializer().write(buf, it.key!!, syncing)
                    valueSerializer().write(buf, it.value!!, syncing)
                }
            })
        })
    }
}
