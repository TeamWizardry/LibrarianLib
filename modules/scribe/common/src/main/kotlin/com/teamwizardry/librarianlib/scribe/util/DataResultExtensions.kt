package com.teamwizardry.librarianlib.scribe.util

import com.mojang.serialization.DataResult

public fun <T> DataResult<T>.getOrNull(): T? = when (this) {
    is DataResult.Success -> value
    is DataResult.Error -> null
}

@Suppress("UNCHECKED_CAST")
public fun <T> DataResult<T>.assertNotNull(message: () -> String = { "Value is null" }): DataResult<T & Any> =
    when (this) {
        is DataResult.Success -> if (value == null) DataResult.error(message) else this as DataResult<T & Any>
        is DataResult.Error -> this as DataResult<T & Any>
    }

public inline fun <T, E> DataResult<T>.orAbort(abortFn: (DataResult<E>) -> Nothing): T = when (this) {
    is DataResult.Success -> value
    is DataResult.Error ->
        @Suppress("UNCHECKED_CAST")
        abortFn(this as DataResult<E>) // we know it's an error state, so the type parameter is meaningless
}
