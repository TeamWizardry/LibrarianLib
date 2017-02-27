package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.generics

import com.teamwizardry.librarianlib.common.util.forEachIndexed
import com.teamwizardry.librarianlib.common.util.handles.MethodHandleHelper
import com.teamwizardry.librarianlib.common.util.readVarInt
import com.teamwizardry.librarianlib.common.util.safeCast
import com.teamwizardry.librarianlib.common.util.saving.FieldTypeGeneric
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerPriority
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.Targets
import com.teamwizardry.librarianlib.common.util.writeVarInt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import java.util.*

/**
 * Created by TheCodeWarrior
 */
object SerializeSets {
    init {
        val classes = setOf(HashSet::class.java)
        SerializerRegistry.register("java:generator.set", Serializer({
            it.clazz in classes || EnumSet::class.java.isAssignableFrom(it.clazz)
        }, SerializerPriority.EXACT))

        SerializerRegistry["java:generator.set"]?.register(Targets.NBT, { type ->
            type as FieldTypeGeneric
            val typeParam = type.generic(0)!!
            val subSerializer = SerializerRegistry.lazyImpl(Targets.NBT, typeParam)

            @Suppress("UNCHECKED_CAST")
            val constructorMH = (
                    if(type.clazz == Set::class.java)
                        { a -> LinkedHashSet<Any?>() } // linked so if order is important it's preserved.
                    else if (EnumSet::class.java.isAssignableFrom(type.clazz))
                        { arr -> RawEnumSetCreator.create(type.clazz) }
                    else
                        MethodHandleHelper.wrapperForConstructor(type.clazz.getConstructor())
                    ) as (Array<Any>) -> MutableSet<Any?>

            Targets.NBT.impl<MutableSet<*>>({ nbt, existing, syncing ->
                val compound = nbt.safeCast(NBTTagCompound::class.java)
                val list = compound.getTag("values").safeCast(NBTTagList::class.java)
                val nullFlag = compound.getBoolean("hasNull")

                @Suppress("UNCHECKED_CAST")
                val set = (existing ?: constructorMH(arrayOf())) as MutableSet<Any?>
                set.clear()
                if (nullFlag)
                    set.add(null)
                list.forEachIndexed<NBTTagCompound> { i, tag ->
                    val v = subSerializer().read(tag, null, syncing)
                    set.add(v)
                }

                set
            }, { value, syncing ->
                val list = NBTTagList()

                value
                        .filterNotNull()
                        .forEach { list.appendTag(subSerializer().write(it, syncing)) }

                val compound = NBTTagCompound()
                compound.setBoolean("hasNull", value.contains(null))
                compound.setTag("values", list)
                compound
            })
        })

        SerializerRegistry["java:generator.set"]?.register(Targets.BYTES, { type ->
            type as FieldTypeGeneric
            val typeParam = type.generic(0)!!
            val subSerializer = SerializerRegistry.lazyImpl(Targets.BYTES, typeParam)

            @Suppress("UNCHECKED_CAST")
            val constructorMH = (if (EnumSet::class.java.isAssignableFrom(type.clazz))
                { arr -> RawEnumSetCreator.create(type.clazz) }
            else
                MethodHandleHelper.wrapperForConstructor(type.clazz.getConstructor())
                    ) as (Array<Any>) -> MutableSet<Any?>

            Targets.BYTES.impl<MutableSet<*>>({ buf, existing, syncing ->
                val nullFlag = buf.readBoolean()
                val len = buf.readVarInt() - if (nullFlag) 1 else 0

                @Suppress("UNCHECKED_CAST")
                val set = (existing ?: constructorMH(arrayOf())) as MutableSet<Any?>
                set.clear()
                if (nullFlag)
                    set.add(null)

                for (i in 0..len - 1) {
                    set.add(subSerializer().read(buf, null, syncing))
                }

                set
            }, { buf, value, syncing ->
                buf.writeBoolean(value.contains(null))
                buf.writeVarInt(value.size)

                value
                        .filterNotNull()
                        .forEach { subSerializer().write(buf, it, syncing) }
            })
        })
    }
}
