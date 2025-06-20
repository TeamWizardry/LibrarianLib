package com.teamwizardry.librarianlib.scribe.codecs

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.teamwizardry.librarianlib.scribe.AutoCodec
import java.nio.ByteBuffer
import java.util.stream.IntStream
import java.util.stream.LongStream

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

@AutoCodec.Register(String::class)
public val STRING_CODEC: Codec<String> = Codec.STRING

@AutoCodec.Register(ByteBuffer::class)
public val BYTE_BUFFER_CODEC: Codec<ByteBuffer> = Codec.BYTE_BUFFER

@AutoCodec.Register(IntStream::class)
public val INT_STREAM_CODEC: Codec<IntStream> = Codec.INT_STREAM

@AutoCodec.Register(LongStream::class)
public val LONG_STREAM_CODEC: Codec<LongStream> = Codec.LONG_STREAM
