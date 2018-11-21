package com.teamwizardry.librarianlib.features.saving.serializers.builtin.generics

import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.features.helpers.castOrDefault
import com.teamwizardry.librarianlib.features.kotlin.readBooleanArray
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

        override fun getDefault(): Pair<Any?, Any?> {
            return Pair(serFirst.getDefault(), serSecond.getDefault())
        }

        override fun readNBT(nbt: NBTBase, existing: Pair<Any?, Any?>?, syncing: Boolean): Pair<Any?, Any?> {
            val tag = nbt.castOrDefault(NBTTagCompound::class.java)

            val tagFirst: NBTBase? = tag.getTag("first")
            val tagSecond: NBTBase? = tag.getTag("second")

            val first = if (tagFirst == null) null else serFirst.read(tagFirst, existing?.first, syncing)
            val second = if (tagSecond == null) null else serSecond.read(tagSecond, existing?.second, syncing)

            return Pair(first, second)
        }

        override fun writeNBT(value: Pair<Any?, Any?>, syncing: Boolean): NBTBase {
            val tag = NBTTagCompound()

            val first = value.first
            val second = value.second

            if (first != null) tag.setTag("first", serFirst.write(first, syncing))
            if (second != null) tag.setTag("second", serSecond.write(second, syncing))

            return tag
        }

        override fun readBytes(buf: ByteBuf, existing: Pair<Any?, Any?>?, syncing: Boolean): Pair<Any?, Any?> {
            val nulls = buf.readBooleanArray()

            val first = if (nulls[0]) null else {
                serFirst.read(buf, existing?.first, syncing)
            }
            val second = if (nulls[1]) null else {
                serSecond.read(buf, existing?.second, syncing)
            }

            return Pair(first, second)
        }

        override fun writeBytes(buf: ByteBuf, value: Pair<Any?, Any?>, syncing: Boolean) {
            val first = value.first
            val second = value.second

            val nulls = booleanArrayOf(first == null, second == null)
            buf.writeBooleanArray(nulls)

            if (first != null) {
                serFirst.write(buf, first, syncing)
            }
            if (second != null) {
                serSecond.write(buf, second, syncing)
            }
        }
    }
}

@SerializerFactoryRegister
object SerializeTripleFactory : SerializerFactory("Triple") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        return this.canApplyExact(type, Triple::class.java)
    }

    override fun create(type: FieldType): Serializer<*> {
        type as FieldTypeGeneric
        return SerializeTriple(type, type.generic(0), type.generic(1), type.generic(2))
    }

    class SerializeTriple(type: FieldType, firstType: FieldType, secondType: FieldType, thirdType: FieldType) : Serializer<Triple<Any?, Any?, Any?>>(type) {
        val serFirst: Serializer<Any> by SerializerRegistry.lazy(firstType)
        val serSecond: Serializer<Any> by SerializerRegistry.lazy(secondType)
        val serThird: Serializer<Any> by SerializerRegistry.lazy(thirdType)

        override fun getDefault(): Triple<Any?, Any?, Any?> {
            return Triple(serFirst.getDefault(), serSecond.getDefault(), serThird.getDefault())
        }

        override fun readNBT(nbt: NBTBase, existing: Triple<Any?, Any?, Any?>?, syncing: Boolean): Triple<Any?, Any?, Any?> {
            val tag = nbt.castOrDefault(NBTTagCompound::class.java)

            val tagFirst: NBTBase? = tag.getTag("first")
            val tagSecond: NBTBase? = tag.getTag("second")
            val tagThird: NBTBase? = tag.getTag("third")

            val first = if (tagFirst == null) null else serFirst.read(tagFirst, existing?.first, syncing)
            val second = if (tagSecond == null) null else serSecond.read(tagSecond, existing?.second, syncing)
            val third = if (tagThird == null) null else serThird.read(tagThird, existing?.third, syncing)

            return Triple(first, second, third)
        }

        override fun writeNBT(value: Triple<Any?, Any?, Any?>, syncing: Boolean): NBTBase {
            val tag = NBTTagCompound()

            val first = value.first
            val second = value.second
            val third = value.third

            if (first != null) tag.setTag("first", serFirst.write(first, syncing))
            if (second != null) tag.setTag("second", serSecond.write(second, syncing))
            if (third != null) tag.setTag("third", serThird.write(third, syncing))

            return tag
        }

        override fun readBytes(buf: ByteBuf, existing: Triple<Any?, Any?, Any?>?, syncing: Boolean): Triple<Any?, Any?, Any?> {
            val nulls = buf.readBooleanArray()

            val first = if (nulls[0]) null else {
                serFirst.read(buf, existing?.first, syncing)
            }
            val second = if (nulls[1]) null else {
                serSecond.read(buf, existing?.second, syncing)
            }
            val third = if (nulls[2]) null else {
                serThird.read(buf, existing?.third, syncing)
            }

            return Triple(first, second, third)
        }

        override fun writeBytes(buf: ByteBuf, value: Triple<Any?, Any?, Any?>, syncing: Boolean) {
            val first = value.first
            val second = value.second
            val third = value.third

            val nulls = booleanArrayOf(first == null, second == null, third == null)
            buf.writeBooleanArray(nulls)

            if (first != null) {
                serFirst.write(buf, first, syncing)
            }
            if (second != null) {
                serSecond.write(buf, second, syncing)
            }
            if (third != null) {
                serThird.write(buf, third, syncing)
            }
        }
    }
}
