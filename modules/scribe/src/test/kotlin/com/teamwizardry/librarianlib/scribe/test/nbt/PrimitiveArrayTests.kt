package com.teamwizardry.librarianlib.scribe.test.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.NbtBuilder
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveBooleanArraySerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveByteArraySerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveCharArraySerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveDoubleArraySerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveFloatArraySerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveIntArraySerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveLongArraySerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveShortArraySerializer
import org.junit.jupiter.api.Test

internal class PrimitiveArrayTests: NbtPrismTest() {
    @Test
    fun `read+write for an empty double array should be symmetrical`()
        = simple<DoubleArray, PrimitiveDoubleArraySerializer>(doubleArrayOf(), NbtBuilder.list())
    @Test
    fun `read+write for an empty float array should be symmetrical`()
        = simple<FloatArray, PrimitiveFloatArraySerializer>(floatArrayOf(), NbtBuilder.list())
    @Test
    fun `read+write for an empty long array should be symmetrical`()
        = simple<LongArray, PrimitiveLongArraySerializer>(longArrayOf(), NbtBuilder.longArray())
    @Test
    fun `read+write for an empty int array should be symmetrical`()
        = simple<IntArray, PrimitiveIntArraySerializer>(intArrayOf(), NbtBuilder.intArray())
    @Test
    fun `read+write for an empty short array should be symmetrical`()
        = simple<ShortArray, PrimitiveShortArraySerializer>(shortArrayOf(), NbtBuilder.intArray())
    @Test
    fun `read+write for an empty byte array should be symmetrical`()
        = simple<ByteArray, PrimitiveByteArraySerializer>(byteArrayOf(), NbtBuilder.byteArray())
    @Test
    fun `read+write for an empty char array should be symmetrical`()
        = simple<CharArray, PrimitiveCharArraySerializer>(charArrayOf(), NbtBuilder.intArray())
    @Test
    fun `read+write for an empty boolean array should be symmetrical`()
        = simple<BooleanArray, PrimitiveBooleanArraySerializer>(booleanArrayOf(), NbtBuilder.byteArray())

    @Test
    fun `read+write for a double array should be symmetrical`()
        = simple<DoubleArray, PrimitiveDoubleArraySerializer>(doubleArrayOf(1.0, 2.0, 3.0), NbtBuilder.list() { +doubles(1.0, 2.0, 3.0) })
    @Test
    fun `read+write for a float array should be symmetrical`()
        = simple<FloatArray, PrimitiveFloatArraySerializer>(floatArrayOf(1f, 2f, 3f), NbtBuilder.list() { +floats(1f, 2f, 3f) })
    @Test
    fun `read+write for a long array should be symmetrical`()
        = simple<LongArray, PrimitiveLongArraySerializer>(longArrayOf(1, 2, 3), NbtBuilder.longArray(1, 2, 3))
    @Test
    fun `read+write for an int array should be symmetrical`()
        = simple<IntArray, PrimitiveIntArraySerializer>(intArrayOf(1, 2, 3), NbtBuilder.intArray(1, 2, 3))
    @Test
    fun `read+write for a short array should be symmetrical`()
        = simple<ShortArray, PrimitiveShortArraySerializer>(shortArrayOf(1, 2, 3), NbtBuilder.intArray(1, 2, 3))
    @Test
    fun `read+write for a byte array should be symmetrical`()
        = simple<ByteArray, PrimitiveByteArraySerializer>(byteArrayOf(1, 2, 3), NbtBuilder.byteArray(1, 2, 3))
    @Test
    fun `read+write for a char array should be symmetrical`()
        = simple<CharArray, PrimitiveCharArraySerializer>(charArrayOf(1.toChar(), 2.toChar(), 3.toChar()), NbtBuilder.intArray(1, 2, 3))
    @Test
    fun `read+write for a boolean array should be symmetrical`()
        = simple<BooleanArray, PrimitiveBooleanArraySerializer>(booleanArrayOf(true, false, true), NbtBuilder.byteArray(1, 0, 1))

    @Test
    fun `read for a double array with FloatNBT should deserialize`()
        = simpleRead<DoubleArray, PrimitiveDoubleArraySerializer>(doubleArrayOf(1.0, 2.0, 3.0), NbtBuilder.list() { +floats(1f, 2f, 3f) })
    @Test
    fun `read for a float array with DoubleNBT should deserialize`()
        = simpleRead<FloatArray, PrimitiveFloatArraySerializer>(floatArrayOf(1f, 2f, 3f), NbtBuilder.list() { +doubles(1.0, 2.0, 3.0) })
    @Test
    fun `read for a double array with IntNBT should deserialize`()
        = simpleRead<DoubleArray, PrimitiveDoubleArraySerializer>(doubleArrayOf(1.0, 2.0, 3.0), NbtBuilder.list() { +ints(1, 2, 3) })
    @Test
    fun `read for a float array with IntNBT should deserialize`()
        = simpleRead<FloatArray, PrimitiveFloatArraySerializer>(floatArrayOf(1f, 2f, 3f), NbtBuilder.list() { +ints(1, 2, 3) })
}