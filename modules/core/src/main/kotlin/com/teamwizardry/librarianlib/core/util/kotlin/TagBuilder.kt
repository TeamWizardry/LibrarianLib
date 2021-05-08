@file:Suppress("NOTHING_TO_INLINE")

package com.teamwizardry.librarianlib.core.util.kotlin

import net.minecraft.nbt.*
import kotlin.jvm.JvmInline

@DslMarker
internal annotation class TagBuilderDslMarker

/**
 * A Kotlin DSL for creating Tag tags.
 *
 * ```kotlin
 * TagBuilder.compound {
 *     "key" *= string("value")
 *     "key" *= list {
 *         +int(3)
 *         +int(4)
 *     }
 * }
 * ```
 */
@TagBuilderDslMarker
public object TagBuilder {
    public inline fun compound(block: CompoundTagBuilder.() -> Unit): CompoundTag =
        CompoundTagBuilder(CompoundTag()).also { it.block() }.tag

    public inline fun list(block: ListTagBuilder.() -> Unit): ListTag =
        ListTagBuilder(ListTag()).also { it.block() }.tag

    public inline fun list(vararg elements: Tag, block: ListTagBuilder.() -> Unit): ListTag =
        ListTagBuilder(ListTag()).also {
            it.addAll(elements.toList())
            it.block()
        }.tag

    public inline fun list(vararg elements: Tag): ListTag = ListTag().also { it.addAll(elements) }

    public inline fun double(value: Number): DoubleTag = DoubleTag.of(value.toDouble())
    public inline fun float(value: Number): FloatTag = FloatTag.of(value.toFloat())
    public inline fun long(value: Number): LongTag = LongTag.of(value.toLong())
    public inline fun int(value: Number): IntTag = IntTag.of(value.toInt())
    public inline fun short(value: Number): ShortTag = ShortTag.of(value.toShort())
    public inline fun byte(value: Number): ByteTag = ByteTag.of(value.toByte())

    public inline fun string(value: String): StringTag = StringTag.of(value)

    public inline fun byteArray(vararg value: Int): ByteArrayTag = ByteArrayTag(ByteArray(value.size) { value[it].toByte() })
    public inline fun byteArray(vararg value: Byte): ByteArrayTag = ByteArrayTag(value)
    public inline fun byteArray(): ByteArrayTag = ByteArrayTag(byteArrayOf()) // avoiding overload ambiguity
    public inline fun longArray(vararg value: Int): LongArrayTag = LongArrayTag(LongArray(value.size) { value[it].toLong() })
    public inline fun longArray(vararg value: Long): LongArrayTag = LongArrayTag(value)
    public inline fun longArray(): LongArrayTag = LongArrayTag(longArrayOf()) // avoiding overload ambiguity
    public inline fun intArray(vararg value: Int): IntArrayTag = IntArrayTag(value)
}

@JvmInline
@TagBuilderDslMarker
public value class CompoundTagBuilder(public val tag: CompoundTag) {
    // configuring this tag

    public operator fun String.timesAssign(nbt: Tag) {
        tag.put(this, nbt)
    }

    // creating new tags

    public inline fun compound(block: CompoundTagBuilder.() -> Unit): CompoundTag =
        CompoundTagBuilder(CompoundTag()).also { it.block() }.tag

    public inline fun list(block: ListTagBuilder.() -> Unit): ListTag =
        ListTagBuilder(ListTag()).also { it.block() }.tag

    public inline fun list(vararg elements: Tag, block: ListTagBuilder.() -> Unit): ListTag =
        ListTagBuilder(ListTag()).also {
            it.addAll(elements.toList())
            it.block()
        }.tag

    public inline fun list(vararg elements: Tag): ListTag = ListTag().also { it.addAll(elements) }

    public inline fun double(value: Number): DoubleTag = DoubleTag.of(value.toDouble())
    public inline fun float(value: Number): FloatTag = FloatTag.of(value.toFloat())
    public inline fun long(value: Number): LongTag = LongTag.of(value.toLong())
    public inline fun int(value: Number): IntTag = IntTag.of(value.toInt())
    public inline fun short(value: Number): ShortTag = ShortTag.of(value.toShort())
    public inline fun byte(value: Number): ByteTag = ByteTag.of(value.toByte())

    public inline fun string(value: String): StringTag = StringTag.of(value)

    public inline fun byteArray(vararg value: Int): ByteArrayTag = ByteArrayTag(ByteArray(value.size) { value[it].toByte() })
    public inline fun byteArray(vararg value: Byte): ByteArrayTag = ByteArrayTag(value)
    public inline fun byteArray(): ByteArrayTag = ByteArrayTag(byteArrayOf()) // avoiding overload ambiguity
    public inline fun longArray(vararg value: Int): LongArrayTag = LongArrayTag(LongArray(value.size) { value[it].toLong() })
    public inline fun longArray(vararg value: Long): LongArrayTag = LongArrayTag(value)
    public inline fun longArray(): LongArrayTag = LongArrayTag(longArrayOf()) // avoiding overload ambiguity
    public inline fun intArray(vararg value: Int): IntArrayTag = IntArrayTag(value)
}

@JvmInline
@TagBuilderDslMarker
public value class ListTagBuilder(public val tag: ListTag) {
    // configuring this tag

    /**
     * Add the given Tag tag to this list
     */
    public operator fun Tag.unaryPlus() {
        tag.add(this)
    }

    /**
     * Add the given Tag tags to this list
     */
    public operator fun Collection<Tag>.unaryPlus() {
        tag.addAll(this)
    }

    /**
     * Add the given Tag tag to this list. This is explicitly defined for [ListTag] because otherwise there is overload
     * ambiguity between the [Tag] and [Collection]<[Tag]> methods.
     */
    public operator fun ListTag.unaryPlus() {
        tag.add(this)
    }

    public fun addAll(nbt: Collection<Tag>) {
        this.tag.addAll(nbt)
    }

    public fun add(nbt: Tag) {
        this.tag.add(nbt)
    }

    // creating new tags

    public inline fun compound(block: CompoundTagBuilder.() -> Unit): CompoundTag =
        CompoundTagBuilder(CompoundTag()).also { it.block() }.tag

    public inline fun list(block: ListTagBuilder.() -> Unit): ListTag =
        ListTagBuilder(ListTag()).also { it.block() }.tag

    public inline fun list(vararg elements: Tag, block: ListTagBuilder.() -> Unit): ListTag =
        ListTagBuilder(ListTag()).also {
            it.addAll(elements.toList())
            it.block()
        }.tag

    public inline fun list(vararg elements: Tag): ListTag = ListTag().also { it.addAll(elements) }

    public inline fun double(value: Number): DoubleTag = DoubleTag.of(value.toDouble())
    public inline fun float(value: Number): FloatTag = FloatTag.of(value.toFloat())
    public inline fun long(value: Number): LongTag = LongTag.of(value.toLong())
    public inline fun int(value: Number): IntTag = IntTag.of(value.toInt())
    public inline fun short(value: Number): ShortTag = ShortTag.of(value.toShort())
    public inline fun byte(value: Number): ByteTag = ByteTag.of(value.toByte())

    public inline fun string(value: String): StringTag = StringTag.of(value)

    public inline fun byteArray(vararg value: Int): ByteArrayTag = ByteArrayTag(ByteArray(value.size) { value[it].toByte() })
    public inline fun byteArray(vararg value: Byte): ByteArrayTag = ByteArrayTag(value)
    public inline fun byteArray(): ByteArrayTag = ByteArrayTag(byteArrayOf()) // avoiding overload ambiguity
    public inline fun longArray(vararg value: Int): LongArrayTag = LongArrayTag(LongArray(value.size) { value[it].toLong() })
    public inline fun longArray(vararg value: Long): LongArrayTag = LongArrayTag(value)
    public inline fun longArray(): LongArrayTag = LongArrayTag(longArrayOf()) // avoiding overload ambiguity
    public inline fun intArray(vararg value: Int): IntArrayTag = IntArrayTag(value)

    public inline fun doubles(vararg value: Int): List<DoubleTag> = value.map { DoubleTag.of(it.toDouble()) }
    public inline fun doubles(vararg value: Double): List<DoubleTag> = value.map { DoubleTag.of(it) }
    public inline fun floats(vararg value: Int): List<FloatTag> = value.map { FloatTag.of(it.toFloat()) }
    public inline fun floats(vararg value: Float): List<FloatTag> = value.map { FloatTag.of(it) }
    public inline fun longs(vararg value: Int): List<LongTag> = value.map { LongTag.of(it.toLong()) }
    public inline fun longs(vararg value: Long): List<LongTag> = value.map { LongTag.of(it) }
    public inline fun ints(vararg value: Int): List<IntTag> = value.map { IntTag.of(it) }
    public inline fun shorts(vararg value: Int): List<ShortTag> = value.map { ShortTag.of(it.toShort()) }
    public inline fun shorts(vararg value: Short): List<ShortTag> = value.map { ShortTag.of(it) }
    public inline fun bytes(vararg value: Int): List<ByteTag> = value.map { ByteTag.of(it.toByte()) }
    public inline fun bytes(vararg value: Byte): List<ByteTag> = value.map { ByteTag.of(it) }

    public fun strings(vararg value: String): List<StringTag> = value.map { StringTag.of(it) }
}
