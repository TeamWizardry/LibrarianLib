package com.teamwizardry.librarianlib.features.saving.serializers

import com.teamwizardry.librarianlib.features.saving.FieldType
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry.register

/**
 * This class manages caching and creation of [Serializer]s.
 *
 * Creation is managed through [SerializerFactory]s. When a serializer is created, [SerializerFactory.canApply] is
 * called for each factory registered with [register], and the one with the most specific match creates the serializer.
 *
 * If a serializer does not have any variation (e.g. it doesn't have any generics and doesn't apply to subclasses) it
 * can be registered with []
 */
object SerializerRegistry {
    private val serializers = mutableMapOf<FieldType, Serializer<Any>>()
    private val factories = linkedMapOf<String, SerializerFactory>()

    fun register(factory: SerializerFactory) {
        factories.put(factory.name, factory)
    }

    fun register(type: FieldType, serializer: Serializer<*>) {
        @Suppress("UNCHECKED_CAST")
        serializers.put(type, serializer as Serializer<Any>)
    }

    /**
     * Get the serializer implementation for the given type.
     *
     * _**DO NOT USE IN SERIALIZER GENERATORS!!!**_ Use [lazy] instead
     */
    fun getOrCreate(type: FieldType): Serializer<Any> {
        return serializers.getOrPut(type, { createSerializerForType(type) })
    }

    /**
     * Get a lazy getter for the serializer for the given type.
     *
     * Use this in serializers and access only when needed.
     * This allows recursive nesting (e.g. `ArrayList<ArrayList<Value>>` or `class FooBar { val bar: FooBar? }`)
     */
    fun lazy(type: FieldType): Lazy<Serializer<Any>> {
        return kotlin.lazy { getOrCreate(type) }
    }

    private fun createSerializerForType(type: FieldType): Serializer<Any> {
        val factory = factories.values.maxBy {
            it.canApply(type)
        }

        if(factory == null || factory.canApply(type) == SerializerFactoryMatch.NONE)
            throw NoSuchSerializerError(type)
        else
            @Suppress("UNCHECKED_CAST")
            return factory.create(type) as Serializer<Any>
    }
}

class NoSuchSerializerError(type: FieldType) : RuntimeException("No serializer for type ${type}")
