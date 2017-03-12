package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.primitives

import com.teamwizardry.librarianlib.common.util.*
import com.teamwizardry.librarianlib.common.util.autoregister.SerializerRegister
import com.teamwizardry.librarianlib.common.util.saving.FieldType
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.*

@SerializerRegister(ByteArray::class)
object SerializeByteArray : Serializer<ByteArray>(FieldType.create(ByteArray::class.java)) {
    override fun readNBT(nbt: NBTBase, existing: ByteArray?, syncing: Boolean): ByteArray {
        return nbt.safeCast(NBTTagByteArray::class.java).byteArray
    }

    override fun writeNBT(value: ByteArray, syncing: Boolean): NBTBase {
        return NBTTagByteArray(value)
    }

    override fun readBytes(buf: ByteBuf, existing: ByteArray?, syncing: Boolean): ByteArray {
        val length = buf.readVarInt()
        val arr = if (existing != null && existing.size == length) existing else ByteArray(length)
        arr.indices.forEach { arr[it] = buf.readByte() }
        return arr
    }

    override fun writeBytes(buf: ByteBuf, value: ByteArray, syncing: Boolean) {
        buf.writeVarInt(value.size)
        value.forEach { buf.writeByte(it.toInt()) }
    }

}

@SerializerRegister(CharArray::class)
object SerializeCharArray : Serializer<CharArray>(FieldType.create(CharArray::class.java)) {
    override fun readNBT(nbt: NBTBase, existing: CharArray?, syncing: Boolean): CharArray {
        val list = nbt.safeCast(NBTTagList::class.java)
        val array = if (existing != null && existing.size == list.tagCount()) existing else CharArray(list.tagCount())

        list.forEachIndexed<NBTBase> { i, tag ->
            array[i] = tag.safeCast<NBTPrimitive>().short.toChar()
        }
        return array
    }

    override fun writeNBT(value: CharArray, syncing: Boolean): NBTBase {
        val tag = NBTTagList()
        value.map(Char::toShort).forEach { v -> tag.appendTag(NBTTagShort(v)) }
        return tag
    }

    override fun readBytes(buf: ByteBuf, existing: CharArray?, syncing: Boolean): CharArray {
        val length = buf.readVarInt()
        val arr = if (existing != null && existing.size == length) existing else CharArray(length)
        arr.indices.forEach { arr[it] = buf.readChar() }
        return arr
    }

    override fun writeBytes(buf: ByteBuf, value: CharArray, syncing: Boolean) {
        buf.writeVarInt(value.size)
        value.forEach { buf.writeChar(it.toInt()) }
    }

}

@SerializerRegister(ShortArray::class)
object SerializeShortArray : Serializer<ShortArray>(FieldType.create(ShortArray::class.java)) {
    override fun readNBT(nbt: NBTBase, existing: ShortArray?, syncing: Boolean): ShortArray {
        val list = nbt.safeCast(NBTTagList::class.java)
        val array = if (existing != null && existing.size == list.tagCount()) existing else ShortArray(list.tagCount())

        list.forEachIndexed<NBTBase> { i, tag ->
            array[i] = tag.safeCast<NBTPrimitive>().short
        }
        return array
    }

    override fun writeNBT(value: ShortArray, syncing: Boolean): NBTBase {
        val tag = NBTTagList()
        value.forEach { v -> tag.appendTag(NBTTagShort(v)) }
        return tag
    }

    override fun readBytes(buf: ByteBuf, existing: ShortArray?, syncing: Boolean): ShortArray {
        val length = buf.readVarInt()
        val arr = if (existing != null && existing.size == length) existing else ShortArray(length)
        arr.indices.forEach { arr[it] = buf.readShort() }
        return arr
    }

    override fun writeBytes(buf: ByteBuf, value: ShortArray, syncing: Boolean) {
        buf.writeVarInt(value.size)
        value.forEach { buf.writeShort(it.toInt()) }
    }

}

@SerializerRegister(IntArray::class)
object SerializeIntArray : Serializer<IntArray>(FieldType.create(IntArray::class.java)) {
    override fun readNBT(nbt: NBTBase, existing: IntArray?, syncing: Boolean): IntArray {
        return nbt.safeCast(NBTTagIntArray::class.java).intArray
    }

    override fun writeNBT(value: IntArray, syncing: Boolean): NBTBase {
        return NBTTagIntArray(value)
    }

    override fun readBytes(buf: ByteBuf, existing: IntArray?, syncing: Boolean): IntArray {
        val length = buf.readVarInt()
        val arr = if (existing != null && existing.size == length) existing else IntArray(length)
        arr.indices.forEach { arr[it] = buf.readInt() }
        return arr
    }

    override fun writeBytes(buf: ByteBuf, value: IntArray, syncing: Boolean) {
        buf.writeVarInt(value.size)
        value.forEach { buf.writeInt(it) }
    }

}

@SerializerRegister(LongArray::class)
object SerializeLongArray : Serializer<LongArray>(FieldType.create(LongArray::class.java)) {
    override fun readNBT(nbt: NBTBase, existing: LongArray?, syncing: Boolean): LongArray {
        val list = nbt.safeCast(NBTTagList::class.java)
        val array = if (existing != null && existing.size == list.tagCount()) existing else LongArray(list.tagCount())

        list.forEachIndexed<NBTBase> { i, tag ->
            array[i] = tag.safeCast<NBTPrimitive>().long
        }
        return array
    }

    override fun writeNBT(value: LongArray, syncing: Boolean): NBTBase {
        val tag = NBTTagList()
        value.forEach { v -> tag.appendTag(NBTTagLong(v)) }
        return tag
    }

    override fun readBytes(buf: ByteBuf, existing: LongArray?, syncing: Boolean): LongArray {
        val length = buf.readVarInt()
        val arr = if (existing != null && existing.size == length) existing else LongArray(length)
        arr.indices.forEach { arr[it] = buf.readLong() }
        return arr
    }

    override fun writeBytes(buf: ByteBuf, value: LongArray, syncing: Boolean) {
        buf.writeVarInt(value.size)
        value.forEach { buf.writeLong(it) }
    }

}

@SerializerRegister(FloatArray::class)
object SerializeFloatArray : Serializer<FloatArray>(FieldType.create(FloatArray::class.java)) {
    override fun readNBT(nbt: NBTBase, existing: FloatArray?, syncing: Boolean): FloatArray {
        val list = nbt.safeCast(NBTTagList::class.java)
        val array = if (existing != null && existing.size == list.tagCount()) existing else FloatArray(list.tagCount())

        list.forEachIndexed<NBTBase> { i, tag ->
            array[i] = tag.safeCast<NBTPrimitive>().float
        }
        return array
    }

    override fun writeNBT(value: FloatArray, syncing: Boolean): NBTBase {
        val tag = NBTTagList()
        value.forEach { v -> tag.appendTag(NBTTagFloat(v)) }
        return tag
    }

    override fun readBytes(buf: ByteBuf, existing: FloatArray?, syncing: Boolean): FloatArray {
        val length = buf.readVarInt()
        val arr = if (existing != null && existing.size == length) existing else FloatArray(length)
        arr.indices.forEach { arr[it] = buf.readFloat() }
        return arr
    }

    override fun writeBytes(buf: ByteBuf, value: FloatArray, syncing: Boolean) {
        buf.writeVarInt(value.size)
        value.forEach { buf.writeFloat(it) }
    }

}

@SerializerRegister(DoubleArray::class)
object SerializeDoubleArray : Serializer<DoubleArray>(FieldType.create(DoubleArray::class.java)) {
    override fun readNBT(nbt: NBTBase, existing: DoubleArray?, syncing: Boolean): DoubleArray {
        val list = nbt.safeCast(NBTTagList::class.java)
        val array = if (existing != null && existing.size == list.tagCount()) existing else DoubleArray(list.tagCount())

        list.forEachIndexed<NBTBase> { i, tag ->
            array[i] = tag.safeCast<NBTPrimitive>().double
        }
        return array
    }

    override fun writeNBT(value: DoubleArray, syncing: Boolean): NBTBase {
        val tag = NBTTagList()
        value.forEach { v -> tag.appendTag(NBTTagDouble(v)) }
        return tag
    }

    override fun readBytes(buf: ByteBuf, existing: DoubleArray?, syncing: Boolean): DoubleArray {
        val length = buf.readVarInt()
        val arr = if (existing != null && existing.size == length) existing else DoubleArray(length)
        arr.indices.forEach { arr[it] = buf.readDouble() }
        return arr
    }

    override fun writeBytes(buf: ByteBuf, value: DoubleArray, syncing: Boolean) {
        buf.writeVarInt(value.size)
        value.forEach { buf.writeDouble(it) }
    }

}

@SerializerRegister(BooleanArray::class)
object SerializeBooleanArray : Serializer<BooleanArray>(FieldType.create(BooleanArray::class.java)) {
    override fun readNBT(nbt: NBTBase, existing: BooleanArray?, syncing: Boolean): BooleanArray {
        val list = nbt.safeCast(NBTTagList::class.java)
        val array = if (existing != null && existing.size == list.tagCount()) existing else BooleanArray(list.tagCount())

        list.forEachIndexed<NBTBase> { i, tag ->
            array[i] = tag.safeCast<NBTPrimitive>().byte != 0.toByte()
        }
        return array
    }

    override fun writeNBT(value: BooleanArray, syncing: Boolean): NBTBase {
        val tag = NBTTagList()
        value.forEach { v -> tag.appendTag(NBTTagByte(if(v) 1 else 0)) }
        return tag
    }

    override fun readBytes(buf: ByteBuf, existing: BooleanArray?, syncing: Boolean): BooleanArray {
        val length = buf.readVarInt()
        val arr = if (existing != null && existing.size == length) existing else BooleanArray(length)
        arr.indices.forEach { arr[it] = buf.readBoolean() }
        return arr
    }

    override fun writeBytes(buf: ByteBuf, value: BooleanArray, syncing: Boolean) {
        buf.writeVarInt(value.size)
        value.forEach { buf.writeBoolean(it) }
    }

}

@SerializerRegister(Array<String>::class)
object SerializeStringArray : Serializer<Array<String>>(FieldType.create(Array<String>::class.java)) {
    override fun readNBT(nbt: NBTBase, existing: Array<String>?, syncing: Boolean): Array<String> {
        val list = nbt.safeCast(NBTTagList::class.java)
        if(existing != null && existing.size == list.tagCount()) {
            list.forEachIndexed<NBTBase> { i, tag ->
                existing[i] = tag.safeCast<NBTTagString>().string
            }
            return existing
        } else {
            return Array(list.tagCount()) { list.getStringTagAt(it) }
        }
    }

    override fun writeNBT(value: Array<String>, syncing: Boolean): NBTBase {
        val tag = NBTTagList()
        value.forEach { v -> tag.appendTag(NBTTagString(v)) }
        return tag
    }

    override fun readBytes(buf: ByteBuf, existing: Array<String>?, syncing: Boolean): Array<String> {
        val length = buf.readVarInt()
        if (existing != null && existing.size == length) {
            existing.indices.forEach { existing[it] = buf.readString() }
            return existing
        } else {
            return Array(length) { buf.readString() }
        }
    }

    override fun writeBytes(buf: ByteBuf, value: Array<String>, syncing: Boolean) {
        buf.writeVarInt(value.size)
        value.forEach { buf.writeString(it) }
    }

}
