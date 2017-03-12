package com.teamwizardry.librarianlib.common.util.saving.serializers

import com.teamwizardry.librarianlib.common.util.saving.FieldType
import com.teamwizardry.librarianlib.common.util.saving.FieldTypeGeneric
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase

/**
 * Created by TheCodeWarrior
 */
//class Serializer(val canApply: (FieldType) -> Boolean, val priority: SerializerPriority) {
//    constructor(vararg classes: Class<*>) : this({ it.clazz in classes }, SerializerPriority.EXACT)
//
//    private val serializers = mutableMapOf<SerializerTarget<*, *>, (FieldType) -> SerializerImpl<*, *>>()
//
//    fun <R, W> register(target: SerializerTarget<R, W>, generator: (FieldType) -> SerializerImpl<*, *>) {
//        serializers.put(target, { type ->
//            if (type is FieldTypeVariable)
//                throw IllegalArgumentException("Cannot create serializer for variable field type")
//            else
//                generator(type)
//        })
//    }
//
//    fun <R, W> register(target: SerializerTarget<R, W>, impl: SerializerImpl<*, *>) {
//        register(target, { impl })
//    }
//
//    fun <R, W> register(target: SerializerTarget<R, W>, read: R, write: W) {
//        val impl = SerializerImpl(read, write)
//        register(target, { impl })
//    }
//
//    operator fun contains(target: SerializerTarget<*, *>) = target in serializers
//
//    @Suppress("UNCHECKED_CAST")
//    operator fun <T> get(target: SerializerTarget<T>): (FieldType) -> SerializerImpl<T> {
//        return serializers[target] as? (FieldType) -> SerializerImpl<T> ?: throw IllegalArgumentException("No serializer target registered!")
//    }
//}

enum class SerializerFactoryMatch {
    /**
     * No match
     */
    NONE,
    /**
     * General class or interface, optionally with arbitrary generic parameters
     */
    GENERAL,
    /**
     * Exact class with arbitrary generic paramters
     */
    GENERIC,
    /**
     * Exact class matching
     */
    EXACT;

    fun or(other: SerializerFactoryMatch): SerializerFactoryMatch {
        if(other > this)
            return other
        else
            return this
    }
}

abstract class Serializer<T: Any>(val type: FieldType) {
    protected abstract fun readNBT(nbt: NBTBase, existing: T?, syncing: Boolean): T
    protected abstract fun writeNBT(value: T, syncing: Boolean): NBTBase

    protected abstract fun readBytes(buf: ByteBuf, existing: T?, syncing: Boolean): T
    protected abstract fun writeBytes(buf: ByteBuf, value: T, syncing: Boolean)


    fun read(nbt: NBTBase, existing: T?, syncing: Boolean): T {
        try {
            return readNBT(nbt, existing, syncing)
        } catch(e: RuntimeException) {
            throw SerializerException("[NBT] Error deserializing $type", e)
        }
    }

    fun write(value: T, syncing: Boolean): NBTBase {
        try {
            return writeNBT(value, syncing)
        } catch(e: RuntimeException) {
            throw SerializerException("[NBT] Error serializing $type", e)
        }
    }

    fun read(buf: ByteBuf, existing: T?, syncing: Boolean): T {
        try {
            return readBytes(buf, existing, syncing)
        } catch(e: RuntimeException) {
            throw SerializerException("[Bytes] Error deserializing $type", e)
        }
    }

    fun write(buf: ByteBuf, value: T, syncing: Boolean)  {
        try {
            return writeBytes(buf, value, syncing)
        } catch(e: RuntimeException) {
            throw SerializerException("[Bytes] Error serializing $type", e)
        }
    }
}

abstract class SerializerFactory(val name: String) {
    abstract fun canApply(type: FieldType): SerializerFactoryMatch

    abstract fun create(type: FieldType): Serializer<*>

    protected fun canApplyExact(type: FieldType, vararg classes: Class<*>): SerializerFactoryMatch {
        if(type.clazz in classes) {
            if(type is FieldTypeGeneric)
                return SerializerFactoryMatch.GENERIC
            else
                return SerializerFactoryMatch.EXACT
        } else {
            return SerializerFactoryMatch.NONE
        }
    }

    protected fun canApplySubclass(type: FieldType, vararg classes: Class<*>): SerializerFactoryMatch {
        if(classes.any { it.isAssignableFrom(type.clazz) })
            return SerializerFactoryMatch.GENERAL
        else
            return SerializerFactoryMatch.NONE
    }
}

class SerializerException : RuntimeException {
    constructor(msg: String) : super(msg)
    constructor(msg: String, cause: Exception) : super(msg, cause)
}
