package com.teamwizardry.librarianlib.features.helpers

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import java.util.*

@Deprecated("Use NBTHelper methods instead")
object ItemNBTHelper {

    @JvmStatic
    @Deprecated("Use raw call instead", ReplaceWith("stack.hasTagCompound()"))
    fun detectNBT(stack: ItemStack) = stack.hasTagCompound()

    @JvmStatic
    @JvmOverloads
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("if (modify) stack.getOrCreateNBT() else (stack.tagCompound ?: NBTTagCompound())", "net.minecraft.nbt.NBTTagCompound"))
    fun getNBT(stack: ItemStack, modify: Boolean = true) =
            if (modify) stack.getOrCreateNBT() else (stack.tagCompound ?: NBTTagCompound())

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.removeNBTEntry(tag)"))
    fun removeEntry(stack: ItemStack, tag: String) = stack.removeNBTEntry(tag)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.hasNBTEntry(tag)"))
    fun verifyExistence(stack: ItemStack, tag: String) = stack.hasNBTEntry(tag)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTBoolean(tag, b)"))
    fun setBoolean(stack: ItemStack, tag: String, b: Boolean) = stack.setNBTBoolean(tag, b)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTByte(tag, b)"))
    fun setByte(stack: ItemStack, tag: String, b: Byte) = stack.setNBTByte(tag, b)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTShort(tag, s)"))
    fun setShort(stack: ItemStack, tag: String, s: Short) = stack.setNBTShort(tag, s)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTInt(tag, i)"))
    fun setInt(stack: ItemStack, tag: String, i: Int) = stack.setNBTInt(tag, i)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTIntArray(tag, arr)"))
    fun setIntArray(stack: ItemStack, tag: String, arr: IntArray) = stack.setNBTIntArray(tag, arr)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTByteArray(tag, arr)"))
    fun setIntArray(stack: ItemStack, tag: String, arr: ByteArray) = stack.setNBTByteArray(tag, arr)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTLong(tag, l)"))
    fun setLong(stack: ItemStack, tag: String, l: Long) = stack.setNBTLong(tag, l)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTFloat(tag, f)"))
    fun setFloat(stack: ItemStack, tag: String, f: Float) = stack.setNBTFloat(tag, f)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTDouble(tag, d)"))
    fun setDouble(stack: ItemStack, tag: String, d: Double) = stack.setNBTDouble(tag, d)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTCompound(tag, cmp)"))
    fun setCompound(stack: ItemStack, tag: String, cmp: NBTTagCompound) = stack.setNBTCompound(tag, cmp)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTString(tag, s)"))
    fun setString(stack: ItemStack, tag: String, s: String) = stack.setNBTString(tag, s)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTList(tag, list)"))
    fun setList(stack: ItemStack, tag: String, list: NBTTagList) = stack.setNBTList(tag, list)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTUniqueId(tag, uuid)"))
    fun setUUID(stack: ItemStack, tag: String, uuid: UUID) = stack.setNBTUniqueId(tag, uuid)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.setNBTTag(tag, value)"))
    fun set(stack: ItemStack, tag: String, value: NBTBase) = stack.setNBTTag(tag, value)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTBoolean(tag, defaultExpected)"))
    fun getBoolean(stack: ItemStack, tag: String, defaultExpected: Boolean) = stack.getNBTBoolean(tag, defaultExpected)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTByte(tag, defaultExpected)"))
    fun getByte(stack: ItemStack, tag: String, defaultExpected: Byte) = stack.getNBTByte(tag, defaultExpected)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTShort(tag, defaultExpected)"))
    fun getShort(stack: ItemStack, tag: String, defaultExpected: Short) = stack.getNBTShort(tag, defaultExpected)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTInt(tag, defaultExpected)"))
    fun getInt(stack: ItemStack, tag: String, defaultExpected: Int) = stack.getNBTInt(tag, defaultExpected)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTIntArray(tag)"))
    fun getIntArray(stack: ItemStack, tag: String): IntArray? = stack.getNBTIntArray(tag)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTByteArray(tag)"))
    fun getByteArray(stack: ItemStack, tag: String): ByteArray? = stack.getNBTByteArray(tag)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTLong(tag, defaultExpected)"))
    fun getLong(stack: ItemStack, tag: String, defaultExpected: Long) = stack.getNBTLong(tag, defaultExpected)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTFloat(tag, defaultExpected)"))
    fun getFloat(stack: ItemStack, tag: String, defaultExpected: Float) = stack.getNBTFloat(tag, defaultExpected)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTDouble(tag, defaultExpected)"))
    fun getDouble(stack: ItemStack, tag: String, defaultExpected: Double) = stack.getNBTDouble(tag, defaultExpected)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTCompound(tag)"))
    fun getCompound(stack: ItemStack, tag: String): NBTTagCompound? = stack.getNBTCompound(tag)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTString(tag) ?: defaultExpected"))
    fun getString(stack: ItemStack, tag: String, defaultExpected: String?) = stack.getNBTString(tag) ?: defaultExpected

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTList(tag, nbtClass)"))
    fun getList(stack: ItemStack, tag: String, nbtClass: Class<out NBTBase>) = stack.getNBTList(tag, nbtClass)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTList(tag, objType)"))
    fun getList(stack: ItemStack, tag: String, objType: Int): NBTTagList? = stack.getNBTList(tag, objType)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTUniqueId(tag)"))
    fun getUUID(stack: ItemStack, tag: String): UUID? = stack.getNBTUniqueId(tag)

    @JvmStatic
    @Deprecated("Use NBTHelper methods instead", ReplaceWith("stack.getNBTTag(tag)"))
    fun get(stack: ItemStack, tag: String) = stack.getNBTTag(tag)


}
