package com.teamwizardry.librarianlib.common.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import java.util.*

object ItemNBTHelper {

    private val EMPTY_INT_ARRAY = IntArray(0)
    private val EMPTY_UUID = UUID(0, 0)

    @JvmStatic fun detectNBT(stack: ItemStack) = stack.hasTagCompound()
    @JvmStatic fun getNBT(stack: ItemStack) = initNBT(stack).tagCompound!!
    @JvmStatic fun initNBT(stack: ItemStack): ItemStack {
        if (!detectNBT(stack))
            stack.tagCompound = NBTTagCompound()
        return stack
    }

    @JvmStatic fun removeEntry(stack: ItemStack, tag: String) = getNBT(stack).removeTag(tag)
    @JvmStatic fun removeUUID(stack: ItemStack, tag: String) {
        getNBT(stack).removeTag(tag + "Most")
        getNBT(stack).removeTag(tag + "Least")
    }

    @JvmStatic fun verifyExistence(stack: ItemStack, tag: String) = getNBT(stack).hasKey(tag)
    @JvmStatic fun verifyUUIDExistence(stack: ItemStack, tag: String) = verifyExistence(stack, tag + "Most") && verifyExistence(stack, tag + "Least")

    @JvmStatic fun setBoolean(stack: ItemStack, tag: String, b: Boolean) = getNBT(stack).setBoolean(tag, b)
    @JvmStatic fun setByte(stack: ItemStack, tag: String, b: Byte) = getNBT(stack).setByte(tag, b)
    @JvmStatic fun setShort(stack: ItemStack, tag: String, s: Short) = getNBT(stack).setShort(tag, s)
    @JvmStatic fun setInt(stack: ItemStack, tag: String, i: Int) = getNBT(stack).setInteger(tag, i)
    @JvmStatic fun setIntArray(stack: ItemStack, tag: String, arr: IntArray) = getNBT(stack).setIntArray(tag, arr)
    @JvmStatic fun setLong(stack: ItemStack, tag: String, l: Long) = getNBT(stack).setLong(tag, l)
    @JvmStatic fun setFloat(stack: ItemStack, tag: String, f: Float) = getNBT(stack).setFloat(tag, f)
    @JvmStatic fun setDouble(stack: ItemStack, tag: String, d: Double) = getNBT(stack).setDouble(tag, d)
    @JvmStatic fun setCompound(stack: ItemStack, tag: String, cmp: NBTTagCompound) = getNBT(stack).setTag(tag, cmp)
    @JvmStatic fun setString(stack: ItemStack, tag: String, s: String) = getNBT(stack).setString(tag, s)
    @JvmStatic fun setList(stack: ItemStack, tag: String, list: NBTTagList) = getNBT(stack).setTag(tag, list)
    @JvmStatic fun setUUID(stack: ItemStack, tag: String, uuid: UUID) = getNBT(stack).setUniqueId(tag, uuid)


    @JvmStatic fun getBoolean(stack: ItemStack, tag: String, defaultExpected: Boolean) =
            if (verifyExistence(stack, tag)) getNBT(stack).getBoolean(tag) else defaultExpected

    @JvmStatic fun getByte(stack: ItemStack, tag: String, defaultExpected: Byte) =
            if (verifyExistence(stack, tag)) getNBT(stack).getByte(tag) else defaultExpected

    @JvmStatic fun getShort(stack: ItemStack, tag: String, defaultExpected: Short) =
            if (verifyExistence(stack, tag)) getNBT(stack).getShort(tag) else defaultExpected

    @JvmStatic fun getInt(stack: ItemStack, tag: String, defaultExpected: Int) =
            if (verifyExistence(stack, tag)) getNBT(stack).getInteger(tag) else defaultExpected

    @JvmStatic fun getIntArray(stack: ItemStack, tag: String) =
            if (verifyExistence(stack, tag)) getNBT(stack).getIntArray(tag) else EMPTY_INT_ARRAY

    @JvmStatic fun getLong(stack: ItemStack, tag: String, defaultExpected: Long) =
            if (verifyExistence(stack, tag)) getNBT(stack).getLong(tag) else defaultExpected

    @JvmStatic fun getFloat(stack: ItemStack, tag: String, defaultExpected: Float) =
            if (verifyExistence(stack, tag)) getNBT(stack).getFloat(tag) else defaultExpected

    @JvmStatic fun getDouble(stack: ItemStack, tag: String, defaultExpected: Double) =
            if (verifyExistence(stack, tag)) getNBT(stack).getDouble(tag) else defaultExpected

    @JvmStatic fun getCompound(stack: ItemStack, tag: String, nullifyOnFail: Boolean) =
            if (verifyExistence(stack, tag)) getNBT(stack).getCompoundTag(tag) else if (nullifyOnFail) null else NBTTagCompound()

    @JvmStatic fun getString(stack: ItemStack, tag: String, defaultExpected: String?) =
            if (verifyExistence(stack, tag)) getNBT(stack).getString(tag) else defaultExpected

    @JvmStatic fun getList(stack: ItemStack, tag: String, nbtClass: Class<NBTBase>, nullifyOnFail: Boolean) =
            getList(stack, tag, nbtClass.newInstance().id.toInt(), nullifyOnFail)

    @JvmStatic fun getList(stack: ItemStack, tag: String, objType: Int, nullifyOnFail: Boolean) =
            if (verifyExistence(stack, tag)) getNBT(stack).getTagList(tag, objType) else if (nullifyOnFail) null else NBTTagList()

    @JvmStatic fun getUUID(stack: ItemStack, tag: String, nullifyOnFail: Boolean) =
            if (verifyUUIDExistence(stack, tag)) getNBT(stack).getUniqueId(tag) else if (nullifyOnFail) null else EMPTY_UUID

}
