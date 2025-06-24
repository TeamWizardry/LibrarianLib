package com.teamwizardry.librarianlib.scribe.util

import com.mojang.serialization.DataResult

public fun <T> DataResult<T>.getOrNull(): T? = when (this) {
    is DataResult.Success -> value
    is DataResult.Error -> null
}