package com.teamwizardry.librarianlib.math;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.teamwizardry.librarianlib.ragdoll.cloth.PointMass3D;

public class Box {
	public Matrix4 matrix, inverse;
	public float minX, minY, minZ, maxX, maxY, maxZ;
	public AxisAlignedBB aabb;
	
	public Box(Matrix4 matrix, Matrix4 inverse, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		super();
		this.matrix = matrix;
		this.inverse = inverse;
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		this.aabb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public Vec3d fix(Vec3d vec) {
		Vec3d vecT = matrix.apply(vec);
		if( !(
				vecT.xCoord > minX && vecT.yCoord > minY && vecT.zCoord > minZ &&
				vecT.xCoord < maxX && vecT.yCoord < maxY && vecT.zCoord < maxZ
			) ) {
			return vec;
		}
		
		double
			distMinX = vecT.xCoord - minX,
			distMinY = vecT.yCoord - minY,
			distMinZ = vecT.zCoord - minZ,
			
			distMaxX = maxX - vecT.xCoord,
			distMaxY = maxY - vecT.yCoord,
			distMaxZ = maxZ - vecT.zCoord;
		
		double tmp = distMinX;
		if(
			//tmp < distMinX &&
			tmp < distMinY &&
			tmp < distMinZ &&
			tmp < distMaxX &&
			tmp < distMaxY &&
			tmp < distMaxZ
			) {
			vecT = new Vec3d(minX, vecT.yCoord, vecT.zCoord);
		}
		
		tmp = distMinY;
		if(
			tmp < distMinX &&
			//tmp < distMinY &&
			tmp < distMinZ &&
			tmp < distMaxX &&
			tmp < distMaxY &&
			tmp < distMaxZ
			) {
			vecT = new Vec3d(vecT.xCoord, minY, vecT.zCoord);
		}
		
		tmp = distMinZ;
		if(
			tmp < distMinX &&
			tmp < distMinY &&
			//tmp < distMinZ &&
			tmp < distMaxX &&
			tmp < distMaxY &&
			tmp < distMaxZ
			) {
			vecT = new Vec3d(vecT.xCoord, vecT.yCoord, minZ);
		}
		
		tmp = distMaxX;
		if(
			tmp < distMinX &&
			tmp < distMinY &&
			tmp < distMinZ &&
			//tmp < distMaxX &&
			tmp < distMaxY &&
			tmp < distMaxZ
			) {
			vecT = new Vec3d(maxX, vecT.yCoord, vecT.zCoord);
		}
		
		tmp = distMaxY;
		if(
			tmp < distMinX &&
			tmp < distMinY &&
			tmp < distMinZ &&
			tmp < distMaxX &&
			//tmp < distMaxY &&
			tmp < distMaxZ
			) {
			vecT = new Vec3d(vecT.xCoord, maxY, vecT.zCoord);
		}
		
		tmp = distMaxZ;
		if(
			tmp < distMinX &&
			tmp < distMinY &&
			tmp < distMinZ &&
			tmp < distMaxX &&
			tmp < distMaxY //&&
			//tmp < distMaxZ
			) {
			vecT = new Vec3d(vecT.xCoord, vecT.yCoord, maxZ);
		}
		
		Vec3d vecWorldSpace = inverse.apply(vecT);
		return vecWorldSpace;
	}
	
	public Vec3d trace(Vec3d start, Vec3d end) {
		start = matrix.apply(start);
		end = matrix.apply(end);
		Vec3d result = calculateIntercept(aabb, start, end, false);
		if(result == null)
			return null;
		return inverse.apply(result);
	}
	
	public Vec3d calculateIntercept(AxisAlignedBB aabb, Vec3d vecA, Vec3d vecB,  boolean yOnly)
    {    	
    	Vec3d vecX = null, vecY = null, vecZ = null;
    	
    	if(vecA.yCoord > vecB.yCoord) {
            vecY = collideWithYPlane(aabb, aabb.maxY, vecA, vecB);
    	}
    	
    	if(vecA.yCoord < vecB.yCoord) {
            vecY = collideWithYPlane(aabb, aabb.minY, vecA, vecB);
    	}
    	
    	if(vecY != null) {
    		return new Vec3d(vecB.xCoord, vecY.yCoord, vecB.zCoord);
    	}
    	
    	if(yOnly)
    		return null;
    	
    	if(vecA.xCoord > vecB.xCoord) {
            vecX = collideWithXPlane(aabb, aabb.maxX, vecA, vecB);
    	}
    	
    	if(vecA.xCoord < vecB.xCoord) {
            vecX = collideWithXPlane(aabb, aabb.minX, vecA, vecB);
    	}
    	
    	if(vecX != null) {
            return new Vec3d(vecX.xCoord, vecB.yCoord, vecB.zCoord);
    	}
    	
    	if(vecA.zCoord > vecB.zCoord) {
            vecZ = collideWithZPlane(aabb, aabb.maxZ, vecA, vecB);
    	}
    	
    	if(vecA.zCoord < vecB.zCoord) {
            vecZ = collideWithZPlane(aabb, aabb.minZ, vecA, vecB);
    	}
    	
    	if(vecZ != null) {
    		return new Vec3d(vecB.xCoord, vecB.yCoord, vecZ.zCoord);
    	}
    	
    	return null;
    }
	
    Vec3d min(Vec3d a, Vec3d b) {
    	if(a == null && b == null)
    		return null;
    	if(a != null && b == null)
    		return a;
    	if(a == null && b != null)
    		return b;
    	
    	if(b.squareDistanceTo(Vec3d.ZERO) < a.squareDistanceTo(Vec3d.ZERO))
    		return b;
    	return a;
    }
    
    @Nullable
    @VisibleForTesting
    Vec3d collideWithXPlane(AxisAlignedBB aabb, double p_186671_1_, Vec3d p_186671_3_, Vec3d p_186671_4_)
    {
        Vec3d vec3d = p_186671_3_.getIntermediateWithXValue(p_186671_4_, p_186671_1_);
        return vec3d != null && this.intersectsWithYZ(aabb, vec3d) ? vec3d : null;
    }

    @Nullable
    @VisibleForTesting
    Vec3d collideWithYPlane(AxisAlignedBB aabb, double p_186663_1_, Vec3d p_186663_3_, Vec3d p_186663_4_)
    {
        Vec3d vec3d = p_186663_3_.getIntermediateWithYValue(p_186663_4_, p_186663_1_);
        return vec3d != null && this.intersectsWithXZ(aabb, vec3d) ? vec3d : null;
    }

    @Nullable
    @VisibleForTesting
    Vec3d collideWithZPlane(AxisAlignedBB aabb, double p_186665_1_, Vec3d p_186665_3_, Vec3d p_186665_4_)
    {
        Vec3d vec3d = p_186665_3_.getIntermediateWithZValue(p_186665_4_, p_186665_1_);
        return vec3d != null && this.intersectsWithXY(aabb, vec3d) ? vec3d : null;
    }
    
    @VisibleForTesting
    public boolean intersectsWithYZ(AxisAlignedBB aabb, Vec3d vec)
    {
    	double m = -0.0;
        return vec.yCoord > aabb.minY+m && vec.yCoord < aabb.maxY-m && vec.zCoord > aabb.minZ+m && vec.zCoord < aabb.maxZ-m;
    }

    @VisibleForTesting
    public boolean intersectsWithXZ(AxisAlignedBB aabb, Vec3d vec)
    {
    	double m = -0.0;
        return vec.xCoord > aabb.minX+m && vec.xCoord < aabb.maxX-m && vec.zCoord > aabb.minZ+m && vec.zCoord < aabb.maxZ-m;
    }

    @VisibleForTesting
    public boolean intersectsWithXY(AxisAlignedBB aabb, Vec3d vec)
    {
        return vec.xCoord > aabb.minX && vec.xCoord < aabb.maxX && vec.yCoord > aabb.minY && vec.yCoord < aabb.maxY;
    }
}
