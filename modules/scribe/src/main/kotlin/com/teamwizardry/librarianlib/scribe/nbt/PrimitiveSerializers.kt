package com.teamwizardry.librarianlib.scribe.nbt

import dev.thecodewarrior.mirror.Mirror
import net.minecraft.nbt.NbtByte
import net.minecraft.nbt.NbtDouble
import net.minecraft.nbt.NbtFloat
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtLong
import net.minecraft.nbt.AbstractNbtNumber
import net.minecraft.nbt.NbtShort
import net.minecraft.nbt.NbtString

internal object StringSerializer: NbtSerializer<String>() {
    override fun deserialize(tag: NbtElement): String {
        return tag.expectType<NbtString>("tag").asString()
    }

    override fun serialize(value: String): NbtElement {
        return NbtString.of(value)
    }
}

internal object PrimitiveDoubleSerializer: NbtSerializer<Double>(Mirror.types.double) {
    override fun deserialize(tag: NbtElement): Double {
        return tag.expectType<AbstractNbtNumber>("tag").doubleValue()
    }

    override fun serialize(value: Double): NbtElement {
        return NbtDouble.of(value)
    }
}
internal object PrimitiveFloatSerializer: NbtSerializer<Float>(Mirror.types.float) {
    override fun deserialize(tag: NbtElement): Float {
        return tag.expectType<AbstractNbtNumber>("tag").floatValue()
    }

    override fun serialize(value: Float): NbtElement {
        return NbtFloat.of(value)
    }
}
internal object PrimitiveLongSerializer: NbtSerializer<Long>(Mirror.types.long) {
    override fun deserialize(tag: NbtElement): Long {
        return tag.expectType<AbstractNbtNumber>("tag").longValue()
    }

    override fun serialize(value: Long): NbtElement {
        return NbtLong.of(value)
    }
}
internal object PrimitiveIntSerializer: NbtSerializer<Int>(Mirror.types.int) {
    override fun deserialize(tag: NbtElement): Int {
        return tag.expectType<AbstractNbtNumber>("tag").intValue()
    }

    override fun serialize(value: Int): NbtElement {
        return NbtInt.of(value)
    }
}
internal object PrimitiveShortSerializer: NbtSerializer<Short>(Mirror.types.short) {
    override fun deserialize(tag: NbtElement): Short {
        return tag.expectType<AbstractNbtNumber>("tag").shortValue()
    }

    override fun serialize(value: Short): NbtElement {
        return NbtShort.of(value)
    }
}
internal object PrimitiveCharSerializer: NbtSerializer<Char>(Mirror.types.char) {
    override fun deserialize(tag: NbtElement): Char {
        return tag.expectType<AbstractNbtNumber>("tag").intValue().toChar()
    }

    override fun serialize(value: Char): NbtElement {
        return NbtInt.of(value.toInt())
    }
}
internal object PrimitiveByteSerializer: NbtSerializer<Byte>(Mirror.types.byte) {
    override fun deserialize(tag: NbtElement): Byte {
        return tag.expectType<AbstractNbtNumber>("tag").byteValue()
    }

    override fun serialize(value: Byte): NbtElement {
        return NbtByte.of(value)
    }
}
internal object PrimitiveBooleanSerializer: NbtSerializer<Boolean>(Mirror.types.boolean) {
    override fun deserialize(tag: NbtElement): Boolean {
        return tag.expectType<AbstractNbtNumber>("tag").byteValue() != 0.toByte()
    }

    override fun serialize(value: Boolean): NbtElement {
        return if(value) NbtByte.ONE else NbtByte.ZERO
    }
}
