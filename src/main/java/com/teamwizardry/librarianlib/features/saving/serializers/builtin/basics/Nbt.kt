package com.teamwizardry.librarianlib.features.saving.serializers.builtin.basics

import com.teamwizardry.librarianlib.features.autoregister.SerializerRegister
import com.teamwizardry.librarianlib.features.kotlin.*
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.saving.FieldType
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.*
import net.minecraft.network.PacketBuffer
import net.minecraftforge.common.util.Constants

/**
 * @author WireSegal
 * Created at 11:45 AM on 1/28/18.
 */

@SerializerRegister(NBTTagByte::class)
object SerializeNBTTagByte : Serializer<NBTTagByte>(FieldType.create(NBTTagByte::class.java)) {
    override fun getDefault()
            = NBTTagByte(0)

    override fun readNBT(nbt: NBTBase, existing: NBTTagByte?, syncing: Boolean): NBTTagByte
            = nbt.safeCast()

    override fun writeNBT(value: NBTTagByte, syncing: Boolean): NBTBase
            = value.copy()

    override fun readBytes(buf: ByteBuf, existing: NBTTagByte?, syncing: Boolean)
            = readTagFromBuffer(Constants.NBT.TAG_BYTE, buf) as NBTTagByte

    override fun writeBytes(buf: ByteBuf, value: NBTTagByte, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

@SerializerRegister(NBTTagShort::class)
object SerializeNBTTagShort : Serializer<NBTTagShort>(FieldType.create(NBTTagShort::class.java)) {
    override fun getDefault()
            = NBTTagShort(0)

    override fun readNBT(nbt: NBTBase, existing: NBTTagShort?, syncing: Boolean): NBTTagShort
            = nbt.safeCast()

    override fun writeNBT(value: NBTTagShort, syncing: Boolean): NBTBase
            = value.copy()

    override fun readBytes(buf: ByteBuf, existing: NBTTagShort?, syncing: Boolean)
            = readTagFromBuffer(Constants.NBT.TAG_SHORT, buf) as NBTTagShort

    override fun writeBytes(buf: ByteBuf, value: NBTTagShort, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

@SerializerRegister(NBTTagInt::class)
object SerializeNBTTagInt : Serializer<NBTTagInt>(FieldType.create(NBTTagInt::class.java)) {
    override fun getDefault()
            = NBTTagInt(0)

    override fun readNBT(nbt: NBTBase, existing: NBTTagInt?, syncing: Boolean): NBTTagInt
            = nbt.safeCast()

    override fun writeNBT(value: NBTTagInt, syncing: Boolean): NBTBase
            = value.copy()

    override fun readBytes(buf: ByteBuf, existing: NBTTagInt?, syncing: Boolean)
            = readTagFromBuffer(Constants.NBT.TAG_INT, buf) as NBTTagInt

    override fun writeBytes(buf: ByteBuf, value: NBTTagInt, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

@SerializerRegister(NBTTagLong::class)
object SerializeNBTTagLong : Serializer<NBTTagLong>(FieldType.create(NBTTagLong::class.java)) {
    override fun getDefault()
            = NBTTagLong(0L)

    override fun readNBT(nbt: NBTBase, existing: NBTTagLong?, syncing: Boolean): NBTTagLong
            = nbt.safeCast()

    override fun writeNBT(value: NBTTagLong, syncing: Boolean): NBTBase
            = value.copy()

    override fun readBytes(buf: ByteBuf, existing: NBTTagLong?, syncing: Boolean)
            = readTagFromBuffer(Constants.NBT.TAG_LONG, buf) as NBTTagLong

    override fun writeBytes(buf: ByteBuf, value: NBTTagLong, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

@SerializerRegister(NBTTagFloat::class)
object SerializeNBTTagFloat : Serializer<NBTTagFloat>(FieldType.create(NBTTagFloat::class.java)) {
    override fun getDefault()
            = NBTTagFloat(0f)

    override fun readNBT(nbt: NBTBase, existing: NBTTagFloat?, syncing: Boolean): NBTTagFloat
            = nbt.safeCast()

    override fun writeNBT(value: NBTTagFloat, syncing: Boolean): NBTBase
            = value.copy()

    override fun readBytes(buf: ByteBuf, existing: NBTTagFloat?, syncing: Boolean)
            = readTagFromBuffer(Constants.NBT.TAG_FLOAT, buf) as NBTTagFloat

    override fun writeBytes(buf: ByteBuf, value: NBTTagFloat, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

@SerializerRegister(NBTTagDouble::class)
object SerializeNBTTagDouble : Serializer<NBTTagDouble>(FieldType.create(NBTTagDouble::class.java)) {
    override fun getDefault()
            = NBTTagDouble(0.0)

    override fun readNBT(nbt: NBTBase, existing: NBTTagDouble?, syncing: Boolean): NBTTagDouble
            = nbt.safeCast()

    override fun writeNBT(value: NBTTagDouble, syncing: Boolean): NBTBase
            = value.copy()

    override fun readBytes(buf: ByteBuf, existing: NBTTagDouble?, syncing: Boolean)
            = readTagFromBuffer(Constants.NBT.TAG_DOUBLE, buf) as NBTTagDouble

    override fun writeBytes(buf: ByteBuf, value: NBTTagDouble, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

@SerializerRegister(NBTTagByteArray::class)
object SerializeNBTTagByteArray : Serializer<NBTTagByteArray>(FieldType.create(NBTTagByteArray::class.java)) {
    override fun getDefault()
            = NBTTagByteArray(byteArrayOf())

    override fun readNBT(nbt: NBTBase, existing: NBTTagByteArray?, syncing: Boolean): NBTTagByteArray
            = nbt.safeCast()

    override fun writeNBT(value: NBTTagByteArray, syncing: Boolean): NBTBase
            = value.copy()

    override fun readBytes(buf: ByteBuf, existing: NBTTagByteArray?, syncing: Boolean)
            = readTagFromBuffer(Constants.NBT.TAG_BYTE_ARRAY, buf) as NBTTagByteArray

    override fun writeBytes(buf: ByteBuf, value: NBTTagByteArray, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

@SerializerRegister(NBTTagString::class)
object SerializeNBTTagString : Serializer<NBTTagString>(FieldType.create(NBTTagString::class.java)) {
    override fun getDefault()
            = NBTTagString("")

    override fun readNBT(nbt: NBTBase, existing: NBTTagString?, syncing: Boolean): NBTTagString
            = nbt.safeCast()

    override fun writeNBT(value: NBTTagString, syncing: Boolean): NBTBase
            = value.copy()

    override fun readBytes(buf: ByteBuf, existing: NBTTagString?, syncing: Boolean)
            = readTagFromBuffer(Constants.NBT.TAG_STRING, buf) as NBTTagString

    override fun writeBytes(buf: ByteBuf, value: NBTTagString, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

@SerializerRegister(NBTTagList::class)
object SerializeNBTTagList : Serializer<NBTTagList>(FieldType.create(NBTTagList::class.java)) {
    override fun getDefault()
            = NBTTagList()

    override fun readNBT(nbt: NBTBase, existing: NBTTagList?, syncing: Boolean): NBTTagList
            = nbt.safeCast()

    override fun writeNBT(value: NBTTagList, syncing: Boolean): NBTBase
            = value.copy()

    override fun readBytes(buf: ByteBuf, existing: NBTTagList?, syncing: Boolean)
            = readTagFromBuffer(Constants.NBT.TAG_LIST, buf) as NBTTagList

    override fun writeBytes(buf: ByteBuf, value: NBTTagList, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

@SerializerRegister(NBTTagCompound::class)
object SerializeNBTTagCompound : Serializer<NBTTagCompound>(FieldType.create(NBTTagCompound::class.java)) {
    override fun getDefault()
            = NBTTagCompound()

    override fun readNBT(nbt: NBTBase, existing: NBTTagCompound?, syncing: Boolean): NBTTagCompound
            = nbt.safeCast()

    override fun writeNBT(value: NBTTagCompound, syncing: Boolean): NBTBase
            = value.copy()

    override fun readBytes(buf: ByteBuf, existing: NBTTagCompound?, syncing: Boolean)
            = readTagFromBuffer(Constants.NBT.TAG_COMPOUND, buf) as NBTTagCompound

    override fun writeBytes(buf: ByteBuf, value: NBTTagCompound, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

@SerializerRegister(NBTTagIntArray::class)
object SerializeNBTTagIntArray : Serializer<NBTTagIntArray>(FieldType.create(NBTTagIntArray::class.java)) {
    override fun getDefault()
            = NBTTagIntArray(intArrayOf())

    override fun readNBT(nbt: NBTBase, existing: NBTTagIntArray?, syncing: Boolean): NBTTagIntArray
            = nbt.safeCast()

    override fun writeNBT(value: NBTTagIntArray, syncing: Boolean): NBTBase
            = value.copy()

    override fun readBytes(buf: ByteBuf, existing: NBTTagIntArray?, syncing: Boolean)
            = readTagFromBuffer(Constants.NBT.TAG_INT_ARRAY, buf) as NBTTagIntArray

    override fun writeBytes(buf: ByteBuf, value: NBTTagIntArray, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

@SerializerRegister(NBTTagLongArray::class)
object SerializeNBTTagLongArray : Serializer<NBTTagLongArray>(FieldType.create(NBTTagLongArray::class.java)) {
    override fun getDefault()
            = NBTTagLongArray(longArrayOf())

    override fun readNBT(nbt: NBTBase, existing: NBTTagLongArray?, syncing: Boolean): NBTTagLongArray
            = nbt.safeCast()

    override fun writeNBT(value: NBTTagLongArray, syncing: Boolean): NBTBase
            = value.copy()

    override fun readBytes(buf: ByteBuf, existing: NBTTagLongArray?, syncing: Boolean)
            = readTagFromBuffer(Constants.NBT.TAG_LONG_ARRAY, buf) as NBTTagLongArray

    override fun writeBytes(buf: ByteBuf, value: NBTTagLongArray, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

@SerializerRegister(NBTBase::class)
object SerializeNBTGeneric : Serializer<NBTBase>(FieldType.create(NBTBase::class.java)) {
    override fun getDefault()
            = NBTTagLongArray(longArrayOf())

    override fun readNBT(nbt: NBTBase, existing: NBTBase?, syncing: Boolean)
            = nbt

    override fun writeNBT(value: NBTBase, syncing: Boolean): NBTBase
            = value.copy()

    override fun readBytes(buf: ByteBuf, existing: NBTBase?, syncing: Boolean)
            = readTagFromBuffer(buf.readByte(), buf)

    override fun writeBytes(buf: ByteBuf, value: NBTBase, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

fun readTagFromBuffer(id: Int, buf: ByteBuf) = readTagFromBuffer(id.toByte(), buf)

fun readTagFromBuffer(id: Byte, buf: ByteBuf): NBTBase {
    return when (id.toInt()) {
        Constants.NBT.TAG_BYTE -> NBTTagByte(buf.readByte())
        Constants.NBT.TAG_SHORT -> NBTTagShort(buf.readShort())
        Constants.NBT.TAG_INT -> NBTTagInt(buf.readInt())
        Constants.NBT.TAG_LONG -> NBTTagLong(buf.readLong())
        Constants.NBT.TAG_FLOAT -> NBTTagFloat(buf.readFloat())
        Constants.NBT.TAG_DOUBLE -> NBTTagDouble(buf.readDouble())
        Constants.NBT.TAG_BYTE_ARRAY -> NBTTagByteArray(PacketBuffer(buf).readByteArray())
        Constants.NBT.TAG_STRING -> NBTTagString(buf.readString())
        Constants.NBT.TAG_LIST -> {
            val type = buf.readByte()
            val size = buf.readInt()
            val list = NBTTagList()
            for (i in 0 until size) list.appendTag(readTagFromBuffer(type, buf))
            list
        }
        Constants.NBT.TAG_COMPOUND -> buf.readTag()
        Constants.NBT.TAG_INT_ARRAY -> NBTTagIntArray(PacketBuffer(buf).readVarIntArray())
        Constants.NBT.TAG_LONG_ARRAY -> NBTTagLongArray(buf.readLongArray())
        else -> NBTTagEnd()
    }
}



val NBTTagLongArray.longArray by MethodHandleHelper.delegateForReadOnly<NBTTagLongArray, LongArray>(NBTTagLongArray::class.java, "field_193587_b", "data")

fun writeTagToBuffer(tag: NBTBase, buf: ByteBuf) {
    when (tag) {
        is NBTTagByte -> buf.writeByte(tag.int)
        is NBTTagShort -> buf.writeShort(tag.int)
        is NBTTagInt -> buf.writeInt(tag.int)
        is NBTTagLong -> buf.writeLong(tag.long)
        is NBTTagFloat -> buf.writeFloat(tag.float)
        is NBTTagDouble -> buf.writeDouble(tag.double)
        is NBTTagByteArray -> PacketBuffer(buf).writeByteArray(tag.byteArray)
        is NBTTagString -> buf.writeString(tag.string)
        is NBTTagList -> {
            buf.writeByte(tag.tagType)
            buf.writeInt(tag.tagCount())
            for (i in tag)
                writeTagToBuffer(i, buf)
        }
        is NBTTagCompound -> buf.writeTag(tag)
        is NBTTagIntArray -> PacketBuffer(buf).writeVarIntArray(tag.intArray)
        is NBTTagLongArray -> PacketBuffer(buf).writeLongArray(tag.longArray)
        else -> NBTTagEnd()
    }
}

fun ByteBuf.readLongArray(): LongArray {
    val i = this.readVarInt()
    return LongArray(i) {
        readLong()
    }
}
