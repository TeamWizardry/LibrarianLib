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
    ): RecordField<I, V> = RecordField(this, key, codec, nullable, hasDefault, paramName)

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

    public fun <I : Any> recordCodec(typeName: String, constructor: KFunction<I>, fields: List<RecordField<I, *>>): MapCodec<I> {
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
                val decodeErrors = mutableMapOf<String, DataResult.Error<*>>()

                for (field in erasedFields) {
                    val mapValue = input.get(field.key)
                    val decodeValue = when {
                        mapValue != null -> {
                            val parseResult = field.codec.parse(ops, mapValue)
                            if (parseResult is DataResult.Success<*> && parseResult.value() == null && !field.nullable) {
                                DataResult.error { "Non-nullable field decoded to null" }
                            } else {
                                parseResult
                            }
                        }
                        field.hasDefault -> continue
                        field.nullable -> DataResult.success(null)
                        else -> DataResult.error { "Key missing" }
                    }

                    when (decodeValue) {
                        is DataResult.Error<*> -> decodeErrors[field.key] = decodeValue
                        is DataResult.Success<*> -> callParams[field.param] = decodeValue.value
                    }
                }

                if (decodeErrors.isNotEmpty()) {
                    return DataResult.error {
                        "Errors decoding $typeName from $input: " +
                            decodeErrors.entries.joinToString(", ") { (key, value) ->
                                "$key => [ ${value.message()} ]"
                            }
                    }
                }

                return DataResult.success(constructor.callBy(callParams))
            }

            override fun toString(): String = typeName
        }
    }

}
