@file:JvmName("NBTHelper")
package com.teamwizardry.librarianlib.features.helpers

import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraftforge.common.util.Constants
import java.util.*

/**
 * @author WireSegal
 * Created at 12:23 PM on 11/21/18.
 */

// ===================================================================================================== Generic Helpers

private inline fun <T: Any, K, E> T?.getIf(key: K, predicate: T?.(K) -> Boolean, get: T.(K) -> E): E? =
        getIf(key, predicate, get, null)

private inline fun <T: Any, K, E> T?.getIf(key: K, predicate: T?.(K) -> Boolean, get: T.(K) -> E, default: E): E {
    if (this != null && predicate(key))
        return get(key)
    return default
}

// ========================================================================================================= NBT Helpers

fun Class<out NBTBase>.idForClass() = when (this) {
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

fun Int.nbtClassForId(): Class<out NBTBase> {
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

inline fun <reified T : NBTBase> NBTBase.castOrDefault(): T = this.castOrDefault(T::class.java)

@Suppress("UNCHECKED_CAST")
fun <T : NBTBase> NBTBase.castOrDefault(clazz: Class<T>): T {
    return (when {
        clazz.isAssignableFrom(this.javaClass) -> this
        else -> clazz.defaultNBTValue()
    }) as T
}

inline fun <reified T : NBTBase> defaultNBTValue(): T = T::class.java.defaultNBTValue()

@Suppress("UNCHECKED_CAST")
fun <T : NBTBase> Class<T>.defaultNBTValue(): T {
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

// ====================================================================================================== Legacy Support

private fun NBTTagList.toUniqueId(): UUID? {
    if (tagCount() != 2 || get(0) !is NBTPrimitive) return null
    return UUID((get(0) as NBTPrimitive).long, (get(1) as NBTPrimitive).long)
}

private fun NBTTagCompound.updateLegacy(tag: String): NBTTagCompound {
    if (hasKey(tag, Constants.NBT.TAG_LIST)) {
        val list = getTagList(tag, Constants.NBT.TAG_ANY_NUMERIC)
        val converted = list.toUniqueId()
        if (converted != null) {
            removeTag(tag)
            setUniqueId(tag, converted)
        }
    }

    return this
}

// ===================================================================================================== NBTTagCompound?

fun NBTTagCompound?.removeTag(tag: String) = this?.removeTag(tag)

fun NBTTagCompound?.hasNumericKey(tag: String) = this.hasKey(tag, Constants.NBT.TAG_ANY_NUMERIC)
fun NBTTagCompound?.hasKey(tag: String) = this != null && this.hasKey(tag)
fun NBTTagCompound?.hasKey(tag: String, type: Class<out NBTBase>) = this.hasKey(tag, type.idForClass())
fun NBTTagCompound?.hasKey(tag: String, id: Int) = this != null && this.hasKey(tag, id)
fun NBTTagCompound?.hasUniqueId(tag: String) = this != null && this.hasUniqueId(tag)

fun NBTTagCompound?.setBoolean(tag: String, value: Boolean) = this?.setBoolean(tag, value)
fun NBTTagCompound?.setByte(tag: String, value: Byte) = this?.setByte(tag, value)
fun NBTTagCompound?.setShort(tag: String, value: Short) = this?.setShort(tag, value)
fun NBTTagCompound?.setInteger(tag: String, value: Int) = this?.setInteger(tag, value)
fun NBTTagCompound?.setIntArray(tag: String, value: IntArray) = this?.setIntArray(tag, value)
fun NBTTagCompound?.setByteArray(tag: String, value: ByteArray) = this?.setByteArray(tag, value)
fun NBTTagCompound?.setLong(tag: String, value: Long) = this?.setLong(tag, value)
fun NBTTagCompound?.setFloat(tag: String, value: Float) = this?.setFloat(tag, value)
fun NBTTagCompound?.setDouble(tag: String, value: Double) = this?.setDouble(tag, value)
fun NBTTagCompound?.setCompoundTag(tag: String, value: NBTTagCompound) = setTag(tag, value)
fun NBTTagCompound?.setString(tag: String, value: String) = this?.setString(tag, value)
fun NBTTagCompound?.setTagList(tag: String, value: NBTTagList) = setTag(tag, value)
fun NBTTagCompound?.setUniqueId(tag: String, value: UUID) = this?.setUniqueId(tag, value)
fun NBTTagCompound?.setTag(tag: String, value: NBTBase) = this?.setTag(tag, value)

@JvmOverloads
fun NBTTagCompound?.getBoolean(tag: String, defaultExpected: Boolean = false) = getIf(tag, NBTTagCompound?::hasNumericKey, NBTTagCompound::getBoolean, defaultExpected)

@JvmOverloads
fun NBTTagCompound?.getByte(tag: String, defaultExpected: Byte = 0) = getIf(tag, NBTTagCompound?::hasNumericKey, NBTTagCompound::getByte, defaultExpected)

@JvmOverloads
fun NBTTagCompound?.getShort(tag: String, defaultExpected: Short = 0) = getIf(tag, NBTTagCompound?::hasNumericKey, NBTTagCompound::getShort, defaultExpected)

@JvmOverloads
fun NBTTagCompound?.getInteger(tag: String, defaultExpected: Int = 0) = getIf(tag, NBTTagCompound?::hasNumericKey, NBTTagCompound::getInteger, defaultExpected)
fun NBTTagCompound?.getIntArray(tag: String) = getIf(tag, NBTTagCompound?::hasKey, NBTTagCompound::getIntArray)
fun NBTTagCompound?.getByteArray(tag: String) = getIf(tag, NBTTagCompound?::hasKey, NBTTagCompound::getByteArray)

@JvmOverloads
fun NBTTagCompound?.getLong(tag: String, defaultExpected: Long = 0) = getIf(tag, NBTTagCompound?::hasNumericKey, NBTTagCompound::getLong, defaultExpected)

@JvmOverloads
fun NBTTagCompound?.getFloat(tag: String, defaultExpected: Float = 0f) = getIf(tag, NBTTagCompound?::hasNumericKey, NBTTagCompound::getFloat, defaultExpected)

@JvmOverloads
fun NBTTagCompound?.getDouble(tag: String, defaultExpected: Double = 0.0) = getIf(tag, NBTTagCompound?::hasNumericKey, NBTTagCompound::getDouble, defaultExpected)
fun NBTTagCompound?.getCompoundTag(tag: String): NBTTagCompound? = getIf(tag, NBTTagCompound?::hasKey, NBTTagCompound::getCompoundTag)
fun NBTTagCompound?.getString(tag: String) = getIf(tag, NBTTagCompound?::hasKey, NBTTagCompound::getString)
fun NBTTagCompound?.getTagList(tag: String, type: Class<out NBTBase>) = getTagList(tag, type.idForClass())
fun NBTTagCompound?.getTagList(tag: String, objType: Int) = getIf(tag, NBTTagCompound?::hasKey) { getTagList(it, objType) }
fun NBTTagCompound?.getUniqueId(tag: String) = getIf(tag, NBTTagCompound?::hasUniqueId, NBTTagCompound::getUniqueId)
fun NBTTagCompound?.getTag(tag: String) = getIf(tag, NBTTagCompound?::hasKey, NBTTagCompound::getTag)

// =========================================================================================================== ItemStack

fun ItemStack.getOrCreateNBT(): NBTTagCompound {
    val compound = this.tagCompound ?: net.minecraft.nbt.NBTTagCompound()
    this.tagCompound = compound
    return compound
}

fun ItemStack.removeNBTEntry(tag: String) = tagCompound.removeTag(tag)

fun ItemStack.hasNBTEntry(tag: String) = tagCompound.hasKey(tag)
fun ItemStack.hasNBTUniqueIdEntry(tag: String) = tagCompound?.updateLegacy(tag).hasUniqueId(tag)

@JvmName("setBoolean")
fun ItemStack.setNBTBoolean(tag: String, value: Boolean) = getOrCreateNBT().setBoolean(tag, value)

@JvmName("setByte")
fun ItemStack.setNBTByte(tag: String, value: Byte) = getOrCreateNBT().setByte(tag, value)

@JvmName("setShort")
fun ItemStack.setNBTShort(tag: String, value: Short) = getOrCreateNBT().setShort(tag, value)

@JvmName("setInt")
fun ItemStack.setNBTInt(tag: String, value: Int) = getOrCreateNBT().setInteger(tag, value)

@JvmName("setIntArray")
fun ItemStack.setNBTIntArray(tag: String, value: IntArray) = getOrCreateNBT().setIntArray(tag, value)

@JvmName("setByteArray")
fun ItemStack.setNBTByteArray(tag: String, value: ByteArray) = getOrCreateNBT().setByteArray(tag, value)

@JvmName("setLong")
fun ItemStack.setNBTLong(tag: String, value: Long) = getOrCreateNBT().setLong(tag, value)

@JvmName("setFloat")
fun ItemStack.setNBTFloat(tag: String, value: Float) = getOrCreateNBT().setFloat(tag, value)

@JvmName("setDouble")
fun ItemStack.setNBTDouble(tag: String, value: Double) = getOrCreateNBT().setDouble(tag, value)

@JvmName("setCompound")
fun ItemStack.setNBTCompound(tag: String, value: NBTTagCompound) = setNBTTag(tag, value)

@JvmName("setString")
fun ItemStack.setNBTString(tag: String, value: String) = getOrCreateNBT().setString(tag, value)

@JvmName("setList")
fun ItemStack.setNBTList(tag: String, value: NBTTagList) = setNBTTag(tag, value)

@JvmName("setUniqueId")
fun ItemStack.setNBTUniqueId(tag: String, value: UUID) = getOrCreateNBT().setUniqueId(tag, value)

@JvmName("setTag")
fun ItemStack.setNBTTag(tag: String, value: NBTBase) = getOrCreateNBT().setTag(tag, value)

@JvmOverloads
@JvmName("getBoolean")
fun ItemStack.getNBTBoolean(tag: String, defaultExpected: Boolean = false) = tagCompound.getBoolean(tag, defaultExpected)

@JvmOverloads
@JvmName("getByte")
fun ItemStack.getNBTByte(tag: String, defaultExpected: Byte = 0) = tagCompound.getByte(tag, defaultExpected)

@JvmOverloads
@JvmName("getShort")
fun ItemStack.getNBTShort(tag: String, defaultExpected: Short = 0) = tagCompound.getShort(tag, defaultExpected)

@JvmOverloads
@JvmName("getInt")
fun ItemStack.getNBTInt(tag: String, defaultExpected: Int = 0) = tagCompound.getInteger(tag, defaultExpected)

@JvmName("getIntArray")
fun ItemStack.getNBTIntArray(tag: String) = tagCompound.getIntArray(tag)

@JvmName("getByteArray")
fun ItemStack.getNBTByteArray(tag: String) = tagCompound.getByteArray(tag)

@JvmOverloads
@JvmName("getLong")
fun ItemStack.getNBTLong(tag: String, defaultExpected: Long = 0) = tagCompound.getLong(tag, defaultExpected)

@JvmOverloads
@JvmName("getFloat")
fun ItemStack.getNBTFloat(tag: String, defaultExpected: Float = 0f) = tagCompound.getFloat(tag, defaultExpected)

@JvmOverloads
@JvmName("getDouble")
fun ItemStack.getNBTDouble(tag: String, defaultExpected: Double = 0.0) = tagCompound.getDouble(tag, defaultExpected)

@JvmName("getCompound")
fun ItemStack.getNBTCompound(tag: String): NBTTagCompound? = tagCompound.getCompoundTag(tag)

@JvmName("getString")
fun ItemStack.getNBTString(tag: String) = tagCompound.getString(tag)

@JvmName("getList")
fun ItemStack.getNBTList(tag: String, type: Class<out NBTBase>) = getNBTList(tag, type.idForClass())

@JvmName("getList")
fun ItemStack.getNBTList(tag: String, objType: Int) = tagCompound.getTagList(tag, objType)

@JvmName("getUniqueId")
fun ItemStack.getNBTUniqueId(tag: String) = tagCompound?.updateLegacy(tag).getUniqueId(tag)

@JvmName("getTag")
fun ItemStack.getNBTTag(tag: String) = tagCompound.getTag(tag)

// ========================================================================================================== Extensions

operator fun NBTTagCompound?.contains(key: String) = hasKey(key)
