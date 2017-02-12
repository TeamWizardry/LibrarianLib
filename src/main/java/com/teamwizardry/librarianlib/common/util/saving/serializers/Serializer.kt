package com.teamwizardry.librarianlib.common.util.saving.serializers

import com.teamwizardry.librarianlib.common.util.saving.FieldType
import com.teamwizardry.librarianlib.common.util.saving.FieldTypeVariable

/**
 * Created by TheCodeWarrior
 */
class Serializer(val canApply: (FieldType) -> Boolean, val priority: SerializerPriority) {
    constructor(vararg classes: Class<*>) : this({ it.clazz in classes }, SerializerPriority.EXACT)

    private val serializers = mutableMapOf<SerializerTarget<*, *>, (FieldType) -> SerializerImpl<*, *>>()

    fun <R, W> register(target: SerializerTarget<R, W>, generator: (FieldType) -> SerializerImpl<*, *>) {
        serializers.put(target, { type ->
            if (type is FieldTypeVariable)
                throw IllegalArgumentException("Cannot create serializer for variable field type")
            else
                generator(type)
        })
    }

    fun <R, W> register(target: SerializerTarget<R, W>, impl: SerializerImpl<*, *>) {
        register(target, { impl })
    }

    fun <R, W> register(target: SerializerTarget<R, W>, read: R, write: W) {
        val impl = SerializerImpl(read, write)
        register(target, { impl })
    }

    operator fun contains(target: SerializerTarget<*, *>) = target in serializers

    @Suppress("UNCHECKED_CAST")
    operator fun <R, W> get(target: SerializerTarget<R, W>): (FieldType) -> SerializerImpl<R, W> {
        return serializers[target] as? (FieldType) -> SerializerImpl<R, W> ?: throw IllegalArgumentException("No serializer target registered!")
    }
}

enum class SerializerPriority {
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
    EXACT
}

abstract class SerializerTarget<R, W>(val name: String)

class SerializerImpl<out R, out W>(val read: R, val write: W)

class SerializerException(msg: String) : RuntimeException(msg)
