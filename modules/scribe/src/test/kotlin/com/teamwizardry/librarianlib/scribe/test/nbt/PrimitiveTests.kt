package com.teamwizardry.librarianlib.scribe.test.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.NbtBuilder
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveBooleanSerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveByteSerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveCharSerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveDoubleSerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveFloatSerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveIntSerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveLongSerializer
import com.teamwizardry.librarianlib.scribe.nbt.PrimitiveShortSerializer
import dev.thecodewarrior.mirror.Mirror
import org.junit.jupiter.api.Test

internal class PrimitiveTests: NbtPrismTest() {
    @Test
    fun `read+write for a double should be symmetrical`()
        = simple<PrimitiveDoubleSerializer>(Mirror.types.double, 1.0, NbtBuilder.double(1))
    @Test
    fun `read+write for a float should be symmetrical`()
        = simple<PrimitiveFloatSerializer>(Mirror.types.float, 1f, NbtBuilder.float(1))
    @Test
    fun `read+write for a long should be symmetrical`()
        = simple<PrimitiveLongSerializer>(Mirror.types.long, 1L, NbtBuilder.long(1))
    @Test
    fun `read+write for an int should be symmetrical`()
        = simple<PrimitiveIntSerializer>(Mirror.types.int, 1, NbtBuilder.int(1))
    @Test
    fun `read+write for a short should be symmetrical`()
        = simple<PrimitiveShortSerializer>(Mirror.types.short, 1.toShort(), NbtBuilder.short(1))
    @Test
    fun `read+write for a byte should be symmetrical`()
        = simple<PrimitiveByteSerializer>(Mirror.types.byte, 1.toByte(), NbtBuilder.byte(1))
    @Test
    fun `read+write for a char should be symmetrical`()
        = simple<PrimitiveCharSerializer>(Mirror.types.char, 1.toChar(), NbtBuilder.int(1))
    @Test
    fun `read+write for true should be symmetrical`()
        = simple<PrimitiveBooleanSerializer>(Mirror.types.boolean, true, NbtBuilder.byte(1))
    @Test
    fun `read+write for false should be symmetrical`()
        = simple<PrimitiveBooleanSerializer>(Mirror.types.boolean, false, NbtBuilder.byte(0))

    @Test
    fun `read for double with IntTag should cast`()
        = simpleRead<PrimitiveDoubleSerializer>(Mirror.types.double, 1.0, NbtBuilder.int(1))
    @Test
    fun `read for float with IntTag should cast`()
        = simpleRead<PrimitiveFloatSerializer>(Mirror.types.float, 1f, NbtBuilder.int(1))
    @Test
    fun `read for long with DoubleTag should cast and clamp`()
        = simpleRead<PrimitiveLongSerializer>(Mirror.types.long, Long.MAX_VALUE, NbtBuilder.double(1e20))
    @Test
    fun `read for int with DoubleTag should cast and clamp`()
        = simpleRead<PrimitiveIntSerializer>(Mirror.types.int, Int.MAX_VALUE, NbtBuilder.double(1e10))
    @Test
    fun `read for short with DoubleTag should cast and truncate`()
        = simpleRead<PrimitiveShortSerializer>(Mirror.types.short, 100000.toShort(), NbtBuilder.double(1e5))
    @Test
    fun `read for byte with DoubleTag should cast and truncate`()
        = simpleRead<PrimitiveByteSerializer>(Mirror.types.byte, 1000.toByte(), NbtBuilder.double(1e3))
    @Test
    fun `read for char with DoubleTag should cast and truncate`()
        = simpleRead<PrimitiveCharSerializer>(Mirror.types.char, 100000.toChar(), NbtBuilder.double(1e5))
}