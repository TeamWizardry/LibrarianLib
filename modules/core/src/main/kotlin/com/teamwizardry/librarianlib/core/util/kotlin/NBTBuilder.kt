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
annotation class NBTBuilderDslMarker

@NBTBuilderDslMarker
open class NBTBuilder @PublishedApi internal constructor() {
    inline fun compound(block: CompoundNBTBuilder.() -> Unit): CompoundNBT {
        val builder = CompoundNBTBuilder()
        builder.block()
        return builder.tag
    }

    inline fun list(block: ListNBTBuilder.() -> Unit): ListNBT {
        val builder = ListNBTBuilder()
        builder.block()
        return builder.tag
    }

    inline fun list(vararg elements: INBT, block: ListNBTBuilder.() -> Unit): ListNBT {
        val builder = ListNBTBuilder()
        builder.addAll(elements.toList())
        builder.block()
        return builder.tag
    }

    fun list(vararg elements: INBT): ListNBT {
        val tag = ListNBT()
        tag.addAll(elements)
        return tag
    }

    fun double(value: Int): DoubleNBT = DoubleNBT.valueOf(value.toDouble())
    fun double(value: Double): DoubleNBT = DoubleNBT.valueOf(value)
    fun float(value: Int): FloatNBT = FloatNBT.valueOf(value.toFloat())
    fun float(value: Float): FloatNBT = FloatNBT.valueOf(value)
    fun long(value: Int): LongNBT = LongNBT.valueOf(value.toLong())
    fun long(value: Long): LongNBT = LongNBT.valueOf(value)
    fun int(value: Int): IntNBT = IntNBT.valueOf(value)
    fun short(value: Int): ShortNBT = ShortNBT.valueOf(value.toShort())
    fun short(value: Short): ShortNBT = ShortNBT.valueOf(value)
    fun byte(value: Int): ByteNBT = ByteNBT.valueOf(value.toByte())
    fun byte(value: Byte): ByteNBT = ByteNBT.valueOf(value)

    fun string(value: String): StringNBT = StringNBT.valueOf(value)

    fun byteArray(vararg value: Int): ByteArrayNBT = ByteArrayNBT(value.map { it.toByte() }.toByteArray())
    fun byteArray(vararg value: Byte): ByteArrayNBT = ByteArrayNBT(value)
    fun byteArray(): ByteArrayNBT = ByteArrayNBT(byteArrayOf()) // avoiding overload ambiguity
    fun longArray(vararg value: Int): LongArrayNBT = LongArrayNBT(value.map { it.toLong() }.toLongArray())
    fun longArray(vararg value: Long): LongArrayNBT = LongArrayNBT(value)
    fun longArray(): LongArrayNBT = LongArrayNBT(longArrayOf()) // avoiding overload ambiguity
    fun intArray(vararg value: Int): IntArrayNBT = IntArrayNBT(value)

    companion object: NBTBuilder()
}

class CompoundNBTBuilder @PublishedApi internal constructor(): NBTBuilder() {
    val tag: CompoundNBT = CompoundNBT()

    operator fun String.timesAssign(nbt: INBT) {
        tag.put(this, nbt)
    }
}

class ListNBTBuilder @PublishedApi internal constructor(): NBTBuilder() {
    val tag: ListNBT = ListNBT()

    /**
     * A short alias for `this` which, in combination with [plus], allows syntax like `n+ SomeTag()`
     */
    val n: ListNBTBuilder = this

    /**
     * Add the given NBT tag to this list
     */
    operator fun plus(nbt: INBT) {
        this.tag.add(nbt)
    }

    /**
     * Add the given NBT tags to this list
     */
    operator fun plus(nbt: Collection<INBT>) {
        this.tag.addAll(nbt)
    }

    /**
     * Add the given NBT tag to this list. This is explicitly defined for [ListNBT] because otherwise there is overload
     * ambiguity between the [INBT] and [Collection]<[INBT]> methods.
     */
    operator fun plus(nbt: ListNBT) {
        this.tag.add(nbt)
    }

    fun addAll(nbt: Collection<INBT>) {
        this.tag.addAll(nbt)
    }
    fun add(nbt: INBT) {
        this.tag.add(nbt)
    }

    fun doubles(vararg value: Int): List<DoubleNBT> = value.map { DoubleNBT.valueOf(it.toDouble()) }
    fun doubles(vararg value: Double): List<DoubleNBT> = value.map { DoubleNBT.valueOf(it) }
    fun floats(vararg value: Int): List<FloatNBT> = value.map { FloatNBT.valueOf(it.toFloat()) }
    fun floats(vararg value: Float): List<FloatNBT> = value.map { FloatNBT.valueOf(it) }
    fun longs(vararg value: Int): List<LongNBT> = value.map { LongNBT.valueOf(it.toLong()) }
    fun longs(vararg value: Long): List<LongNBT> = value.map { LongNBT.valueOf(it) }
    fun ints(vararg value: Int): List<IntNBT> = value.map { IntNBT.valueOf(it) }
    fun shorts(vararg value: Int): List<ShortNBT> = value.map { ShortNBT.valueOf(it.toShort()) }
    fun shorts(vararg value: Short): List<ShortNBT> = value.map { ShortNBT.valueOf(it) }
    fun bytes(vararg value: Int): List<ByteNBT> = value.map { ByteNBT.valueOf(it.toByte()) }
    fun bytes(vararg value: Byte): List<ByteNBT> = value.map { ByteNBT.valueOf(it) }

    fun strings(vararg value: String): List<StringNBT> = value.map { StringNBT.valueOf(it) }
}
