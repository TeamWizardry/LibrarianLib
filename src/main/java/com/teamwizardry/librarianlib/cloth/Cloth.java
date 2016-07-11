package com.teamwizardry.librarianlib.cloth;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

public class Cloth {

	public PointMass[][] masses;
	public List<Link> links = new ArrayList<>();
	public int solvePasses = 3;
	public Vec3d[] top;
	public int height;
	public Vec3d size;
	
	
	public Cloth(Vec3d[] top, int height, Vec3d size) {
		this.top = top;
		this.height = height;
		this.size = size;
		
		init();
	}
	
	public void init() {
		masses = new PointMass[height][top.length];
		links = new ArrayList<>();
		
		for(int i = 0; i < height; i++) {
			
			for(int j = 0; j < top.length; j++) {
				masses[i][j] = new PointMass(top[j].add(size.scale(i)), 0.1f);
			}
			
		}
		
		for (int x = 0; x < masses.length; x++) {
			for (int z = 0; z < masses[x].length; z++) {
				if(x == 0)
					masses[x][z].pin = true;
				if(x+1 < masses.length)
					links.add(new Link(masses[x][z], masses[x+1][z]));
				if(z+1 < masses[x].length)
					links.add(new Link(masses[x][z], masses[x][z+1]));
				if(x+1 < masses.length && z+1 < masses[x].length)
					links.add(new Link(masses[x][z], masses[x+1][z+1]));
			}
		}
	}
	
	public void tick(List<AxisAlignedBB> aabbs) {
		for (int i = 0; i < aabbs.size(); i++) {
		}
		Vec3d gravity = new Vec3d(0, -0.05, 0);
		
		for (PointMass[] column : masses) {
			for (PointMass point : column) {
				if(point.pin)
					continue;
				point.origPos = point.pos;
				point.pos = point.pos.add(gravity).add(point.pos.subtract(point.prevPos));
			}
		}
		
		for (int i = 0; i < solvePasses; i++) {
			for (Link link : links) {
				link.resolve();
			}
		}
		
		for (PointMass[] column : masses) {
			for (PointMass point : column) {
				if(!point.pin) {
					point.origPos = point.origPos.subtract(point.pos.subtract(point.origPos).scale(0.001));
					for (AxisAlignedBB aabb : aabbs) {
						Vec3d res = calculateIntercept(aabb, point.origPos, point.pos);
						if(res != null) {
							point.pos = res;
						}
					}
				}
				point.prevPos = point.pos;
			}
		}
	}
	
    public Vec3d calculateIntercept(AxisAlignedBB aabb, Vec3d vecA, Vec3d vecB)
    {
    	Vec3d vecX = null, vecY = null, vecZ = null;
    	
    	if(vecA.xCoord > vecB.xCoord) {
            vecX = collideWithXPlane(aabb, aabb.maxX, vecA, vecB);
    	}
    	
    	if(vecA.xCoord < vecB.xCoord) {
            vecX = collideWithXPlane(aabb, aabb.minX, vecA, vecB);
    	}
    	
    	if(vecA.yCoord > vecB.yCoord) {
            vecY = collideWithYPlane(aabb, aabb.maxY, vecA, vecB);
    	}
    	
    	if(vecA.yCoord < vecB.yCoord) {
            vecY = collideWithYPlane(aabb, aabb.minY, vecA, vecB);
    	}
    	
    	if(vecA.zCoord > vecB.zCoord) {
            vecZ = collideWithZPlane(aabb, aabb.maxZ, vecA, vecB);
    	}
    	
    	if(vecA.zCoord < vecB.zCoord) {
            vecZ = collideWithZPlane(aabb, aabb.minZ, vecA, vecB);
    	}
    	
    	return min(vecX, min(vecY, vecZ));
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
        return vec.yCoord > aabb.minY && vec.yCoord < aabb.maxY && vec.zCoord > aabb.minZ && vec.zCoord < aabb.maxZ;
    }

    @VisibleForTesting
    public boolean intersectsWithXZ(AxisAlignedBB aabb, Vec3d vec)
    {
        return vec.xCoord > aabb.minX && vec.xCoord < aabb.maxX && vec.zCoord > aabb.minZ && vec.zCoord < aabb.maxZ;
    }

    @VisibleForTesting
    public boolean intersectsWithXY(AxisAlignedBB aabb, Vec3d vec)
    {
        return vec.xCoord > aabb.minX && vec.xCoord < aabb.maxX && vec.yCoord > aabb.minY && vec.yCoord < aabb.maxY;
    }
    
}
