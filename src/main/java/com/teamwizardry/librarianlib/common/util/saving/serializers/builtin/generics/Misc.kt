package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.generics

import com.teamwizardry.librarianlib.common.util.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.common.util.readBooleanArray
import com.teamwizardry.librarianlib.common.util.safeCast
import com.teamwizardry.librarianlib.common.util.saving.FieldType
import com.teamwizardry.librarianlib.common.util.saving.FieldTypeGeneric
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerFactoryMatch
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.writeBooleanArray
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound

//@SerializerFactoryRegister
//object Serialize_V_Factory : SerializerFactory("_V_") {
//    override fun canApply(type: FieldType): SerializerFactoryMatch {
//        return this.canApplyExact(type, _V_::class.java)
//    }
//
//    override fun create(type: FieldType): Serializer<*> {
//        type as FieldTypeGeneric
//        return Serialize_V_(type, type.generic(0), type.generic(1))
//    }
//
//    class Serialize_V_(type: FieldType, firstType: FieldType, secondType: FieldType) : Serializer<_V_>(type) {
//
//        val serFirst: Serializer<Any> by SerializerRegistry.lazy(firstType)
//        val serSecond: Serializer<Any> by SerializerRegistry.lazy(secondType)
//
//        override fun readNBT(nbt: NBTBase, existing: _V_?, syncing: Boolean): _V_ {
//        }
//
//        override fun writeNBT(value: _V_, syncing: Boolean): NBTBase {
//        }
//
//        override fun readBytes(buf: ByteBuf, existing: _V_?, syncing: Boolean): _V_ {
//        }
//
//        override fun writeBytes(buf: ByteBuf, value: _V_, syncing: Boolean) {
//        }
//    }
//}

@SerializerFactoryRegister
object SerializePairFactory : SerializerFactory("Pair") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        return this.canApplyExact(type, Pair::class.java)
    }

    override fun create(type: FieldType): Serializer<*> {
        type as FieldTypeGeneric
        return SerializePair(type, type.generic(0), type.generic(1))
    }

    class SerializePair(type: FieldType, firstType: FieldType, secondType: FieldType) : Serializer<Pair<Any?, Any?>>(type) {

        val serFirst: Serializer<Any> by SerializerRegistry.lazy(firstType)
        val serSecond: Serializer<Any> by SerializerRegistry.lazy(secondType)

        override fun readNBT(nbt: NBTBase, existing: Pair<Any?, Any?>?, syncing: Boolean): Pair<Any?, Any?> {
            val tag = nbt.safeCast<NBTTagCompound>()

            val tagFirst = tag.getTag("first")
            val tagSecond = tag.getTag("second")

            val first = if(tagFirst == null) null else serFirst.read(tagFirst, existing?.first, syncing)
            val second = if(tagSecond == null) null else serSecond.read(tagSecond, existing?.second, syncing)

            return Pair(first, second)
        }

        override fun writeNBT(value: Pair<Any?, Any?>, syncing: Boolean): NBTBase {
            val tag = NBTTagCompound()

            val first = value.first
            val second = value.second

            if(first != null) tag.setTag("first", serFirst.write(first, syncing))
            if(second != null) tag.setTag("second", serSecond.write(second, syncing))

            return tag
        }

        override fun readBytes(buf: ByteBuf, existing: Pair<Any?, Any?>?, syncing: Boolean): Pair<Any?, Any?> {
            val nulls = buf.readBooleanArray()

            val first = if(nulls[0]) null else {
                serFirst.read(buf, existing?.first, syncing)
            }
            val second = if(nulls[1]) null else {
                serSecond.read(buf, existing?.second, syncing)
            }

            return Pair(first, second)
        }

        override fun writeBytes(buf: ByteBuf, value: Pair<Any?, Any?>, syncing: Boolean) {
            val first = value.first
            val second = value.second

            val nulls = booleanArrayOf(first == null, second == null)
            buf.writeBooleanArray(nulls)

            if(first != null) {
                serFirst.write(buf, first, syncing)
            }
            if(second != null) {
                serSecond.write(buf, second, syncing)
            }
        }
    }
}
