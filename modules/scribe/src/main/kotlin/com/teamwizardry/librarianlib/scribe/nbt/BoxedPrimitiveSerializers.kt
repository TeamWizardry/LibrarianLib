package com.teamwizardry.librarianlib.scribe.nbt

import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.AbstractNumberTag
import net.minecraft.nbt.ShortTag

internal object NumberSerializer: NbtSerializer<Number>() {
    override fun deserialize(tag: Tag, existing: Number?): Number {
        return tag.expectType<AbstractNumberTag>("tag").number
    }

    override fun serialize(value: Number): Tag {
        return when(value) {
            is Double -> DoubleTag.of(value)
            is Float -> FloatTag.of(value)
            is Long -> LongTag.of(value)
            is Int -> IntTag.of(value)
            is Short -> ShortTag.of(value)
            is Byte -> ByteTag.of(value)
            else -> DoubleTag.of(value.toDouble())
        }
    }
}
internal object DoubleSerializer: NbtSerializer<Double>() {
    override fun deserialize(tag: Tag, existing: Double?): Double {
        return tag.expectType<AbstractNumberTag>("tag").double
    }

    override fun serialize(value: Double): Tag {
        return DoubleTag.of(value)
    }
}
internal object FloatSerializer: NbtSerializer<Float>() {
    override fun deserialize(tag: Tag, existing: Float?): Float {
        return tag.expectType<AbstractNumberTag>("tag").float
    }

    override fun serialize(value: Float): Tag {
        return FloatTag.of(value)
    }
}
internal object LongSerializer: NbtSerializer<Long>() {
    override fun deserialize(tag: Tag, existing: Long?): Long {
        return tag.expectType<AbstractNumberTag>("tag").long
    }

    override fun serialize(value: Long): Tag {
        return LongTag.of(value)
    }
}
internal object IntegerSerializer: NbtSerializer<Int>() {
    override fun deserialize(tag: Tag, existing: Int?): Int {
        return tag.expectType<AbstractNumberTag>("tag").int
    }

    override fun serialize(value: Int): Tag {
        return IntTag.of(value)
    }
}
internal object ShortSerializer: NbtSerializer<Short>() {
    override fun deserialize(tag: Tag, existing: Short?): Short {
        return tag.expectType<AbstractNumberTag>("tag").short
    }

    override fun serialize(value: Short): Tag {
        return ShortTag.of(value)
    }
}
internal object CharacterSerializer: NbtSerializer<Char>() {
    override fun deserialize(tag: Tag, existing: Char?): Char {
        return tag.expectType<AbstractNumberTag>("tag").int.toChar()
    }

    override fun serialize(value: Char): Tag {
        return IntTag.of(value.code)
    }
}
internal object ByteSerializer: NbtSerializer<Byte>() {
    override fun deserialize(tag: Tag, existing: Byte?): Byte {
        return tag.expectType<AbstractNumberTag>("tag").byte
    }

    override fun serialize(value: Byte): Tag {
        return ByteTag.of(value)
    }
}
internal object BooleanSerializer: NbtSerializer<Boolean>() {
    override fun deserialize(tag: Tag, existing: Boolean?): Boolean {
        return tag.expectType<AbstractNumberTag>("tag").byte != 0.toByte()
    }

    override fun serialize(value: Boolean): Tag {
        return if(value) ByteTag.ONE else ByteTag.ZERO
    }
}

