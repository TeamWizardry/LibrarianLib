@file:JvmName("CoreUtils")
package com.teamwizardry.librarianlib.core.util

/**
 * A method for casting objects without the IDE or compiler complaining about casts always failing.
 */
@Suppress("UNCHECKED_CAST")
public fun<T> mixinCast(obj: Any): T = obj as T
