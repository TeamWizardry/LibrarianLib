package com.teamwizardry.librarianlib.scribe.codecs

import com.mojang.serialization.Codec
import com.teamwizardry.librarianlib.scribe.AutoCodec
import kotlin.collections.toMutableList

@AutoCodec.Register(List::class)
public fun <E> listCodec(e: Codec<E>): Codec<List<E>> = Codec.list(e)

@AutoCodec.Register(MutableList::class)
public fun <E> mutableListCodec(e: Codec<E>): Codec<MutableList<E>> =
    listCodec(e).xmap({ it.toMutableList() }, { it })

@AutoCodec.Register(Map::class)
public fun <K, V> mapCodec(k: Codec<K>, v: Codec<V>): Codec<Map<K, V>> {
    // todo: test isStringLike for stuff like Identifier
    return if (!isStringLike(k)) {
        Codec.pair(
            k.fieldOf("k").codec(),
            v.fieldOf("v").codec()
        ).listOf().xmap(
            { it.associate { pair -> pair.first to pair.second } },
            { it.map { (key, value) -> com.mojang.datafixers.util.Pair(key, value) } }
        )
    } else {
        Codec.unboundedMap(k, v)
    }
}

@AutoCodec.Register(MutableMap::class)
public fun <K, V> mutableMapCodec(k: Codec<K>, v: Codec<V>): Codec<MutableMap<K, V>> =
    mapCodec(k, v).xmap({ it.toMutableMap() }, { it })

private val stringCodecPattern =
    """^String(?:\[xmapped]|\[comapFlatMapped]|\[flatComapMapped]|\[flatXmapped])*$""".toRegex()

private fun isStringLike(codec: Codec<*>): Boolean = codec.toString().matches(stringCodecPattern)
