package com.teamwizardry.librarianlib.scribe.helpers

import com.mojang.serialization.Codec

/**
 * Used to mark when a codec could not be found. Typically, this is because the type failed to resolve.
 */
@Deprecated("See docs for more information", level = DeprecationLevel.ERROR)
public fun codecNotFound(type: String): Codec<Nothing> {
    throw UnsupportedOperationException("Codec not found: $type")
}

/**
 * Nullable fields on `@AutoCodec.Record` classes are supported, but nullable generics are not.
 * Instead of nullable generics, you can use [java.util.Optional].
 *
 * # Rationale
 * This is primarily in the interest of serialization stability. The main issue is that adding or removing the
 * nullability will fundamentally change the way values are encoded, making previously serialized data invalid. This is
 * unexpected behavior for such a trivial change, so to prevent accidentally introducing serialization incompatible
 * changes, nullable generics are not supported.
 *
 * Using an [Optional][java.util.Optional] in Kotlin code is inconvenient, however forcing the user to explicitly wrap
 * the type makes it clear that the encoding will change when doing so.
 *
 * Nullable fields on an `@AutoCodec.Record` class are allowed because the encoding for those values is stable when
 * adding or removing the nullability.
 */
@Deprecated("See docs for more information", level = DeprecationLevel.ERROR)
public fun unsupportedNullableGeneric(type: String): Codec<Nothing> {
    throw UnsupportedOperationException("Unsupported nullable generic: $type")
}
