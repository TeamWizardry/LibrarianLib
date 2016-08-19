package com.teamwizardry.librarianlib.bloat

import com.teamwizardry.librarianlib.bloat.MathUtil
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d

object AABBUtils {

    /**
     * Finds the closest point not inside `aabb`. If the `point` isn't inside `aabb`, `point` is returned
     * @param aabb
     * *
     * @param point
     * *
     * @return
     */
    fun closestOutsidePoint(aabb: AxisAlignedBB, point: Vec3d): Vec3d {
        var point = point
        if (point.xCoord <= aabb.minX || point.yCoord <= aabb.minY || point.zCoord <= aabb.minZ ||
                point.xCoord >= aabb.maxX || point.yCoord >= aabb.maxY || point.zCoord >= aabb.maxZ) {
            return point
        }

        val distMinX = point.xCoord - aabb.minX
        val distMinY = point.yCoord - aabb.minY
        val distMinZ = point.zCoord - aabb.minZ

        val distMaxX = aabb.maxX - point.xCoord
        val distMaxY = aabb.maxY - point.yCoord
        val distMaxZ = aabb.maxZ - point.zCoord

        if (MathUtil.isLessThanOthers(distMinX, distMinY, distMinZ, distMaxX, distMaxY, distMaxZ)) {
            point = Vec3d(aabb.minX, point.yCoord, point.zCoord)
        } else if (MathUtil.isLessThanOthers(distMaxX, distMinX, distMinY, distMinZ, distMaxY, distMaxZ)) {
            point = Vec3d(aabb.maxX, point.yCoord, point.zCoord)
        } else if (MathUtil.isLessThanOthers(distMinY, distMinX, distMinZ, distMaxX, distMaxY, distMaxZ)) {
            point = Vec3d(point.yCoord, aabb.minY, point.zCoord)
        } else if (MathUtil.isLessThanOthers(distMaxY, distMinX, distMinY, distMinZ, distMaxX, distMaxZ)) {
            point = Vec3d(point.yCoord, aabb.maxY, point.zCoord)
        } else if (MathUtil.isLessThanOthers(distMinZ, distMinX, distMinY, distMaxX, distMaxY, distMaxZ)) {
            point = Vec3d(point.yCoord, point.yCoord, aabb.minZ)
        } else if (MathUtil.isLessThanOthers(distMaxZ, distMinX, distMinY, distMinZ, distMaxX, distMaxY)) {
            point = Vec3d(point.yCoord, point.yCoord, aabb.maxZ)
        }

        return point
    }

}
