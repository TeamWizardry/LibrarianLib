package com.teamwizardry.librarianlib.data

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import java.util.*

open class DataNode {
    protected var type: EnumNodeType? = null
    protected var stringValue: String? = null
    protected var mapValue: MutableMap<String, DataNode>? = null
    protected var listValue: MutableList<DataNode>? = null

    constructor(value: String) {
        type = EnumNodeType.STRING
        stringValue = value
    }

    constructor(value: MutableMap<String, DataNode>) {
        type = EnumNodeType.MAP
        mapValue = value
    }

    constructor(value: MutableList<DataNode>) {
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

    open fun asStringOr(defaultValue: String): String {
        return stringValue ?: defaultValue
    }

    open fun asString(): String? {
        return stringValue
    }

    fun asIntOr(i: Int): Int {
        val str = asString() ?: return i
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
        val str = asString() ?: return i
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

    open fun asMap(): Map<String, DataNode> {
        return mapValue ?: emptyMap()
    }

    open fun asList(): List<DataNode> {
        return listValue ?: emptyList()
    }

    open operator fun get(key: String): DataNode {
        return mapValue?.get(key) ?: NULL
    }

    open operator fun get(index: Int): DataNode {
        return listValue?.get(index) ?: NULL
    }

    fun put(key: String, str: String): Boolean {
        return put(key, DataNode.str(str))
    }

    fun put(key: String, node: DataNode): Boolean {
        if (mapValue == null)
            return false

        mapValue?.put(key, node)

        return true
    }

    fun put(index: Int, str: String): Boolean {
        return put(index, DataNode.str(str))
    }

    fun put(index: Int, node: DataNode): Boolean {
        if (listValue == null)
            return false

        listValue?.set(index, node)

        return true
    }

    fun add(str: String): Boolean {
        return add(DataNode.str(str))
    }

    fun add(node: DataNode): Boolean {
        if (listValue == null)
            return false

        listValue?.add(node)

        return true
    }

    override fun toString(): String {
        return "DataNode(" + type + ")-[" + (if (type == EnumNodeType.MAP) mapValue else if (type == EnumNodeType.LIST) listValue else stringValue) + "]"
    }

    protected enum class EnumNodeType {
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

        override fun asStringOr(defaultValue: String): String {
            return defaultValue
        }

        override fun asString(): String? {
            return null
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
    }

    companion object {

        @JvmStatic
        val NULL: DataNode = DataNodeNull()
        private val RANDOM_NUMBER_CHECK_VALUE = -262920932

        @JvmStatic
        fun str(value: String): DataNode {
            return DataNode(value)
        }

        @JvmStatic
        fun map(): DataNode {
            return DataNode(HashMap<String, DataNode>())
        }

        @JvmStatic
        fun list(): DataNode {
            return DataNode(ArrayList<DataNode>())
        }
    }

}
