package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class PageText(entry: Entry, jsonElement: JsonObject) : PageString(entry) {

    private val holder: TranslationHolder?

    override val searchableStrings: Collection<String>?
        get() = listOf(holder.toString())

    override val text: String
        @SideOnly(Side.CLIENT)
        get() = holder.toString()

    init {
        holder = TranslationHolder.fromJson(jsonElement)
    }

    class TranslationHolder(val key: String, val args: Array<Any?>) {

        override fun toString(): String {
            return LibrarianLib.PROXY.translate(key, *args).replace('&', '§')
        }

        companion object {

            fun fromJson(jsonElement: JsonElement?): TranslationHolder? {
                if (jsonElement == null || jsonElement.isJsonNull)
                    return null

                try {
                    val arguments = mutableListOf<Any?>()

                    if (jsonElement !is JsonObject)
                        return TranslationHolder(jsonElement.asString, arrayOf())

                    val key = jsonElement.getAsJsonPrimitive("value").asString
                    if (jsonElement.has("args"))
                        for (arg in jsonElement.getAsJsonArray("args"))
                            when {
                                arg.isJsonPrimitive -> if (arg.asJsonPrimitive.isNumber) {
                                    if (arg.asLong.toDouble() != arg.asDouble)
                                        arguments.add(arg.asDouble)
                                    else
                                        arguments.add(arg.asInt)
                                } else if (arg.asJsonPrimitive.isBoolean)
                                    arguments.add(arg.asBoolean)
                                else
                                    arguments.add(TranslationHolder.fromJson(arg))
                                arg.isJsonNull -> arguments.add("null")
                                arg.isJsonObject -> arguments.add(fromJson(arg.asJsonObject))
                            }
                    return TranslationHolder(key, arguments.toTypedArray())
                } catch (ignored: Exception) {
                    // NO-OP
                }

                return null
            }
        }
    }
}
