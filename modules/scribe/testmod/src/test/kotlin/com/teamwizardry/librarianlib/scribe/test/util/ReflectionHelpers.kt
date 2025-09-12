package com.teamwizardry.librarianlib.scribe.test.util

import org.junit.jupiter.api.fail
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.staticProperties

@Suppress("UNCHECKED_CAST")
fun <T> KClass<*>._getStaticFieldValue(name: String): T {
    val javaProperty = this.staticProperties.find { it.name == name }
    if (javaProperty != null) {
        return javaProperty.call() as T
    }
    val companionObject = this.companionObjectInstance
        ?: fail { "Failed to get `$name` field from `${this.qualifiedName}` ==> No static field or companion object" }
    val codecProperty = this.companionObject!!.memberProperties.find { it.name == "CODEC" }
        ?: fail { "Failed to get `$name` field from `${this.qualifiedName}` ==> No '`$name`' property on companion object" }
    return codecProperty.call(companionObject) as T
}

@Suppress("UNCHECKED_CAST")
fun <T> KClass<*>._createSimpleInstance(vararg args: Any?): T {
    val constructor = this.primaryConstructor
        ?: fail { "Failed to create instance of `${this.qualifiedName}` ==> No primary constructor" }
    return constructor.call(*args) as T
}
