package com.teamwizardry.librarianlib.core.common

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.kotlin.JSON
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
 * @author WireSegal
 * Created at 1:45 PM on 6/24/17.
 */
object RecipeGeneratorHandler {
    @JvmStatic
    fun addShapelessRecipe(output: ItemStack, vararg inputs: Any) {

    }

    fun createJsonFromStack(stack: ItemStack): JsonObject {
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
}
