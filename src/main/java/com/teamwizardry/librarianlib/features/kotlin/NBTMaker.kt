@file:JvmName("NBTMaker")

package com.teamwizardry.librarianlib.features.kotlin

import net.minecraft.nbt.*
import net.minecraft.util.ResourceLocation
import java.util.*
import java.util.function.Consumer

/**
 * @author WireSegal
 * Created at 3:36 PM on 10/20/16.
 */


@JvmName("create")
fun tagCompound(lambda: NbtDsl.() -> Unit) = NbtDsl().apply(lambda).root

fun <T> list(vararg args: T): NBTTagList {
    val list = NBTTagList()
    args.forEach { list.appendTag(convertNBT(it)) }
    return list
}

fun compound(vararg args: Pair<String, *>): NBTTagCompound {
    val comp = NBTTagCompound()
    args.forEach { comp.setTag(it.first, convertNBT(it.second)) }
    return comp
}

class NbtDsl(val root: NBTTagCompound = NBTTagCompound()) {
    operator fun String.invoke(lambda: NbtDsl.() -> Unit) {
        root[this] = tagCompound(lambda)
    }

    infix fun String.to(lambda: NbtDsl.() -> Unit) = this(lambda)

    @JvmName("append")
    operator fun String.invoke(lambda: Consumer<NbtDsl>) = this { lambda.accept(this) }

    @JvmName("append")
    operator fun String.invoke(vararg values: Any?) {
        root[this] = if (values.size == 1) convertNBT(values.first()) else convertNBT(values)
    }

    infix fun String.to(value: Any?) = this(value)
}

operator fun NBTTagCompound.set(key: String, value: NBTBase) = setTag(key, value)


fun convertNBT(value: Any?): NBTBase = when (value) {
    is NBTBase -> value

    is Boolean -> NBTTagByte(if (value) 1 else 0)
    is Byte -> NBTTagByte(value)
    is Char -> NBTTagShort(value.toShort())
    is Short -> NBTTagShort(value)
    is Int -> NBTTagInt(value)
    is Long -> NBTTagLong(value)
    is Float -> NBTTagFloat(value)
    is Double -> NBTTagDouble(value)
    is ByteArray -> NBTTagByteArray(value)
    is String -> NBTTagString(value)
    is IntArray -> NBTTagIntArray(value)
    is UUID -> NBTTagList().apply {
        appendTag(NBTTagLong(value.leastSignificantBits))
        appendTag(NBTTagLong(value.mostSignificantBits))
    }
    is Array<*> -> list(*value)
    is Collection<*> -> list(*value.toTypedArray())
    is Map<*, *> -> compound(*value.toList().map { it.first.toString() to it.second }.toTypedArray())
    is ResourceLocation -> NBTTagString(value.toString())

    else -> throw IllegalArgumentException("Unrecognized type: $value")
}




@Deprecated("Use NbtDsl")
object NBT {

    @Deprecated("", ReplaceWith("list(*args)", "com.teamwizardry.librarianlib.features.kotlin.list"))
    fun <T> list(vararg args: T): NBTTagList = com.teamwizardry.librarianlib.features.kotlin.list(*args)

    @Deprecated("", ReplaceWith("compound(*args)", "com.teamwizardry.librarianlib.features.kotlin.compound"))
    fun comp(vararg args: Pair<String, *>) = compound(*args)
}

@Suppress("DEPRECATION")
@Deprecated("Use new nbt syntax instead",
        ReplaceWith("tagCompound(lambda)", "com.teamwizardry.librarianlib.features.kotlin.tagCompound"),
        DeprecationLevel.ERROR)
fun nbt(lambda: NBT.() -> NBTBase) = NBT.lambda()
