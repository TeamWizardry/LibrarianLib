package com.teamwizardry.librarianlib.features.saving.serializers.builtin.primitives

import com.teamwizardry.librarianlib.features.kotlin.*
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.features.saving.serializers.builtin.Targets
import net.minecraft.nbt.*

/**
 * Created by TheCodeWarrior
 */
object SerializePrimitiveArrays {
    init {
        char()
        short()
        byte()
        int()
        long()
        float()
        double()
        boolean()
    }

    private fun char() {
        SerializerRegistry.register("java:char[]", Serializer(CharArray::class.java, Array<Char>::class.java))

        SerializerRegistry["java:char[]"]?.register(Targets.NBT, Targets.NBT.impl<CharArray>
        ({ nbt, existing, _ ->
            val list = nbt.safeCast(NBTTagList::class.java)
            val array = if (existing != null && existing.size == list.tagCount()) existing else CharArray(list.tagCount())

            list.forEachIndexed<NBTBase> { i, tag ->
                array[i] = tag.safeCast(NBTPrimitive::class.java).short.toChar()
            }
            array
        }, { value, _ ->
            val tag = NBTTagList()
            value.map(Char::toShort).forEach { v -> tag.appendTag(NBTTagShort(v)) }
            tag
        }))

        SerializerRegistry["java:char[]"]?.register(Targets.BYTES, Targets.BYTES.impl<CharArray>
        ({ buf, existing, _ ->
            val length = buf.readVarInt()
            val arr = if (existing != null && existing.size == length) existing else CharArray(length)
            arr.indices.forEach { arr[it] = buf.readChar() }
            arr
        }, { buf, value, _ ->
            buf.writeVarInt(value.size)
            value.forEach { buf.writeChar(it.toInt()) }
        }))
    }

    private fun short() {
        SerializerRegistry.register("java:short[]", Serializer(ShortArray::class.java, Array<Short>::class.java))

        SerializerRegistry["java:short[]"]?.register(Targets.NBT, Targets.NBT.impl<ShortArray>
        ({ nbt, existing, _ ->
            val list = nbt.safeCast(NBTTagList::class.java)
            val array = if (existing != null && existing.size == list.tagCount()) existing else ShortArray(list.tagCount())

            list.forEachIndexed<NBTBase> { i, tag ->
                array[i] = tag.safeCast(NBTPrimitive::class.java).short
            }
            array
        }, { value, _ ->
            val tag = NBTTagList()
            value.forEach { v -> tag.appendTag(NBTTagShort(v)) }
            tag
        }))

        SerializerRegistry["java:short[]"]?.register(Targets.BYTES, Targets.BYTES.impl<ShortArray>
        ({ buf, existing, _ ->
            val length = buf.readVarInt()
            val arr = if (existing != null && existing.size == length) existing else ShortArray(length)
            arr.indices.forEach { arr[it] = buf.readShort() }
            arr
        }, { buf, value, _ ->
            buf.writeVarInt(value.size)
            value.forEach { buf.writeShort(it.toInt()) }
        }))
    }

    private fun byte() {
        SerializerRegistry.register("java:byte[]", Serializer(ByteArray::class.java, Array<Byte>::class.java))

        SerializerRegistry["java:byte[]"]?.register(Targets.NBT, Targets.NBT.impl<ByteArray>
        ({ nbt, _, _ ->
            nbt.safeCast(NBTTagByteArray::class.java).byteArray
        }, { value, _ ->
            NBTTagByteArray(value)
        }))

        SerializerRegistry["java:byte[]"]?.register(Targets.BYTES, Targets.BYTES.impl<ByteArray>
        ({ buf, existing, _ ->
            val length = buf.readVarInt()
            val arr = if (existing != null && existing.size == length) existing else ByteArray(length)
            arr.indices.forEach { arr[it] = buf.readByte() }
            arr
        }, { buf, value, _ ->
            buf.writeVarInt(value.size)
            buf.writeBytes(value)
        }))
    }

    private fun int() {
        SerializerRegistry.register("java:int[]", Serializer(IntArray::class.java, Array<Int>::class.java))

        SerializerRegistry["java:int[]"]?.register(Targets.NBT, Targets.NBT.impl<IntArray>
        ({ nbt, _, _ ->
            nbt.safeCast(NBTTagIntArray::class.java).intArray
        }, { value, _ ->
            NBTTagIntArray(value)
        }))

        SerializerRegistry["java:int[]"]?.register(Targets.BYTES, Targets.BYTES.impl<IntArray>
        ({ buf, existing, _ ->
            val length = buf.readVarInt()
            val arr = if (existing != null && existing.size == length) existing else IntArray(length)
            arr.indices.forEach { arr[it] = buf.readInt() }
            arr
        }, { buf, value, _ ->
            buf.writeVarInt(value.size)
            value.forEach { buf.writeInt(it) }
        }))
    }

    private fun long() {
        SerializerRegistry.register("java:long[]", Serializer(LongArray::class.java, Array<Long>::class.java))

        SerializerRegistry["java:long[]"]?.register(Targets.NBT, Targets.NBT.impl<LongArray>
        ({ nbt, existing, _ ->
            val list = nbt.safeCast(NBTTagList::class.java)
            val array = if (existing != null && existing.size == list.tagCount()) existing else LongArray(list.tagCount())

            list.forEachIndexed<NBTBase> { i, tag ->
                array[i] = tag.safeCast(NBTPrimitive::class.java).long
            }
            array
        }, { value, _ ->
            val tag = NBTTagList()
            value.forEach { tag.appendTag(NBTTagLong(it)) }
            tag
        }))

        SerializerRegistry["java:long[]"]?.register(Targets.BYTES, Targets.BYTES.impl<LongArray>
        ({ buf, existing, _ ->
            val length = buf.readVarInt()
            val arr = if (existing != null && existing.size == length) existing else LongArray(length)
            arr.indices.forEach { arr[it] = buf.readLong() }
            arr
        }, { buf, value, _ ->
            buf.writeVarInt(value.size)
            value.forEach { buf.writeLong(it) }
        }))
    }

    private fun float() {
        SerializerRegistry.register("java:float[]", Serializer(FloatArray::class.java, Array<Float>::class.java))

        SerializerRegistry["java:float[]"]?.register(Targets.NBT, Targets.NBT.impl<FloatArray>
        ({ nbt, existing, _ ->
            val list = nbt.safeCast(NBTTagList::class.java)
            val array = if (existing != null && existing.size == list.tagCount()) existing else FloatArray(list.tagCount())

            list.forEachIndexed<NBTBase> { i, tag ->
                array[i] = tag.safeCast(NBTPrimitive::class.java).float
            }
            array
        }, { value, _ ->
            val tag = NBTTagList()
            value.forEach { tag.appendTag(NBTTagFloat(it)) }
            tag
        }))

        SerializerRegistry["java:float[]"]?.register(Targets.BYTES, Targets.BYTES.impl<FloatArray>
        ({ buf, existing, _ ->
            val length = buf.readVarInt()
            val arr = if (existing != null && existing.size == length) existing else FloatArray(length)
            arr.indices.forEach { arr[it] = buf.readFloat() }
            arr
        }, { buf, value, _ ->
            buf.writeVarInt(value.size)
            value.forEach { buf.writeFloat(it) }
        }))
    }

    private fun double() {
        SerializerRegistry.register("java:double[]", Serializer(DoubleArray::class.java, Array<Double>::class.java))

        SerializerRegistry["java:double[]"]?.register(Targets.NBT, Targets.NBT.impl<DoubleArray>
        ({ nbt, existing, _ ->
            val list = nbt.safeCast(NBTTagList::class.java)
            val array = if (existing != null && existing.size == list.tagCount()) existing else DoubleArray(list.tagCount())

            list.forEachIndexed<NBTBase> { i, tag ->
                array[i] = tag.safeCast(NBTPrimitive::class.java).double
            }
            array
        }, { value, _ ->
            val tag = NBTTagList()
            value.forEach { tag.appendTag(NBTTagDouble(it)) }
            tag
        }))

        SerializerRegistry["java:double[]"]?.register(Targets.BYTES, Targets.BYTES.impl<DoubleArray>
        ({ buf, existing, _ ->
            val length = buf.readVarInt()
            val arr = if (existing != null && existing.size == length) existing else DoubleArray(length)
            arr.indices.forEach { arr[it] = buf.readDouble() }
            arr
        }, { buf, value, _ ->
            buf.writeVarInt(value.size)
            value.forEach { buf.writeDouble(it) }
        }))
    }

    private fun boolean() {
        SerializerRegistry.register("java:boolean[]", Serializer(BooleanArray::class.java, Array<Boolean>::class.java))

        SerializerRegistry["java:boolean[]"]?.register(Targets.NBT, Targets.NBT.impl<BooleanArray>
        ({ nbt, _, _ ->
            nbt.safeCast(NBTTagByteArray::class.java).byteArray.map { it == 1.toByte() }.toBooleanArray()
        }, { value, _ ->
            NBTTagByteArray(value.map { if (it) 1.toByte() else 0.toByte() }.toByteArray())
        }))

        SerializerRegistry["java:boolean[]"]?.register(Targets.BYTES, Targets.BYTES.impl<BooleanArray>
        ({ buf, existing, _ ->
            buf.readBooleanArray(existing)
        }, { buf, value, _ ->
            buf.writeBooleanArray(value)
        }))
    }
}
