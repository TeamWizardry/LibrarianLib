@file:JvmName("NBTMaker")
package com.teamwizardry.librarianlib.common.util

import net.minecraft.nbt.*

/**
 * @author WireSegal
 * Created at 3:36 PM on 10/20/16.
 */

object NBT {

    fun <T> list(vararg args: T) : NBTTagList {
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

    is Byte -> NBTTagByte(value)
    is Short -> NBTTagShort(value)
    is Int -> NBTTagInt(value)
    is Long -> NBTTagLong(value)
    is Float -> NBTTagFloat(value)
    is Double -> NBTTagDouble(value)
    is ByteArray -> NBTTagByteArray(value)
    is String -> NBTTagString(value)
    is IntArray -> NBTTagIntArray(value)

    else -> throw IllegalArgumentException("Unrecognized type: " + value)
}

inline fun nbt(lambda: NBT.() -> NBTBase) = NBT.lambda()
