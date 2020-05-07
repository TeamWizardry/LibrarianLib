package com.teamwizardry.librarianlib.prism.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import net.minecraft.nbt.ByteArrayNBT
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.IntNBT
import net.minecraft.nbt.NBTUtil
import net.minecraft.nbt.NumberNBT
import java.math.BigDecimal
import java.math.BigInteger
import java.util.BitSet
import java.util.UUID

open class PairSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<Pair<*, *>>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return PairSerializer(prism, mirror as ClassMirror)
    }

    class PairSerializer(prism: NBTPrism, type: ClassMirror): NBTSerializer<Pair<Any?, Any?>>(type) {
        private val firstSerializer by prism[type.typeParameters[0]]
        private val secondSerializer by prism[type.typeParameters[1]]

        override fun deserialize(tag: INBT, existing: Pair<Any?, Any?>?): Pair<Any?, Any?> {
            @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
            return Pair(
                if(tag.contains("First")) firstSerializer.read(tag.expect("First"), existing?.first) else null,
                if(tag.contains("Second")) secondSerializer.read(tag.expect("Second"), existing?.second) else null
            )
        }

        override fun serialize(value: Pair<Any?, Any?>): INBT {
            val tag = CompoundNBT()
            value.first?.also { tag.put("First", firstSerializer.write(it)) }
            value.second?.also { tag.put("Second", secondSerializer.write(it)) }
            return tag
        }
    }
}

open class TripleSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<Triple<*, *, *>>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return TripleSerializer(prism, mirror as ClassMirror)
    }

    class TripleSerializer(prism: NBTPrism, type: ClassMirror): NBTSerializer<Triple<Any?, Any?, Any?>>(type) {
        private val firstSerializer by prism[type.typeParameters[0]]
        private val secondSerializer by prism[type.typeParameters[1]]
        private val thirdSerializer by prism[type.typeParameters[2]]

        override fun deserialize(tag: INBT, existing: Triple<Any?, Any?, Any?>?): Triple<Any?, Any?, Any?> {
            @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
            return Triple(
                if(tag.contains("First")) firstSerializer.read(tag.expect("First"), existing?.first) else null,
                if(tag.contains("Second")) secondSerializer.read(tag.expect("Second"), existing?.second) else null,
                if(tag.contains("Third")) thirdSerializer.read(tag.expect("Third"), existing?.third) else null
            )
        }

        override fun serialize(value: Triple<Any?, Any?, Any?>): INBT {
            val tag = CompoundNBT()
            value.first?.also { tag.put("First", firstSerializer.write(it)) }
            value.second?.also { tag.put("Second", secondSerializer.write(it)) }
            value.third?.also { tag.put("Third", thirdSerializer.write(it)) }
            return tag
        }
    }
}

object BigIntegerSerializer: NBTSerializer<BigInteger>() {
    override fun deserialize(tag: INBT, existing: BigInteger?): BigInteger {
        return BigInteger(tag.expectType<ByteArrayNBT>("tag").byteArray)
    }

    override fun serialize(value: BigInteger): INBT {
        return ByteArrayNBT(value.toByteArray())
    }
}

object BigDecimalSerializer: NBTSerializer<BigDecimal>() {
    override fun deserialize(tag: INBT, existing: BigDecimal?): BigDecimal {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return BigDecimal(
            BigInteger(tag.expect<ByteArrayNBT>("Value").byteArray),
            tag.expect<NumberNBT>("Scale").int
        )
    }

    override fun serialize(value: BigDecimal): INBT {
        return CompoundNBT().also {
            it.put("Value", ByteArrayNBT(value.unscaledValue().toByteArray()))
            it.put("Scale", IntNBT.valueOf(value.scale()))
        }
    }
}

object BitSetSerializer: NBTSerializer<BitSet>() {
    override fun deserialize(tag: INBT, existing: BitSet?): BitSet {
        val bitset = BitSet.valueOf(tag.expectType<ByteArrayNBT>("tag").byteArray)
        return existing?.also {
            it.clear()
            it.or(bitset)
        } ?: bitset
    }

    override fun serialize(value: BitSet): INBT {
        return ByteArrayNBT(value.toByteArray())
    }
}

object UUIDSerializer: NBTSerializer<UUID>() {
    override fun deserialize(tag: INBT, existing: UUID?): UUID {
        return NBTUtil.readUniqueId(tag.expectType("tag"))
    }

    override fun serialize(value: UUID): INBT {
        return NBTUtil.writeUniqueId(value)
    }
}

