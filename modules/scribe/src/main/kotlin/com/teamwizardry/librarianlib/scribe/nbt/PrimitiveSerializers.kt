package com.teamwizardry.librarianlib.scribe.nbt

import dev.thecodewarrior.mirror.Mirror
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.AbstractNumberTag
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag

internal object StringSerializer: NbtSerializer<String>() {
    override fun deserialize(tag: Tag, existing: String?): String {
        return tag.expectType<StringTag>("tag").asString()
    }

    override fun serialize(value: String): Tag {
        return StringTag.of(value)
    }
}

internal object PrimitiveDoubleSerializer: NbtSerializer<Double>(Mirror.types.double) {
    override fun deserialize(tag: Tag, existing: Double?): Double {
        return tag.expectType<AbstractNumberTag>("tag").double
    }

    override fun serialize(value: Double): Tag {
        return DoubleTag.of(value)
    }
}
internal object PrimitiveFloatSerializer: NbtSerializer<Float>(Mirror.types.float) {
    override fun deserialize(tag: Tag, existing: Float?): Float {
        return tag.expectType<AbstractNumberTag>("tag").float
    }

    override fun serialize(value: Float): Tag {
        return FloatTag.of(value)
    }
}
internal object PrimitiveLongSerializer: NbtSerializer<Long>(Mirror.types.long) {
    override fun deserialize(tag: Tag, existing: Long?): Long {
        return tag.expectType<AbstractNumberTag>("tag").long
    }

    override fun serialize(value: Long): Tag {
        return LongTag.of(value)
    }
}
internal object PrimitiveIntSerializer: NbtSerializer<Int>(Mirror.types.int) {
    override fun deserialize(tag: Tag, existing: Int?): Int {
        return tag.expectType<AbstractNumberTag>("tag").int
    }

    override fun serialize(value: Int): Tag {
        return IntTag.of(value)
    }
}
internal object PrimitiveShortSerializer: NbtSerializer<Short>(Mirror.types.short) {
    override fun deserialize(tag: Tag, existing: Short?): Short {
        return tag.expectType<AbstractNumberTag>("tag").short
    }

    override fun serialize(value: Short): Tag {
        return ShortTag.of(value)
    }
}
internal object PrimitiveCharSerializer: NbtSerializer<Char>(Mirror.types.char) {
    override fun deserialize(tag: Tag, existing: Char?): Char {
        return tag.expectType<AbstractNumberTag>("tag").int.toChar()
    }

    override fun serialize(value: Char): Tag {
        return IntTag.of(value.toInt())
    }
}
internal object PrimitiveByteSerializer: NbtSerializer<Byte>(Mirror.types.byte) {
    override fun deserialize(tag: Tag, existing: Byte?): Byte {
        return tag.expectType<AbstractNumberTag>("tag").byte
    }

    override fun serialize(value: Byte): Tag {
        return ByteTag.of(value)
    }
}
internal object PrimitiveBooleanSerializer: NbtSerializer<Boolean>(Mirror.types.boolean) {
    override fun deserialize(tag: Tag, existing: Boolean?): Boolean {
        return tag.expectType<AbstractNumberTag>("tag").byte != 0.toByte()
    }

    override fun serialize(value: Boolean): Tag {
        return if(value) ByteTag.ONE else ByteTag.ZERO
    }
}
