package com.teamwizardry.librarianlib.prism.testmod.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.NBTBuilder
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveBooleanSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveByteSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveCharSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveDoubleSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveFloatSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveIntSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveLongSerializer
import com.teamwizardry.librarianlib.prism.nbt.PrimitiveShortSerializer
import dev.thecodewarrior.mirror.Mirror
import org.junit.jupiter.api.Test

internal class PrimitiveTests: NBTPrismTest() {
    @Test
    fun readWrite_withDouble_shouldBeSymmetrical()
        = simple<PrimitiveDoubleSerializer>(Mirror.types.double, 1.0, NBTBuilder.double(1))
    @Test
    fun readWrite_withFloat_shouldBeSymmetrical()
        = simple<PrimitiveFloatSerializer>(Mirror.types.float, 1f, NBTBuilder.float(1))
    @Test
    fun readWrite_withLong_shouldBeSymmetrical()
        = simple<PrimitiveLongSerializer>(Mirror.types.long, 1L, NBTBuilder.long(1))
    @Test
    fun readWrite_withInt_shouldBeSymmetrical()
        = simple<PrimitiveIntSerializer>(Mirror.types.int, 1, NBTBuilder.int(1))
    @Test
    fun readWrite_withShort_shouldBeSymmetrical()
        = simple<PrimitiveShortSerializer>(Mirror.types.short, 1.toShort(), NBTBuilder.short(1))
    @Test
    fun readWrite_withByte_shouldBeSymmetrical()
        = simple<PrimitiveByteSerializer>(Mirror.types.byte, 1.toByte(), NBTBuilder.byte(1))
    @Test
    fun readWrite_withChar_shouldBeSymmetrical()
        = simple<PrimitiveCharSerializer>(Mirror.types.char, 1.toChar(), NBTBuilder.int(1))
    @Test
    fun readWrite_withBooleanTrue_shouldBeSymmetrical()
        = simple<PrimitiveBooleanSerializer>(Mirror.types.boolean, true, NBTBuilder.byte(1))
    @Test
    fun readWrite_withBooleanFalse_shouldBeSymmetrical()
        = simple<PrimitiveBooleanSerializer>(Mirror.types.boolean, false, NBTBuilder.byte(0))

    @Test
    fun read_withDouble_andIntNBT_shouldCast()
        = simpleRead<PrimitiveDoubleSerializer>(Mirror.types.double, 1.0, NBTBuilder.int(1))
    @Test
    fun read_withFloat_andIntNBT_shouldCast()
        = simpleRead<PrimitiveFloatSerializer>(Mirror.types.float, 1f, NBTBuilder.int(1))
    @Test
    fun read_withLong_andDoubleNBT_shouldCastAndClamp()
        = simpleRead<PrimitiveLongSerializer>(Mirror.types.long, Long.MAX_VALUE, NBTBuilder.double(1e20))
    @Test
    fun read_withInt_andDoubleNBT_shouldCastAndClamp()
        = simpleRead<PrimitiveIntSerializer>(Mirror.types.int, Int.MAX_VALUE, NBTBuilder.double(1e10))
    @Test
    fun read_withShort_andDoubleNBT_shouldCastAndTruncate()
        = simpleRead<PrimitiveShortSerializer>(Mirror.types.short, 100000.toShort(), NBTBuilder.double(1e5))
    @Test
    fun read_withByte_andDoubleNBT_shouldCastAndTruncate()
        = simpleRead<PrimitiveByteSerializer>(Mirror.types.byte, 1000.toByte(), NBTBuilder.double(1e3))
    @Test
    fun read_withChar_andDoubleNBT_shouldCastAndTruncate()
        = simpleRead<PrimitiveCharSerializer>(Mirror.types.char, 100000.toChar(), NBTBuilder.double(1e5))
}