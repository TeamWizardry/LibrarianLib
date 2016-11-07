package com.teamwizardry.librarianlib.common.util.saving

import com.teamwizardry.librarianlib.common.util.*
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraftforge.items.ItemStackHandler
import java.awt.Color
import java.util.*

/**
 * @author WireSegal
 * Created at 10:32 PM on 10/27/16.
 */
object ByteBufSerializationHandlers {
    private val map = HashMap<Class<*>, BufferSerializer>()

    init {
        // Primitives and String
        mapHandler(Char::class.javaPrimitiveType!!, { buf, obj -> buf.writeChar(obj.toInt()) }, ByteBuf::readChar)
        mapHandler(Byte::class.javaPrimitiveType!!, { buf, obj -> buf.writeByte(obj.toInt()) }, ByteBuf::readByte)
        mapHandler(Short::class.javaPrimitiveType!!, { buf, obj -> buf.writeShort(obj.toInt()) }, ByteBuf::readShort)
        mapHandler(Int::class.javaPrimitiveType!!, ByteBuf::writeInt, ByteBuf::readInt)
        mapHandler(Long::class.javaPrimitiveType!!, ByteBuf::writeLong, ByteBuf::readLong)

        mapHandler(Float::class.javaPrimitiveType!!, ByteBuf::writeFloat, ByteBuf::readFloat)
        mapHandler(Double::class.javaPrimitiveType!!, ByteBuf::writeDouble, ByteBuf::readDouble)
        mapHandler(Boolean::class.javaPrimitiveType!!, ByteBuf::writeBoolean, ByteBuf::readBoolean)
        mapHandler(String::class.java, ByteBuf::writeString, ByteBuf::readString)

        // Primitive arrays
        mapHandler((CharArray(0)).javaClass, { buf, obj ->
            val len = obj.size
            buf.writeVarInt(len)
            for (i in 0..len - 1) {
                buf.writeChar(obj[i].toInt())
            }
        }, reader@{ buf ->
            val len = buf.readVarInt()
            val array = CharArray(len)
            for (i in 0..len - 1) {
                array[i] = buf.readChar()
            }
            return@reader array
        })
        mapHandler((ByteArray(0)).javaClass, { buf, obj ->
            val len = obj.size
            buf.writeVarInt(len)
            for (i in 0..len - 1) {
                buf.writeByte(obj[i].toInt())
            }
        }, reader@{ buf ->
            val len = buf.readVarInt()
            val array = ByteArray(len)
            for (i in 0..len - 1) {
                array[i] = buf.readByte()
            }
            return@reader array
        })
        mapHandler((ShortArray(0)).javaClass, { buf, obj ->
            val len = obj.size
            buf.writeVarInt(len)
            for (i in 0..len - 1) {
                buf.writeShort(obj[i].toInt())
            }
        }, reader@{ buf ->
            val len = buf.readVarInt()
            val array = ShortArray(len)
            for (i in 0..len - 1) {
                array[i] = buf.readShort()
            }
            return@reader array
        })
        mapHandler((IntArray(0)).javaClass, { buf, obj ->
            val len = obj.size
            buf.writeVarInt(len)
            for (i in 0..len - 1) {
                buf.writeInt(obj[i])
            }
        }, reader@{ buf ->
            val len = buf.readVarInt()
            val array = IntArray(len)
            for (i in 0..len - 1) {
                array[i] = buf.readInt()
            }
            return@reader array
        })
        mapHandler((LongArray(0)).javaClass, { buf, obj ->
            val len = obj.size
            buf.writeVarInt(len)
            for (i in 0..len - 1) {
                buf.writeLong(obj[i])
            }
        }, reader@{ buf ->
            val len = buf.readVarInt()
            val array = LongArray(len)
            for (i in 0..len - 1) {
                array[i] = buf.readLong()
            }
            return@reader array
        })

        mapHandler((FloatArray(0)).javaClass, { buf, obj ->
            val len = obj.size.toShort()
            buf.writeShort(len.toInt())
            for (i in 0..len - 1) {
                buf.writeFloat(obj[i])
            }
        }, reader@{ buf ->
            val len = buf.readShort().toInt()
            val array = FloatArray(len)
            for (i in 0..len - 1) {
                array[i] = buf.readFloat()
            }
            return@reader array
        })
        mapHandler((DoubleArray(0)).javaClass, { buf, obj ->
            val len = obj.size.toShort()
            buf.writeShort(len.toInt())
            for (i in 0..len - 1) {
                buf.writeDouble(obj[i])
            }
        }, reader@{ buf ->
            val len = buf.readShort().toInt()
            val array = DoubleArray(len)
            for (i in 0..len - 1) {
                array[i] = buf.readDouble()
            }
            return@reader array
        })
        mapHandler((BooleanArray(0)).javaClass, ByteBuf::writeBooleanArray, ByteBuf::readBooleanArray)

        // Misc.
        mapHandler(Color::class.java, { buf, obj -> buf.writeInt(obj.rgb) }, { Color(it.readInt(), true) })
        mapHandler(NBTTagCompound::class.java, ByteBuf::writeTag, ByteBuf::readTag)
        mapHandler(ItemStack::class.java, ByteBuf::writeStack, ByteBuf::readStack)
        mapHandler(ItemStackHandler::class.java, {
            buf, obj ->
            buf.writeTag(obj.serializeNBT())
        }, {
            val handler = ItemStackHandler()
            handler.deserializeNBT(it.readTag())
            handler
        })
        mapHandler(ItemStackHandler::class.java, {
            buf, obj ->
            buf.writeTag(obj.serializeNBT())
        }, {
            val handler = ItemStackHandler()
            handler.deserializeNBT(it.readTag())
            handler
        })

        // Vectors
        mapHandler(Vec3d::class.java, {
            buf, obj ->
            buf.writeDouble(obj.xCoord).writeDouble(obj.yCoord).writeDouble(obj.zCoord)
        }, {
            Vec3d(it.readDouble(), it.readDouble(), it.readDouble())
        })
        mapHandler(Vec3i::class.java, {
            buf, obj ->
            buf.writeInt(obj.x).writeInt(obj.y).writeInt(obj.z)
        }, {
            Vec3i(it.readInt(), it.readInt(), it.readInt())
        })
        mapHandler(Vec2d::class.java, {
            buf, obj ->
            buf.writeDouble(obj.x).writeDouble(obj.y)
        }, {
            Vec2d(it.readDouble(), it.readDouble())
        })
        mapHandler(BlockPos::class.java, { buf, obj -> buf.writeLong(obj.toLong()) }, { BlockPos.fromLong(it.readLong()) })
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> mapHandler(clazz: Class<T>, writer: (ByteBuf, T) -> Any?, reader: (ByteBuf) -> T) {
        map.put(clazz, BufferSerializer((reader as (ByteBuf) -> Any), { buf: ByteBuf, obj: Any -> writer(buf, obj as T) } as (ByteBuf, Any) -> Unit))
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getWriter(clazz: Class<T>): ((ByteBuf, T) -> Unit)? {
        val unchecked = getWriterUnchecked(clazz) ?: return null
        return unchecked as (ByteBuf, T) -> Unit
    }

    @JvmStatic
    fun getWriterUnchecked(clazz: Class<*>): ((ByteBuf, Any) -> Unit)? {
        val pair = map[clazz] ?: createSpecialMapping(clazz) ?: return null
        return pair.write
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getReader(clazz: Class<T>): ((ByteBuf) -> T)? {
        val unchecked = getReaderUnchecked(clazz) ?: return null
        return unchecked as (ByteBuf) -> T
    }

    @JvmStatic
    fun getReaderUnchecked(clazz: Class<*>): ((ByteBuf) -> Any)? {
        val pair = map[clazz] ?: createSpecialMapping(clazz) ?: return null
        return pair.read
    }

    private fun createSpecialMapping(clazz: Class<*>): BufferSerializer? {
        return createArrayMapping(clazz) ?: createEnumMapping(clazz)
    }

    private fun createEnumMapping(clazz: Class<*>): BufferSerializer? {
        if(!clazz.isEnum)
            return null

        val values = clazz.enumConstants
        val size = values.size
        val serializer = BufferSerializer(reader@{ buf ->
            if(size > 256)
                values[buf.readShort().toInt()]
            else
                values[buf.readByte().toInt()]
        }, writer@{ buf, obj ->
            if(size > 256)
                buf.writeShort((obj as Enum<*>).ordinal)
            else
                buf.writeByte((obj as Enum<*>).ordinal)
        })

        return serializer
    }

    private fun createArrayMapping(clazz: Class<*>): BufferSerializer? {
        if (!clazz.isArray)
            return null

        val subclass = clazz.componentType

        val subReader = getReaderUnchecked(subclass)
        val subWriter = getWriterUnchecked(subclass)

        if (subReader == null || subWriter == null)
            return null

        val serializer = BufferSerializer(reader@{ buf ->
            val nullsig = buf.readBooleanArray()
            val array = ArrayReflect.newInstance(subclass, nullsig.size)
            for (i in 0..nullsig.size - 1) {
                ArrayReflect.set(array, i, if (nullsig[i]) null else subReader(buf))
            }
            return@reader array
        }, { buf, obj ->
            val len = ArrayReflect.getLength(obj)
            val nullsig = BooleanArray(len) { ArrayReflect.get(obj, it) == null }
            buf.writeBooleanArray(nullsig)
            for (i in 0..len - 1) {
                if (!nullsig[i])
                    subWriter(buf, ArrayReflect.get(obj, i))
            }
        })

        map[clazz] = serializer
        return serializer
    }
}

data class BufferSerializer(val read: (ByteBuf) -> Any, val write: (ByteBuf, Any) -> Unit)

object NBTSerializationHandlers {
    private val map = HashMap<Class<*>, NBTSerializer>()

    @Suppress("UNCHECKED_CAST")
    private fun <T : NBTBase> castNBTTag(tag: NBTBase, clazz: Class<T>): T {
        return (
                if (clazz.isAssignableFrom(tag.javaClass))
                    tag
                else if (clazz == NBTPrimitive::class.java)
                    NBTTagByte(0)
                else if (clazz == NBTTagByteArray::class.java)
                    NBTTagByteArray(ByteArray(0))
                else if (clazz == NBTTagString::class.java)
                    NBTTagString("")
                else if (clazz == NBTTagList::class.java)
                    NBTTagList()
                else if (clazz == NBTTagCompound::class.java)
                    NBTTagCompound()
                else if (clazz == NBTTagIntArray::class.java)
                    NBTTagIntArray(IntArray(0))
                else
                    throw IllegalArgumentException("Unknown NBT type to cast to")
                ) as T
    }

    init {
        // Primitives and String
        mapHandler(Char::class.javaPrimitiveType!!, { NBTTagByte(it.toByte()) }, { castNBTTag(it, NBTPrimitive::class.java).byte.toChar() })
        mapHandler(Byte::class.javaPrimitiveType!!, ::NBTTagByte, { castNBTTag(it, NBTPrimitive::class.java).byte })
        mapHandler(Short::class.javaPrimitiveType!!, ::NBTTagShort, { castNBTTag(it, NBTPrimitive::class.java).short })
        mapHandler(Int::class.javaPrimitiveType!!, ::NBTTagInt, { castNBTTag(it, NBTPrimitive::class.java).int })
        mapHandler(Long::class.javaPrimitiveType!!, ::NBTTagLong, { castNBTTag(it, NBTPrimitive::class.java).long })

        mapHandler(Float::class.javaPrimitiveType!!, ::NBTTagFloat, { castNBTTag(it, NBTPrimitive::class.java).float })
        mapHandler(Double::class.javaPrimitiveType!!, ::NBTTagDouble, { castNBTTag(it, NBTPrimitive::class.java).double })
        mapHandler(Boolean::class.javaPrimitiveType!!, { NBTTagByte(if (it) 1 else 0) }, { castNBTTag(it, NBTPrimitive::class.java).byte == 1.toByte() })
        mapHandler(String::class.java, ::NBTTagString, { castNBTTag(it, NBTTagString::class.java).string })

        mapHandler(CharArray::class.java, { NBTTagIntArray(it.map(Char::toInt).toIntArray()) }, { castNBTTag(it, NBTTagIntArray::class.java).intArray.map(Int::toChar).toCharArray() })
        mapHandler(ShortArray::class.java, { NBTTagIntArray(it.map(Short::toInt).toIntArray()) }, { castNBTTag(it, NBTTagIntArray::class.java).intArray.map(Int::toShort).toShortArray() })
        mapHandler(ByteArray::class.java, ::NBTTagByteArray, { castNBTTag(it, NBTTagByteArray::class.java).byteArray })
        mapHandler(IntArray::class.java, ::NBTTagIntArray, { castNBTTag(it, NBTTagIntArray::class.java).intArray })
        mapHandler(LongArray::class.java, {
            val tag = NBTTagList()
            it.forEachIndexed { i, l ->
                tag.appendTag(NBTTagLong(l))
            }
            tag
        }, {
            val tag = castNBTTag(it, NBTTagList::class.java)
            val list = LongArray(tag.tagCount())
            tag.forEachIndexed<NBTTagLong> { i, t ->
                list[i] = t.long
            }
            list
        })

        mapHandler(FloatArray::class.java, {
            val tag = NBTTagList()
            it.forEachIndexed { i, f ->
                tag.appendTag(NBTTagFloat(f))
            }
            tag
        }, {
            val tag = castNBTTag(it, NBTTagList::class.java)
            val list = FloatArray(tag.tagCount())
            tag.forEachIndexed<NBTTagFloat> { i, t ->
                list[i] = t.float
            }
            list
        })
        mapHandler(DoubleArray::class.java, {
            val tag = NBTTagList()
            it.forEachIndexed { i, d ->
                tag.appendTag(NBTTagDouble(d))
            }
            tag
        }, {
            val tag = castNBTTag(it, NBTTagList::class.java)
            val list = DoubleArray(tag.tagCount())
            tag.forEachIndexed<NBTTagDouble> { i, t ->
                list[i] = t.double
            }
            list
        })
        mapHandler(BooleanArray::class.java, {
            NBTTagByteArray(it.map { if (it) 1.toByte() else 0.toByte() }.toByteArray())
        }, {
            castNBTTag(it, NBTTagByteArray::class.java).byteArray.map { if (it == 1.toByte()) true else false }.toBooleanArray()
        })

        // Misc.
        mapHandler(Color::class.java, { NBTTagInt(it.rgb) }, { Color(castNBTTag(it, NBTPrimitive::class.java).int, true) })
        mapHandler(NBTTagCompound::class.java, { it }, { castNBTTag(it, NBTTagCompound::class.java) })

        // Item Handlers
        mapHandler(ItemStack::class.java, { it.serializeNBT() ?: NBTTagCompound() }, {
            val compound = castNBTTag(it, NBTTagCompound::class.java)
            ItemStack.loadItemStackFromNBT(compound)
        })
        mapHandler(ItemStackHandler::class.java, { it.serializeNBT() ?: NBTTagCompound() }, {
            val handler = ItemStackHandler()
            val compound = castNBTTag(it, NBTTagCompound::class.java)
            handler.deserializeNBT(compound)
            handler
        })


        // Vectors
        mapHandler(Vec3d::class.java, {
            val list = NBTTagList()
            list.appendTag(NBTTagDouble(it.xCoord))
            list.appendTag(NBTTagDouble(it.yCoord))
            list.appendTag(NBTTagDouble(it.zCoord))
            list
        }, {
            val tag = castNBTTag(it, NBTTagList::class.java)
            val x = tag.getDoubleAt(0)
            val y = tag.getDoubleAt(1)
            val z = tag.getDoubleAt(2)
            Vec3d(x, y, z)
        })
        mapHandler(Vec3i::class.java, {
            val list = NBTTagList()
            list.appendTag(NBTTagInt(it.x))
            list.appendTag(NBTTagInt(it.y))
            list.appendTag(NBTTagInt(it.z))
            list
        }, {
            val tag = castNBTTag(it, NBTTagList::class.java)
            val x = tag.getIntAt(0)
            val y = tag.getIntAt(1)
            val z = tag.getIntAt(2)
            Vec3i(x, y, z)
        })
        mapHandler(Vec2d::class.java, {
            val list = NBTTagList()
            list.appendTag(NBTTagDouble(it.x))
            list.appendTag(NBTTagDouble(it.y))
            list
        }, {
            val tag = castNBTTag(it, NBTTagList::class.java)
            val x = tag.getDoubleAt(0)
            val y = tag.getDoubleAt(1)
            Vec2d(x, y)
        })
        mapHandler(BlockPos::class.java, { NBTTagLong(it.toLong()) }, { BlockPos.fromLong(castNBTTag(it, NBTPrimitive::class.java).long) })
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> mapHandler(clazz: Class<T>, writer: (T) -> NBTBase, reader: (NBTBase) -> T) {
        map.put(clazz, NBTSerializer(reader as (NBTBase) -> Any, writer as (Any) -> NBTBase))
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getWriter(clazz: Class<T>): ((T) -> NBTBase)? {
        val unchecked = getWriterUnchecked(clazz) ?: return null
        return unchecked as (T) -> NBTBase
    }

    @JvmStatic
    fun getWriterUnchecked(clazz: Class<*>): ((Any) -> NBTBase)? {
        val pair = map[clazz] ?: createSpecialMapping(clazz) ?: return null
        return pair.writer
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getReader(clazz: Class<T>): ((NBTBase) -> T)? {
        val unchecked = getReaderUnchecked(clazz) ?: return null
        return unchecked as (NBTBase) -> T
    }

    @JvmStatic
    fun getReaderUnchecked(clazz: Class<*>): ((NBTBase) -> Any)? {
        val pair = map[clazz] ?: createSpecialMapping(clazz) ?: return null
        return pair.reader
    }

    private fun createSpecialMapping(clazz: Class<*>): NBTSerializer? {
        return createArrayMapping(clazz) ?: createEnumMapping(clazz)
    }

    private fun createEnumMapping(clazz: Class<*>): NBTSerializer? {
        if(!clazz.isEnum)
            return null

        val values = clazz.enumConstants
        val serializer = NBTSerializer(reader@{ nbt ->
            values[castNBTTag(nbt, NBTTagShort::class.java).short.toInt()]
        }, writer@{ obj ->
            NBTTagShort((obj as Enum<*>).ordinal.toShort())
        })

        return serializer
    }

    private fun createArrayMapping(clazz: Class<*>): NBTSerializer? {
        if (!clazz.isArray)
            return null

        val subclass = clazz.componentType

        val subReader = getReaderUnchecked(subclass)
        val subWriter = getWriterUnchecked(subclass)

        if (subReader == null || subWriter == null)
            return null

        val serializer = NBTSerializer(reader@{ nbt ->
            val compound = castNBTTag(nbt, NBTTagCompound::class.java)
            val list = castNBTTag(compound.getTag("list"), NBTTagList::class.java)

            val array = ArrayReflect.newInstance(subclass, list.tagCount())
            list.forEachIndexed<NBTTagCompound> { i, compound ->
                val tag = compound.getTag("-")
                if (tag == null)
                    ArrayReflect.set(array, i, null)
                else
                    ArrayReflect.set(array, i, subReader(tag))
            }
            return@reader array
        }, { obj ->
            val list = NBTTagList()
            val len = ArrayReflect.getLength(obj)
            for (i in 0..len - 1) {
                val value = ArrayReflect.get(obj, i)
                val compound = NBTTagCompound()
                if (value != null)
                    compound.setTag("-", subWriter(value))
                list.appendTag(compound)
            }

            val compound = NBTTagCompound()
            compound.setTag("list", list)
            compound
        })

        map[clazz] = serializer
        return serializer
    }

}

data class NBTSerializer(val reader: (NBTBase) -> Any, val writer: (Any) -> NBTBase)
