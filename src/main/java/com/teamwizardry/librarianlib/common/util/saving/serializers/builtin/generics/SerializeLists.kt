package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.generics

import com.teamwizardry.librarianlib.common.util.*
import com.teamwizardry.librarianlib.common.util.saving.FieldTypeGeneric
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.Targets
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import java.util.*

/**
 * Created by TheCodeWarrior
 */
object SerializeLists {
    // check for list interface and 0 arg constructor
    init {
        SerializerRegistry.register("java:generator.list", Serializer(ArrayList::class.java, LinkedList::class.java))

        SerializerRegistry["java:generator.list"]?.register(Targets.NBT, { type ->
            type as FieldTypeGeneric
            val typeParam = type.generic(0)!!
            val subSerializer = SerializerRegistry.lazyImpl(Targets.NBT, typeParam)

            @Suppress("UNCHECKED_CAST")
            val constructorMH = MethodHandleHelper.wrapperForConstructor(type.clazz.getConstructor()) as (Array<Any>) -> MutableList<Any?>

            Targets.NBT.impl<MutableList<*>>({ nbt, existing, syncing ->
                val list = nbt.safeCast(NBTTagList::class.java)

                @Suppress("UNCHECKED_CAST")
                val array = (existing ?: constructorMH(arrayOf())) as MutableList<Any?>

                while (array.size > list.tagCount())
                    array.removeAt(array.size - 1)

                list.forEachIndexed<NBTTagCompound> { i, container ->
                    val tag = container.getTag("-")
                    val v = if (tag == null) null else subSerializer().read(tag, array.getOrNull(i), syncing)
                    if (i >= array.size) {
                        array.add(v)
                    } else {
                        array.set(i, v)
                    }
                }

                array
            }, { value, syncing ->
                val list = NBTTagList()

                for (i in 0..value.size - 1) {
                    val container = NBTTagCompound()
                    list.appendTag(container)
                    val v = value[i]
                    if (v != null) {
                        container.setTag("-", subSerializer().write(v, syncing))
                    }
                }

                list
            })
        })

        SerializerRegistry["java:generator.list"]?.register(Targets.BYTES, { type ->
            type as FieldTypeGeneric
            val typeParam = type.generic(0)!!
            val subSerializer = SerializerRegistry.lazyImpl(Targets.BYTES, typeParam)

            @Suppress("UNCHECKED_CAST")
            val constructorMH = MethodHandleHelper.wrapperForConstructor(type.clazz.getConstructor()) as (Array<Any>) -> MutableList<Any?>

            Targets.BYTES.impl<MutableList<*>>({ buf, existing, syncing ->
                val nullsig = buf.readBooleanArray()

                @Suppress("UNCHECKED_CAST")
                val array = (existing ?: constructorMH(arrayOf())) as MutableList<Any?>

                while (array.size > nullsig.size)
                    array.removeAt(array.size - 1)

                for (i in 0..nullsig.size - 1) {
                    val v = if (nullsig[i]) null else subSerializer().read(buf, array.getOrNull(i), syncing)
                    if (i >= array.size) {
                        array.add(v)
                    } else {
                        array.set(i, v)
                    }
                }
                array
            }, { buf, value, syncing ->
                val nullsig = BooleanArray(value.size) { value.get(it) == null }
                buf.writeBooleanArray(nullsig)

                for (i in 0..value.size - 1) {
                    if (!nullsig[i])
                        subSerializer().write(buf, value.get(i)!!, syncing)
                }
            })
        })
    }
}
