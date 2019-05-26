package com.teamwizardry.librarianlib.features.utilities

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
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
@Mod.EventBusSubscriber(modid = LibrarianLib.MODID)
class RayWorldCollider private constructor(world: World) {

    private val worldRef = WeakReference(world)

    private val world: World
        get() = worldRef.get()!!

    private val cache = Long2ObjectOpenHashMap<List<AxisAlignedBB>>()

    private var countdown = 0

    /**
     * This specifies when to reset the blockstate cache for collision checking.
     * Setting it to 2, for example, will clear the cache every 2 ticks.
     *
     * Setting it to -1 will disable cache clearing. BE VERY CAREFUL WHEN YOU DO THIS.
     * SERIOUSLY. BE ABSOLUTELY SURE YOU RESET IT PROPERLY YOURSELF. YOU WILL RUN OUT OF MEMORY IF YOU DON'T.
     * Run requestRefresh() to clear it yourself.
     *
     * The cache will only reset when the world unloads on the client if the timer is set to -1.
     *
     * It is heavily advised to just set to a massive number instead.
     * Remember, this works in game ticks.
     */
    @JvmField
    var cacheResetTimer: Int = 10

    /**
     * Request that the cache be cleared. Use this sparingly as it can negatively impact performance.
     *
     * This method _immediately_ clears the cache, meaning calling it repeatedly between [collide] calls can severely
     * impact performance.
     */
    fun requestRefresh() = cache.clear()

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

        val minX = floor(min(posX, posX + velX)).toInt()
        val minY = floor(min(posY, posY + velY)).toInt()
        val minZ = floor(min(posZ, posZ + velZ)).toInt()
        val maxX = floor(max(posX, posX + velX)).toInt()
        val maxY = floor(max(posY, posY + velY)).toInt()
        val maxZ = floor(max(posZ, posZ + velZ)).toInt()

        val invVelX = 1 / velX
        val invVelY = 1 / velY
        val invVelZ = 1 / velZ

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val list = getAABBs(x, y, z)
                    for (i in 0 until list.size) {
                        collide(result, list[i],
                                x, y, z,
                                posX, posY, posZ,
                                invVelX, invVelY, invVelZ
                        )
                    }
                }
            }
        }
    }

    private fun collide(
            result: RayHitResult,
            aabb: AxisAlignedBB,
            blockX: Int,
            blockY: Int,
            blockZ: Int,
            posX: Double,
            posY: Double,
            posZ: Double,
            invVelX: Double,
            invVelY: Double,
            invVelZ: Double
    ) {
        val tx1 = (aabb.minX - posX) * invVelX
        val tx2 = (aabb.maxX - posX) * invVelX

        var tmin = min(tx1, tx2)
        var tmax = max(tx1, tx2)

        val ty1 = (aabb.minY - posY) * invVelY
        val ty2 = (aabb.maxY - posY) * invVelY

        tmin = max(tmin, min(ty1, ty2))
        tmax = min(tmax, max(ty1, ty2))

        val tz1 = (aabb.minZ - posZ) * invVelZ
        val tz2 = (aabb.maxZ - posZ) * invVelZ

        tmin = max(tmin, min(tz1, tz2))
        tmax = min(tmax, max(tz1, tz2))

        if (tmax >= tmin && tmax >= 0 && tmin >= 0 && tmin < result.collisionFraction) {
            result.collisionNormalX = if (tmin == tx1) -1.0 else if (tmin == tx2) 1.0 else 0.0
            result.collisionNormalY = if (tmin == ty1) -1.0 else if (tmin == ty2) 1.0 else 0.0
            result.collisionNormalZ = if (tmin == tz1) -1.0 else if (tmin == tz2) 1.0 else 0.0
            result.collisionBlockX = blockX
            result.collisionBlockY = blockY
            result.collisionBlockZ = blockZ
            result.collisionFraction = tmin
            return
        }
    }

    private val mutablePos = BlockPos.MutableBlockPos()
    private val infiniteAABB = AxisAlignedBB(
            Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
    )

    private fun getAABBs(x: Int, y: Int, z: Int): List<AxisAlignedBB> {
        mutablePos.setPos(x, y, z)
        cache[mutablePos.toLong()]?.let { return it }

        val list: List<AxisAlignedBB>
        if (!world.isBlockLoaded(mutablePos) || mutablePos.y < 0 || mutablePos.y > world.actualHeight) {
            list = emptyList()
        } else {
            val blockstate: IBlockState = world.getBlockState(mutablePos)
            if (blockstate.material == Material.AIR ||
                !blockstate.material.blocksMovement() || blockstate.material.isLiquid) {
                list = emptyList()
            } else {
                list = ArrayList(1)
                blockstate.addCollisionBoxToList(world, mutablePos, infiniteAABB, list, null, false)
            }
        }
        cache[mutablePos.toLong()] = list

        return list
    }


    companion object {
        @JvmStatic
        private val worldMap = WeakHashMap<World, RayWorldCollider>()

        @SubscribeEvent
        @JvmStatic
        fun tick(e: TickEvent.ClientTickEvent) {
            for (value in worldMap.values) {
                if (value.cacheResetTimer == -1) return
                if (value.cache.size == 0) {
                    value.countdown = value.cacheResetTimer
                    return
                }

                if (value.countdown <= 0) {
                    value.cache.clear()
                    value.countdown = value.cacheResetTimer
                }
                value.countdown--
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
                ClientRunnable.run {
                    handler = ClientRayWorldCollider.get()
                }
                return handler
            }
    }
}

class RayHitResult {
    /**
     * The fraction along the raytrace that an impact occured, or 1.0 if no impact occured
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

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = [Side.CLIENT], modid = LibrarianLib.MODID)
private object ClientRayWorldCollider {

    var cache: RayWorldCollider? = null

    @JvmStatic
    @SubscribeEvent
    fun unloadWorld(e: WorldEvent.Unload) {
        cache = null
    }

    fun get(): RayWorldCollider {
        var cache = this.cache
        if (cache == null) {
            cache = RayWorldCollider[Minecraft.getMinecraft().world]
            this.cache = cache
            return cache
        }
        return cache
    }
}

/*
var fastAirCheck = true
fun blah() {
    val isAir = false
    if(fastAirCheck) {
        try {
            isAir = this.isAir(x, y, z)
        } catch(e: Exception) {
            fastAirCheck = false
        }
    }
    if(isAir) {
        list = emptyList()
    } else {
        val blockState = world.getBlockState(...)
        if(blockState.block == Air || materialAirStuff || whatever)
        list = ArrayList(1)
        // add from blockstate and whatnot.
    }
}
private fun isAir(x: Int, y: Int, z: Int): Boolean {
    val chunk = world.chunkProvider.getLoadedChunk(x shr 4, z shr 4) ?: return true
    val storageArrays = chunk.storageArrays_mh
    if(y < 0 || y shr 4 >= storageArrays.size) return true
    val storage = storageArrays[y shr 4]

    if(storage == Chunk.NULL_BLOCK_STORAGE) return true
    val data = storage.data_mh
    val index = (y and 0xf shl 8) or (z and 0xf shl 4) or (x and 0xf)
    return data.storage_mh.getAt(index) == 0
}
val Chunk.storageArrays_mh by MethodHandleHelper.delegateForReadOnly<Chunk, Array<ExtendedBlockStorage>>(Chunk::class.java, "field_78725_b", "renderPosX")
val ExtendedBlockStorage.data_mh by MethodHandleHelper.delegateForReadOnly<ExtendedBlockStorage, BlockStateContainer>(ExtendedBlockStorage::class.java, "field_78725_b", "renderPosX")
val BlockStateContainer.storage_mh by MethodHandleHelper.delegateForReadOnly<BlockStateContainer, BitArray>(BlockStateContainer::class.java, "field_78725_b", "renderPosX")
 */
 */
