package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry
import net.minecraft.client.resources.I18n
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class PageText(entry: Entry, jsonElement: JsonObject) : PageString(entry) {

    private val key: String
    private val args: Array<Any?>

    override val searchableKeys: Collection<String>?
        get() = mutableListOf(key)

    override val searchableStrings: Collection<String>?
        get() = args.map { it.toString() }

    override val text: String
        @SideOnly(Side.CLIENT)
        get() = I18n.format(key, *args).replace("&", "§")

    init {
        val holder = TranslationHolder.fromJson(jsonElement)
        key = holder?.key ?: ""
        args = holder?.args ?: arrayOf()
    }

    class TranslationHolder(val key: String, val args: Array<Any?>) {

        override fun toString(): String {
            return LibrarianLib.PROXY.translate(key)
        }

        companion object {

            fun fromJson(jsonElement: JsonObject): TranslationHolder? {
                val arguments = mutableListOf<Any?>()
                try {
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
                                    arguments.add(TranslationHolder(arg.asString, arrayOf()))
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
