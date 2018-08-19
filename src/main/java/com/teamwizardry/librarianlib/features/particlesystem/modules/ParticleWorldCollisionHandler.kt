package com.teamwizardry.librarianlib.features.particlesystem.modules

import gnu.trove.map.hash.TLongObjectHashMap
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
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
object ParticleWorldCollisionHandler {
    init { MinecraftForge.EVENT_BUS.register(this) }
    private val cache = TLongObjectHashMap<List<AxisAlignedBB>>()
    private var countdown = 0

    /**
     * The fraction along the raytrace that an impact occured, or 1.0 if no impact occured
     */
    @JvmStatic
    var collisionFraction: Double = 0.0

    /**
     * The X component of the impacted face's normal, or 0.0 if no impact occurred
     */
    @JvmStatic
    var collisionNormalX: Double = 0.0
    /**
     * The Y component of the impacted face's normal, or 0.0 if no impact occurred
     */
    @JvmStatic
    var collisionNormalY: Double = 0.0
    /**
     * The Z component of the impacted face's normal, or 0.0 if no impact occurred
     */
    @JvmStatic
    var collisionNormalZ: Double = 0.0

    /**
     * The X component of the impacted block's position, or 0.0 if no impact occurred. Test if [collisionFraction] is
     * less than 1.0 to tell between a collision at (0,0,0) and no collision.
     */
    @JvmStatic
    var collisionBlockX: Int = 0
    /**
     * The Y component of the impacted block's position, or 0.0 if no impact occurred. Test if [collisionFraction] is
     * less than 1.0 to tell between a collision at (0,0,0) and no collision.
     */
    @JvmStatic
    var collisionBlockY: Int = 0
    /**
     * The Z component of the impacted block's position, or 0.0 if no impact occurred. Test if [collisionFraction] is
     * less than 1.0 to tell between a collision at (0,0,0) and no collision.
     */
    @JvmStatic
    var collisionBlockZ: Int = 0

    /**
     * Request
     */
    @JvmStatic
    fun requestRefresh() {
        cache.clear()
    }

    @JvmStatic
    @JvmOverloads
    fun collide(
            posX: Double,
            posY: Double,
            posZ: Double,
            velX: Double,
            velY: Double,
            velZ: Double,
            maxBounds: Double = 5.0
    ) {
        collisionFraction = 1.0
        collisionNormalX = 0.0
        collisionNormalY = 0.0
        collisionNormalZ = 0.0
        collisionBlockX = 0
        collisionBlockY = 0
        collisionBlockZ = 0

        @Suppress("NAME_SHADOWING")
        val velX = min(maxBounds, max(-maxBounds, velX))
        @Suppress("NAME_SHADOWING")
        val velY = min(maxBounds, max(-maxBounds, velY))
        @Suppress("NAME_SHADOWING")
        val velZ = min(maxBounds, max(-maxBounds, velZ))

        val minX = floor(min(posX, posX+velX)).toInt()
        val minY = floor(min(posY, posY+velY)).toInt()
        val minZ = floor(min(posZ, posZ+velZ)).toInt()
        val maxX = floor(max(posX, posX+velX)).toInt()
        val maxY = floor(max(posY, posY+velY)).toInt()
        val maxZ = floor(max(posZ, posZ+velZ)).toInt()

        val invVelX = 1/velX
        val invVelY = 1/velY
        val invVelZ = 1/velZ

        for(x in minX..maxX) {
            for(y in minY..maxY) {
                for(z in minZ..maxZ) {
                    val list = getAABBs(x, y, z)
                    for(i in 0 until list.size) {
                        collide(list[i],
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
        val tx1 = (aabb.minX - posX)*invVelX
        val tx2 = (aabb.maxX - posX)*invVelX

        var tmin = min(tx1, tx2)
        var tmax = max(tx1, tx2)

        val ty1 = (aabb.minY - posY)*invVelY
        val ty2 = (aabb.maxY - posY)*invVelY

        tmin = max(tmin, min(ty1, ty2))
        tmax = min(tmax, max(ty1, ty2))

        val tz1 = (aabb.minZ - posZ)*invVelZ
        val tz2 = (aabb.maxZ - posZ)*invVelZ

        tmin = max(tmin, min(tz1, tz2))
        tmax = min(tmax, max(tz1, tz2))

        if(tmax >= tmin && tmax >= 0 && tmin >= 0 && tmin < this.collisionFraction) {
            this.collisionNormalX = if(tmin == tx1) -1.0 else if(tmin == tx2) 1.0 else 0.0
            this.collisionNormalY = if(tmin == ty1) -1.0 else if(tmin == ty2) 1.0 else 0.0
            this.collisionNormalZ = if(tmin == tz1) -1.0 else if(tmin == tz2) 1.0 else 0.0
            this.collisionBlockX = blockX
            this.collisionBlockY = blockY
            this.collisionBlockZ = blockZ
            this.collisionFraction = tmin
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
        cache[mutablePos.toLong()]?.let {
            return it
        }

        val world = Minecraft.getMinecraft().world

        val list: List<AxisAlignedBB>
        if(!world.isBlockLoaded(mutablePos)) {
            list = emptyList()
        } else {
            val blockstate = world.getBlockState(mutablePos)
            if(blockstate.block == Blocks.AIR) {
                list = emptyList()
            } else {
                list = ArrayList(1)
                blockstate.addCollisionBoxToList(world, mutablePos, infiniteAABB, list, null, false)
            }
        }
        cache.put(mutablePos.toLong(), list)

        return list
    }

    @SubscribeEvent
    private fun tick(e: TickEvent.ClientTickEvent) {
        if(countdown == 0) {
            cache.clear()
        }
        if(cache.isEmpty) {
            countdown = 2
        }
        countdown--
    }

}