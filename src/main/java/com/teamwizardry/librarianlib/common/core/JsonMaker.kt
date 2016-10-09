@file:JvmName("JsonMaker")
package com.teamwizardry.librarianlib.common.core

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
        args.forEach { arr.add(convert(it)) }
        return arr
    }

    fun obj(vararg args: Pair<String, *>): JsonObject {
        val obj = JsonObject()
        args.forEach { obj.add(it.first, convert(it.second)) }
        return obj
    }
}

fun convert(value: Any?) : JsonElement = when (value) {
    null -> JsonNull.INSTANCE
    is Char -> JsonPrimitive(value)
    is Number -> JsonPrimitive(value)
    is String -> JsonPrimitive(value)
    is Boolean -> JsonPrimitive(value)
    is JsonElement -> value
    else -> throw IllegalArgumentException("Unrecognized type: " + value)
}

fun json(lambda: JSON.() -> JsonObject): JsonObject {
    return JSON.lambda()
}

fun JsonElement.serialize(): String {
    val stringWriter = StringWriter()
    val jsonWriter = JsonWriter(stringWriter)
    jsonWriter.serializeNulls = true
    jsonWriter.setIndent("\t")
    Streams.write(this, jsonWriter)
    return stringWriter.toString() + "\n"
}
