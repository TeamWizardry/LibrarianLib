package com.teamwizardry.librarianlib.common.util.saving

import com.teamwizardry.librarianlib.common.util.NBTTypes
import com.teamwizardry.librarianlib.common.util.forEach
import com.teamwizardry.librarianlib.common.util.forEachIndexed
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraftforge.items.ItemStackHandler
import java.awt.Color
import java.util.*


object NBTSerializationHandlers {
    val map = HashMap<FieldType, NBTSerializer>()
    private val specialHandlers = mutableListOf<(FieldType) -> NBTSerializer?>()
    private val genericHandlers = mutableListOf<(FieldTypeGeneric) -> NBTSerializer?>()

    @Suppress("UNCHECKED_CAST")
    fun <T : NBTBase> castNBTTag(tag: NBTBase, clazz: Class<T>): T {
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
            val list = if (existing == null || existing.size != tag.tagCount()) LongArray(tag.tagCount()) else existing
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
            val list = if (existing == null || existing.size != tag.tagCount()) FloatArray(tag.tagCount()) else existing
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
            val list = if (existing == null || existing.size != tag.tagCount()) DoubleArray(tag.tagCount()) else existing
            tag.forEachIndexed<NBTBase> { i, t ->
                list[i] = castNBTTag(t, NBTPrimitive::class.java).double
            }
            list
        })
        mapHandler(BooleanArray::class.java, {
            NBTTagByteArray(it.map { if (it) 1.toByte() else 0.toByte() }.toByteArray())
        }, { it, existing ->
            val tag = castNBTTag(it, NBTTagByteArray::class.java)
            val list = if (existing == null || existing.size != tag.byteArray.size) BooleanArray(tag.byteArray.size) else existing
            tag.byteArray.forEachIndexed { i, t ->
                list[i] = if (t == 1.toByte()) true else false
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
                    if(keyType.clazz == String::class.java) {
                        NBTSerializer(reader@{ nbt, existing ->
                            existing as HashMap<String, Any?>?

                            val tag = castNBTTag(nbt, NBTTagCompound::class.java)
                            val nullSig = tag.getTagList("null", NBTTypes.STRING)
                            val values = tag.getCompoundTag("values")
                            val map = type.clazz.newInstance() as HashMap<Any, Any?>

                            values.keySet.forEach { k ->
                                map[k] = valueReader(values.getTag(k), existing?.get(k))
                            }

                            nullSig.forEach<NBTTagString> {
                                map[it.string] = null
                            }

                            map
                        }, writer@{ obj ->
                            obj as HashMap<String, *>

                            val tag = NBTTagCompound()
                            val nullSig = NBTTagList()
                            val values = NBTTagCompound()

                            tag.setTag("nulls", nullSig)
                            tag.setTag("values", values)

                            obj.forEach { k, v ->
                                if(v == null)
                                    nullSig.appendTag(NBTTagString(k))
                                else
                                    values.setTag(k, valueWriter(v))
                            }

                            tag
                        })
                    } else {
                        NBTSerializer(reader@{ nbt, existing ->
                            existing as HashMap<Any, Any?>?

                            val entries = castNBTTag(nbt, NBTTagList::class.java)
                            val map = type.clazz.newInstance() as HashMap<Any, Any?>

                            entries.forEach<NBTTagCompound> { compound ->
                                val key = keyReader(compound.getTag("key"), null)
                                val value = if(compound.hasKey("value")) valueReader(compound.getTag("value"), existing?.get(key)) else null
                                map[key] = value
                            }

                            map
                        }, writer@{ obj ->
                            obj as HashMap<Any, *>

                            val entries = NBTTagList()

                            obj.forEach { k, v ->
                                val compound = NBTTagCompound()
                                compound.setTag("key", keyWriter(k))
                                if(v != null) compound.setTag("value", valueWriter(v))

                                entries.appendTag(compound)
                            }

                            entries
                        })
                    }

            return@handler null
        }
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> mapHandler(clazz: Class<T>, writer: (T) -> NBTBase, reader: (NBTBase, T?) -> T) {
        map.put(FieldType.create(clazz), NBTSerializer(reader as (NBTBase, Any?) -> Any, writer as (Any) -> NBTBase))
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun registerSpecialHandler(handler: (FieldType) -> NBTSerializer?) {
        specialHandlers.add(handler)
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun registerGenericHandler(handler: (FieldTypeGeneric) -> NBTSerializer?) {
        genericHandlers.add(handler)
    }

    @JvmStatic
    fun getWriterUnchecked(type: FieldType): ((Any) -> NBTBase)? {
        val pair = map[type] ?: createSpecialMapping(type) ?: return null
        return pair.writer
    }

    @JvmStatic
    fun getReaderUnchecked(type: FieldType): ((NBTBase, Any?) -> Any)? {
        val pair = map[type] ?: createSpecialMapping(type) ?: return null
        return pair.reader
    }

    fun createSpecialMapping(type: FieldType): NBTSerializer? {
        if(type is FieldTypeGeneric) {
            for (handler in genericHandlers) {
                val serializer = handler(type)
                if (serializer != null) {
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

    private fun createEnumMapping(type: FieldType): NBTSerializer? {
        if (type !is FieldTypeClass || !type.clazz.isEnum)
            return null

        val values = type.clazz.enumConstants
        val serializer = NBTSerializer(reader@{ nbt, existing ->
            values[castNBTTag(nbt, NBTTagShort::class.java).short.toInt()]
        }, writer@{ obj ->
            NBTTagShort((obj as Enum<*>).ordinal.toShort())
        })

        return serializer
    }

    private fun createArrayMapping(type: FieldType): NBTSerializer? {
        if (type !is FieldTypeArray)
            return null

        val componentType = type.componentType

        val subReader = getReaderUnchecked(componentType)
        val subWriter = getWriterUnchecked(componentType)

        if (subReader == null || subWriter == null)
            return null

        val serializer = NBTSerializer(reader@{ nbt, existing ->
            existing as Array<*>?
            val compound = castNBTTag(nbt, NBTTagCompound::class.java)
            val list = castNBTTag(compound.getTag("list"), NBTTagList::class.java)

            val array: Array<Any?> = if (existing == null || existing.size != list.tagCount()) ArrayReflect.newInstance(componentType.clazz, list.tagCount()) else existing as Array<Any?>
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
        return serializer
    }

}

data class NBTSerializer(val reader: (NBTBase, Any?) -> Any, val writer: (Any) -> NBTBase)
