@file:Suppress("NOTHING_TO_INLINE")

package com.teamwizardry.librarianlib.core.util.kotlin

import net.minecraft.nbt.*
import kotlin.jvm.JvmInline

@DslMarker
internal annotation class NbtBuilderDslMarker

/**
 * A Kotlin DSL for creating NbtElement tags.
 *
 * ```kotlin
 * NbtBuilder.compound {
 *     "key" %= string("value")
 *     "key" %= list {
 *         +int(3)
 *         +int(4)
 *     }
 * }
 * ```
 */
@NbtBuilderDslMarker
public object NbtBuilder {
    public inline fun use(tag: NbtCompound, block: NbtCompoundBuilder.() -> Unit): NbtCompound =
        NbtCompoundBuilder(tag).also(block).tag

    public inline fun compound(block: NbtCompoundBuilder.() -> Unit): NbtCompound =
        NbtCompoundBuilder(NbtCompound()).also(block).tag

    public inline fun list(block: NbtListBuilder.() -> Unit): NbtList =
        NbtListBuilder(NbtList()).also(block).tag

    public inline fun list(vararg elements: NbtElement, block: NbtListBuilder.() -> Unit): NbtList =
        NbtListBuilder(NbtList()).also {
            it.addAll(elements.toList())
            it.block()
        }.tag

    public inline fun list(vararg elements: NbtElement): NbtList = NbtList().also { it.addAll(elements) }
    public inline fun list(elements: Collection<NbtElement>): NbtList = NbtList().also { it.addAll(elements) }

    public inline fun double(value: Number): NbtDouble = NbtDouble.of(value.toDouble())
    public inline fun float(value: Number): NbtFloat = NbtFloat.of(value.toFloat())
    public inline fun long(value: Number): NbtLong = NbtLong.of(value.toLong())
    public inline fun int(value: Number): NbtInt = NbtInt.of(value.toInt())
    public inline fun short(value: Number): NbtShort = NbtShort.of(value.toShort())
    public inline fun byte(value: Number): NbtByte = NbtByte.of(value.toByte())

    public inline fun string(value: String): NbtString = NbtString.of(value)

    public inline fun byteArray(vararg value: Int): NbtByteArray = NbtByteArray(ByteArray(value.size) { value[it].toByte() })
    public inline fun byteArray(vararg value: Byte): NbtByteArray = NbtByteArray(value)
    public inline fun byteArray(): NbtByteArray = NbtByteArray(byteArrayOf()) // avoiding overload ambiguity
    public inline fun longArray(vararg value: Int): NbtLongArray = NbtLongArray(LongArray(value.size) { value[it].toLong() })
    public inline fun longArray(vararg value: Long): NbtLongArray = NbtLongArray(value)
    public inline fun longArray(): NbtLongArray = NbtLongArray(longArrayOf()) // avoiding overload ambiguity
    public inline fun intArray(vararg value: Int): NbtIntArray = NbtIntArray(value)
}

@JvmInline
@NbtBuilderDslMarker
public value class NbtCompoundBuilder(public val tag: NbtCompound) {
    // configuring this tag

    public operator fun String.remAssign(nbt: NbtElement) {
        tag.put(this, nbt)
    }

    // creating new tags

    public inline fun compound(block: NbtCompoundBuilder.() -> Unit): NbtCompound =
        NbtCompoundBuilder(NbtCompound()).also { it.block() }.tag

    public inline fun list(block: NbtListBuilder.() -> Unit): NbtList =
        NbtListBuilder(NbtList()).also { it.block() }.tag

    public inline fun list(vararg elements: NbtElement, block: NbtListBuilder.() -> Unit): NbtList =
        NbtListBuilder(NbtList()).also {
            it.addAll(elements.toList())
            it.block()
        }.tag

    public inline fun list(vararg elements: NbtElement): NbtList = NbtList().also { it.addAll(elements) }
    public inline fun list(elements: Collection<NbtElement>): NbtList = NbtList().also { it.addAll(elements) }

    public inline fun double(value: Number): NbtDouble = NbtDouble.of(value.toDouble())
    public inline fun float(value: Number): NbtFloat = NbtFloat.of(value.toFloat())
    public inline fun long(value: Number): NbtLong = NbtLong.of(value.toLong())
    public inline fun int(value: Number): NbtInt = NbtInt.of(value.toInt())
    public inline fun short(value: Number): NbtShort = NbtShort.of(value.toShort())
    public inline fun byte(value: Number): NbtByte = NbtByte.of(value.toByte())

    public inline fun string(value: String): NbtString = NbtString.of(value)

    public inline fun byteArray(vararg value: Int): NbtByteArray = NbtByteArray(ByteArray(value.size) { value[it].toByte() })
    public inline fun byteArray(vararg value: Byte): NbtByteArray = NbtByteArray(value)
    public inline fun byteArray(): NbtByteArray = NbtByteArray(byteArrayOf()) // avoiding overload ambiguity
    public inline fun longArray(vararg value: Int): NbtLongArray = NbtLongArray(LongArray(value.size) { value[it].toLong() })
    public inline fun longArray(vararg value: Long): NbtLongArray = NbtLongArray(value)
    public inline fun longArray(): NbtLongArray = NbtLongArray(longArrayOf()) // avoiding overload ambiguity
    public inline fun intArray(vararg value: Int): NbtIntArray = NbtIntArray(value)
}

@JvmInline
@NbtBuilderDslMarker
public value class NbtListBuilder(public val tag: NbtList) {
    // configuring this tag

    /**
     * Add the given NbtElement tag to this list
     */
    public operator fun NbtElement.unaryPlus() {
        tag.add(this)
    }

    /**
     * Add the given NbtElement tags to this list
     */
    public operator fun Collection<NbtElement>.unaryPlus() {
        tag.addAll(this)
    }

    /**
     * Add the given NbtElement tag to this list. This is explicitly defined for [NbtList] because otherwise there is overload
     * ambiguity between the [NbtElement] and [Collection]<[NbtElement]> methods.
     */
    public operator fun NbtList.unaryPlus() {
        tag.add(this)
    }

    public fun addAll(nbt: Collection<NbtElement>) {
        this.tag.addAll(nbt)
    }

    public fun add(nbt: NbtElement) {
        this.tag.add(nbt)
    }

    // creating new tags

    public inline fun compound(block: NbtCompoundBuilder.() -> Unit): NbtCompound =
        NbtCompoundBuilder(NbtCompound()).also { it.block() }.tag

    public inline fun list(block: NbtListBuilder.() -> Unit): NbtList =
        NbtListBuilder(NbtList()).also { it.block() }.tag

    public inline fun list(vararg elements: NbtElement, block: NbtListBuilder.() -> Unit): NbtList =
        NbtListBuilder(NbtList()).also {
            it.addAll(elements.toList())
            it.block()
        }.tag

    public inline fun list(vararg elements: NbtElement): NbtList = NbtList().also { it.addAll(elements) }
    public inline fun list(elements: Collection<NbtElement>): NbtList = NbtList().also { it.addAll(elements) }

    public inline fun double(value: Number): NbtDouble = NbtDouble.of(value.toDouble())
    public inline fun float(value: Number): NbtFloat = NbtFloat.of(value.toFloat())
    public inline fun long(value: Number): NbtLong = NbtLong.of(value.toLong())
    public inline fun int(value: Number): NbtInt = NbtInt.of(value.toInt())
    public inline fun short(value: Number): NbtShort = NbtShort.of(value.toShort())
    public inline fun byte(value: Number): NbtByte = NbtByte.of(value.toByte())

    public inline fun string(value: String): NbtString = NbtString.of(value)

    public inline fun byteArray(vararg value: Int): NbtByteArray = NbtByteArray(ByteArray(value.size) { value[it].toByte() })
    public inline fun byteArray(vararg value: Byte): NbtByteArray = NbtByteArray(value)
    public inline fun byteArray(): NbtByteArray = NbtByteArray(byteArrayOf()) // avoiding overload ambiguity
    public inline fun longArray(vararg value: Int): NbtLongArray = NbtLongArray(LongArray(value.size) { value[it].toLong() })
    public inline fun longArray(vararg value: Long): NbtLongArray = NbtLongArray(value)
    public inline fun longArray(): NbtLongArray = NbtLongArray(longArrayOf()) // avoiding overload ambiguity
    public inline fun intArray(vararg value: Int): NbtIntArray = NbtIntArray(value)

    public inline fun doubles(vararg value: Int): List<NbtDouble> = value.map { NbtDouble.of(it.toDouble()) }
    public inline fun doubles(vararg value: Double): List<NbtDouble> = value.map { NbtDouble.of(it) }
    public inline fun floats(vararg value: Int): List<NbtFloat> = value.map { NbtFloat.of(it.toFloat()) }
    public inline fun floats(vararg value: Float): List<NbtFloat> = value.map { NbtFloat.of(it) }
    public inline fun longs(vararg value: Int): List<NbtLong> = value.map { NbtLong.of(it.toLong()) }
    public inline fun longs(vararg value: Long): List<NbtLong> = value.map { NbtLong.of(it) }
    public inline fun ints(vararg value: Int): List<NbtInt> = value.map { NbtInt.of(it) }
    public inline fun shorts(vararg value: Int): List<NbtShort> = value.map { NbtShort.of(it.toShort()) }
    public inline fun shorts(vararg value: Short): List<NbtShort> = value.map { NbtShort.of(it) }
    public inline fun bytes(vararg value: Int): List<NbtByte> = value.map { NbtByte.of(it.toByte()) }
    public inline fun bytes(vararg value: Byte): List<NbtByte> = value.map { NbtByte.of(it) }

    public fun strings(vararg value: String): List<NbtString> = value.map { NbtString.of(it) }
}
