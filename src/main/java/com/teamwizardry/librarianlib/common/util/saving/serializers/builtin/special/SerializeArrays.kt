package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.special

import com.teamwizardry.librarianlib.common.util.forEachIndexed
import com.teamwizardry.librarianlib.common.util.readBooleanArray
import com.teamwizardry.librarianlib.common.util.safeCast
import com.teamwizardry.librarianlib.common.util.saving.ArrayReflect
import com.teamwizardry.librarianlib.common.util.saving.FieldTypeArray
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerPriority
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.Targets
import com.teamwizardry.librarianlib.common.util.writeBooleanArray
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

/**
 * Created by TheCodeWarrior
 */
object SerializeArrays {
    init {
        SerializerRegistry.register("java:generator.array", Serializer({ type -> type is FieldTypeArray }, SerializerPriority.GENERAL))

        SerializerRegistry["java:generator.array"]?.register(Targets.NBT, { type ->
            type as FieldTypeArray
            val subSerializer = SerializerRegistry.lazyImpl(Targets.NBT, type.componentType)

            Targets.NBT.impl<Array<*>>({ nbt, existing, syncing ->
                val list = nbt.safeCast(NBTTagList::class.java)

                val reuse = existing != null && existing.size == list.tagCount()
                @Suppress("UNCHECKED_CAST")
                val array = if (reuse) existing as Array<Any?> else ArrayReflect.newInstanceRaw(type.componentType.clazz, list.tagCount())

                list.forEachIndexed<NBTTagCompound> { i, container ->
                    val tag = container.getTag("-")
                    if (tag == null)
                        array[i] = null
                    else
                        array[i] = subSerializer().read(tag, if (reuse) array[i] else null, syncing)
                }

                array
            }, { value, syncing ->
                val list = NBTTagList()

                val len = ArrayReflect.getLength(value)

                for (i in 0..len - 1) {
                    val v = ArrayReflect.get(value, i)
                    val container = NBTTagCompound()
                    list.appendTag(container)
                    if (v != null) {
                        container.setTag("-", subSerializer().write(v, syncing))
                    }
                }

                list
            })
        })

        SerializerRegistry["java:generator.array"]?.register(Targets.BYTES, { type ->
            type as FieldTypeArray
            val subSerializer = SerializerRegistry.lazyImpl(Targets.BYTES, type.componentType)

            Targets.BYTES.impl<Array<*>>({ buf, existing, syncing ->
                val nullsig = buf.readBooleanArray()
                val reuse = existing != null && existing.size == nullsig.size
                @Suppress("UNCHECKED_CAST")
                val array = if (reuse) existing as Array<Any?> else ArrayReflect.newInstanceRaw(type.componentType.clazz, nullsig.size)

                for (i in 0..nullsig.size - 1) {
                    array[i] = if (nullsig[i]) null else subSerializer().read(buf, array[i], syncing)
                }
                array
            }, { buf, value, syncing ->
                val len = ArrayReflect.getLength(value)
                val nullsig = BooleanArray(len) { ArrayReflect.get(value, it) == null }
                buf.writeBooleanArray(nullsig)
                for (i in 0..len - 1) {
                    if (!nullsig[i])
                        subSerializer().write(buf, ArrayReflect.get(value, i), syncing)
                }
            })
        })
    }
}
