package com.teamwizardry.librarianlib.glitter

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import com.teamwizardry.librarianlib.etcetera.DirectRaycaster
import com.teamwizardry.librarianlib.etcetera.IntersectingBlocksIterator
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.block.Blocks
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.util.math.Box
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.chunk.ChunkSection
import net.minecraft.world.chunk.ChunkStatus
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.event.TickEvent
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

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
@OnlyIn(Dist.CLIENT)
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
        mutablePos.setPos(x, y, z)
        val toLong = mutablePos.toLong()
        if(lightCache.containsKey(toLong))
            return lightCache.get(toLong)

        val light = computeCombinedLight(mutablePos)
        lightCache.put(toLong, light)

        return light
    }

    private fun computeCombinedLight(pos: BlockPos): Int {
        val world = Client.minecraft.world ?: return 0

        if (pos.y < 0 || pos.y > world.height)
            return 0

        if (!world.isBlockLoaded(pos))
            return 0

        return WorldRenderer.getCombinedLight(world, pos)
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    public fun tick(e: TickEvent.ClientTickEvent) {
        lightCacheManager.tick()
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    public fun unloadWorld(e: WorldEvent.Unload) {
        clearCache()
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
