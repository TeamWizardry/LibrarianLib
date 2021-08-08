package com.teamwizardry.librarianlib.scribe.test.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.NbtBuilder
import com.teamwizardry.librarianlib.scribe.nbt.BooleanSerializer
import com.teamwizardry.librarianlib.scribe.nbt.ByteSerializer
import com.teamwizardry.librarianlib.scribe.nbt.CharacterSerializer
import com.teamwizardry.librarianlib.scribe.nbt.DoubleSerializer
import com.teamwizardry.librarianlib.scribe.nbt.FloatSerializer
import com.teamwizardry.librarianlib.scribe.nbt.IntegerSerializer
import com.teamwizardry.librarianlib.scribe.nbt.LongSerializer
import com.teamwizardry.librarianlib.scribe.nbt.ShortSerializer
import org.junit.jupiter.api.Test

internal class BoxedPrimitiveTests: NbtPrismTest() {
    @Test
    fun `read+write for Double should be symmetrical`()
        = simple<Double, DoubleSerializer>(1.0, NbtBuilder.double(1))
    @Test
    fun `read+write for Float should be symmetrical`()
        = simple<Float, FloatSerializer>(1f, NbtBuilder.float(1))
    @Test
    fun `read+write for Long should be symmetrical`()
        = simple<Long, LongSerializer>(1L, NbtBuilder.long(1))
    @Test
    fun `read+write for Integer should be symmetrical`()
        = simple<Int, IntegerSerializer>(1, NbtBuilder.int(1))
    @Test
    fun `read+write for Short should be symmetrical`()
        = simple<Short, ShortSerializer>(1.toShort(), NbtBuilder.short(1))
    @Test
    fun `read+write for Byte should be symmetrical`()
        = simple<Byte, ByteSerializer>(1.toByte(), NbtBuilder.byte(1))
    @Test
    fun `read+write for Character should be symmetrical`()
        = simple<Char, CharacterSerializer>(1.toChar(), NbtBuilder.int(1))
    @Test
    fun `read+write for Boolean true should be symmetrical`()
        = simple<Boolean, BooleanSerializer>(true, NbtBuilder.byte(1))
    @Test
    fun `read+write for Boolean false should be symmetrical`()
        = simple<Boolean, BooleanSerializer>(false, NbtBuilder.byte(0))

    @Test
    fun `read for Double with IntNBT should cast`()
        = simpleRead<Double, DoubleSerializer>(1.0, NbtBuilder.int(1))
    @Test
    fun `read for Float with IntNBT should cast`()
        = simpleRead<Float, FloatSerializer>(1f, NbtBuilder.int(1))
    @Test
    fun `read for Long with DoubleNBT should cast and clamp`()
        = simpleRead<Long, LongSerializer>(Long.MAX_VALUE, NbtBuilder.double(1e20))
    @Test
    fun `read for Int with DoubleNBT should cast and clamp`()
        = simpleRead<Int, IntegerSerializer>(Int.MAX_VALUE, NbtBuilder.double(1e10))
    @Test
    fun `read for Short with DoubleNBT should cast and truncate`()
        = simpleRead<Short, ShortSerializer>(100000.toShort(), NbtBuilder.double(1e5))
    @Test
    fun `read for Byte with DoubleNBT should cast and truncate`()
        = simpleRead<Byte, ByteSerializer>(1000.toByte(), NbtBuilder.double(1e3))
    @Test
    fun `read for Char with DoubleNBT should cast and truncate`()
        = simpleRead<Char, CharacterSerializer>(100000.toChar(), NbtBuilder.double(1e5))
}