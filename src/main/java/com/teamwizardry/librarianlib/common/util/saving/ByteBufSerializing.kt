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
        BasicByteBufSerializers.primitives()
        BasicByteBufSerializers.primitiveArrays()
        BasicByteBufSerializers.misc()
        BasicByteBufSerializers.vectors()

        SpecialByteBufSerializers.specials()
        SpecialByteBufSerializers.generics()
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> mapHandler(clazz: Class<T>, writer: (ByteBuf, T) -> Any?, reader: (ByteBuf, T?) -> T) {
        map.put(FieldType.create(clazz), BufferSerializer((reader as (ByteBuf, Any?) -> Any), { buf: ByteBuf, obj: Any -> writer(buf, obj as T) } as (ByteBuf, Any) -> Unit))
    }

    @JvmStatic
    fun aliasAs(aliasTo: Class<*>, clazz: Class<*>) {
        val aliasValue = map[FieldType.create(aliasTo)]
        if(aliasValue != null)
            map[FieldType.create(clazz)] = aliasValue
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
}

data class BufferSerializer(val read: (ByteBuf, Any?) -> Any, val write: (ByteBuf, Any) -> Unit)


private object BasicByteBufSerializers {

    fun primitives() {
        ByteBufSerializationHandlers.mapHandler(Char::class.javaPrimitiveType!!, { buf, obj -> buf.writeChar(obj.toInt()) }, { buf, existing -> buf.readChar() })
        ByteBufSerializationHandlers.mapHandler(Byte::class.javaPrimitiveType!!, { buf, obj -> buf.writeByte(obj.toInt()) }, { buf, existing -> buf.readByte() })
        ByteBufSerializationHandlers.mapHandler(Short::class.javaPrimitiveType!!, { buf, obj -> buf.writeShort(obj.toInt()) }, { buf, existing -> buf.readShort() })
        ByteBufSerializationHandlers.mapHandler(Int::class.javaPrimitiveType!!, ByteBuf::writeInt, { buf, existing -> buf.readInt() })
        ByteBufSerializationHandlers.mapHandler(Long::class.javaPrimitiveType!!, ByteBuf::writeLong, { buf, existing -> buf.readLong() })

        ByteBufSerializationHandlers.mapHandler(Float::class.javaPrimitiveType!!, ByteBuf::writeFloat, { buf, existing -> buf.readFloat() })
        ByteBufSerializationHandlers.mapHandler(Double::class.javaPrimitiveType!!, ByteBuf::writeDouble, { buf, existing -> buf.readDouble() })
        ByteBufSerializationHandlers.mapHandler(Boolean::class.javaPrimitiveType!!, ByteBuf::writeBoolean, { buf, existing -> buf.readBoolean() })
        ByteBufSerializationHandlers.mapHandler(String::class.java, ByteBuf::writeString, { buf, existing -> buf.readString() })

        ByteBufSerializationHandlers.aliasAs(Char::class.javaPrimitiveType!!, Char::class.javaObjectType)
        ByteBufSerializationHandlers.aliasAs(Byte::class.javaPrimitiveType!!, Byte::class.javaObjectType)
        ByteBufSerializationHandlers.aliasAs(Short::class.javaPrimitiveType!!, Short::class.javaObjectType)
        ByteBufSerializationHandlers.aliasAs(Int::class.javaPrimitiveType!!, Int::class.javaObjectType)
        ByteBufSerializationHandlers.aliasAs(Long::class.javaPrimitiveType!!, Long::class.javaObjectType)

        ByteBufSerializationHandlers.aliasAs(Float::class.javaPrimitiveType!!, Float::class.javaObjectType)
        ByteBufSerializationHandlers.aliasAs(Double::class.javaPrimitiveType!!, Double::class.javaObjectType)
        ByteBufSerializationHandlers.aliasAs(Boolean::class.javaPrimitiveType!!, Boolean::class.javaObjectType)

    }

    fun primitiveArrays() {
        ByteBufSerializationHandlers.mapHandler((CharArray(0)).javaClass, { buf, obj ->
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
        ByteBufSerializationHandlers.mapHandler((ByteArray(0)).javaClass, { buf, obj ->
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
        ByteBufSerializationHandlers.mapHandler((ShortArray(0)).javaClass, { buf, obj ->
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
        ByteBufSerializationHandlers.mapHandler((IntArray(0)).javaClass, { buf, obj ->
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
        ByteBufSerializationHandlers.mapHandler((LongArray(0)).javaClass, { buf, obj ->
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

        ByteBufSerializationHandlers.mapHandler((FloatArray(0)).javaClass, { buf, obj ->
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
        ByteBufSerializationHandlers.mapHandler((DoubleArray(0)).javaClass, { buf, obj ->
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
        ByteBufSerializationHandlers.mapHandler((BooleanArray(0)).javaClass, ByteBuf::writeBooleanArray, ByteBuf::readBooleanArray)

        ByteBufSerializationHandlers.aliasAs(CharArray::class.java, Array<Char>::class.java)
        ByteBufSerializationHandlers.aliasAs(ByteArray::class.java, Array<Byte>::class.java)
        ByteBufSerializationHandlers.aliasAs(ShortArray::class.java, Array<Short>::class.java)
        ByteBufSerializationHandlers.aliasAs(IntArray::class.java, Array<Int>::class.java)
        ByteBufSerializationHandlers.aliasAs(LongArray::class.java, Array<Long>::class.java)

        ByteBufSerializationHandlers.aliasAs(FloatArray::class.java, Array<Float>::class.java)
        ByteBufSerializationHandlers.aliasAs(DoubleArray::class.java, Array<Double>::class.java)
        ByteBufSerializationHandlers.aliasAs(BooleanArray::class.java, Array<Boolean>::class.java)

    }

    fun misc() {
        ByteBufSerializationHandlers.mapHandler(Color::class.java, { buf, obj -> buf.writeInt(obj.rgb) }, { buf, existing -> Color(buf.readInt(), true) })
        ByteBufSerializationHandlers.mapHandler(NBTTagCompound::class.java, ByteBuf::writeTag, { buf, existing -> buf.readTag() })
        ByteBufSerializationHandlers.mapHandler(ItemStack::class.java, ByteBuf::writeStack, { buf, existing -> buf.readStack() })
        ByteBufSerializationHandlers.mapHandler(ItemStackHandler::class.java, {
            buf, obj ->
            buf.writeTag(obj.serializeNBT())
        }, { buf, existing ->
            val handler = existing ?: ItemStackHandler()
            handler.deserializeNBT(buf.readTag())
            handler
        })
        ByteBufSerializationHandlers.mapHandler(ItemStackHandler::class.java, {
            buf, obj ->
            buf.writeTag(obj.serializeNBT())
        }, { buf, existing ->
            val handler = existing ?: ItemStackHandler()
            handler.deserializeNBT(buf.readTag())
            handler
        })
    }

    fun vectors() {
        ByteBufSerializationHandlers.mapHandler(Vec3d::class.java, {
            buf, obj ->
            buf.writeDouble(obj.xCoord).writeDouble(obj.yCoord).writeDouble(obj.zCoord)
        }, { buf, existing ->
            Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble())
        })
        ByteBufSerializationHandlers.mapHandler(Vec3i::class.java, {
            buf, obj ->
            buf.writeInt(obj.x).writeInt(obj.y).writeInt(obj.z)
        }, { buf, existing ->
            Vec3i(buf.readInt(), buf.readInt(), buf.readInt())
        })
        ByteBufSerializationHandlers.mapHandler(Vec2d::class.java, {
            buf, obj ->
            buf.writeDouble(obj.x).writeDouble(obj.y)
        }, { buf, existing ->
            Vec2d(buf.readDouble(), buf.readDouble())
        })
        ByteBufSerializationHandlers.mapHandler(BlockPos::class.java, { buf, obj -> buf.writeLong(obj.toLong()) }, { buf, existing -> BlockPos.fromLong(buf.readLong()) })
    }
}

private object SpecialByteBufSerializers {

    fun specials() {
        ByteBufSerializationHandlers.registerSpecialHandler { createArrayMapping(it) }
        ByteBufSerializationHandlers.registerSpecialHandler { createEnumMapping(it) }
        ByteBufSerializationHandlers.registerSpecialHandler { createSavableMapping(it) }
    }

    fun generics() {
        ByteBufSerializationHandlers.registerGenericHandler { createHashMap(it) }
    }

    private fun createSavableMapping(type: FieldType): BufferSerializer? {
        val v = type.clazz.annotations.find { it is Savable } as? Savable ?: return null

        val con = type.clazz.getDeclaredConstructor()
        con.isAccessible = true // to allow for not exposing the zero-arg constructor

        val constructor = MethodHandleHelper.wrapperForConstructor(con)

        val serializer = if(v.mutable) {
            BufferSerializer(reader@{ buf, existing ->
                val instance = existing ?: constructor(arrayOf())
                AbstractSaveHandler.readAutoBytes(instance, buf)
                instance
            }, writer@{ buf, obj ->
                val sync = AbstractSaveHandler.isSyncing
                AbstractSaveHandler.writeAutoBytes(obj, buf, sync)
                AbstractSaveHandler.isSyncing = sync
            })
        } else {
            BufferSerializer(reader@{ buf, existing ->
                val instance = existing ?: constructor(arrayOf())
                AbstractSaveHandler.readAutoBytes(instance, buf)
                instance
            }, writer@{ buf, obj ->
                val sync = AbstractSaveHandler.isSyncing
                AbstractSaveHandler.writeAutoBytes(obj, buf, sync)
                AbstractSaveHandler.isSyncing = sync
            })
        }
        return serializer
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

        val subReader = ByteBufSerializationHandlers.getReaderUnchecked(type.componentType)
        val subWriter = ByteBufSerializationHandlers.getWriterUnchecked(type.componentType)

        if (subReader == null || subWriter == null)
            return null

        val serializer = BufferSerializer(reader@{ buf, existing ->
            existing as Array<*>?
            val nullsig = buf.readBooleanArray()
            val array: Array<Any?> = if (existing == null || existing.size != nullsig.size) ArrayReflect.newInstanceRaw(type.clazz, nullsig.size) else existing as Array<Any?>
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

    private fun createHashMap(type: FieldTypeGeneric): BufferSerializer? {
        if (!HashMap::class.java.isAssignableFrom(type.clazz))
            return null

        val keyType = type.generic(0) ?: throw IllegalArgumentException("Couldn't find key generic for HashMap serializer generation")
        val valueType = type.generic(1) ?: throw IllegalArgumentException("Couldn't find value generic for HashMap serializer generation")

        val keyReader = ByteBufSerializationHandlers.getReaderUnchecked(keyType)
        val keyWriter = ByteBufSerializationHandlers.getWriterUnchecked(keyType)

        val valueReader = ByteBufSerializationHandlers.getReaderUnchecked(valueType)
        val valueWriter = ByteBufSerializationHandlers.getWriterUnchecked(valueType)

        if (keyReader == null || keyWriter == null || valueReader == null || valueWriter == null)
            return null

        @Suppress("UNCHECKED_CAST")
        val serializer =
                BufferSerializer(reader@{ buf, existing ->
                    existing as HashMap<Any, Any?>?

                    val nullSig = buf.readBooleanArray()
                    val map = type.clazz.newInstance() as HashMap<Any, Any?>

                    nullSig.forEach { isNull ->
                        val key = keyReader(buf, null)
                        if (isNull) {
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
                        if (!nullSig[i]) valueWriter(buf, entry.value)
                    }
                })

        return serializer
    }
}
