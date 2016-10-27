@file:JvmName("JsonMaker")
package com.teamwizardry.librarianlib.common.util.builders

import com.google.gson.*
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import java.io.StringWriter

/**
 * @author WireSegal
 * Created at 7:34 PM on 9/28/16.
 */

object JSON {

    fun array(vararg args: Any?) : JsonArray {
        val arr = JsonArray()
        args.forEach { arr.add(convertJSON(it)) }
        return arr
    }

    fun obj(vararg args: Pair<String, *>): JsonObject {
        val obj = JsonObject()
        args.forEach { obj.add(it.first, convertJSON(it.second)) }
        return obj
    }
}

fun convertJSON(value: Any?): JsonElement = when (value) {
    null -> JsonNull.INSTANCE
    is Char -> JsonPrimitive(value)
    is Number -> JsonPrimitive(value)
    is String -> JsonPrimitive(value)
    is Boolean -> JsonPrimitive(value)
    is Array<*> -> JSON.array(*value)
    is Collection<*> -> JSON.array(*value.toTypedArray())
    is Map<*, *> -> JSON.obj(*value.toList().map { it.first.toString() to it.second }.toTypedArray())
    is JsonElement -> value
    else -> throw IllegalArgumentException("Unrecognized type: " + value)
}

inline fun json(lambda: JSON.() -> JsonElement) = JSON.lambda()

fun JsonElement.serialize(): String {
    val stringWriter = StringWriter()
    val jsonWriter = JsonWriter(stringWriter)
    jsonWriter.serializeNulls = true
    jsonWriter.setIndent("\t")
    Streams.write(this, jsonWriter)
    return stringWriter.toString() + "\n"
}
