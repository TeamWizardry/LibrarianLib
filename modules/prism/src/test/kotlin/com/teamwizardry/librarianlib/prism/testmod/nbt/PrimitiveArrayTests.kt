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

internal class PrimitiveArrayTests: NBTPrismTest() {
    @Test
    fun `read+write with empty double array should be symmetrical`()
        = simple<DoubleArray, PrimitiveDoubleArraySerializer>(doubleArrayOf(), NBTBuilder.list())
    @Test
    fun `read+write with empty float array should be symmetrical`()
        = simple<FloatArray, PrimitiveFloatArraySerializer>(floatArrayOf(), NBTBuilder.list())
    @Test
    fun `read+write with empty long array should be symmetrical`()
        = simple<LongArray, PrimitiveLongArraySerializer>(longArrayOf(), NBTBuilder.longArray())
    @Test
    fun `read+write with empty int array should be symmetrical`()
        = simple<IntArray, PrimitiveIntArraySerializer>(intArrayOf(), NBTBuilder.intArray())
    @Test
    fun `read+write with empty short array should be symmetrical`()
        = simple<ShortArray, PrimitiveShortArraySerializer>(shortArrayOf(), NBTBuilder.intArray())
    @Test
    fun `read+write with empty byte array should be symmetrical`()
        = simple<ByteArray, PrimitiveByteArraySerializer>(byteArrayOf(), NBTBuilder.byteArray())
    @Test
    fun `read+write with empty char array should be symmetrical`()
        = simple<CharArray, PrimitiveCharArraySerializer>(charArrayOf(), NBTBuilder.intArray())
    @Test
    fun `read+write with empty boolean array should be symmetrical`()
        = simple<BooleanArray, PrimitiveBooleanArraySerializer>(booleanArrayOf(), NBTBuilder.byteArray())

    @Test
    fun `read+write with double array should be symmetrical`()
        = simple<DoubleArray, PrimitiveDoubleArraySerializer>(doubleArrayOf(1.0, 2.0, 3.0), NBTBuilder.list() { n+ doubles(1.0, 2.0, 3.0) })
    @Test
    fun `read+write with float array should be symmetrical`()
        = simple<FloatArray, PrimitiveFloatArraySerializer>(floatArrayOf(1f, 2f, 3f), NBTBuilder.list() { n+ floats(1f, 2f, 3f) })
    @Test
    fun `read+write with long array should be symmetrical`()
        = simple<LongArray, PrimitiveLongArraySerializer>(longArrayOf(1, 2, 3), NBTBuilder.longArray(1, 2, 3))
    @Test
    fun `read+write with int array should be symmetrical`()
        = simple<IntArray, PrimitiveIntArraySerializer>(intArrayOf(1, 2, 3), NBTBuilder.intArray(1, 2, 3))
    @Test
    fun `read+write with short array should be symmetrical`()
        = simple<ShortArray, PrimitiveShortArraySerializer>(shortArrayOf(1, 2, 3), NBTBuilder.intArray(1, 2, 3))
    @Test
    fun `read+write with byte array should be symmetrical`()
        = simple<ByteArray, PrimitiveByteArraySerializer>(byteArrayOf(1, 2, 3), NBTBuilder.byteArray(1, 2, 3))
    @Test
    fun `read+write with char array should be symmetrical`()
        = simple<CharArray, PrimitiveCharArraySerializer>(charArrayOf(1.toChar(), 2.toChar(), 3.toChar()), NBTBuilder.intArray(1, 2, 3))
    @Test
    fun `read+write with boolean array should be symmetrical`()
        = simple<BooleanArray, PrimitiveBooleanArraySerializer>(booleanArrayOf(true, false, true), NBTBuilder.byteArray(1, 0, 1))

    @Test
    fun `read with double array and FloatNBT should deserialize`()
        = simpleRead<DoubleArray, PrimitiveDoubleArraySerializer>(doubleArrayOf(1.0, 2.0, 3.0), NBTBuilder.list() { n+ floats(1f, 2f, 3f) })
    @Test
    fun `read with float array and DoubleNBT should deserialize`()
        = simpleRead<FloatArray, PrimitiveFloatArraySerializer>(floatArrayOf(1f, 2f, 3f), NBTBuilder.list() { n+ doubles(1.0, 2.0, 3.0) })
    @Test
    fun `read with double array and IntNBT should deserialize`()
        = simpleRead<DoubleArray, PrimitiveDoubleArraySerializer>(doubleArrayOf(1.0, 2.0, 3.0), NBTBuilder.list() { n+ ints(1, 2, 3) })
    @Test
    fun `read with float array and IntNBT should deserialize`()
        = simpleRead<FloatArray, PrimitiveFloatArraySerializer>(floatArrayOf(1f, 2f, 3f), NBTBuilder.list() { n+ ints(1, 2, 3) })
}