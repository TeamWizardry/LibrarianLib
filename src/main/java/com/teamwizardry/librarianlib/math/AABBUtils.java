package com.teamwizardry.librarianlib.math;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class AABBUtils {

	/**
	 * Finds the closest point not inside {@code aabb}. If the {@code point} isn't inside {@code aabb}, {@code point} is returned
	 * @param aabb
	 * @param point
	 * @return
	 */
	public static Vec3d closestOutsidePoint(AxisAlignedBB aabb, Vec3d point) {
		if(
			point.xCoord <= aabb.minX || point.yCoord <= aabb.minY || point.zCoord <= aabb.minZ ||
			point.xCoord >= aabb.maxX || point.yCoord >= aabb.maxY || point.zCoord >= aabb.maxZ
			) {
			return point;
		}
		
		double
			distMinX = point.xCoord - aabb.minX,
			distMinY = point.yCoord - aabb.minY,
			distMinZ = point.zCoord - aabb.minZ,
			
			distMaxX = aabb.maxX - point.xCoord,
			distMaxY = aabb.maxY - point.yCoord,
			distMaxZ = aabb.maxZ - point.zCoord;
		
		if(MathUtil.isLessThanOthers(distMinX, distMinY, distMinZ, distMaxX, distMaxY, distMaxZ)) {
			point = new Vec3d(aabb.minX, point.yCoord, point.zCoord);
		} else 
		if(MathUtil.isLessThanOthers(distMaxX, distMinX, distMinY, distMinZ, distMaxY, distMaxZ)) {
			point = new Vec3d(aabb.maxX, point.yCoord, point.zCoord);
		} else 
		if(MathUtil.isLessThanOthers(distMinY, distMinX, distMinZ, distMaxX, distMaxY, distMaxZ)) {
			point = new Vec3d(point.yCoord, aabb.minY, point.zCoord);
		} else 
		if(MathUtil.isLessThanOthers(distMaxY, distMinX, distMinY, distMinZ, distMaxX, distMaxZ)) {
			point = new Vec3d(point.yCoord, aabb.maxY, point.zCoord);
		} else 
		if(MathUtil.isLessThanOthers(distMinZ, distMinX, distMinY, distMaxX, distMaxY, distMaxZ)) {
			point = new Vec3d(point.yCoord, point.yCoord, aabb.minZ);
		} else 
		if(MathUtil.isLessThanOthers(distMaxZ, distMinX, distMinY, distMinZ, distMaxX, distMaxY)) {
			point = new Vec3d(point.yCoord, point.yCoord, aabb.maxZ);
		}
		
		return point;
	}
	
}
