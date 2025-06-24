package com.teamwizardry.librarianlib.scribe.helpers

import com.mojang.serialization.*
import com.teamwizardry.librarianlib.scribe.util.getOrNull
import java.util.stream.Stream

public object ScribeRecordCodec {
    public fun <I : Any> recordCodec(
        codecName: String,
        keys: Set<String>,
        encode: EncodeContext.(I) -> Unit,
        decode: DecodeContext<I>.() -> DataResult<I>,
    ): MapCodec<I> {
        return object : MapCodec<I>() {
            override fun <T : Any> keys(ops: DynamicOps<T>): Stream<T> {
                return keys.stream().map(ops::createString)
            }

            override fun <T : Any> encode(input: I, ops: DynamicOps<T>, prefix: RecordBuilder<T>): RecordBuilder<T> {
                var recordBuilder = prefix
                val context = object : EncodeContext() {
                    override fun <T> encode(key: String, codec: Codec<T & Any>, optional: Boolean, value: T) {
                        if (value == null && !optional) {
                            recordBuilder = recordBuilder.add(key, DataResult.error { "Non-nullable field was null" })
                        } else if (value != null) {
                            recordBuilder = recordBuilder.add(key, codec.encodeStart(ops, value))
                        }
                    }
                }
                context.encode(input)
                return recordBuilder
            }

            override fun <T : Any> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<I> {
                val context = object : DecodeContext<I>() {
                    override fun <T> decode(key: String, codec: Codec<T & Any>, optional: Boolean): DataResult<T> {
                        val mapValue = input.get(key)
                        if (mapValue == null && !optional) {
                            return DataResult.error { "No key `$key` in $input" }
                        }
                        val result: DataResult<T> = if(mapValue != null) {
                            codec.parse(ops, mapValue)
                        } else {
                            DataResult.success(null)
                        }
                        if (result.isSuccess && result.getOrNull() == null && !optional) {
                            return DataResult.error { "Non-optional key `$key` decoded to null" }
                        }
                        return result
                    }
                }
                return context.decode()
            }

            override fun toString(): String {
                return codecName
            }
        }
    }

    public abstract class EncodeContext {
        public abstract fun <T> encode(key: String, codec: Codec<T & Any>, optional: Boolean, value: T)
    }

    public abstract class DecodeContext<I : Any> {
        public abstract fun <T> decode(key: String, codec: Codec<T & Any>, optional: Boolean): DataResult<T>

        public inline fun <T> DataResult<T>.orAbort(abortFn: (DataResult<I>) -> Nothing): T = when (this) {
            is DataResult.Success -> value
            is DataResult.Error ->
                @Suppress("UNCHECKED_CAST")
                abortFn(this as DataResult<I>) // we know it's an error state, so the type parameter is meaningless
        }
    }

}
