package com.teamwizardry.librarianlib.core.util

import com.google.auto.service.AutoService
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import java.util.*
import kotlin.jvm.optionals.getOrNull

public object ServiceLoaderHelper {
    public inline fun <reified T : Any> required(): Lazy<T> = required(T::class.java)
    public inline fun <reified T : Any> optional(): Lazy<T?> = optional(T::class.java)
    public inline fun <reified T : Any> all(): Lazy<List<T>> = all(T::class.java)

    public fun <T : Any> required(type: Class<T>): Lazy<T> = lazy {
        ServiceLoader.load(type).findFirst().getOrNull()
            ?: throw IllegalStateException("Could not load service ${type.canonicalName}")
    }

    public fun <T : Any> optional(type: Class<T>): Lazy<T?> = lazy {
        ServiceLoader.load(type).findFirst().getOrNull()
    }

    public fun <T : Any> all(type: Class<T>): Lazy<List<T>> = lazy {
        ServiceLoader.load(type).toList().unmodifiableView()
    }
}
