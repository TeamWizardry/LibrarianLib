package com.teamwizardry.librarianlib.features.saving.serializers.builtin.generics


import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.features.kotlin.forEach
import com.teamwizardry.librarianlib.features.kotlin.readBooleanArray
import com.teamwizardry.librarianlib.features.kotlin.safeCast
import com.teamwizardry.librarianlib.features.kotlin.writeBooleanArray
import com.teamwizardry.librarianlib.features.saving.FieldType
import com.teamwizardry.librarianlib.features.saving.FieldTypeGeneric
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactoryMatch
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import java.util.*

@SerializerFactoryRegister
object SerializeDequeFactory : SerializerFactory("Deque") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        return canApplySubclass(type, Deque::class.java)
    }

    override fun create(type: FieldType): Serializer<*> {
        type as FieldTypeGeneric
        return SerializeDeque(type, type.resolveGeneric(Deque::class.java, 0))
    }

    class SerializeDeque(type: FieldType, val componentType: FieldType) : Serializer<Deque<Any?>>(type) {
        override fun getDefault(): Deque<Any?> {
            return ArrayDeque()
        }

        val serComponent: Serializer<Any> by SerializerRegistry.lazy(componentType)

        override fun readNBT(nbt: NBTBase, existing: Deque<Any?>?, syncing: Boolean): Deque<Any?> {
            val list = nbt.safeCast(NBTTagList::class.java)

            @Suppress("UNCHECKED_CAST")
            val deque = existing ?: getDefault()
            deque.clear()

            list.forEach<NBTTagCompound> { container ->
                val tag = container.getTag("-")
                deque.push(
                        serComponent.read(tag, null, syncing)
                )
            }

            return deque
        }

        override fun writeNBT(value: Deque<Any?>, syncing: Boolean): NBTBase {
            val list = NBTTagList()

            for (v in value) {
                val container = NBTTagCompound()
                list.appendTag(container)
                if (v != null) {
                    container.setTag("-", serComponent.write(v, syncing))
                }
            }

            return list
        }

        override fun readBytes(buf: ByteBuf, existing: Deque<Any?>?, syncing: Boolean): Deque<Any?> {
            val nullsig = buf.readBooleanArray()
            val deque = existing ?: getDefault()
            deque.clear()

            for (i in 0..nullsig.size - 1) {
                deque.push(if (nullsig[i]) null else serComponent.read(buf, null, syncing))
            }

            return deque
        }

        override fun writeBytes(buf: ByteBuf, value: Deque<Any?>, syncing: Boolean) {
            val nullsig = value.map { it == null }.toTypedArray().toBooleanArray()
            buf.writeBooleanArray(nullsig)
            for (v in value) {
                if (v != null)
                    serComponent.write(buf, v, syncing)
            }
        }
    }
}
