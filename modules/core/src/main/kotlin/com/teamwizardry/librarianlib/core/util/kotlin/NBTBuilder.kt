package com.teamwizardry.librarianlib.core.util.kotlin

import net.minecraft.nbt.ByteArrayNBT
import net.minecraft.nbt.ByteNBT
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.DoubleNBT
import net.minecraft.nbt.FloatNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.IntArrayNBT
import net.minecraft.nbt.IntNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.nbt.LongArrayNBT
import net.minecraft.nbt.LongNBT
import net.minecraft.nbt.ShortNBT
import net.minecraft.nbt.StringNBT

@DslMarker
internal annotation class NBTBuilderDslMarker

@NBTBuilderDslMarker
public open class NBTBuilder @PublishedApi internal constructor() {
    public inline fun compound(block: CompoundNBTBuilder.() -> Unit): CompoundNBT {
        val builder = CompoundNBTBuilder()
        builder.block()
        return builder.tag
    }

    public inline fun list(block: ListNBTBuilder.() -> Unit): ListNBT {
        val builder = ListNBTBuilder()
        builder.block()
        return builder.tag
    }

    public inline fun list(vararg elements: INBT, block: ListNBTBuilder.() -> Unit): ListNBT {
        val builder = ListNBTBuilder()
        builder.addAll(elements.toList())
        builder.block()
        return builder.tag
    }

    public fun list(vararg elements: INBT): ListNBT {
        val tag = ListNBT()
        tag.addAll(elements)
        return tag
    }

    public fun double(value: Int): DoubleNBT = DoubleNBT.valueOf(value.toDouble())
    public fun double(value: Double): DoubleNBT = DoubleNBT.valueOf(value)
    public fun float(value: Int): FloatNBT = FloatNBT.valueOf(value.toFloat())
    public fun float(value: Float): FloatNBT = FloatNBT.valueOf(value)
    public fun long(value: Int): LongNBT = LongNBT.valueOf(value.toLong())
    public fun long(value: Long): LongNBT = LongNBT.valueOf(value)
    public fun int(value: Int): IntNBT = IntNBT.valueOf(value)
    public fun short(value: Int): ShortNBT = ShortNBT.valueOf(value.toShort())
    public fun short(value: Short): ShortNBT = ShortNBT.valueOf(value)
    public fun byte(value: Int): ByteNBT = ByteNBT.valueOf(value.toByte())
    public fun byte(value: Byte): ByteNBT = ByteNBT.valueOf(value)

    public fun string(value: String): StringNBT = StringNBT.valueOf(value)

    public fun byteArray(vararg value: Int): ByteArrayNBT = ByteArrayNBT(value.map { it.toByte() }.toByteArray())
    public fun byteArray(vararg value: Byte): ByteArrayNBT = ByteArrayNBT(value)
    public fun byteArray(): ByteArrayNBT = ByteArrayNBT(byteArrayOf()) // avoiding overload ambiguity
    public fun longArray(vararg value: Int): LongArrayNBT = LongArrayNBT(value.map { it.toLong() }.toLongArray())
    public fun longArray(vararg value: Long): LongArrayNBT = LongArrayNBT(value)
    public fun longArray(): LongArrayNBT = LongArrayNBT(longArrayOf()) // avoiding overload ambiguity
    public fun intArray(vararg value: Int): IntArrayNBT = IntArrayNBT(value)

    public companion object: NBTBuilder()
}

public class CompoundNBTBuilder @PublishedApi internal constructor(): NBTBuilder() {
    public val tag: CompoundNBT = CompoundNBT()

    public operator fun String.timesAssign(nbt: INBT) {
        tag.put(this, nbt)
    }
}

public class ListNBTBuilder @PublishedApi internal constructor(): NBTBuilder() {
    public val tag: ListNBT = ListNBT()

    /**
     * A short alias for `this` which, in combination with [plus], allows syntax like `n+ SomeTag()`
     */
    public val n: ListNBTBuilder = this

    /**
     * Add the given NBT tag to this list
     */
    public operator fun plus(nbt: INBT) {
        this.tag.add(nbt)
    }

    /**
     * Add the given NBT tags to this list
     */
    public operator fun plus(nbt: Collection<INBT>) {
        this.tag.addAll(nbt)
    }

    /**
     * Add the given NBT tag to this list. This is explicitly defined for [ListNBT] because otherwise there is overload
     * ambiguity between the [INBT] and [Collection]<[INBT]> methods.
     */
    public operator fun plus(nbt: ListNBT) {
        this.tag.add(nbt)
    }

    public fun addAll(nbt: Collection<INBT>) {
        this.tag.addAll(nbt)
    }
    public fun add(nbt: INBT) {
        this.tag.add(nbt)
    }

    public fun doubles(vararg value: Int): List<DoubleNBT> = value.map { DoubleNBT.valueOf(it.toDouble()) }
    public fun doubles(vararg value: Double): List<DoubleNBT> = value.map { DoubleNBT.valueOf(it) }
    public fun floats(vararg value: Int): List<FloatNBT> = value.map { FloatNBT.valueOf(it.toFloat()) }
    public fun floats(vararg value: Float): List<FloatNBT> = value.map { FloatNBT.valueOf(it) }
    public fun longs(vararg value: Int): List<LongNBT> = value.map { LongNBT.valueOf(it.toLong()) }
    public fun longs(vararg value: Long): List<LongNBT> = value.map { LongNBT.valueOf(it) }
    public fun ints(vararg value: Int): List<IntNBT> = value.map { IntNBT.valueOf(it) }
    public fun shorts(vararg value: Int): List<ShortNBT> = value.map { ShortNBT.valueOf(it.toShort()) }
    public fun shorts(vararg value: Short): List<ShortNBT> = value.map { ShortNBT.valueOf(it) }
    public fun bytes(vararg value: Int): List<ByteNBT> = value.map { ByteNBT.valueOf(it.toByte()) }
    public fun bytes(vararg value: Byte): List<ByteNBT> = value.map { ByteNBT.valueOf(it) }

    public fun strings(vararg value: String): List<StringNBT> = value.map { StringNBT.valueOf(it) }
}
