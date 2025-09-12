package com.teamwizardry.librarianlib.scribe.test.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.JsonOps

private val prettyPrinter = GsonBuilder().setPrettyPrinting().create()

fun <T> Codec<T>.encodeJson(value: T): DataResult<JsonElement> = this.encodeStart(JsonOps.INSTANCE, value)
fun <T> Codec<T>.decodeJson(value: JsonElement): DataResult<T> = this.parse(JsonOps.INSTANCE, value)
fun JsonElement.prettyPrint(): String = prettyPrinter.toJson(this)
