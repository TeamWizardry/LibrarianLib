package com.teamwizardry.librarianlib.core.common

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.JSON
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
 * @author WireSegal
 * Created at 1:45 PM on 6/24/17.
 */
object RecipeGeneratorHandler {
    @JvmStatic
    fun addShapelessRecipe(name: String, output: ItemStack, vararg inputs: Any) {
        val basePath = JsonGenerationUtils.getPathForRecipe(currentModId, name)
        
    }

    fun createJsonFromStackOutput(stack: ItemStack): JsonObject {
        val obj = JSON.obj("item" to stack.item.registryName)

        if (stack.hasSubtypes)
            obj.addProperty("data", stack.itemDamage)
        if (stack.count > 1)
            obj.addProperty("count", stack.count)

        val nbt = stack.tagCompound ?: NBTTagCompound()

        val written = stack.writeToNBT(NBTTagCompound())
        if (written.hasKey("ForgeCaps"))
            nbt.setTag("ForgeCaps", written.getTag("ForgeCaps"))

        if (nbt.size != 0)
            obj.addProperty("nbt", nbt.toString())

        return obj
    }

    fun createJsonFromStack(stack: ItemStack): JsonObject {
        val obj = JSON.obj("item" to stack.item.registryName)

        if (stack.hasSubtypes)
            obj.addProperty("data", stack.itemDamage)

        return obj
    }

    fun createJsonFromString(string: String) = JSON.obj("type" to "forge:ore_dict", "ore" to string)

    fun createJsonFromList(l: Array<*>): JsonArray = createJsonFromList(l.toList())
    fun createJsonFromList(l: List<*>): JsonArray {
        val arr = JsonArray()
        for (i in l) {
            if (i is String)
                arr.add(createJsonFromString(i))
            else if (i is ItemStack)
                arr.add(createJsonFromStack(i))
            else if (i is Item)
                arr.add(createJsonFromStack(ItemStack(i)))
            else if (i is Block)
                arr.add(createJsonFromStack(ItemStack(i)))
            else if (i is List<*>)
                arr.add(createJsonFromList(l))
            else if (i is Array<*>)
                arr.add(createJsonFromList(i))
        }
        return arr
    }
}
