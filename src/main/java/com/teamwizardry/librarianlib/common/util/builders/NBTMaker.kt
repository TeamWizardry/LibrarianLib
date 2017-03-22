@file:JvmName("NBTMaker")

package com.teamwizardry.librarianlib.common.util.builders

import com.sun.org.apache.xpath.internal.operations.Bool
import net.minecraft.nbt.*
import java.util.*

/**
 * @author WireSegal
 * Created at 3:36 PM on 10/20/16.
 */

object NBT {

    fun <T> list(vararg args: T): NBTTagList {
        val list = NBTTagList()
        args.forEach { list.appendTag(convertNBT(it)) }
        return list
    }

    fun comp(vararg args: Pair<String, *>): NBTTagCompound {
        val comp = NBTTagCompound()
        args.forEach { comp.setTag(it.first, convertNBT(it.second)) }
        return comp
    }
}

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
    is Array<*> -> NBT.list(*value)
    is Collection<*> -> NBT.list(*value.toTypedArray())
    is Map<*, *> -> NBT.comp(*value.toList().map { it.first.toString() to it.second }.toTypedArray())

    else -> throw IllegalArgumentException("Unrecognized type: " + value)
}

// Not inline because hot reloading fails on inline obfuscated classes under some circumstances
fun nbt(lambda: NBT.() -> NBTBase) = NBT.lambda()
