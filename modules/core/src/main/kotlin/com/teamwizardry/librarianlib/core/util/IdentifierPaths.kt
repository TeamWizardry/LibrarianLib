@file:JvmName("IdentifierPaths")
package com.teamwizardry.librarianlib.core.util

import net.minecraft.util.Identifier
import java.net.URI

/**
 * Resolve the specified path relative to this `Identifier`, normalizing `.` and `..` components.
 *
 * ```
 * Identifier("mymod:my/path").resolve("file.png") == Identifier("mymod:my/path/file.png")
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
