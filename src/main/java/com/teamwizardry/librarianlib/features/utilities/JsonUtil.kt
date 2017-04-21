@file:JvmName("JsonUtil")

package com.teamwizardry.librarianlib.features.utilities

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper

private val MATCHER = "(?:(?:(?:\\[\\d+\\])|(?:[^.\\[\\]]+))(\\.|$|(?=\\[)))+".toRegex()

private val TOKENIZER = "((?:\\[\\d+\\])|(?:[^.\\[\\]]+))(?=[.\\[]|$)".toRegex()

fun JsonElement.getObject(key: String): JsonElement? {
    if (!MATCHER.matches(key)) return null

    var currentElement = this

    val matched = TOKENIZER.findAll(key)
    for (match in matched) {
        val m = match.groupValues[1]
        if (m.startsWith("[")) {
            if (!currentElement.isJsonArray) return null
            val arr = currentElement.asJsonArray
            val ind = m.removePrefix("[").removeSuffix("]").toInt()
            if (arr.size() < ind + 1) return null
            currentElement = arr[ind]
        } else {
            if (!currentElement.isJsonObject) return null
            val obj = currentElement.asJsonObject
            if (!obj.has(m)) return null
            currentElement = obj[m]
        }
    }
    return currentElement
}

private val elementsFromArray = MethodHandleHelper.wrapperForGetter(JsonArray::class.java, "elements")

fun JsonObject.setObject(key: String, el: JsonElement): Boolean {
    if (!MATCHER.matches(key)) return false

    var currentElement: JsonElement = this

    val matched = TOKENIZER.findAll(key).toList()
    val max = matched.size - 1
    for ((index, match) in matched.withIndex()) {
        val m = match.groupValues[1]
        val done = index == max
        if (m.startsWith("[")) {
            val ind = m.removePrefix("[").removeSuffix("]").toInt()
            if (currentElement is JsonArray) {
                if (currentElement.size() < ind + 1 && !done) {
                    val new = if (matched[index + 1].groupValues[1].startsWith("[")) JsonArray() else JsonObject()
                    currentElement.add(new)
                    currentElement = new
                } else {
                    if (!done) currentElement = currentElement[ind]
                    else {
                        if (ind > currentElement.size()) currentElement.add(el)
                        else {
                            @Suppress("UNCHECKED_CAST")
                            val elements = elementsFromArray(currentElement) as MutableList<JsonElement>
                            elements[ind] = el
                        }
                    }
                }
            } else return false
        } else if (currentElement is JsonObject) {
            if (!currentElement.has(m) && !done) {
                val new = if (matched[index + 1].groupValues[1].startsWith("[")) JsonArray() else JsonObject()
                currentElement.add(m, new)
                currentElement = new
            } else {
                if (!done) currentElement = currentElement.get(m)
                else currentElement.add(m, el)
            }
        } else return false
    }
    return true
}
