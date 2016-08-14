package com.teamwizardry.librarianlib.data

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap

import java.util.ArrayList
import java.util.HashMap

open class DataNode {
    protected var type: EnumNodeType? = null
    protected var stringValue: String
    protected var mapValue: Map<String, DataNode>? = null
    protected var listValue: List<DataNode>? = null

    constructor(value: String) {
        type = EnumNodeType.STRING
        stringValue = value
    }

    constructor(value: Map<String, DataNode>) {
        type = EnumNodeType.MAP
        mapValue = value
    }

    constructor(value: List<DataNode>) {
        type = EnumNodeType.LIST
        listValue = value
    }

    open fun exists(): Boolean {
        return true
    }

    val isString: Boolean
        get() = type == EnumNodeType.STRING

    val isInt: Boolean
        get() = type == EnumNodeType.STRING && asIntOr(RANDOM_NUMBER_CHECK_VALUE) != RANDOM_NUMBER_CHECK_VALUE

    val isDouble: Boolean
        get() = type == EnumNodeType.STRING && asDoubleOr(RANDOM_NUMBER_CHECK_VALUE.toDouble()) != RANDOM_NUMBER_CHECK_VALUE.toDouble()

    val isList: Boolean
        get() = type == EnumNodeType.LIST

    val isMap: Boolean
        get() = type == EnumNodeType.MAP

    open fun asStringOr(defaultValue: String?): String? {
        if (!isString)
            return defaultValue
        return stringValue
    }

    open fun asString(): String {
        return asStringOr(null)
    }

    fun asIntOr(i: Int): Int {
        val str = asStringOr(null) ?: return i
        try {
            return Integer.parseInt(str)
        } catch (e: NumberFormatException) {
            // TODO: logging
        }

        return i
    }

    fun asInt(): Int {
        return asIntOr(0)
    }

    fun asDoubleOr(i: Double): Double {
        val str = asStringOr(null) ?: return i
        try {
            return java.lang.Double.parseDouble(str)
        } catch (e: NumberFormatException) {
            // TODO: logging
        }

        return i
    }

    fun asDouble(): Double {
        return asIntOr(0).toDouble()
    }

    open fun asMap(): MutableMap<String, DataNode> {
        if (!isMap)
            return ImmutableMap.of<String, DataNode>()
        return mapValue
    }

    open fun asList(): MutableList<DataNode> {
        if (!isList)
            return ImmutableList.of<DataNode>()
        return listValue
    }

    open operator fun get(key: String): DataNode {
        if (!isMap || !mapValue!!.containsKey(key))
            return NULL
        return mapValue!![key]
    }

    open operator fun get(index: Int): DataNode {
        if (!isList || index < 0 || index >= listValue!!.size)
            return NULL
        return listValue!![index]
    }

    open fun getValue(vararg path: String): DataNode {
        var node = this

        for (part in path) {
            if (node === NULL)
                break
            node = node[part]
        }
        return node
    }

    fun put(key: String, str: String): Boolean {
        return put(key, DataNode.str(str))
    }

    fun put(key: String, node: DataNode): Boolean {
        if (!isMap)
            return false

        asMap().put(key, node)

        return true
    }

    fun put(index: Int, str: String): Boolean {
        return put(index, DataNode.str(str))
    }

    fun put(index: Int, node: DataNode): Boolean {
        if (!isList)
            return false

        asList()[index] = node

        return true
    }

    fun add(str: String): Boolean {
        return add(DataNode.str(str))
    }

    fun add(node: DataNode): Boolean {
        if (!isList)
            return false

        asList().add(node)

        return true
    }

    override fun toString(): String {
        return "DataNode(" + type + ")-[" + (if (type == EnumNodeType.MAP) mapValue else if (type == EnumNodeType.LIST) listValue else stringValue) + "]"
    }

    private enum class EnumNodeType {
        STRING, LIST, MAP
    }

    private class DataNodeNull : DataNode("") {
        init {
            this.type = null
            this.listValue = null
            this.mapValue = null
        }

        override fun exists(): Boolean {
            return false
        }

        override fun asStringOr(defaultValue: String?): String? {
            return defaultValue
        }

        override fun asString(): String {
            return asStringOr(null)
        }

        override fun asMap(): MutableMap<String, DataNode> {
            return ImmutableMap.of<String, DataNode>()
        }

        override fun asList(): MutableList<DataNode> {
            return ImmutableList.of<DataNode>()
        }

        override fun get(key: String): DataNode {
            return NULL
        }

        override fun get(index: Int): DataNode {
            return NULL
        }

        override fun getValue(vararg path: String): DataNode {
            return NULL
        }
    }

    companion object {

        val NULL: DataNode = DataNodeNull()
        private val RANDOM_NUMBER_CHECK_VALUE = -262920932

        fun str(value: String): DataNode {
            return DataNode(value)
        }

        fun map(): DataNode {
            return DataNode(HashMap<String, DataNode>())
        }

        fun list(): DataNode {
            return DataNode(ArrayList<DataNode>())
        }
    }

}
