package com.teamwizardry.librarianlib.etcetera

import com.teamwizardry.librarianlib.core.util.SidedRunnable
import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.shapes.VoxelShape
import net.minecraft.world.World
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
    private val intersectingIterator: IntersectingBlocksIterator by threadLocal { IntersectingBlocksIterator() }
    private val raycaster: DirectRaycaster by threadLocal { DirectRaycaster() }

    private var blockCacheAge = 0
    private var shapeCacheAge = 0

    /**
     * This specifies when to reset the blockstate cache for collision checking.
     * Setting it to 2, for example, will clear the cache every 2 ticks. The default value is 10 ticks (half a second).
     *
     * Setting it to -1 will disable cache clearing. BE VERY CAREFUL WHEN YOU DO THIS.
     * SERIOUSLY. BE ABSOLUTELY SURE YOU RESET IT PROPERLY YOURSELF. YOU WILL RUN OUT OF MEMORY IF YOU DON'T.
     * Run requestRefresh() to clear it yourself.
     *
     * The cache will only reset when the world unloads on the client if the timer is set to -1.
     */
    @JvmField
    var blockCacheRefreshInterval: Int = 10

    /**
     * This specifies when to reset the `VoxelShape` -> `List<AxisAlignedBB>` cache for collision checking.
     * Setting it to 2, for example, will clear the cache every 2 ticks. Default 1,200 ticks (one minute).
     *
     * Setting it to -1 will disable cache clearing. This is not advised, though nowhere near as bad as setting
     * [blockCacheRefreshInterval] to -1.
     *
     * The cache will only reset when the world unloads on the client if the timer is set to -1.
     *
     * Remember, this works in game ticks.
     */
    @JvmField
    var shapeCacheRefreshInterval: Int = 1200

    /**
     * Request that the cache be cleared. Use this sparingly as it can negatively impact performance.
     *
     * This method _immediately_ clears the cache, meaning calling it repeatedly between [collide] calls can severely
     * impact performance.
     */
    fun requestRefresh() {
        blockCache.clear()
        blockCacheAge = 0
        shapeCache.clear()
        shapeCacheAge = 0
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

        if(minTestX == maxTestX && minTestY == maxTestY && minTestZ == maxTestZ) {
            getBoundingBoxes(minTestX, minTestY, minTestZ).forEach { bb ->
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
                getBoundingBoxes(block.x, block.y, block.z).forEach { bb ->
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

    private val mutablePos = BlockPos.Mutable()

    private fun getBoundingBoxes(x: Int, y: Int, z: Int): List<AxisAlignedBB> {
        mutablePos.setPos(x, y, z)
        val toLong = mutablePos.toLong()
        blockCache[toLong]?.let { return it }

        val boundingBoxes: List<AxisAlignedBB>
        @Suppress("DEPRECATION")
        if (!world.isBlockLoaded(mutablePos) || mutablePos.y < 0 || mutablePos.y > world.actualHeight) {
            boundingBoxes = emptyList()
        } else {
            val blockstate = world.getBlockState(mutablePos)
            if (blockstate.material == Material.AIR ||
                !blockstate.material.blocksMovement() || blockstate.material.isLiquid) {
                boundingBoxes = emptyList()
            } else {
                boundingBoxes = getBoundingBoxes(blockstate.getCollisionShape(world, mutablePos))
            }
        }
        blockCache[toLong] = boundingBoxes

        return boundingBoxes
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
                run {
                    if (value.blockCache.size == 0) {
                        value.blockCacheAge = 0
                        return@run
                    }
                    value.blockCacheAge++

                    if (value.blockCacheRefreshInterval < -1)
                        return@run
                    if (value.blockCacheAge >= value.blockCacheRefreshInterval) {
                        value.blockCache.clear()
                        value.blockCacheAge = 0
                    }
                }
                run {
                    if (value.shapeCache.size == 0) {
                        value.shapeCacheAge = 0
                        return@run
                    }
                    value.shapeCacheAge++

                    if (value.shapeCacheRefreshInterval < -1)
                        return@run
                    if (value.shapeCacheAge >= value.shapeCacheRefreshInterval) {
                        value.shapeCache.clear()
                        value.shapeCacheAge = 0
                    }
                }
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
}

class RayHitResult {
    /**
     * The fraction along the raytrace that an impact occurred, or 1.0 if no impact occurred
     */
    @JvmField
    var collisionFraction: Double = 0.0

    /**
     * The X component of the impacted face's normal, or 0.0 if no impact occurred
     */
    @JvmField
    var collisionNormalX: Double = 0.0
    /**
     * The Y component of the impacted face's normal, or 0.0 if no impact occurred
     */
    @JvmField
    var collisionNormalY: Double = 0.0
    /**
     * The Z component of the impacted face's normal, or 0.0 if no impact occurred
     */
    @JvmField
    var collisionNormalZ: Double = 0.0

    /**
     * The X component of the impacted block's position, or 0.0 if no impact occurred. Test if [collisionFraction] is
     * less than 1.0 to tell between a collision at (0,0,0) and no collision.
     */
    @JvmField
    var collisionBlockX: Int = 0
    /**
     * The Y component of the impacted block's position, or 0.0 if no impact occurred. Test if [collisionFraction] is
     * less than 1.0 to tell between a collision at (0,0,0) and no collision.
     */
    @JvmField
    var collisionBlockY: Int = 0
    /**
     * The Z component of the impacted block's position, or 0.0 if no impact occurred. Test if [collisionFraction] is
     * less than 1.0 to tell between a collision at (0,0,0) and no collision.
     */
    @JvmField
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
        } else if(cache == null) {
            cache = RayWorldCollider[world]
        }
        ClientRayWorldCollider.cache = cache
        return cache
    }
}
