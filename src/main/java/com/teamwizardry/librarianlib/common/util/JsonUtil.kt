@file:JvmName("JsonUtil")
package com.teamwizardry.librarianlib.common.util

import com.google.gson.JsonElement

/**
 * @author WireSegal
 * Created at 9:43 AM on 12/6/16.
 */

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
