package com.teamwizardry.librarianlib.common.util.saving

import com.teamwizardry.librarianlib.common.util.NBTTypes
import com.teamwizardry.librarianlib.common.util.forEach
import com.teamwizardry.librarianlib.common.util.forEachIndexed
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import com.teamwizardry.librarianlib.common.util.safeCast
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

    init {
        BasicNBTSerializers.primitives()
        BasicNBTSerializers.primitiveArrays()
        BasicNBTSerializers.misc()
        BasicNBTSerializers.vectors()

        SpecialNBTSerializers.specials()
        SpecialNBTSerializers.generics()
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> mapHandler(clazz: Class<T>, writer: (T) -> NBTBase, reader: (NBTBase, T?) -> T) {
        map.put(FieldType.create(clazz), NBTSerializer(reader as (NBTBase, Any?) -> Any, writer as (Any) -> NBTBase))
    }

    @JvmStatic
    fun aliasAs(aliasTo: Class<*>, clazz: Class<*>) {
        val aliasValue = map[FieldType.create(aliasTo)]
        if(aliasValue != null)
            map[FieldType.create(clazz)] = aliasValue
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
        if (type is FieldTypeGeneric) {
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
}

data class NBTSerializer(val reader: (NBTBase, Any?) -> Any, val writer: (Any) -> NBTBase)

private object BasicNBTSerializers {

    fun primitives() {
        NBTSerializationHandlers.mapHandler(Char::class.javaPrimitiveType!!, { NBTTagByte(it.toByte()) }, { it, existing -> it.safeCast(NBTPrimitive::class.java).byte.toChar() })
        NBTSerializationHandlers.mapHandler(Byte::class.javaPrimitiveType!!, ::NBTTagByte, { it, existing -> it.safeCast(NBTPrimitive::class.java).byte })
        NBTSerializationHandlers.mapHandler(Short::class.javaPrimitiveType!!, ::NBTTagShort, { it, existing -> it.safeCast(NBTPrimitive::class.java).short })
        NBTSerializationHandlers.mapHandler(Int::class.javaPrimitiveType!!, ::NBTTagInt, { it, existing -> it.safeCast(NBTPrimitive::class.java).int })
        NBTSerializationHandlers.mapHandler(Long::class.javaPrimitiveType!!, ::NBTTagLong, { it, existing -> it.safeCast(NBTPrimitive::class.java).long })

        NBTSerializationHandlers.mapHandler(Float::class.javaPrimitiveType!!, ::NBTTagFloat, { it, existing -> it.safeCast(NBTPrimitive::class.java).float })
        NBTSerializationHandlers.mapHandler(Double::class.javaPrimitiveType!!, ::NBTTagDouble, { it, existing -> it.safeCast(NBTPrimitive::class.java).double })
        NBTSerializationHandlers.mapHandler(Boolean::class.javaPrimitiveType!!, { NBTTagByte(if (it) 1 else 0) }, { it, existing -> it.safeCast(NBTPrimitive::class.java).byte == 1.toByte() })
        NBTSerializationHandlers.mapHandler(String::class.java, ::NBTTagString, { it, existing -> it.safeCast(NBTTagString::class.java).string })

        NBTSerializationHandlers.aliasAs(Char::class.javaPrimitiveType!!, Char::class.javaObjectType)
        NBTSerializationHandlers.aliasAs(Byte::class.javaPrimitiveType!!, Byte::class.javaObjectType)
        NBTSerializationHandlers.aliasAs(Short::class.javaPrimitiveType!!, Short::class.javaObjectType)
        NBTSerializationHandlers.aliasAs(Int::class.javaPrimitiveType!!, Int::class.javaObjectType)
        NBTSerializationHandlers.aliasAs(Long::class.javaPrimitiveType!!, Long::class.javaObjectType)

        NBTSerializationHandlers.aliasAs(Float::class.javaPrimitiveType!!, Float::class.javaObjectType)
        NBTSerializationHandlers.aliasAs(Double::class.javaPrimitiveType!!, Double::class.javaObjectType)
        NBTSerializationHandlers.aliasAs(Boolean::class.javaPrimitiveType!!, Boolean::class.javaObjectType)
    }

    fun primitiveArrays() {
        NBTSerializationHandlers.mapHandler(CharArray::class.java, { NBTTagIntArray(it.map(Char::toInt).toIntArray()) }, { it, existing -> it.safeCast(NBTTagIntArray::class.java).intArray.map(Int::toChar).toCharArray() })
        NBTSerializationHandlers.mapHandler(ShortArray::class.java, { NBTTagIntArray(it.map(Short::toInt).toIntArray()) }, { it, existing -> it.safeCast(NBTTagIntArray::class.java).intArray.map(Int::toShort).toShortArray() })
        NBTSerializationHandlers.mapHandler(ByteArray::class.java, ::NBTTagByteArray, { it, existing -> it.safeCast(NBTTagByteArray::class.java).byteArray })
        NBTSerializationHandlers.mapHandler(IntArray::class.java, ::NBTTagIntArray, { it, existing -> it.safeCast(NBTTagIntArray::class.java).intArray })
        NBTSerializationHandlers.mapHandler(LongArray::class.java, {
            val tag = NBTTagList()
            it.forEachIndexed { i, l ->
                tag.appendTag(NBTTagLong(l))
            }
            tag
        }, { it, existing ->
            val tag = it.safeCast(NBTTagList::class.java)
            val list = if (existing == null || existing.size != tag.tagCount()) LongArray(tag.tagCount()) else existing
            tag.forEachIndexed<NBTBase> { i, t ->
                list[i] = t.safeCast(NBTPrimitive::class.java).long
            }
            list
        })

        NBTSerializationHandlers.mapHandler(FloatArray::class.java, {
            val tag = NBTTagList()
            it.forEachIndexed { i, f ->
                tag.appendTag(NBTTagFloat(f))
            }
            tag
        }, { it, existing ->
            val tag = it.safeCast(NBTTagList::class.java)
            val list = if (existing == null || existing.size != tag.tagCount()) FloatArray(tag.tagCount()) else existing
            tag.forEachIndexed<NBTBase> { i, t ->
                list[i] = t.safeCast(NBTPrimitive::class.java).float
            }
            list
        })
        NBTSerializationHandlers.mapHandler(DoubleArray::class.java, {
            val tag = NBTTagList()
            it.forEachIndexed { i, d ->
                tag.appendTag(NBTTagDouble(d))
            }
            tag
        }, { it, existing ->
            val tag = it.safeCast(NBTTagList::class.java)
            val list = if (existing == null || existing.size != tag.tagCount()) DoubleArray(tag.tagCount()) else existing
            tag.forEachIndexed<NBTBase> { i, t ->
                list[i] = t.safeCast(NBTPrimitive::class.java).double
            }
            list
        })
        NBTSerializationHandlers.mapHandler(BooleanArray::class.java, {
            NBTTagByteArray(it.map { if (it) 1.toByte() else 0.toByte() }.toByteArray())
        }, { it, existing ->
            val tag = it.safeCast(NBTTagByteArray::class.java)
            val list = if (existing == null || existing.size != tag.byteArray.size) BooleanArray(tag.byteArray.size) else existing
            tag.byteArray.forEachIndexed { i, t ->
                list[i] = if (t == 1.toByte()) true else false
            }
            list
        })

        NBTSerializationHandlers.aliasAs(CharArray::class.java, Array<Char>::class.java)
        NBTSerializationHandlers.aliasAs(ByteArray::class.java, Array<Byte>::class.java)
        NBTSerializationHandlers.aliasAs(ShortArray::class.java, Array<Short>::class.java)
        NBTSerializationHandlers.aliasAs(IntArray::class.java, Array<Int>::class.java)
        NBTSerializationHandlers.aliasAs(LongArray::class.java, Array<Long>::class.java)

        NBTSerializationHandlers.aliasAs(FloatArray::class.java, Array<Float>::class.java)
        NBTSerializationHandlers.aliasAs(DoubleArray::class.java, Array<Double>::class.java)
        NBTSerializationHandlers.aliasAs(BooleanArray::class.java, Array<Boolean>::class.java)
    }

    fun misc() {
        NBTSerializationHandlers.mapHandler(Color::class.java, { NBTTagInt(it.rgb) }, { it, existing -> Color(it.safeCast(NBTPrimitive::class.java).int, true) })
        NBTSerializationHandlers.mapHandler(NBTTagCompound::class.java, { it }, { it, existing -> it.safeCast(NBTTagCompound::class.java) })

        // Item Handlers
        NBTSerializationHandlers.mapHandler(ItemStack::class.java, { it.serializeNBT() ?: NBTTagCompound() }, { it, existing ->
            val compound = it.safeCast(NBTTagCompound::class.java)
            ItemStack.loadItemStackFromNBT(compound)
        })
        NBTSerializationHandlers.mapHandler(ItemStackHandler::class.java, { it.serializeNBT() ?: NBTTagCompound() }, { it, existing ->
            val handler = ItemStackHandler()
            val compound = it.safeCast(NBTTagCompound::class.java)
            handler.deserializeNBT(compound)
            handler
        })
    }

    fun vectors() {
        NBTSerializationHandlers.mapHandler(Vec3d::class.java, {
            val list = NBTTagList()
            list.appendTag(NBTTagDouble(it.xCoord))
            list.appendTag(NBTTagDouble(it.yCoord))
            list.appendTag(NBTTagDouble(it.zCoord))
            list
        }, { it, existing ->
            val tag = it.safeCast(NBTTagList::class.java)
            val x = tag.getDoubleAt(0)
            val y = tag.getDoubleAt(1)
            val z = tag.getDoubleAt(2)
            Vec3d(x, y, z)
        })
        NBTSerializationHandlers.mapHandler(Vec3i::class.java, {
            val list = NBTTagList()
            list.appendTag(NBTTagInt(it.x))
            list.appendTag(NBTTagInt(it.y))
            list.appendTag(NBTTagInt(it.z))
            list
        }, { it, existing ->
            val tag = it.safeCast(NBTTagList::class.java)
            val x = tag.getIntAt(0)
            val y = tag.getIntAt(1)
            val z = tag.getIntAt(2)
            Vec3i(x, y, z)
        })
        NBTSerializationHandlers.mapHandler(Vec2d::class.java, {
            val list = NBTTagList()
            list.appendTag(NBTTagDouble(it.x))
            list.appendTag(NBTTagDouble(it.y))
            list
        }, { it, existing ->
            val tag = it.safeCast(NBTTagList::class.java)
            val x = tag.getDoubleAt(0)
            val y = tag.getDoubleAt(1)
            Vec2d(x, y)
        })
        NBTSerializationHandlers.mapHandler(BlockPos::class.java, { NBTTagLong(it.toLong()) }, { it, existing -> BlockPos.fromLong(it.safeCast(NBTPrimitive::class.java).long) })
    }
}

private object SpecialNBTSerializers {

    fun specials() {
        NBTSerializationHandlers.registerSpecialHandler { createArrayMapping(it) }
        NBTSerializationHandlers.registerSpecialHandler { createEnumMapping(it) }
    }

    fun generics() {
        NBTSerializationHandlers.registerGenericHandler { createHashMap(it) }
    }

    private fun createHashMap(type: FieldTypeGeneric): NBTSerializer? {
        if (!HashMap::class.java.isAssignableFrom(type.clazz))
            return null

        val keyType = type.generic(0) ?: throw IllegalArgumentException("Couldn't find key generic for HashMap serializer generation")
        val valueType = type.generic(1) ?: throw IllegalArgumentException("Couldn't find value generic for HashMap serializer generation")

        val keyReader = NBTSerializationHandlers.getReaderUnchecked(keyType)
        val keyWriter = NBTSerializationHandlers.getWriterUnchecked(keyType)

        val valueReader = NBTSerializationHandlers.getReaderUnchecked(valueType)
        val valueWriter = NBTSerializationHandlers.getWriterUnchecked(valueType)

        if (keyReader == null || keyWriter == null || valueReader == null || valueWriter == null)
            return null

        @Suppress("UNCHECKED_CAST")
        val serializer =
                if (keyType.clazz == String::class.java) {
                    NBTSerializer(reader@{ nbt, existing ->
                        existing as HashMap<String, Any?>?

                        val tag = nbt.safeCast(NBTTagCompound::class.java)
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
                            if (v == null)
                                nullSig.appendTag(NBTTagString(k))
                            else
                                values.setTag(k, valueWriter(v))
                        }

                        tag
                    })
                } else {
                    NBTSerializer(reader@{ nbt, existing ->
                        existing as HashMap<Any, Any?>?

                        val entries = nbt.safeCast(NBTTagList::class.java)
                        val map = type.clazz.newInstance() as HashMap<Any, Any?>

                        entries.forEach<NBTTagCompound> { compound ->
                            val key = keyReader(compound.getTag("key"), null)
                            val value = if (compound.hasKey("value")) valueReader(compound.getTag("value"), existing?.get(key)) else null
                            map[key] = value
                        }

                        map
                    }, writer@{ obj ->
                        obj as HashMap<Any, *>

                        val entries = NBTTagList()

                        obj.forEach { k, v ->
                            val compound = NBTTagCompound()
                            compound.setTag("key", keyWriter(k))
                            if (v != null) compound.setTag("value", valueWriter(v))

                            entries.appendTag(compound)
                        }

                        entries
                    })
                }

        return serializer
    }

    private fun createEnumMapping(type: FieldType): NBTSerializer? {
        if (type !is FieldTypeClass || !type.clazz.isEnum)
            return null

        val values = type.clazz.enumConstants
        val serializer = NBTSerializer(reader@{ nbt, existing ->
            values[nbt.safeCast(NBTTagShort::class.java).short.toInt()]
        }, writer@{ obj ->
            NBTTagShort((obj as Enum<*>).ordinal.toShort())
        })

        return serializer
    }

    private fun createArrayMapping(type: FieldType): NBTSerializer? {
        if (type !is FieldTypeArray)
            return null

        val componentType = type.componentType

        val subReader = NBTSerializationHandlers.getReaderUnchecked(componentType)
        val subWriter = NBTSerializationHandlers.getWriterUnchecked(componentType)

        if (subReader == null || subWriter == null)
            return null

        val serializer = NBTSerializer(reader@{ nbt, existing ->
            existing as Array<*>?
            val compound = nbt.safeCast(NBTTagCompound::class.java)
            val list = compound.getTag("list").safeCast(NBTTagList::class.java)

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
