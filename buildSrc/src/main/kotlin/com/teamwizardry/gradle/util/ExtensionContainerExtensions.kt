package com.teamwizardry.gradle.util

import org.gradle.api.plugins.ExtensionContainer

inline fun <reified T> ExtensionContainer.create(
    name: String,
    instanceType: Class<out T>,
    vararg constructionArguments: Any?
): T {
    return this.create(typeOf<T>(), name, instanceType, *constructionArguments)
}

inline fun <reified T> ExtensionContainer.create(name: String, vararg constructionArguments: Any?): T {
    return this.create(name, T::class.java, *constructionArguments)
}
