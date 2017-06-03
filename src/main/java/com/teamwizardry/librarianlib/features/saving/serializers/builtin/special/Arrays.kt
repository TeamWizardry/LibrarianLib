package com.teamwizardry.librarianlib.features.saving.serializers.builtin.special

import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.features.kotlin.forEachIndexed
import com.teamwizardry.librarianlib.features.kotlin.readBooleanArray
import com.teamwizardry.librarianlib.features.kotlin.safeCast
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
        return if(type is FieldTypeArray) SerializerFactoryMatch.GENERAL else SerializerFactoryMatch.NONE
    }

    override fun create(type: FieldType): Serializer<*> {
        type as FieldTypeArray
        return SerializeArray(type, type.componentType)
    }

    class SerializeArray(type: FieldType, val componentType: FieldType) : Serializer<Array<Any?>>(type) {
        override fun getDefault(): Array<Any?> {
            return Array(0) {}
        }

        val serComponent: Serializer<Any> by SerializerRegistry.lazy(componentType)

        override fun readNBT(nbt: NBTBase, existing: Array<Any?>?, syncing: Boolean): Array<Any?> {
            val list = nbt.safeCast(NBTTagList::class.java)

            val reuse = existing != null && existing.size == list.tagCount()
            @Suppress("UNCHECKED_CAST")
            val array = if (reuse) existing as Array<Any?> else ArrayReflect.newInstanceRaw(componentType.clazz, list.tagCount())

            list.forEachIndexed<NBTTagCompound> { i, container ->
                val tag = container.getTag("-")
                if (tag == null)
                    array[i] = null
                else
                    array[i] = serComponent.read(tag, if (reuse) array[i] else null, syncing)
            }

            return array
        }

        override fun writeNBT(value: Array<Any?>, syncing: Boolean): NBTBase {
            val list = NBTTagList()

            val len = ArrayReflect.getLength(value)

            for (i in 0..len - 1) {
                val v = ArrayReflect.get(value, i)
                val container = NBTTagCompound()
                list.appendTag(container)
                if (v != null) {
                    container.setTag("-", serComponent.write(v, syncing))
                }
            }

            return list
        }

        override fun readBytes(buf: ByteBuf, existing: Array<Any?>?, syncing: Boolean): Array<Any?> {
            val nullsig = buf.readBooleanArray()
            val reuse = existing != null && existing.size == nullsig.size
            @Suppress("UNCHECKED_CAST")
            val array = if (reuse) existing as Array<Any?> else ArrayReflect.newInstanceRaw(componentType.clazz, nullsig.size)

            for (i in 0..nullsig.size - 1) {
                array[i] = if (nullsig[i]) null else serComponent.read(buf, array[i], syncing)
            }
            return array
        }

        override fun writeBytes(buf: ByteBuf, value: Array<Any?>, syncing: Boolean) {
            val len = ArrayReflect.getLength(value)
            val nullsig = BooleanArray(len) { ArrayReflect.get(value, it) == null }
            buf.writeBooleanArray(nullsig)
            for (i in 0..len - 1) {
                if (!nullsig[i])
                    serComponent.write(buf, ArrayReflect.get(value, i), syncing)
            }
        }
    }
}
