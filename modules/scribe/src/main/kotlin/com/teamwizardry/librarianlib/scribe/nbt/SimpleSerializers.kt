package com.teamwizardry.librarianlib.scribe.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import net.minecraft.nbt.NbtByteArray
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.AbstractNbtNumber
import java.math.BigDecimal
import java.math.BigInteger
import java.util.BitSet
import java.util.UUID

internal class PairSerializerFactory(prism: NbtPrism): NBTSerializerFactory(prism, Mirror.reflect<Pair<*, *>>()) {
    override fun create(mirror: TypeMirror): NbtSerializer<*> {
        return PairSerializer(prism, mirror as ClassMirror)
    }

    class PairSerializer(prism: NbtPrism, type: ClassMirror): NbtSerializer<Pair<Any?, Any?>>(type) {
        private val firstSerializer by prism[type.typeParameters[0]]
        private val secondSerializer by prism[type.typeParameters[1]]

        override fun deserialize(tag: NbtElement): Pair<Any?, Any?> {
            @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
            return Pair(
                if(tag.contains("First")) firstSerializer.read(tag.expect("First")) else null,
                if(tag.contains("Second")) secondSerializer.read(tag.expect("Second")) else null
            )
        }

        override fun serialize(value: Pair<Any?, Any?>): NbtElement {
            val tag = NbtCompound()
            value.first?.also { tag.put("First", firstSerializer.write(it)) }
            value.second?.also { tag.put("Second", secondSerializer.write(it)) }
            return tag
        }
    }
}

internal class TripleSerializerFactory(prism: NbtPrism): NBTSerializerFactory(prism, Mirror.reflect<Triple<*, *, *>>()) {
    override fun create(mirror: TypeMirror): NbtSerializer<*> {
        return TripleSerializer(prism, mirror as ClassMirror)
    }

    class TripleSerializer(prism: NbtPrism, type: ClassMirror): NbtSerializer<Triple<Any?, Any?, Any?>>(type) {
        private val firstSerializer by prism[type.typeParameters[0]]
        private val secondSerializer by prism[type.typeParameters[1]]
        private val thirdSerializer by prism[type.typeParameters[2]]

        override fun deserialize(tag: NbtElement): Triple<Any?, Any?, Any?> {
            @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
            return Triple(
                if(tag.contains("First")) firstSerializer.read(tag.expect("First")) else null,
                if(tag.contains("Second")) secondSerializer.read(tag.expect("Second")) else null,
                if(tag.contains("Third")) thirdSerializer.read(tag.expect("Third")) else null
            )
        }

        override fun serialize(value: Triple<Any?, Any?, Any?>): NbtElement {
            val tag = NbtCompound()
            value.first?.also { tag.put("First", firstSerializer.write(it)) }
            value.second?.also { tag.put("Second", secondSerializer.write(it)) }
            value.third?.also { tag.put("Third", thirdSerializer.write(it)) }
            return tag
        }
    }
}

internal object BigIntegerSerializer: NbtSerializer<BigInteger>() {
    override fun deserialize(tag: NbtElement): BigInteger {
        return BigInteger(tag.expectType<NbtByteArray>("tag").byteArray)
    }

    override fun serialize(value: BigInteger): NbtElement {
        return NbtByteArray(value.toByteArray())
    }
}

internal object BigDecimalSerializer: NbtSerializer<BigDecimal>() {
    override fun deserialize(tag: NbtElement): BigDecimal {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        return BigDecimal(
            BigInteger(tag.expect<NbtByteArray>("Value").byteArray),
            tag.expect<AbstractNbtNumber>("Scale").intValue()
        )
    }

    override fun serialize(value: BigDecimal): NbtElement {
        return NbtCompound().also {
            it.put("Value", NbtByteArray(value.unscaledValue().toByteArray()))
            it.put("Scale", NbtInt.of(value.scale()))
        }
    }
}

internal object BitSetSerializer: NbtSerializer<BitSet>() {
    override fun deserialize(tag: NbtElement): BitSet {
        return BitSet.valueOf(tag.expectType<NbtByteArray>("tag").byteArray)
    }

    override fun serialize(value: BitSet): NbtElement {
        return NbtByteArray(value.toByteArray())
    }
}

internal object UUIDSerializer: NbtSerializer<UUID>() {
    override fun deserialize(tag: NbtElement): UUID {
        return NbtHelper.toUuid(tag.expectType("tag"))
    }

    override fun serialize(value: UUID): NbtElement {
        return NbtHelper.fromUuid(value)
    }
}

