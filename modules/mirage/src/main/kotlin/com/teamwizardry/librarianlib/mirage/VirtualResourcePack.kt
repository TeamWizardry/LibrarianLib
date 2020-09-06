package com.teamwizardry.librarianlib.mirage

import net.minecraft.util.ResourceLocation
import java.io.InputStream

public interface VirtualResourcePack {
    /**
     * Get the input stream for the passed location, if it exists
     */
    public fun getStream(location: ResourceLocation): InputStream?

    /**
     * Recursively list the resources in this pack. These can optionally be limited by path prefix and recursion depth.
     * These filter parameters are there primarily for performance when walking the resource tree, since the returned
     * resources are automatically filtered. If they would require manual filtering (e.g. they're already in a list,
     * which would then have to be filtered), they should be returned directly.
     *
     * @param path The returned locations can optionally be limited to those whose [path][ResourceLocation.getPath] has
     * this prefix
     * @param maxDepth The returned locations can optionally be limited to those up to this many directories after the
     * passed path prefix. A value of zero means only the immediate children of the passed path.
     */
    public fun listResources(path: String, maxDepth: Int): Collection<ResourceLocation>

    /**
     * Returns true if this pack contains the passed resource
     */
    public operator fun contains(location: ResourceLocation): Boolean
}

