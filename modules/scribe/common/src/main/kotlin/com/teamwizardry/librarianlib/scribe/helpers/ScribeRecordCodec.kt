package com.teamwizardry.librarianlib.scribe.helpers

import com.mojang.serialization.*
import com.teamwizardry.librarianlib.scribe.util.orAbort
import java.util.stream.Stream
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findParameterByName

public object ScribeRecordCodec {
    public fun <I, V> KProperty1<I, V>.recordField(
        key: String,
        codec: Codec<V & Any>,
        nullable: Boolean = false,
        hasDefault: Boolean = false,
        paramName: String = this.name,
    ) = RecordField(this, key, codec, nullable, hasDefault, paramName)

    public data class RecordField<I, V>(
        val property: KProperty1<I, V>,
        val key: String,
        val codec: Codec<V & Any>,
        val nullable: Boolean,
        val hasDefault: Boolean,
        val paramName: String,
    ) {
        internal lateinit var param: KParameter
    }

    public fun <I : Any> recordCodec(constructor: KFunction<I>, fields: List<RecordField<I, *>>): MapCodec<I> {
        @Suppress("UNCHECKED_CAST")
        val erasedFields = fields.map { it as RecordField<I, Any?> }
        val keys = fields.map { it.key }

        for (field in fields) {
            field.param = constructor.findParameterByName(field.paramName)
                ?: throw IllegalArgumentException("Couldn't find parameter ${field.paramName}")
        }

        return object : MapCodec<I>() {
            override fun <T : Any> keys(ops: DynamicOps<T>): Stream<T> {
                return keys.stream().map(ops::createString)
            }

            override fun <T : Any> encode(input: I, ops: DynamicOps<T>, prefix: RecordBuilder<T>): RecordBuilder<T> {
                var recordBuilder = prefix
                for (field in erasedFields) {
                    val value = field.property.get(input)
                    if (value == null && !field.nullable) {
                        recordBuilder = recordBuilder.add(field.key, DataResult.error { "Non-nullable field was null" })
                    } else if (value != null) {
                        recordBuilder = recordBuilder.add(field.key, field.codec.encodeStart(ops, value))
                    }
                }
                return recordBuilder
            }

            override fun <T : Any> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<I> {
                val callParams = mutableMapOf<KParameter, Any?>()
                for (field in erasedFields) {
                    val mapValue = input.get(field.key)
                    if (mapValue == null && field.hasDefault) continue
                    if (mapValue == null && !field.nullable) return DataResult.error { "No key `${field.key}` in $input" }

                    val result: DataResult<Any?> = if(mapValue == null) {
                        DataResult.success(null)
                    } else {
                        field.codec.parse(ops, mapValue)
                    }

                    val decoded = result.orAbort { return it }

                    if (decoded == null && !field.nullable) return DataResult.error { "Non-nullable key `${field.key}` decoded to null" }

                    callParams[field.param] = decoded
                }
                return DataResult.success(constructor.callBy(callParams))
            }

            override fun toString(): String {
                return constructor.name
            }
        }
    }

}
