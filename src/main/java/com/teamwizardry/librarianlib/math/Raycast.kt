package com.teamwizardry.librarianlib.math

import com.google.common.base.Predicate
import com.google.common.base.Predicates
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.util.EntitySelectors
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d

object Raycast {
    /**
     * Gets the first block or entity along the given line from the given entity

     * @param entity
     * *            The entity from which to search
     * *
     * @param dir
     * *            The direction along which to search
     * *
     * @param distance
     * *            The restingDistance to check
     * *
     * @return The first block or entity along the given ray
     */
    fun cast(entity: Entity?, dir: Vec3d, distance: Double): RayTraceResult? {
        if (entity != null) {
            var focusedBlock: RayTraceResult? = blockTrace(entity, distance)
            val eyePos = entity.getPositionEyes(1f)
            var blockDistance = distance

            if (focusedBlock != null)
                blockDistance = focusedBlock.hitVec.distanceTo(eyePos)

            val cast = eyePos.addVector(dir.xCoord * distance, dir.yCoord * distance, dir.zCoord * distance)
            var focusedEntity: Entity? = null
            var vec: Vec3d? = null
            val list = entity.worldObj.getEntitiesInAABBexcluding(entity, entity.entityBoundingBox.addCoord(dir.xCoord * distance, dir.yCoord * distance, dir.zCoord * distance).expand(1.0, 1.0, 1.0), Predicates.and(Predicate<net.minecraft.entity.Entity> { apply -> apply != null && (apply.canBeCollidedWith() || apply is EntityItem) }, EntitySelectors.NOT_SPECTATING))
            var blockDistCopy = blockDistance

            var j = 0
            while (j < list.size) {
                val current = list[j]
                val axis = current.entityBoundingBox.expandXyz(current.collisionBorderSize.toDouble())
                val result = axis.calculateIntercept(eyePos, cast)

                if (axis.isVecInside(eyePos)) {
                    if (blockDistCopy > 0) {
                        focusedEntity = current
                        vec = if (result == null) eyePos else result.hitVec
                        blockDistCopy = 0.0
                    }
                } else if (result != null) {
                    val entityDistance = eyePos.distanceTo(result.hitVec)

                    if (entityDistance < blockDistCopy || blockDistCopy == 0.0) {
                        if (current.lowestRidingEntity === entity.lowestRidingEntity && !entity.canRiderInteract()) {
                            if (blockDistCopy == 0.0) {
                                focusedEntity = current
                                vec = result.hitVec
                            }
                        } else {
                            focusedEntity = current
                            vec = result.hitVec
                            blockDistCopy = entityDistance
                        }
                    }
                }

                if (focusedEntity != null && (blockDistCopy < blockDistance || focusedBlock == null)) {
                    focusedBlock = RayTraceResult(focusedEntity, vec!!)
                    if (focusedEntity is EntityLivingBase || focusedEntity is EntityItem) {
                        return focusedBlock
                    }
                }
                j++
            }
            return focusedBlock
        }
        return null
    }

    /**
     * Gets the first block or entity along the given entity's look vector

     * @param entity
     * *            The entity being cast from
     * *
     * @param distance
     * *            The restingDistance to cast the vector
     * *
     * @return The first block or entity colliding with the vector
     */
    fun cast(entity: Entity, distance: Double): RayTraceResult? {
        return cast(entity, entity.getLook(1f), distance)
    }

    private fun blockTrace(entity: Entity, distance: Double): RayTraceResult? {
        return blockTrace(entity, entity.getLook(1f), distance)
    }

    private fun blockTrace(entity: Entity, ray: Vec3d, distance: Double): RayTraceResult? {
        val pos = entity.getPositionEyes(1f)
        val cast = pos.add(ray.scale(distance))
        return entity.worldObj.rayTraceBlocks(pos, cast, false, false, true)
    }
}
