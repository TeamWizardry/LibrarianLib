package com.teamwizardry.librarianlib.prism.testmod.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.NBTBuilder
import com.teamwizardry.librarianlib.prism.nbt.BooleanSerializer
import com.teamwizardry.librarianlib.prism.nbt.ByteSerializer
import com.teamwizardry.librarianlib.prism.nbt.CharacterSerializer
import com.teamwizardry.librarianlib.prism.nbt.DoubleSerializer
import com.teamwizardry.librarianlib.prism.nbt.FloatSerializer
import com.teamwizardry.librarianlib.prism.nbt.IntegerSerializer
import com.teamwizardry.librarianlib.prism.nbt.LongSerializer
import com.teamwizardry.librarianlib.prism.nbt.ShortSerializer
import org.junit.jupiter.api.Test

class BoxedPrimitiveTests: NBTPrismTest() {
    @Test
    fun readWrite_withDouble_shouldBeSymmetrical()
        = simple<Double, DoubleSerializer>(1.0, NBTBuilder.double(1))
    @Test
    fun readWrite_withFloat_shouldBeSymmetrical()
        = simple<Float, FloatSerializer>(1f, NBTBuilder.float(1))
    @Test
    fun readWrite_withLong_shouldBeSymmetrical()
        = simple<Long, LongSerializer>(1L, NBTBuilder.long(1))
    @Test
    fun readWrite_withInt_shouldBeSymmetrical()
        = simple<Int, IntegerSerializer>(1, NBTBuilder.int(1))
    @Test
    fun readWrite_withShort_shouldBeSymmetrical()
        = simple<Short, ShortSerializer>(1.toShort(), NBTBuilder.short(1))
    @Test
    fun readWrite_withByte_shouldBeSymmetrical()
        = simple<Byte, ByteSerializer>(1.toByte(), NBTBuilder.byte(1))
    @Test
    fun readWrite_withChar_shouldBeSymmetrical()
        = simple<Char, CharacterSerializer>(1.toChar(), NBTBuilder.int(1))
    @Test
    fun readWrite_withBooleanTrue_shouldBeSymmetrical()
        = simple<Boolean, BooleanSerializer>(true, NBTBuilder.byte(1))
    @Test
    fun readWrite_withBooleanFalse_shouldBeSymmetrical()
        = simple<Boolean, BooleanSerializer>(false, NBTBuilder.byte(0))

    @Test
    fun read_withDouble_andIntNBT_shouldCast()
        = simpleRead<Double, DoubleSerializer>(1.0, NBTBuilder.int(1))
    @Test
    fun read_withFloat_andIntNBT_shouldCast()
        = simpleRead<Float, FloatSerializer>(1f, NBTBuilder.int(1))
    @Test
    fun read_withLong_andDoubleNBT_shouldCastAndClamp()
        = simpleRead<Long, LongSerializer>(Long.MAX_VALUE, NBTBuilder.double(1e20))
    @Test
    fun read_withInt_andDoubleNBT_shouldCastAndClamp()
        = simpleRead<Int, IntegerSerializer>(Int.MAX_VALUE, NBTBuilder.double(1e10))
    @Test
    fun read_withShort_andDoubleNBT_shouldCastAndTruncate()
        = simpleRead<Short, ShortSerializer>(100000.toShort(), NBTBuilder.double(1e5))
    @Test
    fun read_withByte_andDoubleNBT_shouldCastAndTruncate()
        = simpleRead<Byte, ByteSerializer>(1000.toByte(), NBTBuilder.double(1e3))
    @Test
    fun read_withChar_andDoubleNBT_shouldCastAndTruncate()
        = simpleRead<Char, CharacterSerializer>(100000.toChar(), NBTBuilder.double(1e5))
}