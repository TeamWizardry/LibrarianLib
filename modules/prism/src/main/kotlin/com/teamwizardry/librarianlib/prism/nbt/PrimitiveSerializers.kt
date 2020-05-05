package com.teamwizardry.librarianlib.prism.nbt

import dev.thecodewarrior.mirror.Mirror
import net.minecraft.nbt.ByteNBT
import net.minecraft.nbt.DoubleNBT
import net.minecraft.nbt.FloatNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.IntNBT
import net.minecraft.nbt.LongNBT
import net.minecraft.nbt.NumberNBT
import net.minecraft.nbt.ShortNBT
import net.minecraft.nbt.StringNBT

object StringSerializer: NBTSerializer<String>() {
    override fun deserialize(tag: INBT, existing: String?): String {
        return tag.expectType<StringNBT>("tag").string
    }

    override fun serialize(value: String): INBT {
        return StringNBT.valueOf(value)
    }
}

object PrimitiveDoubleSerializer: NBTSerializer<Double>(Mirror.types.double) {
    override fun deserialize(tag: INBT, existing: Double?): Double {
        return tag.expectType<NumberNBT>("tag").double
    }

    override fun serialize(value: Double): INBT {
        return DoubleNBT.valueOf(value)
    }
}
object PrimitiveFloatSerializer: NBTSerializer<Float>(Mirror.types.float) {
    override fun deserialize(tag: INBT, existing: Float?): Float {
        return tag.expectType<NumberNBT>("tag").float
    }

    override fun serialize(value: Float): INBT {
        return FloatNBT.valueOf(value)
    }
}
object PrimitiveLongSerializer: NBTSerializer<Long>(Mirror.types.long) {
    override fun deserialize(tag: INBT, existing: Long?): Long {
        return tag.expectType<NumberNBT>("tag").long
    }

    override fun serialize(value: Long): INBT {
        return LongNBT.valueOf(value)
    }
}
object PrimitiveIntSerializer: NBTSerializer<Int>(Mirror.types.int) {
    override fun deserialize(tag: INBT, existing: Int?): Int {
        return tag.expectType<NumberNBT>("tag").int
    }

    override fun serialize(value: Int): INBT {
        return IntNBT.valueOf(value)
    }
}
object PrimitiveShortSerializer: NBTSerializer<Short>(Mirror.types.short) {
    override fun deserialize(tag: INBT, existing: Short?): Short {
        return tag.expectType<NumberNBT>("tag").short
    }

    override fun serialize(value: Short): INBT {
        return ShortNBT.valueOf(value)
    }
}
object PrimitiveCharSerializer: NBTSerializer<Char>(Mirror.types.char) {
    override fun deserialize(tag: INBT, existing: Char?): Char {
        return tag.expectType<NumberNBT>("tag").int.toChar()
    }

    override fun serialize(value: Char): INBT {
        return IntNBT.valueOf(value.toInt())
    }
}
object PrimitiveByteSerializer: NBTSerializer<Byte>(Mirror.types.byte) {
    override fun deserialize(tag: INBT, existing: Byte?): Byte {
        return tag.expectType<NumberNBT>("tag").byte
    }

    override fun serialize(value: Byte): INBT {
        return ByteNBT.valueOf(value)
    }
}
object PrimitiveBooleanSerializer: NBTSerializer<Boolean>(Mirror.types.boolean) {
    override fun deserialize(tag: INBT, existing: Boolean?): Boolean {
        return tag.expectType<NumberNBT>("tag").byte != 0.toByte()
    }

    override fun serialize(value: Boolean): INBT {
        return if(value) ByteNBT.ONE else ByteNBT.ZERO
    }
}
