package com.teamwizardry.librarianlib.scribe.codecs

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.teamwizardry.librarianlib.scribe.AutoCodec
import com.teamwizardry.librarianlib.scribe.util.orAbort
import java.util.Optional

@AutoCodec.Register(Optional::class)
public fun <E : Any> optionalCodec(e: Codec<E>): Codec<Optional<E>> {
    return OptionalCodec(e)
}

public class OptionalCodec<E : Any>(public val elementCodec: Codec<E>) : Codec<Optional<E>> {
    override fun <T> encode(input: Optional<E>, ops: DynamicOps<T>, prefix: T): DataResult<T> {
        return if (input.isEmpty) {
            DataResult.success(ops.createMap(emptyMap()))
        } else {
            when (val elementResult = elementCodec.encode(input.get(), ops, prefix)) {
                is DataResult.Error -> elementResult
                is DataResult.Success -> DataResult.success(
                    ops.createMap(mapOf(ops.createString("value") to elementResult.value))
                )
            }
        }
    }

    override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<Optional<E>, T>> {
        val inputMap = ops.getMap(input).orAbort { return it }
        val inputValue = inputMap.get("value")
            ?: return DataResult.success(Pair.of(Optional.empty(), ops.empty()))
        return elementCodec.decode(ops, inputValue)
            .map { Pair.of(Optional.ofNullable(it.first), ops.empty()) }
    }

    override fun toString(): String {
        return "OptionalCodec($elementCodec)"
    }
}
