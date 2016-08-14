package com.teamwizardry.librarianlib.data

import com.google.gson.*
import jline.internal.InputStreamReader

import java.io.InputStream
import java.util.ArrayList
import java.util.HashMap

object DataParser {

    private val parser = JsonParser()

    fun parse(stream: InputStream): DataNode {
        val jsonElement = parser.parse(InputStreamReader(stream))

        return parseNode(jsonElement)
    }

    private fun parseNode(elem: JsonElement): DataNode? {
        if (elem is JsonObject)
            return parseObject(elem)
        if (elem is JsonArray)
            return parseList(elem)
        if (elem is JsonNull)
            return null
        return parseOther(elem)
    }

    private fun parseObject(`object`: JsonObject): DataNode {

        val map = HashMap<String, DataNode>()

        for ((key, value) in `object`) {
            val node = parseNode(value)
            if (node != null)
                map.put(key, node)
        }

        return DataNode(map)
    }

    private fun parseList(array: JsonArray): DataNode {

        val list = ArrayList<DataNode>()

        for (elem in array) {
            val node = parseNode(elem)
            if (node != null)
                list.add(node)
        }

        return DataNode(list)
    }

    private fun parseOther(elem: JsonElement): DataNode {

        if (elem is JsonPrimitive) {
            return DataNode(elem.asString)
        }


        return DataNode("!!ERROR!!")
    }

}
