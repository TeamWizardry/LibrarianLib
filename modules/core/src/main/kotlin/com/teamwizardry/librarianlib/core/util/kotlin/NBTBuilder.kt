@file:Suppress("NOTHING_TO_INLINE")

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

/**
 * A Kotlin DSL for creating NBT tags.
 *
 * ```kotlin
 * NBTBuilder.compound {
 *     "key" *= string("value")
 *     "key" *= list {
 *         +int(3)
 *         +int(4)
 *     }
 * }
 * ```
 */
@NBTBuilderDslMarker
public object NBTBuilder {
    public inline fun compound(block: CompoundNBTBuilder.() -> Unit): CompoundNBT =
        CompoundNBTBuilder(CompoundNBT()).also { it.block() }.tag

    public inline fun list(block: ListNBTBuilder.() -> Unit): ListNBT =
        ListNBTBuilder(ListNBT()).also { it.block() }.tag

    public inline fun list(vararg elements: INBT, block: ListNBTBuilder.() -> Unit): ListNBT =
        ListNBTBuilder(ListNBT()).also {
            it.addAll(elements.toList())
            it.block()
        }.tag

    public inline fun list(vararg elements: INBT): ListNBT = ListNBT().also { it.addAll(elements) }

    public inline fun double(value: Number): DoubleNBT = DoubleNBT.valueOf(value.toDouble())
    public inline fun float(value: Number): FloatNBT = FloatNBT.valueOf(value.toFloat())
    public inline fun long(value: Number): LongNBT = LongNBT.valueOf(value.toLong())
    public inline fun int(value: Number): IntNBT = IntNBT.valueOf(value.toInt())
    public inline fun short(value: Number): ShortNBT = ShortNBT.valueOf(value.toShort())
    public inline fun byte(value: Number): ByteNBT = ByteNBT.valueOf(value.toByte())

    public fun string(value: String): StringNBT = StringNBT.valueOf(value)

    public fun byteArray(vararg value: Int): ByteArrayNBT = ByteArrayNBT(value.map { it.toByte() }.toByteArray())
    public fun byteArray(vararg value: Byte): ByteArrayNBT = ByteArrayNBT(value)
    public fun byteArray(): ByteArrayNBT = ByteArrayNBT(byteArrayOf()) // avoiding overload ambiguity
    public fun longArray(vararg value: Int): LongArrayNBT = LongArrayNBT(value.map { it.toLong() }.toLongArray())
    public fun longArray(vararg value: Long): LongArrayNBT = LongArrayNBT(value)
    public fun longArray(): LongArrayNBT = LongArrayNBT(longArrayOf()) // avoiding overload ambiguity
    public fun intArray(vararg value: Int): IntArrayNBT = IntArrayNBT(value)
}

@NBTBuilderDslMarker
public inline class CompoundNBTBuilder(public val tag: CompoundNBT) {
    // configuring this tag

    public operator fun String.timesAssign(nbt: INBT) {
        tag.put(this, nbt)
    }

    // creating new tags

    public inline fun compound(block: CompoundNBTBuilder.() -> Unit): CompoundNBT =
        CompoundNBTBuilder(CompoundNBT()).also { it.block() }.tag

    public inline fun list(block: ListNBTBuilder.() -> Unit): ListNBT =
        ListNBTBuilder(ListNBT()).also { it.block() }.tag

    public inline fun list(vararg elements: INBT, block: ListNBTBuilder.() -> Unit): ListNBT =
        ListNBTBuilder(ListNBT()).also {
            it.addAll(elements.toList())
            it.block()
        }.tag

    public inline fun list(vararg elements: INBT): ListNBT = ListNBT().also { it.addAll(elements) }

    public inline fun double(value: Number): DoubleNBT = DoubleNBT.valueOf(value.toDouble())
    public inline fun float(value: Number): FloatNBT = FloatNBT.valueOf(value.toFloat())
    public inline fun long(value: Number): LongNBT = LongNBT.valueOf(value.toLong())
    public inline fun int(value: Number): IntNBT = IntNBT.valueOf(value.toInt())
    public inline fun short(value: Number): ShortNBT = ShortNBT.valueOf(value.toShort())
    public inline fun byte(value: Number): ByteNBT = ByteNBT.valueOf(value.toByte())

    public fun string(value: String): StringNBT = StringNBT.valueOf(value)

    public fun byteArray(vararg value: Int): ByteArrayNBT = ByteArrayNBT(value.map { it.toByte() }.toByteArray())
    public fun byteArray(vararg value: Byte): ByteArrayNBT = ByteArrayNBT(value)
    public fun byteArray(): ByteArrayNBT = ByteArrayNBT(byteArrayOf()) // avoiding overload ambiguity
    public fun longArray(vararg value: Int): LongArrayNBT = LongArrayNBT(value.map { it.toLong() }.toLongArray())
    public fun longArray(vararg value: Long): LongArrayNBT = LongArrayNBT(value)
    public fun longArray(): LongArrayNBT = LongArrayNBT(longArrayOf()) // avoiding overload ambiguity
    public fun intArray(vararg value: Int): IntArrayNBT = IntArrayNBT(value)
}

@NBTBuilderDslMarker
public inline class ListNBTBuilder(public val tag: ListNBT) {
    // configuring this tag

    /**
     * Add the given NBT tag to this list
     */
    public operator fun INBT.unaryPlus() {
        tag.add(this)
    }

    /**
     * Add the given NBT tags to this list
     */
    public operator fun Collection<INBT>.unaryPlus() {
        tag.addAll(this)
    }

    /**
     * Add the given NBT tag to this list. This is explicitly defined for [ListNBT] because otherwise there is overload
     * ambiguity between the [INBT] and [Collection]<[INBT]> methods.
     */
    public operator fun ListNBT.unaryPlus() {
        tag.add(this)
    }

    public fun addAll(nbt: Collection<INBT>) {
        this.tag.addAll(nbt)
    }

    public fun add(nbt: INBT) {
        this.tag.add(nbt)
    }

    // creating new tags

    public inline fun compound(block: CompoundNBTBuilder.() -> Unit): CompoundNBT =
        CompoundNBTBuilder(CompoundNBT()).also { it.block() }.tag

    public inline fun list(block: ListNBTBuilder.() -> Unit): ListNBT =
        ListNBTBuilder(ListNBT()).also { it.block() }.tag

    public inline fun list(vararg elements: INBT, block: ListNBTBuilder.() -> Unit): ListNBT =
        ListNBTBuilder(ListNBT()).also {
            it.addAll(elements.toList())
            it.block()
        }.tag

    public inline fun list(vararg elements: INBT): ListNBT = ListNBT().also { it.addAll(elements) }

    public inline fun double(value: Number): DoubleNBT = DoubleNBT.valueOf(value.toDouble())
    public inline fun float(value: Number): FloatNBT = FloatNBT.valueOf(value.toFloat())
    public inline fun long(value: Number): LongNBT = LongNBT.valueOf(value.toLong())
    public inline fun int(value: Number): IntNBT = IntNBT.valueOf(value.toInt())
    public inline fun short(value: Number): ShortNBT = ShortNBT.valueOf(value.toShort())
    public inline fun byte(value: Number): ByteNBT = ByteNBT.valueOf(value.toByte())

    public fun string(value: String): StringNBT = StringNBT.valueOf(value)

    public fun byteArray(vararg value: Int): ByteArrayNBT = ByteArrayNBT(value.map { it.toByte() }.toByteArray())
    public fun byteArray(vararg value: Byte): ByteArrayNBT = ByteArrayNBT(value)
    public fun byteArray(): ByteArrayNBT = ByteArrayNBT(byteArrayOf()) // avoiding overload ambiguity
    public fun longArray(vararg value: Int): LongArrayNBT = LongArrayNBT(value.map { it.toLong() }.toLongArray())
    public fun longArray(vararg value: Long): LongArrayNBT = LongArrayNBT(value)
    public fun longArray(): LongArrayNBT = LongArrayNBT(longArrayOf()) // avoiding overload ambiguity
    public fun intArray(vararg value: Int): IntArrayNBT = IntArrayNBT(value)

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
