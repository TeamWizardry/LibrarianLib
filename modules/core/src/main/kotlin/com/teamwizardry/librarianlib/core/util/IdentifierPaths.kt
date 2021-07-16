@file:JvmName("IdentifierPaths")
package com.teamwizardry.librarianlib.core.util

import net.minecraft.util.Identifier
import java.net.URI

/**
 * Append the given string to this identifier's path.
 *
 * ```
 * Identifier("mymod:my/path").append("_file.png") == Identifier("mymod:my/path_file.png")
 * ```
 */
public fun Identifier.append(suffix: String): Identifier {
    return Identifier(this.namespace, this.path + suffix)
}

/**
 * Resolve the specified path relative to this `Identifier`, normalizing `.` and `..` components.
 *
 * ```
 * Identifier("mymod:my/path").resolve("file.png") == Identifier("mymod:my/path/file.png")
 * ```
 */
public fun Identifier.resolve(path: String): Identifier {
    return Identifier(this.namespace, URI(this.path + "/", null, null).resolve(path).path)
}

/**
 * Resolve the specified path relative to this `Identifier`'s parent, normalizing `.` and `..` components.
 *
 * ```
 * Identifier("mymod:my/path/file1.png").resolveSibling("file2.png") == Identifier("mymod:my/path/file2.png")
 * ```
 */
public fun Identifier.resolveSibling(path: String): Identifier {
    return Identifier(this.namespace, URI(this.path, null, null).resolve(path).path)
}

/**
 * Get the parent path of this `Identifier`
 *
 * ```
 * Identifier("mymod:my/path/file1.png").parent == Identifier("mymod:my/path")
 * ```
 */
public val Identifier.parent: Identifier
    get() = Identifier(this.namespace, URI(this.path, null, null).resolve(".").path)

/**
 * Get the last path component of this `Identifier`
 *
 * ```
 * Identifier("mymod:my/path/file1.png").filename == "file1.png"
 * Identifier("mymod:x").filename == "x"
 * ```
 */
public val Identifier.filename: String
    get() = this.path.substringAfterLast('/')

/**
 * Get the text after the final `.` of this `Identifier`'s [filename], or an empty string if there is none
 *
 * ```
 * Identifier("mymod:my/path/file1.png").extension == "png"
 * Identifier("mymod:my/path/file1.").extension == ""
 * Identifier("mymod:my/path/file").extension == ""
 * Identifier("mymod:my/path.ext/file1").extension == ""
 * ```
 */
public val Identifier.extension: String
    get() = this.filename.substringAfterLast('.', missingDelimiterValue = "")
