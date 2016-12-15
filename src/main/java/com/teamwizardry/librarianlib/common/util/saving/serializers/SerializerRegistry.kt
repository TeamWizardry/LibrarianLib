package com.teamwizardry.librarianlib.common.util.saving.serializers

import com.teamwizardry.librarianlib.common.util.saving.FieldType

/**
 * Created by TheCodeWarrior
 */
object SerializerRegistry {
    private val cached = mutableMapOf<SerializerTarget<*, *>, MutableMap<FieldType, SerializerImpl<*, *>?>>()
    private val serializers = linkedMapOf<String, Serializer>()

    fun register(name: String, serializer: Serializer) {
        serializers.put(name, serializer)
    }

    operator fun get(loc: String) = serializers[loc]

    /**
     * Get the serializer implementation for the given type.
     *
     * _**DO NOT USE IN SERIALIZER GENERATORS!!!**_ Use [lazyImpl] instead
     *
     * @throws
     */
    @Suppress("UNCHECKED_CAST")
    fun <R, W> impl(target: SerializerTarget<R, W>, type: FieldType): SerializerImpl<R, W> {
        return implInternal(target, type)
    }

    /**
     * Get a lazy getter for the serializer for the given type.
     *
     * Use this in serializer generators and invoke the returned value only when
     * needed. This allows self-nesting (e.g. `ArrayList<ArrayList<Value>>`)
     */
    @Suppress("UNCHECKED_CAST")
    fun <R, W> lazyImpl(target: SerializerTarget<R, W>, type: FieldType): () -> SerializerImpl<R, W> {
        val cachedImpl = cached.getOrPut(target, { mutableMapOf() }).get(type)
        if(cachedImpl != null) {
            return { cachedImpl as SerializerImpl<R, W> }
        }

        var lazyInstance: SerializerImpl<R,W>? = null
        return {
            if(lazyInstance == null) {
                lazyInstance = implInternal(target, type)
            }
            lazyInstance!!
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <R, W> implInternal(target: SerializerTarget<R, W>, type: FieldType): SerializerImpl<R, W> {
        return cached.getOrPut(target, { mutableMapOf() }).getOrPut(type, l@ {
            var impl: SerializerImpl<*, *>? = null
            serializers.forEach { name, serializer ->
                if (impl == null && target in serializer && serializer.canApply(type)) {
                    impl = serializer[target](type)
                }
            }
            impl ?: throw NoSuchSerializerError(target, type)
        }) as SerializerImpl<R, W>
    }
}

class NoSuchSerializerError(target: SerializerTarget<*,*>, type: FieldType) : RuntimeException(calcMessage(target, type)) {
    companion object {
        fun calcMessage(target: SerializerTarget<*, *>, type: FieldType): String {
            return type.toString() + " for " + target.name
        }
    }
}
