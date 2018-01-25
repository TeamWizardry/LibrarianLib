package com.teamwizardry.librarianlib.core.common

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.helpers.modIdOverride
import com.teamwizardry.librarianlib.features.kotlin.JSON
import com.teamwizardry.librarianlib.features.kotlin.serialize
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils.generatedFiles
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.LoaderState
import java.io.File

/**
 * @author WireSegal
 * Created at 1:45 PM on 6/24/17.
 */
object RecipeGeneratorHandler {
    internal fun fireRecipes() {
        shapelessToDo.forEach { (name, group, output, inputs, modId) ->
            modIdOverride = modId
            addShapelessRecipe(name, group, output, *inputs.toTypedArray())
            modIdOverride = null
        }
        shapedToDo.forEach { (name, group, output, inputs, modId) ->
            modIdOverride = modId
            addShapedRecipe(name, group, output, *inputs.toTypedArray())
            modIdOverride = null
        }
    }

    private data class Recipe(val name: String, val group: String?, val output: ItemStack, val inp: List<Any>, val modId: String)

    private val shapelessToDo = mutableListOf<Recipe>()
    private val shapedToDo = mutableListOf<Recipe>()

    @JvmStatic
    fun addShapelessRecipe(name: String, output: ItemStack, vararg inputs: Any) = addShapelessRecipe(name, null, output, *inputs)

    @JvmStatic
    fun addShapelessRecipe(name: String, group: String?, output: ItemStack, vararg inputs: Any) {
        if (!shouldGenerateAnyJson()) return

        if (!Loader.instance().hasReachedState(LoaderState.INITIALIZATION)) {
            shapelessToDo.add(Recipe(name, group, output, inputs.toList(), currentModId))
            return
        }

        val basePath = JsonGenerationUtils.getPathForRecipe(currentModId, name)
        val file = File(basePath)
        file.parentFile.mkdirs()
        if (file.createNewFile()) {
            val obj = JSON.obj(
                    "type" to "forge:ore_shapeless",
                    "ingredients" to createJsonFromList(inputs),
                    "result" to createJsonFromStackOutput(output)
            )
            if (group != null) obj.addProperty("group", group)
            file.writeText(serialize(obj))
            LibrarianLog.info("Creating ${file.name} for shapeless recipe with output ${output.displayName} x ${output.count}")
            generatedFiles.add(basePath)
        }
    }

    @JvmStatic
    fun addShapedRecipe(name: String, output: ItemStack, vararg inputs: Any) = addShapedRecipe(name, null, output, *inputs)

    @JvmStatic
    fun addShapedRecipe(name: String, group: String?, output: ItemStack, vararg inputs: Any) {
        if (!shouldGenerateAnyJson()) return

        if (!Loader.instance().hasReachedState(LoaderState.INITIALIZATION)) {
            shapedToDo.add(Recipe(name, group, output, inputs.toList(), currentModId))
            return
        }

        val (mirrored, pattern, mapping) = deconstructShaped(*inputs)

        val basePath = JsonGenerationUtils.getPathForRecipe(currentModId, name)
        val file = File(basePath)
        file.parentFile.mkdirs()
        if (file.createNewFile()) {
            val obj = JSON.obj(
                    "type" to "forge:ore_shaped",
                    "pattern" to pattern,
                    "key" to mapping,
                    "result" to createJsonFromStackOutput(output)
            )
            if (group != null) obj.addProperty("group", group)
            if (mirrored) obj.addProperty("mirrored", mirrored)
            file.writeText(serialize(obj))
            LibrarianLog.info("Creating ${file.name} for shaped recipe with output ${output.displayName} x ${output.count}")
            generatedFiles.add(basePath)
        }
    }

    private fun deconstructShaped(vararg inputs: Any): Triple<Boolean, List<String>, Map<Char, JsonElement>> {
        var inputArray: Array<*> = inputs

        val pattern = mutableListOf<String>()
        val map = mutableMapOf<Char, JsonElement>()
        var mirrored = false
        var index = 0

        // Check if mirrored
        if (inputArray[index] is Boolean) {
            index++
            mirrored = true
        }

        if (inputArray[index] is Array<*>)
            inputArray = inputArray[index] as Array<*>

        // Get input pattern
        if (inputArray[index] is Array<*>)
            (inputArray[index++] as Array<*>).mapTo(pattern) { it.toString() }
        else {
            index--
            while (++index < inputArray.size && inputArray[index] is String)
                pattern.add(inputArray[index] as String)
        }

        index--

        // Get char-ingredient map
        while (++index < inputArray.size && inputArray[index] is Char) {
            val char = inputArray[index] as Char
            if (char == ' ') break
            if (++index < inputArray.size && index > 0 && inputArray[index] !is Char) {
                val obj = createJsonFromObject(inputArray[index])
                map.put(char, obj)
            }
        }

        return Triple(mirrored, pattern, map)
    }

    fun shouldGenerateAnyJson() = LibrarianLib.DEV_ENVIRONMENT && LibLibConfig.generateJson && currentModId in OwnershipHandler.DEV_OWNED

    fun serialize(el: JsonElement)
            = if (LibLibConfig.prettyJsonSerialization) el.serialize() else el.toString() + "\n"

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
        if (stack.hasTagCompound()) {
            val obj = createJsonFromStackOutput(stack)
            obj.addProperty("type", "minecraft:item_nbt")
            return obj
        }

        val obj = JSON.obj("item" to stack.item.registryName)

        if (stack.hasSubtypes)
            obj.addProperty("data", stack.itemDamage)

        return obj
    }

    fun createJsonFromObject(obj: Any?): JsonElement {
        return when (obj) {
            is String -> createJsonFromString(obj)
            is ItemStack -> createJsonFromStack(obj)
            is Item -> createJsonFromStack(ItemStack(obj))
            is Block -> createJsonFromStack(ItemStack(obj))
            is List<*> -> createJsonFromList(obj)
            is Array<*> -> createJsonFromList(obj)
            else -> throw IllegalArgumentException("$obj isn't parsable as an ingredient!")
        }
    }

    fun createJsonFromString(string: String) = JSON.obj("type" to "forge:ore_dict", "ore" to string)

    fun createJsonFromList(l: Array<*>): JsonArray = createJsonFromList(l.toList())
    fun createJsonFromList(l: List<*>): JsonArray {
        val arr = JsonArray()
        l.forEach { arr.add(createJsonFromObject(it)) }
        return arr
    }
}
