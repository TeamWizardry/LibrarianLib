package com.teamwizardry.librarianlib.common.util.saving

import com.teamwizardry.librarianlib.common.util.*
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
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
    val map = hashMapOf<FieldType, BufferSerializer>()
    private val specialHandlers = mutableListOf<(FieldType) -> BufferSerializer?>()
    private val genericHandlers = mutableListOf<(FieldTypeGeneric) -> BufferSerializer?>()

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
            val len = buf.readVarInt()
            val array = if (existing == null || existing.size != len) CharArray(len) else existing
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
            val len = buf.readVarInt()
            val array = if (existing == null || existing.size != len) ByteArray(len) else existing
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
            val len = buf.readVarInt()
            val array = if (existing == null || existing.size != len) ShortArray(len) else existing
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
            val len = buf.readVarInt()
            val array = if (existing == null || existing.size != len) IntArray(len) else existing
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
            val len = buf.readVarInt()
            val array = if (existing == null || existing.size != len) LongArray(len) else existing
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
            val len = buf.readShort().toInt()
            val array = if (existing == null || existing.size != len) FloatArray(len) else existing
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
            val len = buf.readShort().toInt()
            val array = if (existing == null || ArrayReflect.getLength(existing) != len) DoubleArray(len) else existing
            for (i in 0..len - 1) {
                array[i] = buf.readDouble()
            }
            return@reader array
        })
        mapHandler((BooleanArray(0)).javaClass, ByteBuf::writeBooleanArray, ByteBuf::readBooleanArray)

        // Misc.
        mapHandler(Color::class.java, { buf, obj -> buf.writeInt(obj.rgb) }, { buf, existing -> Color(buf.readInt(), true) })
        mapHandler(NBTTagCompound::class.java, ByteBuf::writeTag, { buf, existing -> buf.readTag() })
        mapHandler(ItemStack::class.java, ByteBuf::writeStack, { buf, existing -> buf.readStack() })
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

        // Generic generators
        registerGenericHandler handler@{ type ->
            if (!HashMap::class.java.isAssignableFrom(type.clazz))
                return@handler null

            val keyType = type.generic(0) ?: throw IllegalArgumentException("Couldn't find key generic for HashMap serializer generation")
            val valueType = type.generic(1) ?: throw IllegalArgumentException("Couldn't find value generic for HashMap serializer generation")

            val keyReader = getReaderUnchecked(keyType)
            val keyWriter = getWriterUnchecked(keyType)

            val valueReader = getReaderUnchecked(valueType)
            val valueWriter = getWriterUnchecked(valueType)

            if (keyReader == null || keyWriter == null || valueReader == null || valueWriter == null)
                return@handler null

            @Suppress("UNCHECKED_CAST")
            val serializer =
                    BufferSerializer(reader@{ buf, existing ->
                        existing as HashMap<Any, Any?>?

                        val nullSig = buf.readBooleanArray()
                        val map = type.clazz.newInstance() as HashMap<Any, Any?>

                        nullSig.forEach{ isNull ->
                            val key = keyReader(buf, null)
                            if(isNull) {
                                map[key] = null
                            } else {
                                map[key] = valueReader(buf, existing?.get(key))
                            }
                        }
                        map
                    }, writer@{ buf, obj ->
                        obj as HashMap<*, *>
                        val nullSig = BooleanArray(obj.entries.size)
                        obj.entries.forEachIndexed { i, entry -> // WARNING: this is dangerous!!!
                            // If someone uses a stupid HashMap implementation that doesn't have a stable iteration
                            // order between the two calls this WILL break in vague and hard to track down ways
                            nullSig[i] = entry.value == null
                        }
                        buf.writeBooleanArray(nullSig)
                        obj.entries.forEachIndexed { i, entry ->
                            keyWriter(buf, entry.key)
                            if(!nullSig[i]) valueWriter(buf, entry.value)
                        }
                    })

            return@handler null
        }
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> mapHandler(clazz: Class<T>, writer: (ByteBuf, T) -> Any?, reader: (ByteBuf, T?) -> T) {
        map.put(FieldType.create(clazz), BufferSerializer((reader as (ByteBuf, Any?) -> Any), { buf: ByteBuf, obj: Any -> writer(buf, obj as T) } as (ByteBuf, Any) -> Unit))
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun registerSpecialHandler(handler: (FieldType) -> BufferSerializer?) {
        specialHandlers.add(handler)
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun registerGenericHandler(handler: (FieldTypeGeneric) -> BufferSerializer?) {
        genericHandlers.add(handler)
    }

    @JvmStatic
    fun getWriterUnchecked(type: FieldType): ((ByteBuf, Any) -> Unit)? {
        val pair = map[type] ?: createSpecialMappings(type) ?: return null
        return pair.write
    }

    @JvmStatic
    fun getReaderUnchecked(type: FieldType): ((ByteBuf, Any?) -> Any)? {
        val pair = map[type] ?: createSpecialMappings(type) ?: return null
        return pair.read
    }

    fun createSpecialMappings(type: FieldType): BufferSerializer? {
        if(type is FieldTypeGeneric) {
            for (handler in genericHandlers) {
                val serializer = handler(type)
                if(serializer != null) {
                    map[type] = serializer
                    return serializer
                }
            }
        }
        for (handler in specialHandlers) {
            val serializer = handler(type)
            if (serializer != null) {
                map[type] = serializer
                return serializer
            }
        }
        return null
    }

    private fun createEnumMapping(type: FieldType): BufferSerializer? {
        if (type !is FieldTypeClass || !type.clazz.isEnum)
            return null

        val values = type.clazz.enumConstants
        val size = values.size
        val serializer = BufferSerializer(reader@{ buf, existing ->
            if (size > 256)
                values[buf.readShort().toInt()]
            else
                values[buf.readByte().toInt()]
        }, writer@{ buf, obj ->
            if (size > 256)
                buf.writeShort((obj as Enum<*>).ordinal)
            else
                buf.writeByte((obj as Enum<*>).ordinal)
        })

        return serializer
    }

    private fun createArrayMapping(type: FieldType): BufferSerializer? {
        if (type !is FieldTypeArray)
            return null

        val subReader = getReaderUnchecked(type.componentType)
        val subWriter = getWriterUnchecked(type.componentType)

        if (subReader == null || subWriter == null)
            return null

        val serializer = BufferSerializer(reader@{ buf, existing ->
            existing as Array<*>?
            val nullsig = buf.readBooleanArray()
            val array: Array<Any?> = if (existing == null || existing.size != nullsig.size) ArrayReflect.newInstance(type.clazz, nullsig.size) else existing as Array<Any?>
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

        return serializer
    }
}

data class BufferSerializer(val read: (ByteBuf, Any?) -> Any, val write: (ByteBuf, Any) -> Unit)
