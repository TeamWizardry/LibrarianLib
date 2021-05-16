package com.teamwizardry.librarianlib.glitter

import com.teamwizardry.librarianlib.core.util.Client
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
import net.minecraft.client.render.WorldRenderer
import net.minecraft.util.math.BlockPos

/**
 * A class designed to efficiently cache block lightmaps
 *
 * This class makes two main sacrifices in the name of speed:
 *
 * 1. It doesn't clear its cache every tick. [lightCacheManager] and [clearCache] can be used to clear the cache
 * immediately if needed.
 * 2. It doesn't properly handle collision boxes that extend outside the bounds of their block. This is because, unlike
 * Minecraft's collision handling it doesn't check any blocks outside of those the velocity vector moves through.
 */
public object GlitterLightingCache {
    private val lightCache = Long2IntOpenHashMap()

    /**
     * The cache of block light values. Refreshes every 5 ticks (0.25 seconds) by default
     */
    public val lightCacheManager: CacheManager = CacheManager(5) { lightCache.clear() }

    /**
     * Request that the cache be cleared. Use this sparingly as it can negatively impact performance.
     *
     * This method _immediately_ clears all the caches, meaning calling it repeatedly between [collide] calls can
     * severely impact performance.
     */
    public fun clearCache() {
        lightCache.clear()
    }

    private val mutablePos = BlockPos.Mutable()

    @Suppress("ReplacePutWithAssignment")
    public fun getCombinedLight(x: Int, y: Int, z: Int): Int {
        mutablePos.set(x, y, z)
        val toLong = mutablePos.asLong()
        if(lightCache.containsKey(toLong))
            return lightCache.get(toLong)

        val light = computeLightmapCoordinates(mutablePos)
        lightCache.put(toLong, light)

        return light
    }

    private fun computeLightmapCoordinates(pos: BlockPos): Int {
        val world = Client.minecraft.world ?: return 0

        if (pos.y < 0 || pos.y > world.height)
            return 0

        if (!world.isChunkLoaded(pos))
            return 0

        return WorldRenderer.getLightmapCoordinates(world, pos)
    }

    public fun tickCache() {
        lightCacheManager.tick()
    }

    public class CacheManager(public var interval: Int, private val clearFunction: () -> Unit) {
        private var age = 0

        public fun tick() {
            if (interval < 0) {
                age = 0
                return
            }
            age++

            if (age >= interval) {
                clear()
            }
        }

        public fun clear() {
            clearFunction()
            age = 0
        }
    }
}
