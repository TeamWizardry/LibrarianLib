package com.teamwizardry.librarianlib.scribe.nbt

import net.minecraft.nbt.NbtByte
import net.minecraft.nbt.NbtDouble
import net.minecraft.nbt.NbtFloat
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtLong
import net.minecraft.nbt.AbstractNbtNumber
import net.minecraft.nbt.NbtShort

internal object NumberSerializer: NbtSerializer<Number>() {
    override fun deserialize(tag: NbtElement): Number {
        return tag.expectType<AbstractNbtNumber>("tag").numberValue()
    }

    override fun serialize(value: Number): NbtElement {
        return when(value) {
            is Double -> NbtDouble.of(value)
            is Float -> NbtFloat.of(value)
            is Long -> NbtLong.of(value)
            is Int -> NbtInt.of(value)
            is Short -> NbtShort.of(value)
            is Byte -> NbtByte.of(value)
            else -> NbtDouble.of(value.toDouble())
        }
    }
}
internal object DoubleSerializer: NbtSerializer<Double>() {
    override fun deserialize(tag: NbtElement): Double {
        return tag.expectType<AbstractNbtNumber>("tag").doubleValue()
    }

    override fun serialize(value: Double): NbtElement {
        return NbtDouble.of(value)
    }
}
internal object FloatSerializer: NbtSerializer<Float>() {
    override fun deserialize(tag: NbtElement): Float {
        return tag.expectType<AbstractNbtNumber>("tag").floatValue()
    }

    override fun serialize(value: Float): NbtElement {
        return NbtFloat.of(value)
    }
}
internal object LongSerializer: NbtSerializer<Long>() {
    override fun deserialize(tag: NbtElement): Long {
        return tag.expectType<AbstractNbtNumber>("tag").longValue()
    }

    override fun serialize(value: Long): NbtElement {
        return NbtLong.of(value)
    }
}
internal object IntegerSerializer: NbtSerializer<Int>() {
    override fun deserialize(tag: NbtElement): Int {
        return tag.expectType<AbstractNbtNumber>("tag").intValue()
    }

    override fun serialize(value: Int): NbtElement {
        return NbtInt.of(value)
    }
}
internal object ShortSerializer: NbtSerializer<Short>() {
    override fun deserialize(tag: NbtElement): Short {
        return tag.expectType<AbstractNbtNumber>("tag").shortValue()
    }

    override fun serialize(value: Short): NbtElement {
        return NbtShort.of(value)
    }
}
internal object CharacterSerializer: NbtSerializer<Char>() {
    override fun deserialize(tag: NbtElement): Char {
        return tag.expectType<AbstractNbtNumber>("tag").intValue().toChar()
    }

    override fun serialize(value: Char): NbtElement {
        return NbtInt.of(value.code)
    }
}
internal object ByteSerializer: NbtSerializer<Byte>() {
    override fun deserialize(tag: NbtElement): Byte {
        return tag.expectType<AbstractNbtNumber>("tag").byteValue()
    }

    override fun serialize(value: Byte): NbtElement {
        return NbtByte.of(value)
    }
}
internal object BooleanSerializer: NbtSerializer<Boolean>() {
    override fun deserialize(tag: NbtElement): Boolean {
        return tag.expectType<AbstractNbtNumber>("tag").byteValue() != 0.toByte()
    }

    override fun serialize(value: Boolean): NbtElement {
        return if(value) NbtByte.ONE else NbtByte.ZERO
    }
}

