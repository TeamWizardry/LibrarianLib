@file:JvmName("ResourceLocationPaths")
package com.teamwizardry.librarianlib.core.util

import net.minecraft.util.ResourceLocation
import java.net.URI

/**
 * Resolve the specified path relative to this `ResourceLocation`, normalizing `.` and `..` components.
 *
 * ```
 * ResourceLocation("mymod:my/path").resolve("file.png") == ResourceLocation("mymod:my/path/file.png")
 * ResourceLocation("mymod:my/path").resolve("file.png") == ResourceLocation("mymod:my/path/file.png")
 * ```
 */
public fun ResourceLocation.resolve(path: String): ResourceLocation {
    return ResourceLocation(this.namespace, URI(this.path + "/", null, null).resolve(path).path)
}

/**
 * Resolve the specified path relative to this `ResourceLocation`'s parent, normalizing `.` and `..` components.
 *
 * ```
 * ResourceLocation("mymod:my/path/file1.png").resolveSibling("file2.png") == ResourceLocation("mymod:my/path/file2.png")
 * ```
 */
public fun ResourceLocation.resolveSibling(path: String): ResourceLocation {
    return ResourceLocation(this.namespace, URI(this.path, null, null).resolve(path).path)
}

/**
 * Get the parent path of this `ResourceLocation`
 *
 * ```
 * ResourceLocation("mymod:my/path/file1.png").parent == ResourceLocation("mymod:my/path")
 * ```
 */
public val ResourceLocation.parent: ResourceLocation
    get() = ResourceLocation(this.namespace, URI(this.path, null, null).resolve(".").path)
