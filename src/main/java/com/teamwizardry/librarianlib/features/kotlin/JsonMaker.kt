@file:JvmName("JsonMaker")

package com.teamwizardry.librarianlib.features.kotlin

import com.google.gson.*
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import net.minecraft.util.ResourceLocation
import java.io.StringWriter
import java.util.function.Consumer

/**
 * @author WireSegal
 * Created at 7:34 PM on 9/28/16.
 */

@JvmName("create")
inline fun jsonObject(lambda: JsonDsl.() -> Unit) = JsonDsl().apply(lambda).root

fun array(vararg args: Any?): JsonArray {
    val arr = JsonArray()
    for (argument in args)
        arr.add(convertJSON(argument))
    return arr
}

fun obj(vararg args: Pair<String, *>): JsonObject {
    val obj = JsonObject()
    for ((key, value) in args)
        obj[key] = convertJSON(value)
    return obj
}

class JsonDsl(val root: JsonObject = JsonObject()) {
    inline operator fun String.invoke(lambda: JsonDsl.() -> Unit) {
        root[this] = jsonObject(lambda)
    }

    inline infix fun String.to(lambda: JsonDsl.() -> Unit) = this(lambda)

    @JvmName("append")
    operator fun String.invoke(lambda: Consumer<JsonDsl>) = this { lambda.accept(this) }

    @JvmName("append")
    operator fun String.invoke(vararg values: Any?) {
        root[this] = if (values.size == 1) convertJSON(values.first()) else convertJSON(values)
    }

    infix fun String.to(value: Any?) = this(value)
}

operator fun JsonObject.set(key: String, value: JsonElement) = add(key, value)

fun convertJSON(value: Any?): JsonElement = when (value) {
    null -> JsonNull.INSTANCE
    is Char -> JsonPrimitive(value)
    is Number -> JsonPrimitive(value)
    is String -> JsonPrimitive(value)
    is Boolean -> JsonPrimitive(value)
    is JsonElement -> value
    is Array<*> -> array(*value)
    is Collection<*> -> array(*value.toTypedArray())
    is Map<*, *> -> obj(*value.toList().map { it.first.toString() to it.second }.toTypedArray())
    is ResourceLocation -> JsonPrimitive(value.toString())
    else -> throw IllegalArgumentException("Unrecognized type: $value")
}

fun JsonElement.serialize(): String {
    val stringWriter = StringWriter()
    val jsonWriter = JsonWriter(stringWriter)
    jsonWriter.serializeNulls = true
    jsonWriter.setIndent("\t")
    Streams.write(this, jsonWriter)
    return stringWriter.toString() + "\n"
}





@Deprecated("Use JsonDsl")
object JSON {

    @Deprecated("", ReplaceWith("array(*args)", "com.teamwizardry.librarianlib.features.kotlin.array"))
    fun array(vararg args: Any?) = com.teamwizardry.librarianlib.features.kotlin.array(*args)

    @Deprecated("", ReplaceWith("obj(*args)", "com.teamwizardry.librarianlib.features.kotlin.obj"))
    fun obj(vararg args: Pair<String, *>) = com.teamwizardry.librarianlib.features.kotlin.obj(*args)
}

@Suppress("DEPRECATION")
@Deprecated("Use new json syntax instead",
        ReplaceWith("jsonObject(lambda)", "com.teamwizardry.librarianlib.features.kotlin.jsonObject"),
        DeprecationLevel.ERROR)
inline fun json(lambda: JSON.() -> JsonElement) = JSON.lambda()
