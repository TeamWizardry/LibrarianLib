package com.teamwizardry.librarianlib.prism.nbt

import com.teamwizardry.librarianlib.prism.nbt.PrimitiveByteArraySerializer.expectType
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveCharArraySerializer.expectType
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveDoubleArraySerializer.expectType
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveFloatArraySerializer.expectType
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveIntArraySerializer.expectType
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveLongArraySerializer.expectType
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveShortArraySerializer.expectType
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.nbt.ByteArrayNBT
import net.minecraft.nbt.DoubleNBT
import net.minecraft.nbt.FloatNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.IntArrayNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.nbt.LongArrayNBT
import net.minecraft.nbt.NumberNBT

object PrimitiveDoubleArraySerializer: NBTSerializer<DoubleArray>() {
    override fun deserialize(tag: INBT, existing: DoubleArray?): DoubleArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<ListNBT>("tag")
        val array = if(tag.size == existing?.size) existing else DoubleArray(tag.size)
        tag.forEachIndexed { index, inbt ->
            array[index] = inbt.expectType<NumberNBT>("index $index").double
        }
        return array
    }

    override fun serialize(value: DoubleArray): INBT {
        return value.mapTo(ListNBT()) { DoubleNBT.valueOf(it) }
    }
}

object PrimitiveFloatArraySerializer: NBTSerializer<FloatArray>() {
    override fun deserialize(tag: INBT, existing: FloatArray?): FloatArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<ListNBT>("tag")
        val array = if(tag.size == existing?.size) existing else FloatArray(tag.size)
        tag.forEachIndexed { index, inbt ->
            array[index] = inbt.expectType<NumberNBT>("index $index").float
        }
        return array
    }

    override fun serialize(value: FloatArray): INBT {
        return value.mapTo(ListNBT()) { FloatNBT.valueOf(it) }
    }
}

object PrimitiveLongArraySerializer: NBTSerializer<LongArray>() {
    override fun deserialize(tag: INBT, existing: LongArray?): LongArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<LongArrayNBT>("tag")
        if(tag.asLongArray.size == existing?.size) {
            tag.asLongArray.copyInto(existing)
            return existing
        } else {
            return tag.asLongArray
        }
    }

    override fun serialize(value: LongArray): INBT {
        return LongArrayNBT(value)
    }
}

object PrimitiveIntArraySerializer: NBTSerializer<IntArray>() {
    override fun deserialize(tag: INBT, existing: IntArray?): IntArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<IntArrayNBT>("tag")
        if(tag.intArray.size == existing?.size) {
            tag.intArray.copyInto(existing)
            return existing
        } else {
            return tag.intArray
        }
    }

    override fun serialize(value: IntArray): INBT {
        return IntArrayNBT(value)
    }
}

object PrimitiveShortArraySerializer: NBTSerializer<ShortArray>() {
    override fun deserialize(tag: INBT, existing: ShortArray?): ShortArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<IntArrayNBT>("tag")
        if(tag.intArray.size == existing?.size) {
            tag.intArray.forEachIndexed { index, value ->
                existing[index] = value.toShort()
            }
            return existing
        } else {
            return ShortArray(tag.intArray.size) { tag.intArray[it].toShort() }
        }
    }

    override fun serialize(value: ShortArray): INBT {
        return IntArrayNBT(IntArray(value.size) { value[it].toInt() })
    }
}

object PrimitiveCharArraySerializer: NBTSerializer<CharArray>() {
    override fun deserialize(tag: INBT, existing: CharArray?): CharArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<IntArrayNBT>("tag")
        if(tag.intArray.size == existing?.size) {
            tag.intArray.forEachIndexed { index, value ->
                existing[index] = value.toChar()
            }
            return existing
        } else {
            return CharArray(tag.intArray.size) { tag.intArray[it].toChar() }
        }
    }

    override fun serialize(value: CharArray): INBT {
        return IntArrayNBT(IntArray(value.size) { value[it].toInt() })
    }
}

object PrimitiveByteArraySerializer: NBTSerializer<ByteArray>() {
    override fun deserialize(tag: INBT, existing: ByteArray?): ByteArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<ByteArrayNBT>("tag")
        if(tag.byteArray.size == existing?.size) {
            tag.byteArray.copyInto(existing)
            return existing
        } else {
            return tag.byteArray
        }
    }

    override fun serialize(value: ByteArray): INBT {
        return ByteArrayNBT(value)
    }
}

object PrimitiveBooleanArraySerializer: NBTSerializer<BooleanArray>() {
    override fun deserialize(tag: INBT, existing: BooleanArray?): BooleanArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<ByteArrayNBT>("tag")
        if(tag.byteArray.size == existing?.size) {
            tag.byteArray.forEachIndexed { index, value ->
                existing[index] = value != 0.toByte()
            }
            return existing
        } else {
            return BooleanArray(tag.byteArray.size) { tag.byteArray[it] != 0.toByte() }
        }
    }

    override fun serialize(value: BooleanArray): INBT {
        return ByteArrayNBT(ByteArray(value.size) { if(value[it]) 1.toByte() else 0.toByte() })
    }
}
