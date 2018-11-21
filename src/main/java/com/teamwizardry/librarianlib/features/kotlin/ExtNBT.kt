@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.features.kotlin

import com.google.common.reflect.TypeToken
import com.teamwizardry.librarianlib.features.helpers.*
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList


// NBTTagList ==========================================================================================================

val NBTTagList.size
    get() = tagCount()

val NBTTagList.indices: IntRange
    get() = 0 until size

inline fun <reified T : NBTBase> NBTTagList.forEach(run: (T) -> Unit) {
    for (tag in this)
        run(tag.castOrDefault())
}

inline fun <reified T : NBTBase> NBTTagList.forEachIndexed(run: (Int, T) -> Unit) {
    for ((i, tag) in this.withIndex())
        run(i, tag.castOrDefault())
}

// NBT

@Deprecated("Changed name", ReplaceWith("castOrDefault(T::class.java)", "com.teamwizardry.librarianlib.features.helpers.castOrDefault"))
inline fun <reified T : NBTBase> NBTBase.safeCast(): T = castOrDefault(T::class.java)

@Deprecated("Changed name", ReplaceWith("castOrDefault(clazz)", "com.teamwizardry.librarianlib.features.helpers.castOrDefault"))
fun <T : NBTBase> NBTBase.safeCast(clazz: Class<T>): T = castOrDefault(clazz)

@Deprecated("Changed name", ReplaceWith("defaultNBTValue<T>()", "com.teamwizardry.librarianlib.features.helpers.defaultNBTValue"))
inline fun <reified T : NBTBase> defaultNBT() = defaultNBTValue<T>()

@Deprecated("Changed name", ReplaceWith("defaultNBTValue()", "com.teamwizardry.librarianlib.features.helpers.defaultNBTValue"))
fun <T : NBTBase> Class<T>.defaultNBT() = defaultNBTValue()

@Deprecated("Changed name", ReplaceWith("nbtClassForId()", "com.teamwizardry.librarianlib.features.helpers.nbtClassForId"))
fun Int.nbtClass() = nbtClassForId()

@Deprecated("Changed name", ReplaceWith("idForClass()", "com.teamwizardry.librarianlib.features.helpers.idForClass"))
fun Class<out NBTBase>.idForClazz() = idForClass()

class NBTWrapper(val contained: ItemStack) {
    operator fun set(s: String, tag: Any?) {
        if (tag == null)
            contained.removeNBTEntry(s)
        else contained.setNBTTag(s, convertNBT(tag))
    }

    operator fun get(s: String): NBTBase? {
        return contained.getNBTTag(s)
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

