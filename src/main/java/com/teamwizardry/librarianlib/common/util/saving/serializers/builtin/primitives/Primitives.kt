package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.primitives

import com.teamwizardry.librarianlib.common.util.autoregister.SerializerRegister
import com.teamwizardry.librarianlib.common.util.readString
import com.teamwizardry.librarianlib.common.util.safeCast
import com.teamwizardry.librarianlib.common.util.saving.FieldType
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.writeString
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.*

/**
 * Created by TheCodeWarrior
 */

@SerializerRegister(Byte::class)
object SerializeByte: Serializer<Byte>(FieldType.create(Byte::class.javaPrimitiveType!!)) {
    override fun readBytes(buf: ByteBuf, existing: Byte?, syncing: Boolean): Byte {
        return buf.readByte()
    }

    override fun writeBytes(buf: ByteBuf, value: Byte, syncing: Boolean) {
        buf.writeByte(value.toInt())
    }

    override fun readNBT(nbt: NBTBase, existing: Byte?, syncing: Boolean): Byte {
        return nbt.safeCast<NBTTagByte>().short.toByte()
    }

    override fun writeNBT(value: Byte, syncing: Boolean): NBTBase {
        return NBTTagByte(value)
    }
}

@SerializerRegister(Char::class)
object SerializeChar : Serializer<Char>(FieldType.create(Char::class.javaPrimitiveType!!)) {
    override fun readBytes(buf: ByteBuf, existing: Char?, syncing: Boolean): Char {
        return buf.readChar()
    }

    override fun writeBytes(buf: ByteBuf, value: Char, syncing: Boolean) {
        buf.writeChar(value.toInt())
    }

    override fun readNBT(nbt: NBTBase, existing: Char?, syncing: Boolean): Char {
        return nbt.safeCast<NBTTagShort>().short.toChar()
    }

    override fun writeNBT(value: Char, syncing: Boolean): NBTBase {
        return NBTTagShort(value.toShort())
    }
}

@SerializerRegister(Short::class)
object SerializeShort: Serializer<Short>(FieldType.create(Short::class.javaPrimitiveType!!)) {
    override fun readBytes(buf: ByteBuf, existing: Short?, syncing: Boolean): Short {
        return buf.readShort()
    }

    override fun writeBytes(buf: ByteBuf, value: Short, syncing: Boolean) {
        buf.writeShort(value.toInt())
    }

    override fun readNBT(nbt: NBTBase, existing: Short?, syncing: Boolean): Short {
        return nbt.safeCast<NBTTagShort>().short
    }

    override fun writeNBT(value: Short, syncing: Boolean): NBTBase {
        return NBTTagShort(value)
    }
}

@SerializerRegister(Int::class)
object SerializeInt: Serializer<Int>(FieldType.create(Int::class.javaPrimitiveType!!)) {
    override fun readBytes(buf: ByteBuf, existing: Int?, syncing: Boolean): Int {
        return buf.readInt()
    }

    override fun writeBytes(buf: ByteBuf, value: Int, syncing: Boolean) {
        buf.writeInt(value)
    }

    override fun readNBT(nbt: NBTBase, existing: Int?, syncing: Boolean): Int {
        return nbt.safeCast<NBTTagInt>().int
    }

    override fun writeNBT(value: Int, syncing: Boolean): NBTBase {
        return NBTTagInt(value)
    }
}

@SerializerRegister(Long::class)
object SerializeLong: Serializer<Long>(FieldType.create(Long::class.javaPrimitiveType!!)) {
    override fun readBytes(buf: ByteBuf, existing: Long?, syncing: Boolean): Long {
        return buf.readLong()
    }

    override fun writeBytes(buf: ByteBuf, value: Long, syncing: Boolean) {
        buf.writeLong(value)
    }

    override fun readNBT(nbt: NBTBase, existing: Long?, syncing: Boolean): Long {
        return nbt.safeCast<NBTTagLong>().long
    }

    override fun writeNBT(value: Long, syncing: Boolean): NBTBase {
        return NBTTagLong(value)
    }
}

@SerializerRegister(Float::class)
object SerializeFloat: Serializer<Float>(FieldType.create(Float::class.javaPrimitiveType!!)) {
    override fun readBytes(buf: ByteBuf, existing: Float?, syncing: Boolean): Float {
        return buf.readFloat()
    }

    override fun writeBytes(buf: ByteBuf, value: Float, syncing: Boolean) {
        buf.writeFloat(value)
    }

    override fun readNBT(nbt: NBTBase, existing: Float?, syncing: Boolean): Float {
        return nbt.safeCast<NBTTagFloat>().float
    }

    override fun writeNBT(value: Float, syncing: Boolean): NBTBase {
        return NBTTagFloat(value)
    }
}

@SerializerRegister(Double::class)
object SerializeDouble: Serializer<Double>(FieldType.create(Double::class.javaPrimitiveType!!)) {
    override fun readBytes(buf: ByteBuf, existing: Double?, syncing: Boolean): Double {
        return buf.readDouble()
    }

    override fun writeBytes(buf: ByteBuf, value: Double, syncing: Boolean) {
        buf.writeDouble(value)
    }

    override fun readNBT(nbt: NBTBase, existing: Double?, syncing: Boolean): Double {
        return nbt.safeCast<NBTTagDouble>().double
    }

    override fun writeNBT(value: Double, syncing: Boolean): NBTBase {
        return NBTTagDouble(value)
    }
}

@SerializerRegister(Boolean::class)
object SerializeBoolean: Serializer<Boolean>(FieldType.create(Boolean::class.javaPrimitiveType!!)) {
    override fun readBytes(buf: ByteBuf, existing: Boolean?, syncing: Boolean): Boolean {
        return buf.readBoolean()
    }

    override fun writeBytes(buf: ByteBuf, value: Boolean, syncing: Boolean) {
        buf.writeBoolean(value)
    }

    override fun readNBT(nbt: NBTBase, existing: Boolean?, syncing: Boolean): Boolean {
        return nbt.safeCast<NBTTagByte>().byte != 0.toByte()
    }

    override fun writeNBT(value: Boolean, syncing: Boolean): NBTBase {
        return NBTTagByte(if(value) 1 else 0)
    }
}

@SerializerRegister(String::class)
object SerializeString: Serializer<String>(FieldType.create(String::class.javaPrimitiveType!!)) {
    override fun readBytes(buf: ByteBuf, existing: String?, syncing: Boolean): String {
        return buf.readString()
    }

    override fun writeBytes(buf: ByteBuf, value: String, syncing: Boolean) {
        buf.writeString(value)
    }

    override fun readNBT(nbt: NBTBase, existing: String?, syncing: Boolean): String {
        return nbt.safeCast<NBTTagString>().string
    }

    override fun writeNBT(value: String, syncing: Boolean): NBTBase {
        return NBTTagString(value)
    }
}
