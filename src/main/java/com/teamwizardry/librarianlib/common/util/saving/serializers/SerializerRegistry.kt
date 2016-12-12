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

    @Suppress("UNCHECKED_CAST")
    fun <R, W> impl(target: SerializerTarget<R, W>, type: FieldType): SerializerImpl<R, W>? {
        return cached.getOrPut(target, { mutableMapOf() }).getOrPut(type, l@ {
            var impl: SerializerImpl<*, *>? = null
            serializers.forEach { name, serializer ->
                if (impl == null && target in serializer && serializer.canApply(type)) {
                    impl = serializer[target](type)
                }
            }
            impl
        }) as SerializerImpl<R, W>?
    }

}
