package com.teamwizardry.librarianlib.prism.testmod.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.NBTBuilder
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveBooleanArraySerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveByteArraySerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveCharArraySerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveDoubleArraySerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveFloatArraySerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveIntArraySerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveLongArraySerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveShortArraySerializer
import org.junit.jupiter.api.Test

class PrimitiveArrayTests: NBTPrismTest() {
    @Test
    fun readWrite_withEmptyDoubleArray_shouldBeSymmetrical()
        = simple<DoubleArray, PrimitiveDoubleArraySerializer>(doubleArrayOf(), NBTBuilder.list())
    @Test
    fun readWrite_withEmptyFloatArray_shouldBeSymmetrical()
        = simple<FloatArray, PrimitiveFloatArraySerializer>(floatArrayOf(), NBTBuilder.list())
    @Test
    fun readWrite_withEmptyLongArray_shouldBeSymmetrical()
        = simple<LongArray, PrimitiveLongArraySerializer>(longArrayOf(), NBTBuilder.longArray())
    @Test
    fun readWrite_withEmptyIntArray_shouldBeSymmetrical()
        = simple<IntArray, PrimitiveIntArraySerializer>(intArrayOf(), NBTBuilder.intArray())
    @Test
    fun readWrite_withEmptyShortArray_shouldBeSymmetrical()
        = simple<ShortArray, PrimitiveShortArraySerializer>(shortArrayOf(), NBTBuilder.intArray())
    @Test
    fun readWrite_withEmptyByteArray_shouldBeSymmetrical()
        = simple<ByteArray, PrimitiveByteArraySerializer>(byteArrayOf(), NBTBuilder.byteArray())
    @Test
    fun readWrite_withEmptyCharArray_shouldBeSymmetrical()
        = simple<CharArray, PrimitiveCharArraySerializer>(charArrayOf(), NBTBuilder.intArray())
    @Test
    fun readWrite_withEmptyBooleanArray_shouldBeSymmetrical()
        = simple<BooleanArray, PrimitiveBooleanArraySerializer>(booleanArrayOf(), NBTBuilder.byteArray())

    @Test
    fun readWrite_withDoubleArray_shouldBeSymmetrical()
        = simple<DoubleArray, PrimitiveDoubleArraySerializer>(doubleArrayOf(1.0, 2.0, 3.0), NBTBuilder.list() { n+ doubles(1.0, 2.0, 3.0) })
    @Test
    fun readWrite_withFloatArray_shouldBeSymmetrical()
        = simple<FloatArray, PrimitiveFloatArraySerializer>(floatArrayOf(1f, 2f, 3f), NBTBuilder.list() { n+ floats(1f, 2f, 3f) })
    @Test
    fun readWrite_withLongArray_shouldBeSymmetrical()
        = simple<LongArray, PrimitiveLongArraySerializer>(longArrayOf(1, 2, 3), NBTBuilder.longArray(1, 2, 3))
    @Test
    fun readWrite_withIntArray_shouldBeSymmetrical()
        = simple<IntArray, PrimitiveIntArraySerializer>(intArrayOf(1, 2, 3), NBTBuilder.intArray(1, 2, 3))
    @Test
    fun readWrite_withShortArray_shouldBeSymmetrical()
        = simple<ShortArray, PrimitiveShortArraySerializer>(shortArrayOf(1, 2, 3), NBTBuilder.intArray(1, 2, 3))
    @Test
    fun readWrite_withByteArray_shouldBeSymmetrical()
        = simple<ByteArray, PrimitiveByteArraySerializer>(byteArrayOf(1, 2, 3), NBTBuilder.byteArray(1, 2, 3))
    @Test
    fun readWrite_withCharArray_shouldBeSymmetrical()
        = simple<CharArray, PrimitiveCharArraySerializer>(charArrayOf(1.toChar(), 2.toChar(), 3.toChar()), NBTBuilder.intArray(1, 2, 3))
    @Test
    fun readWrite_withBooleanArray_shouldBeSymmetrical()
        = simple<BooleanArray, PrimitiveBooleanArraySerializer>(booleanArrayOf(true, false, true), NBTBuilder.byteArray(1, 0, 1))

    @Test
    fun read_withDoubleArray_andFloatNBT_shouldDeserialize()
        = simpleRead<DoubleArray, PrimitiveDoubleArraySerializer>(doubleArrayOf(1.0, 2.0, 3.0), NBTBuilder.list() { n+ floats(1f, 2f, 3f) })
    @Test
    fun read_withFloatArray_andDoubleNBT_shouldDeserialize()
        = simpleRead<FloatArray, PrimitiveFloatArraySerializer>(floatArrayOf(1f, 2f, 3f), NBTBuilder.list() { n+ doubles(1.0, 2.0, 3.0) })

    fun read_withDoubleArray_andIntNBT_shouldDeserialize()
        = simpleRead<DoubleArray, PrimitiveDoubleArraySerializer>(doubleArrayOf(1.0, 2.0, 3.0), NBTBuilder.list() { n+ ints(1, 2, 3) })
    @Test
    fun read_withFloatArray_andIntNBT_shouldDeserialize()
        = simpleRead<FloatArray, PrimitiveFloatArraySerializer>(floatArrayOf(1f, 2f, 3f), NBTBuilder.list() { n+ ints(1, 2, 3) })
}