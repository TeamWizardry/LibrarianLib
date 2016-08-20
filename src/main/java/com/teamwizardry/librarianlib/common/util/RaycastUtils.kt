package com.teamwizardry.librarianlib.common.util

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

object RaycastUtils {
    @JvmStatic
    @JvmOverloads
    fun raycast(e: Entity, len: Double, stopOnLiquid: Boolean = false): RayTraceResult? {
        val vec = e.positionVector.addVector(0.0, if (e is EntityPlayer) e.getEyeHeight().toDouble() else 0.0, 0.0)

        val look = e.lookVec
        if (look == null) {
            return null
        } else {
            return raycast(e.worldObj, vec, look, len, stopOnLiquid)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun raycast(world: World, origin: Vec3d, ray: Vec3d, len: Double, stopOnLiquid: Boolean = false): RayTraceResult? {
        val end = origin.add(ray.normalize().scale(len))
        val pos = world.rayTraceBlocks(origin, end, stopOnLiquid)
        return pos
    }

    @JvmStatic
    @JvmOverloads
    fun getEntityLookedAt(e: Entity, maxDistance: Double = 32.0): Entity? {
        var foundEntity: Entity? = null
        var distance = maxDistance
        val pos = raycast(e, maxDistance)
        var positionVector = e.positionVector
        if (e is EntityPlayer) {
            positionVector = positionVector.addVector(0.0, e.getEyeHeight().toDouble(), 0.0)
        }

        if (pos != null) {
            distance = pos.hitVec.distanceTo(positionVector)
        }

        val lookVector = e.lookVec
        val reachVector = positionVector.addVector(lookVector.xCoord * maxDistance, lookVector.yCoord * maxDistance, lookVector.zCoord * maxDistance)
        var lookedEntity: Entity? = null
        val entitiesInBoundingBox = e.worldObj.getEntitiesWithinAABBExcludingEntity(e, e.entityBoundingBox.addCoord(lookVector.xCoord * maxDistance, lookVector.yCoord * maxDistance, lookVector.zCoord * maxDistance).expand(1.0, 1.0, 1.0))
        var minDistance = distance
        val var14 = entitiesInBoundingBox.iterator()

        while (true) {
            do {
                do {
                    if (!var14.hasNext()) {
                        return foundEntity
                    }
                    val next = var14.next()
                    if (next.canBeCollidedWith()) {
                        val collisionBorderSize = next.collisionBorderSize
                        val hitbox = next.entityBoundingBox.expand(collisionBorderSize.toDouble(), collisionBorderSize.toDouble(), collisionBorderSize.toDouble())
                        val interceptPosition = hitbox.calculateIntercept(positionVector, reachVector)
                        if (hitbox.isVecInside(positionVector)) {
                            if (0.0 < minDistance || minDistance == 0.0) {
                                lookedEntity = next
                                minDistance = 0.0
                            }
                        } else if (interceptPosition != null) {
                            val distanceToEntity = positionVector.distanceTo(interceptPosition.hitVec)
                            if (distanceToEntity < minDistance || minDistance == 0.0) {
                                lookedEntity = next
                                minDistance = distanceToEntity
                            }
                        }
                    }
                } while (lookedEntity == null)
            } while (minDistance >= distance && pos != null)

            foundEntity = lookedEntity
        }
    }
}
