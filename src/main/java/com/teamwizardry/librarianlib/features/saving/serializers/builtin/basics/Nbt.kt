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
object SerializeNBTTagByte : NBTSerializer<NBTTagByte>(NBTTagByte::class.java)

@SerializerRegister(NBTTagShort::class)
object SerializeNBTTagShort : NBTSerializer<NBTTagShort>(NBTTagShort::class.java)

@SerializerRegister(NBTTagInt::class)
object SerializeNBTTagInt : NBTSerializer<NBTTagInt>(NBTTagInt::class.java)

@SerializerRegister(NBTTagLong::class)
object SerializeNBTTagLong : NBTSerializer<NBTTagLong>(NBTTagLong::class.java)

@SerializerRegister(NBTTagFloat::class)
object SerializeNBTTagFloat : NBTSerializer<NBTTagFloat>(NBTTagFloat::class.java)

@SerializerRegister(NBTTagDouble::class)
object SerializeNBTTagDouble : NBTSerializer<NBTTagDouble>(NBTTagDouble::class.java)

@SerializerRegister(NBTTagByteArray::class)
object SerializeNBTTagByteArray : NBTSerializer<NBTTagByteArray>(NBTTagByteArray::class.java)

@SerializerRegister(NBTTagString::class)
object SerializeNBTTagString : NBTSerializer<NBTTagString>(NBTTagString::class.java)

@SerializerRegister(NBTTagList::class)
object SerializeNBTTagList : NBTSerializer<NBTTagList>(NBTTagList::class.java)

@SerializerRegister(NBTTagCompound::class)
object SerializeNBTTagCompound : NBTSerializer<NBTTagCompound>(NBTTagCompound::class.java)

@SerializerRegister(NBTTagIntArray::class)
object SerializeNBTTagIntArray : NBTSerializer<NBTTagIntArray>(NBTTagIntArray::class.java)

@SerializerRegister(NBTTagLongArray::class)
object SerializeNBTTagLongArray : NBTSerializer<NBTTagLongArray>(NBTTagLongArray::class.java)

@SerializerRegister(NBTBase::class)
object SerializeNBTGeneric : GenericNBTSerializer<NBTBase>(NBTBase::class.java)

@SerializerRegister(NBTPrimitive::class)
object SerializeNBTPrimitive : GenericNBTSerializer<NBTPrimitive>(NBTPrimitive::class.java)


open class GenericNBTSerializer<T : NBTBase>(val clazz: Class<T>) : Serializer<T>(FieldType.create(clazz)) {

    override fun getDefault(): T
            = clazz.defaultNBT()

    override fun readNBT(nbt: NBTBase, existing: T?, syncing: Boolean): T
            = nbt.safeCast(clazz)

    override fun writeNBT(value: T, syncing: Boolean): NBTBase
            = value.copy()

    @Suppress("UNCHECKED_CAST")
    override fun readBytes(buf: ByteBuf, existing: T?, syncing: Boolean): T
            = readTagFromBuffer(buf.readByte(), buf) as T

    override fun writeBytes(buf: ByteBuf, value: T, syncing: Boolean)
            = writeTagToBuffer(value, buf)
}

open class NBTSerializer<T : NBTBase>(clazz: Class<T>) : GenericNBTSerializer<T>(clazz) {
    val id = clazz.idForClazz().toByte()

    @Suppress("UNCHECKED_CAST")
    override fun readBytes(buf: ByteBuf, existing: T?, syncing: Boolean): T
            = readTagFromBuffer(id, buf) as T
}

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
            NBTTagList(size) { readTagFromBuffer(type, buf) }
        }
        Constants.NBT.TAG_COMPOUND -> buf.readTag()
        Constants.NBT.TAG_INT_ARRAY -> NBTTagIntArray(PacketBuffer(buf).readVarIntArray())
        Constants.NBT.TAG_LONG_ARRAY -> NBTTagLongArray(LongArray(buf.readVarInt()) { buf.readLong() })
        else -> NBTTagCompound()
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
    }
}
