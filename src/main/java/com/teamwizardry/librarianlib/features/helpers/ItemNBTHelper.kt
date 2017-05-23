package com.teamwizardry.librarianlib.features.helpers

import com.teamwizardry.librarianlib.features.kotlin.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraftforge.common.util.Constants
import java.util.*

object ItemNBTHelper {

    private val EMPTY_INT_ARRAY = IntArray(0)

    @JvmStatic fun detectNBT(stack: ItemStack) = stack.hasTagCompound()
    @JvmStatic @JvmOverloads fun getNBT(stack: ItemStack, modify: Boolean = true): NBTTagCompound {
        if (modify && !detectNBT(stack))
            stack.tagCompound = NBTTagCompound()
        return stack.tagCompound ?: NBTTagCompound()
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
    @JvmStatic fun setCompound(stack: ItemStack, tag: String, cmp: NBTTagCompound) = set(stack, tag, cmp)
    @JvmStatic fun setString(stack: ItemStack, tag: String, s: String) = getNBT(stack).setString(tag, s)
    @JvmStatic fun setList(stack: ItemStack, tag: String, list: NBTTagList) = set(stack, tag, list)
    @JvmStatic fun setUUID(stack: ItemStack, tag: String, uuid: UUID) = set(stack, tag, NBTTagList().apply {
        appendTag(NBTTagLong(uuid.leastSignificantBits))
        appendTag(NBTTagLong(uuid.mostSignificantBits))
    })

    @JvmStatic fun set(stack: ItemStack, tag: String, value: NBTBase) = getNBT(stack).setTag(tag, value)


    @JvmStatic fun getBoolean(stack: ItemStack, tag: String, defaultExpected: Boolean) =
            if (verifyExistence(stack, tag)) getNBT(stack, false).getBoolean(tag) else defaultExpected

    @JvmStatic fun getByte(stack: ItemStack, tag: String, defaultExpected: Byte) =
            if (verifyExistence(stack, tag)) getNBT(stack, false).getByte(tag) else defaultExpected

    @JvmStatic fun getShort(stack: ItemStack, tag: String, defaultExpected: Short) =
            if (verifyExistence(stack, tag)) getNBT(stack, false).getShort(tag) else defaultExpected

    @JvmStatic fun getInt(stack: ItemStack, tag: String, defaultExpected: Int) =
            if (verifyExistence(stack, tag)) getNBT(stack, false).getInteger(tag) else defaultExpected

    @JvmStatic fun getIntArray(stack: ItemStack, tag: String) =
            if (verifyExistence(stack, tag)) getNBT(stack, false).getIntArray(tag) else EMPTY_INT_ARRAY

    @JvmStatic fun getLong(stack: ItemStack, tag: String, defaultExpected: Long) =
            if (verifyExistence(stack, tag)) getNBT(stack, false).getLong(tag) else defaultExpected

    @JvmStatic fun getFloat(stack: ItemStack, tag: String, defaultExpected: Float) =
            if (verifyExistence(stack, tag)) getNBT(stack, false).getFloat(tag) else defaultExpected

    @JvmStatic fun getDouble(stack: ItemStack, tag: String, defaultExpected: Double) =
            if (verifyExistence(stack, tag)) getNBT(stack, false).getDouble(tag) else defaultExpected

    @JvmStatic fun getCompound(stack: ItemStack, tag: String) =
            if (verifyExistence(stack, tag)) getNBT(stack, false).getCompoundTag(tag) else null

    @JvmStatic fun getString(stack: ItemStack, tag: String, defaultExpected: String?) =
            if (verifyExistence(stack, tag)) getNBT(stack, false).getString(tag) else defaultExpected

    @JvmStatic fun getList(stack: ItemStack, tag: String, nbtClass: Class<NBTBase>) =
            getList(stack, tag, nbtClass.newInstance().id.toInt())

    @JvmStatic fun getList(stack: ItemStack, tag: String, objType: Int) =
            if (verifyExistence(stack, tag)) getNBT(stack, false).getTagList(tag, objType) else null

    @JvmStatic fun getUUID(stack: ItemStack, tag: String) =
            if (verifyUUIDExistence(stack, tag)) fromList(getNBT(stack, false).getTagList(tag, Constants.NBT.TAG_ANY_NUMERIC)) else null

    @JvmStatic fun get(stack: ItemStack, tag: String) = getNBT(stack, false)[tag]

    private fun fromList(list: NBTTagList): UUID? {
        if (list.tagCount() != 2 || list.get(0) !is NBTPrimitive) return null
        return UUID((list.get(0) as NBTPrimitive).long, (list.get(1) as NBTPrimitive).long)
    }

}
