package com.teamwizardry.librarianlib.data

import com.google.gson.*
import com.teamwizardry.librarianlib.LibrarianLog
import jline.internal.InputStreamReader
import java.io.InputStream
import java.util.*

object DataParser {

    private val parser = JsonParser()

    fun parse(stream: InputStream): DataNode {
        val jsonElement = parser.parse(InputStreamReader(stream))

        return parseNode(jsonElement)
    }

    private fun parseNode(elem: JsonElement): DataNode {
        if (elem is JsonObject)
            return parseObject(elem)
        if (elem is JsonArray)
            return parseList(elem)
        if (elem is JsonNull)
            return DataNode.NULL
        return parseOther(elem)
    }

    private fun parseObject(json: JsonObject): DataNode {

        val map = HashMap<String, DataNode>()

        for ((key, value) in json.entrySet()) {
            val node = parseNode(value)
            if (node != DataNode.NULL)
                map.put(key, node)
        }

        return DataNode(map)
    }

    private fun parseList(array: JsonArray): DataNode {

        val list = ArrayList<DataNode>()

        for (elem in array) {
            val node = parseNode(elem)
            if (node != DataNode.NULL)
                list.add(node)
        }

        return DataNode(list)
    }

    private fun parseOther(elem: JsonElement): DataNode {

        if (elem is JsonPrimitive) {
            return DataNode(elem.asString)
        }

        LibrarianLog.warn("Error parsing Json Element: $elem")
        return DataNode.NULL
    }

}
