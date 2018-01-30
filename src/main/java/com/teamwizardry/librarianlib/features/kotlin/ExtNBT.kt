@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.features.kotlin

import com.google.common.reflect.TypeToken
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*


// NBTTagList ==========================================================================================================

val NBTTagList.indices: IntRange
    get() = 0..this.tagCount() - 1

fun <T : NBTBase> NBTTagList.forEach(run: (T) -> Unit) {
    for (i in this.indices) {
        @Suppress("UNCHECKED_CAST")
        run(this.get(i) as T)
    }
}

fun <T : NBTBase> NBTTagList.forEachIndexed(run: (Int, T) -> Unit) {
    for (i in this.indices) {
        @Suppress("UNCHECKED_CAST")
        run(i, this.get(i) as T)
    }
}

// NBT

inline fun <reified T : NBTBase> NBTBase.safeCast(): T = this.safeCast(T::class.java)

@Suppress("UNCHECKED_CAST")
fun <T : NBTBase> NBTBase.safeCast(clazz: Class<T>): T {
    return (when {
                clazz.isAssignableFrom(this.javaClass) -> this
                else -> clazz.defaultNBT()
            }) as T
}

inline fun <reified T : NBTBase> defaultNBT(): T = T::class.java.defaultNBT()

@Suppress("UNCHECKED_CAST")
fun <T : NBTBase> Class<T>.defaultNBT(): T {
    return (when {
        NBTPrimitive::class.java.isAssignableFrom(this) -> when (this) {
            NBTTagLong::class.java -> NBTTagLong(0)
            NBTTagInt::class.java -> NBTTagInt(0)
            NBTTagShort::class.java -> NBTTagShort(0)
            NBTTagDouble::class.java -> NBTTagDouble(0.0)
            NBTTagFloat::class.java -> NBTTagFloat(0f)
            else -> NBTTagByte(0)
        }
        this == NBTTagByteArray::class.java -> NBTTagByteArray(ByteArray(0))
        this == NBTTagString::class.java -> NBTTagString("")
        this == NBTTagList::class.java -> NBTTagList()
        this == NBTTagCompound::class.java -> NBTTagCompound()
        this == NBTTagIntArray::class.java -> NBTTagIntArray(IntArray(0))
        this == NBTTagLongArray::class.java -> NBTTagLongArray(LongArray(0))
        else -> throw IllegalArgumentException("Unknown NBT type to produce: $this")
    }) as T
}

fun Int.nbtClass(): Class<out NBTBase> {
    return when (this) {
        1 -> NBTTagByte::class.java
        2 -> NBTTagShort::class.java
        3 -> NBTTagInt::class.java
        4 -> NBTTagLong::class.java
        5 -> NBTTagFloat::class.java
        6 -> NBTTagDouble::class.java
        7 -> NBTTagByteArray::class.java
        8 -> NBTTagString::class.java
        9 -> NBTTagList::class.java
        10 -> NBTTagCompound::class.java
        11 -> NBTTagIntArray::class.java
        12 -> NBTTagLongArray::class.java
        else -> throw IllegalArgumentException("Unknown NBT type: $this")
    }
}

fun Class<out NBTBase>.idForClazz(): Int {
    return when (this) {
        NBTTagByte::class.java -> 1
        NBTTagShort::class.java -> 2
        NBTTagInt::class.java -> 3
        NBTTagLong::class.java -> 4
        NBTTagFloat::class.java -> 5
        NBTTagDouble::class.java -> 6
        NBTTagByteArray::class.java -> 7
        NBTTagString::class.java -> 8
        NBTTagList::class.java -> 9
        NBTTagCompound::class.java -> 10
        NBTTagIntArray::class.java -> 11
        NBTTagLongArray::class.java -> 12
        else -> throw IllegalArgumentException("Unknown NBT type: $this")
    }
}

class NBTWrapper(val contained: ItemStack) {
    operator fun set(s: String, tag: Any?) {
        if (tag == null)
            ItemNBTHelper.removeEntry(contained, s)
        else ItemNBTHelper.set(contained, s, convertNBT(tag))
    }

    operator fun get(s: String): NBTBase? {
        return ItemNBTHelper.get(contained, s)
    }
}

val ItemStack.nbt: NBTWrapper
    get() = NBTWrapper(this)

inline fun <reified T : Any> T.toNBT(sync: Boolean = false): NBTBase {
    return AbstractSaveHandler.writeAutoNBTByToken(object : TypeToken<T>() {}, this, sync)
}

inline fun <reified T : Any> NBTBase.fromNBT(sync: Boolean = false): T {
    return AbstractSaveHandler.readAutoNBTByToken(object : TypeToken<T>() {}, this, sync) as T
}
// NBTTagCompound ======================================================================================================

operator fun NBTTagCompound.iterator(): Iterator<Pair<String, NBTBase>> {
    return object : Iterator<Pair<String, NBTBase>> {
        val keys = this@iterator.keySet.iterator()
        override fun hasNext() = keys.hasNext()
        override fun next(): Pair<String, NBTBase> {
            val next = keys.next()
            return next to this@iterator[next]!!
        }
    }
}

operator fun NBTTagCompound.get(key: String): NBTBase? = this.getTag(key)

