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
     * Get the default value for a type from its serializer
     *
     * @throws NoSuchSerializerError if there is no serializer for the passed type
     */
    fun getDefault(type: FieldType): Any {
        return getOrCreate(type).getDefault()
    }

    /**
     * Get the serializer implementation for the given type.
     *
     * _**DO NOT USE IN SERIALIZERS!!!**_ Use [lazy] instead
     *
     * @throws NoSuchSerializerError if there is no serializer registered for the passed type
     */
    fun getOrCreate(type: FieldType, vararg ignoreFactories: SerializerFactory): Serializer<Any> {
        @Suppress("UNCHECKED_CAST")
        return serializers.getOrPut(type, { findFactoryForType(type, ignoreFactories).create(type) as Serializer<Any> })
    }

    /**
     * Get a lazy getter for the serializer for the given type.
     *
     * Use this in serializers and access only when needed.
     * This allows recursive nesting (e.g. `ArrayList<ArrayList<Value>>` or `class FooBar { val bar: FooBar? }`)
     *
     * @throws NoSuchSerializerError if there is no serializer registered for the passed type
     */
    fun lazy(type: FieldType, vararg ignoreFactories: SerializerFactory): Lazy<Serializer<Any>> {
        if(type !in serializers) findFactoryForType(type, ignoreFactories) // throw an error immediately if there is no serializer
        return kotlin.lazy { getOrCreate(type, *ignoreFactories) }
    }

    /**
     * Find an applicable factory for passed type
     *
     * @throws NoSuchSerializerError if there is no factory that can handle the passed type
     */
    private fun findFactoryForType(type: FieldType, ignoreFactories: Array<out SerializerFactory>): SerializerFactory {
        val factory = factories.values.maxBy {
            if(it in ignoreFactories)
                return@maxBy SerializerFactoryMatch.NONE
            it.canApply(type)
        }

        if(factory == null || factory.canApply(type) == SerializerFactoryMatch.NONE)
            throw NoSuchSerializerError(type)
        return factory
    }
}

class NoSuchSerializerError(type: FieldType) : RuntimeException("No serializer for type $type")
