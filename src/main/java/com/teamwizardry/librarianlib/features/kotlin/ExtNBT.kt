@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.features.kotlin

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*


// NBTTagList ==========================================================================================================

val NBTTagList.indices: IntRange
    get() = 0..this.tagCount() - 1

operator fun NBTTagList.iterator(): Iterator<NBTBase> {
    return object : Iterator<NBTBase> {
        var i = 0
        val max = this@iterator.tagCount() - 1
        override fun hasNext() = i < max
        override fun next() = this@iterator[i++]
    }
}

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
    return (
            if (clazz.isAssignableFrom(this.javaClass))
                this
            else if (clazz == NBTPrimitive::class.java)
                NBTTagByte(0)
            else if (clazz == NBTTagByteArray::class.java)
                NBTTagByteArray(ByteArray(0))
            else if (clazz == NBTTagString::class.java)
                NBTTagString("")
            else if (clazz == NBTTagList::class.java)
                NBTTagList()
            else if (clazz == NBTTagCompound::class.java)
                NBTTagCompound()
            else if (clazz == NBTTagIntArray::class.java)
                NBTTagIntArray(IntArray(0))
            else
                throw IllegalArgumentException("Unknown NBT type to cast to: $clazz")
            ) as T
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

