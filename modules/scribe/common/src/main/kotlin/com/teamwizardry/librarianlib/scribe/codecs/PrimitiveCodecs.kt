package com.teamwizardry.librarianlib.scribe.codecs

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.PrimitiveCodec
import com.teamwizardry.librarianlib.scribe.AutoCodec
import java.nio.ByteBuffer
import java.util.stream.IntStream
import java.util.stream.LongStream

/* region == Primitives == */
@AutoCodec.Register(Boolean::class)
public val BOOL_CODEC: Codec<Boolean> = Codec.BOOL

@AutoCodec.Register(Byte::class)
public val BYTE_CODEC: Codec<Byte> = Codec.BYTE

@AutoCodec.Register(Short::class)
public val SHORT_CODEC: Codec<Short> = Codec.SHORT

@AutoCodec.Register(Int::class)
public val INT_CODEC: Codec<Int> = Codec.INT

@AutoCodec.Register(Long::class)
public val LONG_CODEC: Codec<Long> = Codec.LONG

@AutoCodec.Register(Float::class)
public val FLOAT_CODEC: Codec<Float> = Codec.FLOAT

@AutoCodec.Register(Double::class)
public val DOUBLE_CODEC: Codec<Double> = Codec.DOUBLE

@AutoCodec.Register(Number::class)
public val NUMBER_CODEC: Codec<Number> = object : PrimitiveCodec<Number> {
    override fun <T : Any> read(ops: DynamicOps<T>, input: T): DataResult<Number> {
        return ops.getNumberValue(input)
    }

    override fun <T : Any> write(ops: DynamicOps<T>, value: Number): T {
        return ops.createNumeric(value)
    }
}

@AutoCodec.Register(String::class)
public val STRING_CODEC: Codec<String> = Codec.STRING
/* endregion == Primitives == */

/* region == Buffer/streams == */
@AutoCodec.Register(ByteBuffer::class)
public val BYTE_BUFFER_CODEC: Codec<ByteBuffer> = Codec.BYTE_BUFFER

@AutoCodec.Register(IntStream::class)
public val INT_STREAM_CODEC: Codec<IntStream> = Codec.INT_STREAM

@AutoCodec.Register(LongStream::class)
public val LONG_STREAM_CODEC: Codec<LongStream> = Codec.LONG_STREAM
/* endregion == Buffer/streams == */

/* region == Primitive arrays == */
@AutoCodec.Register(BooleanArray::class)
public val BOOL_ARRAY_CODEC: Codec<BooleanArray> = Codec.list(BOOL_CODEC)
    .xmap({ it.toBooleanArray() }, { it.toList() })

@AutoCodec.Register(ByteArray::class)
public val BYTE_ARRAY_CODEC: Codec<ByteArray> = Codec.list(NUMBER_CODEC)
    .xmap({ list ->
        ByteArray(list.size).also { arr ->
            list.forEachIndexed { i, el -> arr[i] = el.toByte() }
        }
    }, { it.toList() })

@AutoCodec.Register(ShortArray::class)
public val SHORT_ARRAY_CODEC: Codec<ShortArray> = Codec.list(NUMBER_CODEC)
    .xmap({ list ->
        ShortArray(list.size).also { arr ->
            list.forEachIndexed { i, el -> arr[i] = el.toShort() }
        }
    }, { it.toList() })

@AutoCodec.Register(IntArray::class)
public val INT_ARRAY_CODEC: Codec<IntArray> = Codec.list(NUMBER_CODEC)
    .xmap({ list ->
        IntArray(list.size).also { arr ->
            list.forEachIndexed { i, el -> arr[i] = el.toInt() }
        }
    }, { it.toList() })

@AutoCodec.Register(LongArray::class)
public val LONG_ARRAY_CODEC: Codec<LongArray> = Codec.list(NUMBER_CODEC)
    .xmap({ list ->
        LongArray(list.size).also { arr ->
            list.forEachIndexed { i, el -> arr[i] = el.toLong() }
        }
    }, { it.toList() })

@AutoCodec.Register(FloatArray::class)
public val FLOAT_ARRAY_CODEC: Codec<FloatArray> = Codec.list(NUMBER_CODEC)
    .xmap({ list ->
        FloatArray(list.size).also { arr ->
            list.forEachIndexed { i, el -> arr[i] = el.toFloat() }
        }
    }, { it.toList() })

@AutoCodec.Register(DoubleArray::class)
public val DOUBLE_ARRAY_CODEC: Codec<DoubleArray> = Codec.list(NUMBER_CODEC)
    .xmap({ list ->
        DoubleArray(list.size).also { arr ->
            list.forEachIndexed { i, el -> arr[i] = el.toDouble() }
        }
    }, { it.toList() })
/* endregion == Primitive arrays == */
