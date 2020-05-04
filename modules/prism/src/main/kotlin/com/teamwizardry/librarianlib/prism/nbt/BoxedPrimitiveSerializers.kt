package com.teamwizardry.librarianlib.prism.nbt

import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.nbt.ByteNBT
import net.minecraft.nbt.DoubleNBT
import net.minecraft.nbt.FloatNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.IntNBT
import net.minecraft.nbt.LongNBT
import net.minecraft.nbt.NumberNBT
import net.minecraft.nbt.ShortNBT

object NumberSerializer: NBTSerializer<Number>() {
    override fun deserialize(tag: INBT, existing: Number?): Number {
        return tag.expectType<NumberNBT>("tag").asNumber
    }

    override fun serialize(value: Number): INBT {
        return when(value) {
            is Double -> DoubleNBT.valueOf(value)
            is Float -> FloatNBT.valueOf(value)
            is Long -> LongNBT.valueOf(value)
            is Int -> IntNBT.valueOf(value)
            is Short -> ShortNBT.valueOf(value)
            is Byte -> ByteNBT.valueOf(value)
            else -> DoubleNBT.valueOf(value.toDouble())
        }
    }
}
object DoubleSerializer: NBTSerializer<Double>() {
    override fun deserialize(tag: INBT, existing: Double?): Double {
        return tag.expectType<NumberNBT>("tag").double
    }

    override fun serialize(value: Double): INBT {
        return DoubleNBT.valueOf(value)
    }
}
object FloatSerializer: NBTSerializer<Float>() {
    override fun deserialize(tag: INBT, existing: Float?): Float {
        return tag.expectType<NumberNBT>("tag").float
    }

    override fun serialize(value: Float): INBT {
        return FloatNBT.valueOf(value)
    }
}
object LongSerializer: NBTSerializer<Long>() {
    override fun deserialize(tag: INBT, existing: Long?): Long {
        return tag.expectType<NumberNBT>("tag").long
    }

    override fun serialize(value: Long): INBT {
        return LongNBT.valueOf(value)
    }
}
object IntSerializer: NBTSerializer<Int>() {
    override fun deserialize(tag: INBT, existing: Int?): Int {
        return tag.expectType<NumberNBT>("tag").int
    }

    override fun serialize(value: Int): INBT {
        return IntNBT.valueOf(value)
    }
}
object ShortSerializer: NBTSerializer<Short>() {
    override fun deserialize(tag: INBT, existing: Short?): Short {
        return tag.expectType<NumberNBT>("tag").short
    }

    override fun serialize(value: Short): INBT {
        return ShortNBT.valueOf(value)
    }
}
object CharSerializer: NBTSerializer<Char>() {
    override fun deserialize(tag: INBT, existing: Char?): Char {
        return tag.expectType<NumberNBT>("tag").int.toChar()
    }

    override fun serialize(value: Char): INBT {
        return IntNBT.valueOf(value.toInt())
    }
}
object ByteSerializer: NBTSerializer<Byte>() {
    override fun deserialize(tag: INBT, existing: Byte?): Byte {
        return tag.expectType<NumberNBT>("tag").byte
    }

    override fun serialize(value: Byte): INBT {
        return ByteNBT.valueOf(value)
    }
}
object BooleanSerializer: NBTSerializer<Boolean>() {
    override fun deserialize(tag: INBT, existing: Boolean?): Boolean {
        return tag.expectType<NumberNBT>("tag").byte != 0.toByte()
    }

    override fun serialize(value: Boolean): INBT {
        return if(value) ByteNBT.ONE else ByteNBT.ZERO
    }
}

