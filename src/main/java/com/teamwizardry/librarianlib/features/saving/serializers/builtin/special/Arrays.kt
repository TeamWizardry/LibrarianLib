package com.teamwizardry.librarianlib.features.saving.serializers.builtin.special

import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.features.helpers.castOrDefault
import com.teamwizardry.librarianlib.features.kotlin.forEachIndexed
import com.teamwizardry.librarianlib.features.kotlin.readBooleanArray
import com.teamwizardry.librarianlib.features.kotlin.writeBooleanArray
import com.teamwizardry.librarianlib.features.saving.ArrayReflect
import com.teamwizardry.librarianlib.features.saving.FieldType
import com.teamwizardry.librarianlib.features.saving.FieldTypeArray
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactoryMatch
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

/**
 * Created by TheCodeWarrior
 */

@SerializerFactoryRegister
object SerializeArrayFactory : SerializerFactory("Array") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        return if (type is FieldTypeArray) SerializerFactoryMatch.GENERAL else SerializerFactoryMatch.NONE
    }

    override fun create(type: FieldType): Serializer<*> {
        return SerializeArray(type as FieldTypeArray, type.componentType)
    }

    class SerializeArray(type: FieldType, val componentType: FieldType) : Serializer<Array<Any?>>(type) {
        override fun getDefault(): Array<Any?> = arrayOf()

        val serComponent: Serializer<Any> by SerializerRegistry.lazy(componentType)

        override fun readNBT(nbt: NBTBase, existing: Array<Any?>?, syncing: Boolean): Array<Any?> {
            val list = nbt.castOrDefault(NBTTagList::class.java)

            val reuse = existing != null && existing.size == list.tagCount()
            @Suppress("UNCHECKED_CAST")
            val array = if (reuse) existing as Array<Any?> else ArrayReflect.newInstanceRaw(componentType.clazz, list.tagCount())

            list.forEachIndexed<NBTTagCompound> { i, container ->
                array[i] = if (container.hasKey("-")) serComponent.read(container.getTag("-"), if (reuse) array[i] else null, syncing) else null
            }

            return array
        }

        override fun writeNBT(value: Array<Any?>, syncing: Boolean): NBTBase {
            val list = NBTTagList()

            val len = ArrayReflect.getLength(value)

            for (i in 0 until len) {
                val v = ArrayReflect.get(value, i)
                val container = NBTTagCompound().apply(list::appendTag)
                if (v != null) container.setTag("-", serComponent.write(v, syncing))
            }

            return list
        }

        override fun readBytes(buf: ByteBuf, existing: Array<Any?>?, syncing: Boolean): Array<Any?> {
            val nullsig = buf.readBooleanArray()
            val reuse = existing != null && existing.size == nullsig.size
            @Suppress("UNCHECKED_CAST")
            val array = if (reuse) existing as Array<Any?> else ArrayReflect.newInstanceRaw(componentType.clazz, nullsig.size)

            repeat(nullsig.size) {
                array[it] = if (nullsig[it]) null else serComponent.read(buf, array[it], syncing)
            }
            return array
        }

        override fun writeBytes(buf: ByteBuf, value: Array<Any?>, syncing: Boolean) {
            val len = ArrayReflect.getLength(value)
            val nullsig = BooleanArray(len) { ArrayReflect.get(value, it) == null }
            buf.writeBooleanArray(nullsig)
            repeat(len) {
                if (!nullsig[it]) serComponent.write(buf, ArrayReflect.get(value, it), syncing)
            }
        }
    }
}
