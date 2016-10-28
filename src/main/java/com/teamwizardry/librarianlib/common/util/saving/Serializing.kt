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
    private val map = HashMap<Class<*>, Pair<(ByteBuf, Any) -> Unit, (ByteBuf) -> Any>>()

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

        // Misc.
        mapHandler(Color::class.java, { buf, obj -> buf.writeInt(obj.rgb) }, { Color(it.readInt(), true) })
        mapHandler(NBTTagCompound::class.java, ByteBuf::writeTag, ByteBuf::readTag)
        mapHandler(ItemStack::class.java, ByteBuf::writeStack, ByteBuf::readStack)

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
        map.put(clazz, ({ buf: ByteBuf, obj: Any -> writer(buf, obj as T) } as (ByteBuf, Any) -> Unit) to (reader as (ByteBuf) -> Any))
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getWriter(clazz: Class<T>): ((ByteBuf, T) -> Unit)? {
        val pair = map[clazz] ?: return null
        return pair.first as (ByteBuf, T) -> Unit
    }

    @JvmStatic
    fun getWriterUnchecked(clazz: Class<*>): ((ByteBuf, Any) -> Unit)? {
        val pair = map[clazz] ?: return null
        return pair.first
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getReader(clazz: Class<T>): ((ByteBuf) -> T)? {
        val pair = map[clazz] ?: return null
        return pair.second as (ByteBuf) -> T
    }

    @JvmStatic
    fun getReaderUnchecked(clazz: Class<*>): ((ByteBuf) -> Any)? {
        val pair = map[clazz] ?: return null
        return pair.second
    }

}

object NBTSerializationHandlers {
    private val map = HashMap<Class<*>, Pair<(Any) -> NBTBase, (NBTBase) -> Any>>()

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
        map.put(clazz, (writer as (Any) -> NBTBase) to (reader as (NBTBase) -> Any))
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getWriter(clazz: Class<T>): ((T) -> NBTBase)? {
        val pair = map[clazz] ?: return null
        return pair.first as (T) -> NBTBase
    }

    @JvmStatic
    fun getWriterUnchecked(clazz: Class<*>): ((Any) -> NBTBase)? {
        val pair = map[clazz] ?: return null
        return pair.first
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getReader(clazz: Class<T>): ((NBTBase) -> T)? {
        val pair = map[clazz] ?: return null
        return pair.second as (NBTBase) -> T
    }

    @JvmStatic
    fun getReaderUnchecked(clazz: Class<*>): ((NBTBase) -> Any)? {
        val pair = map[clazz] ?: return null
        return pair.second
    }

}
