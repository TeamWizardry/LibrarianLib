package com.teamwizardry.librarianlib.common.util.serializatorimpls

import com.teamwizardry.librarianlib.common.util.Serializator
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by Elad on 10/20/2016.
 */
class SerializatorImplItemStackList : Serializator<List<ItemStack>> {
    override fun writeToNBT(t: List<ItemStack>, nbt: NBTTagCompound, name: String) {
        var i = 0
        for(stack in t) {
            var tag = NBTTagCompound()
            tag = stack.writeToNBT(tag)
            nbt.setTag(i.toString(), tag)
            i++
        }
        nbt.setInteger("count", i)
    }

    override fun readFromNBT(nbt: NBTTagCompound, name: String): List<ItemStack> {
        val list = mutableListOf<ItemStack>()
        for(i in 0 until nbt.getInteger("count"))
            list.add(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(i.toString())))
        return list
    }

}