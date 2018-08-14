package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import gnu.trove.map.TLongObjectMap
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

class CollisionUpdateModule(
        private val position: ParticleBinding,
        private val velocity: ParticleBinding,
        private val endPoint: ParticleBinding,
        private val impactNormal: ParticleBinding? = null,
        private val friction: ParticleBinding? = null,
        private val impactFraction: ParticleBinding? = null
): ParticleUpdateModule {
    override fun update(particle: DoubleArray) {
        val posX = position.get(particle, 0)
        val posY = position.get(particle, 1)
        val posZ = position.get(particle, 2)
        val velX = velocity.get(particle, 0)
        val velY = velocity.get(particle, 1)
        val velZ = velocity.get(particle, 2)

        collide(posX, posY, posZ, velX, velY, velZ)

        val endX = posX + velX*collisionFraction
        val endY = posY + velY*collisionFraction
        val endZ = posZ + velZ*collisionFraction
        endPoint.set(particle, 0, endX)
        endPoint.set(particle, 1, endY)
        endPoint.set(particle, 2, endZ)
        impactNormal?.set(particle, 0, collisionNormalX)
        impactNormal?.set(particle, 1, collisionNormalY)
        impactNormal?.set(particle, 2, collisionNormalZ)
        impactFraction?.set(particle, 0, 1.0)
        if(collisionNormalX > 0 || collisionNormalY > 0 || collisionNormalZ > 0) {
            friction?.set(particle, 0, 1-collisionNormalX)
            friction?.set(particle, 1, 1-collisionNormalY)
            friction?.set(particle, 2, 1-collisionNormalZ)
        } else {
            friction?.set(particle, 0, 0.0)
            friction?.set(particle, 1, 0.0)
            friction?.set(particle, 2, 0.0)
        }
    }

    private companion object {
        init { MinecraftForge.EVENT_BUS.register(this) }
        private val cache = TLongObjectHashMap<List<AxisAlignedBB>>()//HashMap<BlockPos, List<AxisAlignedBB>>()//

        private var countdown = 0

        @SubscribeEvent
        fun tick(e: TickEvent.ClientTickEvent) {
            if(countdown == 0) {
                cache.clear()
                countdown = 2
            }
            countdown -= 1
        }

        var collisionFraction: Double = 0.0
        var collisionNormalX: Double = 0.0
        var collisionNormalY: Double = 0.0
        var collisionNormalZ: Double = 0.0

        fun collide(
                posX: Double,
                posY: Double,
                posZ: Double,
                velX: Double,
                velY: Double,
                velZ: Double
        ) {
            collisionFraction = 1.0
            collisionNormalX = 0.0
            collisionNormalY = 0.0
            collisionNormalZ = 0.0

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
                        for(box in getAABBs(x, y, z)) {
                            collide(box,
                                    posX, posY, posZ,
                                    invVelX, invVelY, invVelZ
                            )
                        }
                    }
                }
            }
        }

        fun collide(
                aabb: AxisAlignedBB,
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

            if(tmax >= tmin && tmin < this.collisionFraction) {
                this.collisionNormalX = if(tmin == tx1 || tmin == tx2) 1.0 else 0.0
                this.collisionNormalY = if(tmin == ty1 || tmin == ty2) 1.0 else 0.0
                this.collisionNormalZ = if(tmin == tz1 || tmin == tz2) 1.0 else 0.0
                this.collisionFraction = tmin
                return
            }
        }

        private val mutablePos = BlockPos.MutableBlockPos()
        private val infiniteAABB = AxisAlignedBB(
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
        )

        fun getAABBs(x: Int, y: Int, z: Int): List<AxisAlignedBB> {
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
            cache.put(mutablePos.toLong(), list)//[mutablePos.toLong()] = list

            return list
        }

    }
}
