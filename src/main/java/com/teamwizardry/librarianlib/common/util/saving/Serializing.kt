package com.teamwizardry.librarianlib.common.util.saving

import com.teamwizardry.librarianlib.LibrarianLog
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
    val map = hashMapOf<Class<*>, BufferSerializer>()
    private val specialHandlers = mutableListOf<ByteBufSerializationHandlers.(Class<*>) -> BufferSerializer?>()

    init {
        // Primitives and String
        mapHandler(Char::class.javaPrimitiveType!!, { buf, obj -> buf.writeChar(obj.toInt()) }, { buf, existing -> buf.readChar() })
        mapHandler(Byte::class.javaPrimitiveType!!, { buf, obj -> buf.writeByte(obj.toInt()) }, { buf, existing -> buf.readByte() })
        mapHandler(Short::class.javaPrimitiveType!!, { buf, obj -> buf.writeShort(obj.toInt()) }, { buf, existing -> buf.readShort() })
        mapHandler(Int::class.javaPrimitiveType!!, ByteBuf::writeInt, { buf, existing -> buf.readInt() })
        mapHandler(Long::class.javaPrimitiveType!!, ByteBuf::writeLong, { buf, existing -> buf.readLong() })

        mapHandler(Float::class.javaPrimitiveType!!, ByteBuf::writeFloat, { buf, existing -> buf.readFloat() })
        mapHandler(Double::class.javaPrimitiveType!!, ByteBuf::writeDouble, { buf, existing -> buf.readDouble() })
        mapHandler(Boolean::class.javaPrimitiveType!!, ByteBuf::writeBoolean, { buf, existing -> buf.readBoolean() })
        mapHandler(String::class.java, ByteBuf::writeString, { buf, existing -> buf.readString() })

        // Primitive arrays
        mapHandler((CharArray(0)).javaClass, { buf, obj ->
            val len = obj.size
            buf.writeVarInt(len)
            for (i in 0..len - 1) {
                buf.writeChar(obj[i].toInt())
            }
        }, reader@{ buf, existing ->
            existing as CharArray?
            val len = buf.readVarInt()
            val array = if(existing == null || existing.size != len) CharArray(len) else existing
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
        }, reader@{ buf, existing ->
            existing as ByteArray?
            val len = buf.readVarInt()
            val array = if(existing == null || existing.size != len) ByteArray(len) else existing
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
        }, reader@{ buf, existing ->
            existing as ShortArray?
            val len = buf.readVarInt()
            val array = if(existing == null || existing.size != len) ShortArray(len) else existing
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
        }, reader@{ buf, existing ->
            existing as IntArray?
            val len = buf.readVarInt()
            val array = if(existing == null || existing.size != len) IntArray(len) else existing
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
        }, reader@{ buf, existing ->
            existing as LongArray?
            val len = buf.readVarInt()
            val array = if(existing == null || existing.size != len) LongArray(len) else existing
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
        }, reader@{ buf, existing ->
            existing as FloatArray?
            val len = buf.readShort().toInt()
            val array = if(existing == null || existing.size != len) FloatArray(len) else existing
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
        }, reader@{ buf, existing ->
            existing as DoubleArray?
            val len = buf.readShort().toInt()
            val array = if(existing == null || ArrayReflect.getLength(existing) != len) DoubleArray(len) else existing
            for (i in 0..len - 1) {
                array[i] = buf.readDouble()
            }
            return@reader array
        })
        mapHandler((BooleanArray(0)).javaClass, ByteBuf::writeBooleanArray, ByteBuf::readBooleanArray)

        // Misc.
        mapHandler(Color::class.java, { buf, obj -> buf.writeInt(obj.rgb) }, { buf, existing -> Color(buf.readInt(), true) })
        mapHandler(NBTTagCompound::class.java, ByteBuf::writeTag, { buf, existing -> buf.readTag()})
        mapHandler(ItemStack::class.java, ByteBuf::writeStack, { buf, existing -> buf.readStack()})
        mapHandler(ItemStackHandler::class.java, {
            buf, obj ->
            buf.writeTag(obj.serializeNBT())
        }, { buf, existing ->
            val handler = ItemStackHandler()
            handler.deserializeNBT(buf.readTag())
            handler
        })
        mapHandler(ItemStackHandler::class.java, {
            buf, obj ->
            buf.writeTag(obj.serializeNBT())
        }, { buf, existing ->
            val handler = ItemStackHandler()
            handler.deserializeNBT(buf.readTag())
            handler
        })

        // Vectors
        mapHandler(Vec3d::class.java, {
            buf, obj ->
            buf.writeDouble(obj.xCoord).writeDouble(obj.yCoord).writeDouble(obj.zCoord)
        }, { buf, existing ->
            Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble())
        })
        mapHandler(Vec3i::class.java, {
            buf, obj ->
            buf.writeInt(obj.x).writeInt(obj.y).writeInt(obj.z)
        }, { buf, existing ->
            Vec3i(buf.readInt(), buf.readInt(), buf.readInt())
        })
        mapHandler(Vec2d::class.java, {
            buf, obj ->
            buf.writeDouble(obj.x).writeDouble(obj.y)
        }, { buf, existing ->
            Vec2d(buf.readDouble(), buf.readDouble())
        })
        mapHandler(BlockPos::class.java, { buf, obj -> buf.writeLong(obj.toLong()) }, { buf, existing -> BlockPos.fromLong(buf.readLong()) })



        // Dynamic generators
        registerSpecialHandler { createArrayMapping(it) }
        registerSpecialHandler { createEnumMapping(it) }
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> mapHandler(clazz: Class<T>, writer: (ByteBuf, T) -> Any?, reader: (ByteBuf, T?) -> T) {
        map.put(clazz, BufferSerializer((reader as (ByteBuf, Any?) -> Any), { buf: ByteBuf, obj: Any -> writer(buf, obj as T) } as (ByteBuf, Any) -> Unit))
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun registerSpecialHandler(handler: ByteBufSerializationHandlers.(Class<*>) -> BufferSerializer?) {
        specialHandlers.add(handler)
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getWriter(clazz: Class<T>): ((ByteBuf, T) -> Unit)? {
        val unchecked = getWriterUnchecked(clazz) ?: return null
        return unchecked as (ByteBuf, T) -> Unit
    }

    @JvmStatic
    fun getWriterUnchecked(clazz: Class<*>): ((ByteBuf, Any) -> Unit)? {
        val pair = map[clazz] ?: createSpecialMappings(clazz) ?: return null
        return pair.write
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getReader(clazz: Class<T>): ((ByteBuf, T?) -> T)? {
        val unchecked = getReaderUnchecked(clazz) ?: return null
        return unchecked as (ByteBuf, T?) -> T
    }

    @JvmStatic
    fun getReaderUnchecked(clazz: Class<*>): ((ByteBuf, Any?) -> Any)? {
        val pair = map[clazz] ?: createSpecialMappings(clazz) ?: return null
        return pair.read
    }

    fun createSpecialMappings(clazz: Class<*>): BufferSerializer? {
        for (handler in specialHandlers) {
            val serializer = this.handler(clazz)
            if (serializer != null) return serializer
        }

        return null
    }

    private fun createEnumMapping(clazz: Class<*>): BufferSerializer? {
        if(!clazz.isEnum)
            return null

        val values = clazz.enumConstants
        val size = values.size
        val serializer = BufferSerializer(reader@{ buf, existing ->
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

        val serializer = BufferSerializer(reader@{ buf, existing ->
            existing as Array<*>?
            val nullsig = buf.readBooleanArray()
            val array: Array<Any?> = if(existing == null || existing.size != nullsig.size) ArrayReflect.newInstance(subclass, nullsig.size) as Array<Any?> else existing as Array<Any?>
            for (i in 0..nullsig.size - 1) {
                array[i] = if (nullsig[i]) null else subReader(buf, array[i])
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

data class BufferSerializer(val read: (ByteBuf, Any?) -> Any, val write: (ByteBuf, Any) -> Unit)

object NBTSerializationHandlers {
    val map = HashMap<Class<*>, NBTSerializer>()
    private val specialHandlers = mutableListOf<NBTSerializationHandlers.(Class<*>) -> NBTSerializer?>()

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
        mapHandler(Char::class.javaPrimitiveType!!, { NBTTagByte(it.toByte()) }, { it, existing -> castNBTTag(it, NBTPrimitive::class.java).byte.toChar() })
        mapHandler(Byte::class.javaPrimitiveType!!, ::NBTTagByte, { it, existing -> castNBTTag(it, NBTPrimitive::class.java).byte })
        mapHandler(Short::class.javaPrimitiveType!!, ::NBTTagShort, { it, existing -> castNBTTag(it, NBTPrimitive::class.java).short })
        mapHandler(Int::class.javaPrimitiveType!!, ::NBTTagInt, { it, existing -> castNBTTag(it, NBTPrimitive::class.java).int })
        mapHandler(Long::class.javaPrimitiveType!!, ::NBTTagLong, { it, existing -> castNBTTag(it, NBTPrimitive::class.java).long })

        mapHandler(Float::class.javaPrimitiveType!!, ::NBTTagFloat, { it, existing -> castNBTTag(it, NBTPrimitive::class.java).float })
        mapHandler(Double::class.javaPrimitiveType!!, ::NBTTagDouble, { it, existing -> castNBTTag(it, NBTPrimitive::class.java).double })
        mapHandler(Boolean::class.javaPrimitiveType!!, { NBTTagByte(if (it) 1 else 0) }, { it, existing -> castNBTTag(it, NBTPrimitive::class.java).byte == 1.toByte() })
        mapHandler(String::class.java, ::NBTTagString, { it, existing -> castNBTTag(it, NBTTagString::class.java).string })

        mapHandler(CharArray::class.java, { NBTTagIntArray(it.map(Char::toInt).toIntArray()) }, { it, existing -> castNBTTag(it, NBTTagIntArray::class.java).intArray.map(Int::toChar).toCharArray() })
        mapHandler(ShortArray::class.java, { NBTTagIntArray(it.map(Short::toInt).toIntArray()) }, { it, existing -> castNBTTag(it, NBTTagIntArray::class.java).intArray.map(Int::toShort).toShortArray() })
        mapHandler(ByteArray::class.java, ::NBTTagByteArray, { it, existing -> castNBTTag(it, NBTTagByteArray::class.java).byteArray })
        mapHandler(IntArray::class.java, ::NBTTagIntArray, { it, existing -> castNBTTag(it, NBTTagIntArray::class.java).intArray })
        mapHandler(LongArray::class.java, {
            val tag = NBTTagList()
            it.forEachIndexed { i, l ->
                tag.appendTag(NBTTagLong(l))
            }
            tag
        }, { it, existing ->
            val tag = castNBTTag(it, NBTTagList::class.java)
            val list = if(existing == null || existing.size != tag.tagCount()) LongArray(tag.tagCount()) else existing
            tag.forEachIndexed<NBTBase> { i, t ->
                list[i] = castNBTTag(t, NBTPrimitive::class.java).long
            }
            list
        })

        mapHandler(FloatArray::class.java, {
            val tag = NBTTagList()
            it.forEachIndexed { i, f ->
                tag.appendTag(NBTTagFloat(f))
            }
            tag
        }, { it, existing ->
            val tag = castNBTTag(it, NBTTagList::class.java)
            val list = if(existing == null || existing.size != tag.tagCount()) FloatArray(tag.tagCount()) else existing
            tag.forEachIndexed<NBTBase> { i, t ->
                list[i] = castNBTTag(t, NBTPrimitive::class.java).float
            }
            list
        })
        mapHandler(DoubleArray::class.java, {
            val tag = NBTTagList()
            it.forEachIndexed { i, d ->
                tag.appendTag(NBTTagDouble(d))
            }
            tag
        }, { it, existing ->
            val tag = castNBTTag(it, NBTTagList::class.java)
            val list = if(existing == null || existing.size != tag.tagCount()) DoubleArray(tag.tagCount()) else existing
            tag.forEachIndexed<NBTBase> { i, t ->
                list[i] = castNBTTag(t, NBTPrimitive::class.java).double
            }
            list
        })
        mapHandler(BooleanArray::class.java, {
            NBTTagByteArray(it.map { if (it) 1.toByte() else 0.toByte() }.toByteArray())
        }, { it, existing ->
            val tag = castNBTTag(it, NBTTagByteArray::class.java)
            val list = if(existing == null || existing.size != tag.byteArray.size) BooleanArray(tag.byteArray.size) else existing
            tag.byteArray.forEachIndexed { i, t ->
                list[i] = if(t == 1.toByte()) true else false
            }
            list
        })

        // Misc.
        mapHandler(Color::class.java, { NBTTagInt(it.rgb) }, { it, existing -> Color(castNBTTag(it, NBTPrimitive::class.java).int, true) })
        mapHandler(NBTTagCompound::class.java, { it }, { it, existing -> castNBTTag(it, NBTTagCompound::class.java) })

        // Item Handlers
        mapHandler(ItemStack::class.java, { it.serializeNBT() ?: NBTTagCompound() }, { it, existing ->
            val compound = castNBTTag(it, NBTTagCompound::class.java)
            ItemStack.loadItemStackFromNBT(compound)
        })
        mapHandler(ItemStackHandler::class.java, { it.serializeNBT() ?: NBTTagCompound() }, { it, existing ->
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
        }, { it, existing ->
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
        }, { it, existing ->
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
        }, { it, existing ->
            val tag = castNBTTag(it, NBTTagList::class.java)
            val x = tag.getDoubleAt(0)
            val y = tag.getDoubleAt(1)
            Vec2d(x, y)
        })
        mapHandler(BlockPos::class.java, { NBTTagLong(it.toLong()) }, { it, existing -> BlockPos.fromLong(castNBTTag(it, NBTPrimitive::class.java).long) })



        // Dynamic generators
        registerSpecialHandler { createArrayMapping(it) }
        registerSpecialHandler { createEnumMapping(it) }
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> mapHandler(clazz: Class<T>, writer: (T) -> NBTBase, reader: (NBTBase, T?) -> T) {
        map.put(clazz, NBTSerializer(reader as (NBTBase, Any?) -> Any, writer as (Any) -> NBTBase))
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun registerSpecialHandler(handler: NBTSerializationHandlers.(Class<*>) -> NBTSerializer?) {
        specialHandlers.add(handler)
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
    fun <T> getReader(clazz: Class<T>): ((NBTBase, T?) -> T)? {
        val unchecked = getReaderUnchecked(clazz) ?: return null
        return unchecked as (NBTBase, T?) -> T
    }

    @JvmStatic
    fun getReaderUnchecked(clazz: Class<*>): ((NBTBase, Any?) -> Any)? {
        val pair = map[clazz] ?: createSpecialMapping(clazz) ?: return null
        return pair.reader
    }

    fun createSpecialMapping(clazz: Class<*>): NBTSerializer? {
        for (handler in specialHandlers) {
            val serializer = this.handler(clazz)
            if (serializer != null) return serializer
        }

        return null
    }

    private fun createEnumMapping(clazz: Class<*>): NBTSerializer? {
        if(!clazz.isEnum)
            return null

        val values = clazz.enumConstants
        val serializer = NBTSerializer(reader@{ nbt, existing ->
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

        val serializer = NBTSerializer(reader@{ nbt, existing ->
            existing as Array<*>?
            val compound = castNBTTag(nbt, NBTTagCompound::class.java)
            val list = castNBTTag(compound.getTag("list"), NBTTagList::class.java)

            val array: Array<Any?> = if(existing == null || existing.size != list.tagCount()) ArrayReflect.newInstance(subclass, list.tagCount()) as Array<Any?> else existing as Array<Any?>
            list.forEachIndexed<NBTTagCompound> { i, compound ->
                val tag = compound.getTag("-")
                if (tag == null)
                    array[i] = null
                else
                    array[i] = subReader(tag, array[i])
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

data class NBTSerializer(val reader: (NBTBase, Any?) -> Any, val writer: (Any) -> NBTBase)
