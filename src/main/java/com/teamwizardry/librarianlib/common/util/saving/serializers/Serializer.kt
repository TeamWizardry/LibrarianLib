package com.teamwizardry.librarianlib.common.util.saving.serializers

import com.teamwizardry.librarianlib.common.util.saving.FieldType

/**
 * Created by TheCodeWarrior
 */
class Serializer(val canApply: (FieldType) -> Boolean) {
    constructor(vararg classes: Class<*>) : this({ it.clazz in classes })

    protected val serializers = mutableMapOf<SerializerTarget<*, *>, (FieldType) -> SerializerImpl<*, *>>()

    fun <R, W> register(target: SerializerTarget<R, W>, generator: (FieldType) -> SerializerImpl<*, *>) {
        serializers.put(target, generator)
    }

    fun <R, W> register(target: SerializerTarget<R, W>, impl: SerializerImpl<*,*>) {
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

abstract class SerializerTarget<R, W>(val name: String)

class SerializerImpl<out R, out W>(val read: R, val write: W)
