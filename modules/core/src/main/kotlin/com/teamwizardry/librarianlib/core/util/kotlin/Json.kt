package com.teamwizardry.librarianlib.core.util.kotlin

import com.google.gson.JsonObject
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import java.math.BigDecimal
import java.math.BigInteger

operator fun JsonObject.set(property: String, value: JsonElement) {
    this.add(property, value)
}

operator fun JsonObject.set(property: String, value: String) {
    this.addProperty(property, value)
}

operator fun JsonObject.set(property: String, value: Number) {
    this.addProperty(property, value)
}

operator fun JsonObject.set(property: String, value: Boolean) {
    this.addProperty(property, value)
}

operator fun JsonObject.set(property: String, value: Char) {
    this.addProperty(property, value)
}

operator fun JsonObject.contains(property: String): Boolean = this.has(property)

inline fun <T> JsonObject.parse(rootName: String, dsl: JsonParsingDSL.() -> T): T
    = JsonParsingDSL(this, JsonParsingDSL.JsonPath(rootName, listOf())).dsl()

@DslMarker
annotation class JsonParsingDSLMarker

/**
 * Path syntax emulates javascript access syntax: `root["key"][0]`
 */
@JsonParsingDSLMarker
class JsonParsingDSL @PublishedApi internal constructor(val jsonElement: JsonElement, val path: JsonPath) {
    /** Whether this element is a JSON object */
    val isObject: Boolean
        get() = jsonElement.isJsonObject
    /** Whether this element is a JSON array */
    val isArray: Boolean
        get() = jsonElement.isJsonArray
    /** Whether this element is a JSON primitive (boolean, number, or string) */
    val isPrimitive: Boolean
        get() = jsonElement.isJsonPrimitive
    /** Whether this element is a JSON null */
    val isNull: Boolean
        get() = jsonElement.isJsonNull

    /** Whether this element is a JSON primitive with a boolean value */
    val isBoolean: Boolean
        get() = isPrimitive && jsonElement.asJsonPrimitive.isBoolean
    /** Whether this element is a JSON primitive with a number value */
    val isNumber: Boolean
        get() = isPrimitive && jsonElement.asJsonPrimitive.isNumber
    /** Whether this element is a JSON primitive with a string value */
    val isString: Boolean
        get() = isPrimitive && jsonElement.asJsonPrimitive.isString

    /**
     * A description of this element's type, formatted to fit into a phrase like "this element is ______", e.g.
     * "an array"
     */
    val typeDescription: String
        get() = when {
            isNull -> "null"
            isObject -> "an object"
            isArray -> "an array"
            isBoolean -> "a boolean value"
            isNumber -> "a number"
            isString -> "a string"
            else -> "an unknown type"
        }

    // object ======================================================================================================

    /**
     * Returns this element's backing [JsonObject].
     * @throws JsonSyntaxException if this element is not an object
     */
    fun asObject(): JsonObject = if(isObject) jsonElement as JsonObject else throw typeError("an object")
    /**
     * If this element is an object, returns this element's backing [JsonObject], otherwise returns null.
     */
    fun asObjectOrNull(): JsonObject? = jsonElement as? JsonObject

    // array =======================================================================================================

    /**
     * Returns this element's backing [JsonArray].
     * @throws JsonSyntaxException if this element is not an array
     */
    fun asArray(): JsonArray = if(isArray) jsonElement as JsonArray else throw typeError("an array")
    /**
     * If this element is an array, returns this element's backing [JsonArray], otherwise returns null.
     */
    fun asArrayOrNull(): JsonArray? = jsonElement as? JsonArray

    // primitives ==================================================================================================

    /**
     * Returns this element's boolean value.
     * @throws JsonSyntaxException if this element is not a primitive or this primitive's value is not a boolean value
     */
    fun asBoolean(): Boolean = if(isBoolean) jsonElement.asBoolean else throw typeError("a boolean value")
    /**
     * If this element is a primitive with a boolean value, returns this element's value, otherwise returns null.
     */
    fun asBooleanOrNull(): Boolean? = if(isBoolean) jsonElement.asBoolean else null

    /**
     * Returns this element's primitive value coerced to a string.
     * @throws JsonSyntaxException if this element is not a primitive
     */
    fun asString(): String = if(isString) jsonElement.asString else throw typeError("a string")
    /**
     * If this element is a primitive, returns this element's value coerced to a string, otherwise returns null.
     */
    fun asStringOrNull(): String? = if(isString) jsonElement.asString else null

    /**
     * Returns this element's character value (the first character of its [string][asString] value).
     * @throws JsonSyntaxException if this element is not a primitive
     */
    fun asChar(): Char = if(isString) jsonElement.asCharacter else throw typeError("a string")
    /**
     * If this element is a primitive, returns this element's character value (the first character of its
     * [string][asString] value), otherwise returns null.
     */
    fun asCharOrNull(): Char? = if(isString) jsonElement.asCharacter else null

    // numbers =====================================================================================================

    /**
     * Returns this element's value as a long (truncating if necessary).
     * @throws JsonSyntaxException if this element is not a primitive or this primitive's value is not a number.
     */
    fun asLong(): Long = if(isNumber) jsonElement.asLong else throw typeError("a number")
    /**
     * If this element is a primitive with a numerical value, returns this element's value as a long (truncating if
     * necessary), otherwise returns null.
     */
    fun asLongOrNull(): Long? = if(isNumber) jsonElement.asLong else null

    /**
     * Returns this element's value as a int (truncating if necessary).
     * @throws JsonSyntaxException if this element is not a primitive or this primitive's value is not a number.
     */
    fun asInt(): Int = if(isNumber) jsonElement.asInt else throw typeError("a number")
    /**
     * If this element is a primitive with a numerical value, returns this element's value as an int (truncating if
     * necessary), otherwise returns null.
     */
    fun asIntOrNull(): Int? = if(isNumber) jsonElement.asInt else null

    /**
     * Returns this element's value as a short (truncating if necessary).
     * @throws JsonSyntaxException if this element is not a primitive or this primitive's value is not a number.
     */
    fun asShort(): Short = if(isNumber) jsonElement.asShort else throw typeError("a number")
    /**
     * If this element is a primitive with a numerical value, returns this element's value as a short (truncating if
     * necessary), otherwise returns null.
     */
    fun asShortOrNull(): Short? = if(isNumber) jsonElement.asShort else null

    /**
     * Returns this element's value as a byte (truncating if necessary).
     * @throws JsonSyntaxException if this element is not a primitive or this primitive's value is not a number.
     */
    fun asByte(): Byte = if(isNumber) jsonElement.asByte else throw typeError("a number")
    /**
     * If this element is a primitive with a numerical value, returns this element's value as a byte (truncating if
     * necessary), otherwise returns null.
     */
    fun asByteOrNull(): Byte? = if(isNumber) jsonElement.asByte else null

    /**
     * Returns this element's value as a double.
     * @throws JsonSyntaxException if this element is not a primitive or this primitive's value is not a number.
     */
    fun asDouble(): Double = if(isNumber) jsonElement.asDouble else throw typeError("a number")
    /**
     * If this element is a primitive with a numerical value, returns this element's value as a double, otherwise
     * returns null.
     */
    fun asDoubleOrNull(): Double? = if(isNumber) jsonElement.asDouble else null

    /**
     * Returns this element's value as a float.
     * @throws JsonSyntaxException if this element is not a primitive or this primitive's value is not a number.
     */
    fun asFloat(): Float = if(isNumber) jsonElement.asFloat else throw typeError("a number")
    /**
     * If this element is a primitive with a numerical value, returns this element's value as a float, otherwise
     * returns null.
     */
    fun asFloatOrNull(): Float? = if(isNumber) jsonElement.asFloat else null

    /**
     * Returns this element's value as a BigInteger (truncating if necessary).
     * @throws JsonSyntaxException if this element is not a primitive or this primitive's value is not a number.
     */
    fun asBigInteger(): BigInteger = if(isNumber) jsonElement.asBigInteger else throw typeError("a number")
    /**
     * If this element is a primitive with a numerical value, returns this element's value as a BigInteger (truncating
     * if necessary), otherwise returns null.
     */
    fun asBigIntegerOrNull(): BigInteger? = if(isNumber) jsonElement.asBigInteger else null

    /**
     * Returns this element's value as a BigDecimal.
     * @throws JsonSyntaxException if this element is not a primitive or this primitive's value is not a number.
     */
    fun asBigDecimal(): BigDecimal = if(isNumber) jsonElement.asBigDecimal else throw typeError("a number")
    /**
     * If this element is a primitive with a numerical value, returns this element's value as a BigDecimal, otherwise
     * returns null.
     */
    fun asBigDecimalOrNull(): BigDecimal? = if(isNumber) jsonElement.asBigDecimal else null

    /**
     * Gets the property with the passed name.
     * @throws JsonSyntaxException if this element is not an object or the property is missing
     */
    operator fun get(property: String): JsonParsingDSL = JsonParsingDSL(
        asObject()[property] ?: throw jsonError("Expected an object with a property " +
            "`$property`, but the property was missing."),
        path.child(jsonElement, property)
    )

    /**
     * Gets the property with the passed name, or null if no such property exists.
     * @throws JsonSyntaxException if this element is not an object
     */
    fun getOrNull(property: String): JsonParsingDSL?
        = asObject()[property]?.let { JsonParsingDSL(it, path.child(jsonElement, property)) }

    /**
     * Gets the property value for the target name and runs it through the passed dsl
     * @throws JsonSyntaxException if this element is not an object or the target property doesn't exist
     */
    inline fun <T> get(property: String, dsl: JsonParsingDSL.() -> T): T = get(property).dsl()

    /**
     * Gets the property value for the target name, if it exists, and runs it through the passed dsl
     * @throws JsonSyntaxException if this element is not an object
     */
    inline fun <T> optional(property: String, dsl: JsonParsingDSL.() -> T): T? = getOrNull(property)?.dsl()

    /**
     * Gets the value at the passed index.
     * @throws JsonSyntaxException if this element is not an array or the index is out of bounds
     */
    operator fun get(index: Int): JsonParsingDSL {
        val array = asArray()
        if(index < 0 || index >= array.size()) {
            expectSize(index + 1)
        }
        return JsonParsingDSL(array[index], path.child(jsonElement, index))
    }

    /**
     * Gets the property value for the target name and runs it through the passed dsl
     * @throws JsonSyntaxException if this element is not an array or the index is out of bounds
     */
    inline fun <T> get(index: Int, dsl: JsonParsingDSL.() -> T): T = get(index).dsl()

    /**
     * Gets the number of elements in this array
     * @throws JsonSyntaxException if this element is not an array
     */
    fun size(): Int = asArrayOrNull()?.size() ?: throw typeError("an array")

    /**
     * Throws an exception if this array contains fewer than [expectedSize] elements
     * @throws JsonSyntaxException if this element is not an array or there are fewer than [expectedSize] elements in
     * this array
     */
    fun expectSize(expectedSize: Int) {
        if(size() < expectedSize)
            throw jsonError("Expected an array with at least $expectedSize elements, but it " +
                "only had ${size()} elements.")
    }

    /**
     * Throws an exception if this array does not contain exactly [expectedSize] elements
     * @throws JsonSyntaxException if this element is not an array or the number of elements in this array is not
     * equal to [expectedSize]
     */
    fun expectExactSize(expectedSize: Int) {
        if(size() != expectedSize)
            throw jsonError("Expected an array with precisely $expectedSize elements, but it " +
                "had ${size()} elements.")
    }

    /**
     * Returns a sequence of this array's elements
     * @throws JsonSyntaxException if this element is not an array
     */
    val elements: Sequence<JsonParsingDSL>
        get() = asArray().asSequence().mapIndexed { i, json -> JsonParsingDSL(json, path.child(jsonElement, i)) }

    /**
     * Returns a sequence of this array's elements, or null if this element isn't an array
     */
    fun elementsOrNull(): Sequence<JsonParsingDSL>?
         = asArrayOrNull()?.asSequence()?.mapIndexed { i, json -> JsonParsingDSL(json, path.child(jsonElement, i)) }

    /**
     * Returns a sequence of this object's property name-value pairs
     * @throws JsonSyntaxException if this element is not an object
     */
    val properties: Sequence<Pair<String, JsonParsingDSL>>
        get() = asObject().entrySet().asSequence().map { (name, json) ->
            name to JsonParsingDSL(json, path.child(jsonElement, name))
        }

    /**
     * Returns a sequence of this array's elements, or null if this element isn't an array
     */
    fun propertiesOrNull(): Sequence<Pair<String, JsonParsingDSL>>?
        = asObjectOrNull()?.entrySet()?.asSequence()?.map { (name, json) ->
        name to JsonParsingDSL(json, path.child(jsonElement, name))
    }

    /**
     * Runs this element through the passed dsl function
     */
    inline operator fun <T> invoke(dsl: JsonParsingDSL.() -> T): T = this.dsl()
    /**
     * Runs this element, if it isn't null, through the passed dsl function
     */
    inline infix fun <T> JsonParsingDSL?.ifExists(dsl: JsonParsingDSL.() -> T): T? = this?.dsl()

    /**
     * Gets the property value for the target name and runs it through the passed dsl
     * @throws JsonSyntaxException if this element is not an object or the target property doesn't exist
     */
    inline operator fun <T> String.invoke(dsl: JsonParsingDSL.() -> T): T = get(this).dsl()

    /**
     * Throws an exception stating that this element is the wrong type. The passed string should fit into the phrase
     * "Expected this element to be ___________". For example, "an array or string", "a primitive",
     * "an array of primitives".
     */
    fun typeError(expectedType: String): JsonSyntaxException {
        throw jsonError("Expected this element to be $expectedType, but it was $typeDescription.")
    }

    fun jsonError(message: String): JsonSyntaxException = JsonSyntaxException("$path: $message")

    class JsonPath(val rootName: String, val elements: List<JsonPathElement>) {

        fun child(target: JsonElement, property: String): JsonPath {
            return JsonPath(rootName, elements + listOf(JsonPathElement.Property(target, property)))
        }

        fun child(target: JsonElement, index: Int): JsonPath {
            return JsonPath(rootName, elements + listOf(JsonPathElement.Index(target, index)))
        }

        override fun toString(): String {
            return rootName + elements.joinToString("")
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is JsonPath) return false

            if (elements != other.elements) return false

            return true
        }

        override fun hashCode(): Int {
            return elements.hashCode()
        }
    }

    sealed class JsonPathElement(val target: JsonElement) {
        class Property(target: JsonElement, val property: String): JsonPathElement(target) {
            override fun toString(): String {
                return "[\"$property\"]"
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Property) return false

                if (target != other.target) return false
                if (property != other.property) return false

                return true
            }

            override fun hashCode(): Int {
                var result = target.hashCode()
                result = 31 * result + property.hashCode()
                return result
            }
        }

        class Index(target: JsonElement, val index: Int): JsonPathElement(target) {
            override fun toString(): String {
                return "[$index]"
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Index) return false

                if (target != other.target) return false
                if (index != other.index) return false

                return true
            }

            override fun hashCode(): Int {
                var result = target.hashCode()
                result = 31 * result + index
                return result
            }
        }
    }
}
