package com.teamwizardry.librarianlib.scribe.test.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.NbtBuilder
import com.teamwizardry.librarianlib.scribe.nbt.BigDecimalSerializer
import com.teamwizardry.librarianlib.scribe.nbt.BigIntegerSerializer
import com.teamwizardry.librarianlib.scribe.nbt.BitSetSerializer
import com.teamwizardry.librarianlib.scribe.nbt.PairSerializerFactory
import com.teamwizardry.librarianlib.scribe.nbt.TripleSerializerFactory
import com.teamwizardry.librarianlib.scribe.nbt.UUIDSerializer
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.util.BitSet
import java.util.UUID

internal class SimpleTests: NbtPrismTest() {
    @Test
    fun `read+write for Pair should be symmetrical`() {
        simple<Pair<String, Int>, PairSerializerFactory.PairSerializer>(Pair("value", 10), NbtBuilder.compound {
            "First" %= string("value")
            "Second" %= int(10)
        })
    }

    @Test
    fun `read+write for Pair with null should not include key`() {
        simple<Pair<String, Int?>, PairSerializerFactory.PairSerializer>(Pair("value", null), NbtBuilder.compound {
            "First" %= string("value")
        })
    }

    @Test
    fun `read+write for Triple should be symmetrical`() {
        simple<Triple<String, Int, Double>, TripleSerializerFactory.TripleSerializer>(Triple("value", 10, 3.14), NbtBuilder.compound {
            "First" %= string("value")
            "Second" %= int(10)
            "Third" %= double(3.14)
        })
    }

    @Test
    fun `read+write for Triple with null should not include key`() {
        simple<Triple<String, Int?, Double>, TripleSerializerFactory.TripleSerializer>(Triple("value", null, 3.14), NbtBuilder.compound {
            "First" %= string("value")
            "Third" %= double(3.14)
        })
    }

    @Test
    fun `read+write for BigInteger should be symmetrical`() {
        simple<BigInteger, BigIntegerSerializer>(BigInteger.valueOf(1234567890123456789), NbtBuilder.byteArray(
            0b00010001, 0b00100010, 0b00010000, 0b11110100, 0b01111101, 0b11101001, 0b10000001, 0b00010101
        ))
    }


    @Test
    fun `read+write for BigDecimal should be symmetrical`() {
        simple<BigDecimal, BigDecimalSerializer>(BigDecimal.valueOf(1234567890123456789, 5), NbtBuilder.compound {
            "Value" %= byteArray(
                0b00010001, 0b00100010, 0b00010000, 0b11110100, 0b01111101, 0b11101001, 0b10000001, 0b00010101
            )
            "Scale" %= int(5)
        })
    }

    @Test
    fun `read+write for BitSet should be symmetrical`() {
        simple<BitSet, BitSetSerializer>(BitSet.valueOf(byteArrayOf(0b0001100, 0b01100111)), NbtBuilder.byteArray(
            0b0001100, 0b01100111
        ))
    }


    @Test
    fun `read+write for UUID should be symmetrical`() {
        val uuid = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5")
        val most = uuid.mostSignificantBits
        val least = uuid.leastSignificantBits
        simple<UUID, UUIDSerializer>(uuid, NbtBuilder.intArray(
            (most shr 32).toInt(), most.toInt(),
            (least shr 32).toInt(), least.toInt()
        ))
    }
}