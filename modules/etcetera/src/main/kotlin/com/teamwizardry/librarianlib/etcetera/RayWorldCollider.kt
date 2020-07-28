package com.teamwizardry.librarianlib.etcetera

import com.teamwizardry.librarianlib.core.util.SidedRunnable
import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.block.Blocks
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.shapes.VoxelShape
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkSection
import net.minecraft.world.chunk.ChunkStatus
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.event.TickEvent
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * A class designed to efficiently raytrace collisions with the world. This class uses custom raytracing code to
 * eliminate short-lived objects such as [Vec3d]s.
 *
 * This class makes two main sacrifices in the name of speed:
 *
 * 1. It doesn't clear its cache every tick. [requestRefresh] can be used cause the cache to be cleared immediately.
 * 2. It doesn't properly handle collision boxes that extend outside the bounds of their block. This is because, unlike
 * Minecraft's collision handling it doesn't check any blocks outside of those the velocity vector moves through.
 */
@Mod.EventBusSubscriber
class RayWorldCollider private constructor(world: World) {

    private val worldRef = WeakReference(world)

    private val world: World
        get() = worldRef.get()!!

    private val blockCache = Long2ObjectOpenHashMap<List<AxisAlignedBB>>()
    private val shapeCache = Object2ObjectOpenHashMap<VoxelShape, List<AxisAlignedBB>>()
    private val airCache = LongOpenHashSet()
    private val intersectingIterator: IntersectingBlocksIterator by threadLocal { IntersectingBlocksIterator() }
    private val raycaster: DirectRaycaster by threadLocal { DirectRaycaster() }

    /**
     * The cache of block collision AABBs. Refreshes every 10 ticks (0.5 seconds) by default
     */
    val blockCacheManager: CacheManager = CacheManager(10) { blockCache.clear() }

    /**
     * The cache of [VoxelShapes][VoxelShape] to AABBs. Refreshes every 1200 ticks (60 seconds) by default
     */
    val shapeCacheManager: CacheManager = CacheManager(1200) { shapeCache.clear() }

    /**
     * The cache of chunk sections' empty status. Refreshes every 40 ticks (2 seconds) by default
     */
    val airCacheManager: CacheManager = CacheManager(40) { airCache.clear() }

    /**
     * Request that the cache be cleared. Use this sparingly as it can negatively impact performance. Individual caches
     * can be cleared using [blockCacheManager], [shapeCacheManager], and [airCacheManager]
     *
     * This method _immediately_ clears all the caches, meaning calling it repeatedly between [collide] calls can
     * severely impact performance.
     */
    fun requestRefresh() {
        blockCache.clear()
        shapeCache.clear()
        airCache.clear()
    }

    /**
     * Traces a collision with the world given the specified start position and velocity.
     *
     * The collision uses raytracing to find the impact point on the current world's collision box. The ray begins at
     * the passed `pos` and extends in the direction and for the length of the passed `vel`. Each component of `vel`
     * is clamped to ±[maxBounds] in order to avoid accidental infinite loops or other such nastiness.
     *
     * The results of the collision are stored in the fields of this class in order to avoid allocating a new object to
     * contain them.
     *
     * @see collisionFraction
     * @see collisionNormalX
     * @see collisionNormalY
     * @see collisionNormalZ
     * @see collisionBlockX
     * @see collisionBlockY
     * @see collisionBlockZ
     */
    @JvmOverloads
    fun collide(
        result: RayHitResult,
        posX: Double,
        posY: Double,
        posZ: Double,
        velX: Double,
        velY: Double,
        velZ: Double,
        maxBounds: Double = 5.0
    ) {
        result.collisionFraction = 1.0
        result.collisionNormalX = 0.0
        result.collisionNormalY = 0.0
        result.collisionNormalZ = 0.0
        result.collisionBlockX = 0
        result.collisionBlockY = 0
        result.collisionBlockZ = 0

        @Suppress("NAME_SHADOWING")
        val velX = min(maxBounds, max(-maxBounds, velX))

        @Suppress("NAME_SHADOWING")
        val velY = min(maxBounds, max(-maxBounds, velY))

        @Suppress("NAME_SHADOWING")
        val velZ = min(maxBounds, max(-maxBounds, velZ))

        val minTestX = floor(min(posX, posX + velX)).toInt()
        val minTestY = floor(min(posY, posY + velY)).toInt()
        val minTestZ = floor(min(posZ, posZ + velZ)).toInt()
        val maxTestX = floor(max(posX, posX + velX)).toInt()
        val maxTestY = floor(max(posY, posY + velY)).toInt()
        val maxTestZ = floor(max(posZ, posZ + velZ)).toInt()

        val invVelX = 1 / velX
        val invVelY = 1 / velY
        val invVelZ = 1 / velZ

        val tiny = 0.000_000_000_1

        val raycaster = this.raycaster
        raycaster.reset()

        if (minTestX == maxTestX && minTestY == maxTestY && minTestZ == maxTestZ) {
            val boxes = getBoundingBoxes(minTestX, minTestY, minTestZ)
            for (i in boxes.indices) {
                val bb = boxes[i]
                if (raycaster.cast(
                        true,
                        bb.minX - tiny, bb.minY - tiny, bb.minZ - tiny,
                        bb.maxX + tiny, bb.maxY + tiny, bb.maxZ + tiny,
                        posX - minTestX, posY - minTestY, posZ - minTestZ,
                        invVelX, invVelY, invVelZ
                    )) {
                    result.collisionFraction = raycaster.distance
                    result.collisionNormalX = raycaster.normalX
                    result.collisionNormalY = raycaster.normalY
                    result.collisionNormalZ = raycaster.normalZ
                    result.collisionBlockX = minTestX
                    result.collisionBlockY = minTestY
                    result.collisionBlockZ = minTestZ
                }
            }
        } else {
            val intersectingBlocksIterator = this.intersectingIterator
            intersectingBlocksIterator.reset(posX, posY, posZ, posX + velX, posY + velY, posZ + velZ)

            for (block in intersectingBlocksIterator) {
                val boxes = getBoundingBoxes(block.x, block.y, block.z)
                for (i in boxes.indices) {
                    val bb = boxes[i]
                    if (raycaster.cast(
                            true,
                            bb.minX - tiny, bb.minY - tiny, bb.minZ - tiny,
                            bb.maxX + tiny, bb.maxY + tiny, bb.maxZ + tiny,
                            posX - block.x, posY - block.y, posZ - block.z,
                            invVelX, invVelY, invVelZ
                        )) {
                        result.collisionFraction = raycaster.distance
                        result.collisionNormalX = raycaster.normalX
                        result.collisionNormalY = raycaster.normalY
                        result.collisionNormalZ = raycaster.normalZ
                        result.collisionBlockX = block.x
                        result.collisionBlockY = block.y
                        result.collisionBlockZ = block.z
                    }
                }
            }
        }
    }

    private val sectionPos = BlockPos.Mutable()
    private val mutablePos = BlockPos.Mutable()

    @Suppress("ReplacePutWithAssignment")
    private fun getBoundingBoxes(x: Int, y: Int, z: Int): List<AxisAlignedBB> {
        if (y < 0 || y > world.actualHeight)
            return emptyList()
        sectionPos.setPos(x shr 4, y shr 4, z shr 4)
        if (airCache.contains(sectionPos.toLong()))
            return emptyList()

        mutablePos.setPos(x, y, z)
        val toLong = mutablePos.toLong()
        blockCache.get(toLong)?.let { return it }

        val chunk = world.getChunk(x shr 4, z shr 4, ChunkStatus.EMPTY, false)
        if(chunk == null) {
            for(i in 0 until 16) {
                sectionPos.setPos(x shr 4, i, z shr 4)
                airCache.add(sectionPos.toLong())
            }
            return emptyList()
        }

        val section = chunk.sections[y shr 4]
        if (ChunkSection.isEmpty(section)) {
            airCache.add(sectionPos.toLong())
            return emptyList()
        }

        val state = section.getBlockState(x and 15, y and 15, z and 15)

        val boxes = if (state == Blocks.AIR.defaultState || state.isAir(world, mutablePos)
            || state.material.let { !it.blocksMovement() || it.isLiquid }) {
            emptyList()
        } else {
            getBoundingBoxes(state.getCollisionShape(world, mutablePos))
        }

        blockCache.put(toLong, boxes)
        return boxes
    }

    private fun getBoundingBoxes(shape: VoxelShape): List<AxisAlignedBB> {
        return shapeCache.getOrPut(shape) { shape.toBoundingBoxList() }
    }

    companion object {
        @JvmStatic
        private val worldMap = WeakHashMap<World, RayWorldCollider>()

        @Suppress("UNUSED_PARAMETER")
        @SubscribeEvent
        @JvmStatic
        fun tick(e: TickEvent.ClientTickEvent) {
            for (value in worldMap.values) {
                value.blockCacheManager.tick()
                value.shapeCacheManager.tick()
                value.airCacheManager.tick()
            }
        }

        /**
         * Gets the [RayWorldCollider] for the passed world
         */
        @JvmStatic
        operator fun get(world: World): RayWorldCollider {
            return worldMap.getOrPut(world) { RayWorldCollider(world) }
        }

        /**
         * Gets the [RayWorldCollider] for the client world.
         * Returns null if called server-side.
         */
        @JvmStatic
        val client: RayWorldCollider?
            get() {
                var handler: RayWorldCollider? = null
                SidedRunnable.client {
                    handler = ClientRayWorldCollider.get()
                }
                return handler
            }
    }

    class CacheManager(var interval: Int, private val clearFunction: () -> Unit) {
        private var age = 0

        fun tick() {
            if(interval < 0) {
                age = 0
                return
            }
            age++

            if(age >= interval) {
                clear()
            }
        }

        fun clear() {
            clearFunction()
            age = 0
        }
    }
}

class RayHitResult {
    /**
     * The fraction along the raytrace that an impact occurred, or 1.0 if no impact occurred
     */
    var collisionFraction: Double = 0.0

    /**
     * The X component of the impacted face's normal, or 0.0 if no impact occurred
     */
    var collisionNormalX: Double = 0.0

    /**
     * The Y component of the impacted face's normal, or 0.0 if no impact occurred
     */
    var collisionNormalY: Double = 0.0

    /**
     * The Z component of the impacted face's normal, or 0.0 if no impact occurred
     */
    var collisionNormalZ: Double = 0.0

    /**
     * The X component of the impacted block's position, or 0.0 if no impact occurred. Test if [collisionFraction] is
     * less than 1.0 to tell between a collision at (0,0,0) and no collision.
     */
    var collisionBlockX: Int = 0

    /**
     * The Y component of the impacted block's position, or 0.0 if no impact occurred. Test if [collisionFraction] is
     * less than 1.0 to tell between a collision at (0,0,0) and no collision.
     */
    var collisionBlockY: Int = 0

    /**
     * The Z component of the impacted block's position, or 0.0 if no impact occurred. Test if [collisionFraction] is
     * less than 1.0 to tell between a collision at (0,0,0) and no collision.
     */
    var collisionBlockZ: Int = 0
}

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
private object ClientRayWorldCollider {
    var cache: RayWorldCollider? = null

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun unloadWorld(e: WorldEvent.Unload) {
        cache = null
    }

    fun get(): RayWorldCollider? {
        val world = Minecraft.getInstance().world
        var cache = cache
        if (world == null) {
            cache = null
        } else if (cache == null) {
            cache = RayWorldCollider[world]
        }
        ClientRayWorldCollider.cache = cache
        return cache
    }
}
