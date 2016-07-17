package com.teamwizardry.librarianlib.ragdoll.cloth;

import com.google.common.annotations.VisibleForTesting;
import com.teamwizardry.librarianlib.math.Geometry;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Cloth {

    public PointMass3D[][] masses;
    public List<Link> links = new ArrayList<>();
	public List<Link> hardLinks = new ArrayList<>();
	public int solvePasses = 3;
	public Vec3d[] top;
	public int height;
	public Vec3d size;
	public float stretch = 1, shear = 1, flex = 0.8f, air = 0.2f;
	
	public Cloth(Vec3d[] top, int height, Vec3d size) {
		this.top = top;
		this.height = height;
		this.size = size;
		
		init();
	}
	
	public void init() {
        masses = new PointMass3D[height][top.length];
        links = new ArrayList<>();
		
		for(int i = 0; i < height; i++) {
			
			for(int j = 0; j < top.length; j++) {
                masses[i][j] = new PointMass3D(top[j].add(size.scale(i)), 0.1f);
                if(i == 0)
					masses[i][j].pin = true;
			}
			
		}
		
		for (int x = 0; x < masses.length; x++) {
			for (int z = 0; z < masses[x].length; z++) {
				
				if(x+1 < masses.length)
					hardLinks.add(new HardLink(masses[x][z], masses[x+1][z], 1));
				
				if(x+1 < masses.length)
					links.add(new Link(masses[x][z], masses[x+1][z], stretch));
				if(z+1 < masses[x].length && x != 0)
					links.add(new Link(masses[x][z], masses[x][z+1], stretch));
				
				if(x+1 < masses.length && z+1 < masses[x].length)
					links.add(new Link(masses[x][z], masses[x+1][z+1], shear));
				if(x+1 < masses.length && z-1 >= 0)
					links.add(new Link(masses[x][z], masses[x+1][z-1], shear));
				
				if(x+2 < masses.length) {
					float dist = (float)(
							masses[x  ][z].pos.subtract(masses[x+1][z].pos).lengthVector() +
							masses[x+1][z].pos.subtract(masses[x+2][z].pos).lengthVector()
						); // even if initialized bent, try to keep flat.
					links.add(new Link(masses[x][z], masses[x+2][z], dist, flex));
				}
				if(z+2 < masses[x].length) {
					float dist = (float)(
							masses[x][z  ].pos.subtract(masses[x][z+1].pos).lengthVector() +
							masses[x][z+1].pos.subtract(masses[x][z+2].pos).lengthVector()
						); // even if initialized bent, try to keep flat.
					links.add(new Link(masses[x][z], masses[x][z+2], dist, flex));
				}
			}
		}
	}
	
	public void tick(List<AxisAlignedBB> aabbs) {
		air = 1.0f;
		for (int i = 0; i < aabbs.size(); i++) {
		}
		Vec3d gravity = new Vec3d(0, -0.1, 0);

		for (PointMass3D[] column : masses) {
            for (PointMass3D point : column) {
            	point.origPos = point.pos;
            }
		}
		
		for (int x = 0; x < masses.length; x++) {
            for (int y = 0; y < masses[x].length; y++) {
            	PointMass3D point = masses[x][y];
            	
                if(point.pin)
					continue;
                Vec3d lastMotion = point.pos.subtract(point.prevPos);
				point.pos = point.pos.add(gravity); // gravity
				point.pos.add(lastMotion); // existing motion
				
				Vec3d wind = lastMotion.add(new Vec3d(0.0, 0.0, 10.0));
				Vec3d normal = Vec3d.ZERO;
				
				if(x > 0 && y > 0) {
					normal = normal.add( Geometry.getNormal(point.origPos, masses[x][y-1].origPos, masses[x-1][y].origPos) );
				}
				
				if(x > 0 && y+1 < masses[x].length) {
					normal = normal.add( Geometry.getNormal(point.origPos, masses[x][y+1].origPos, masses[x-1][y].origPos) );
				}
				
				if(x+1 < masses.length && y+1 < masses[x].length) {
					normal = normal.add( Geometry.getNormal(point.origPos, masses[x][y+1].origPos, masses[x+1][y].origPos) );
				}
				
				if(x+1 < masses.length && y > 0) {
					normal = normal.add( Geometry.getNormal(point.origPos, masses[x][y-1].origPos, masses[x+1][y].origPos) );
				}
				
				normal = normal.normalize();
				wind = wind.scale(wind.lengthVector());
				Vec3d force = normal.scale(wind.dotProduct(normal));
				
				point.pos.add(force.scale(-air));//normal.scale(-air).scale(Math.pow(wind.lengthVector(), 2))); // air resistance
			}
		}
		
		for (int i = 0; i < solvePasses; i++) {
			for (Link link : links) {
				link.resolve();
			}
		}
		
		for (Link link : hardLinks) {
			link.resolve();
		}

        for (PointMass3D[] column : masses) {
            for (PointMass3D point : column) {
                if(!point.pin) {
//					point.origPos = point.origPos.subtract(point.pos.subtract(point.origPos).scale(0.1));
					for (AxisAlignedBB aabb : aabbs) {
						Vec3d vecA = point.origPos, vecB = point.pos;
						Vec3d res = null;
						if(vecA.yCoord > vecB.yCoord) {
				            res = collideWithYPlane(aabb, aabb.maxY, vecA, vecB);
				    	}
				    	
				    	if(vecA.yCoord < vecB.yCoord) {
				            res = collideWithYPlane(aabb, aabb.minY, vecA, vecB);
				    	}
				    	
						if(res != null) {
							double f = 0.8;
							point.pos = new Vec3d(vecB.xCoord - (vecB.xCoord-res.xCoord)*f, res.yCoord, vecB.zCoord - (vecB.zCoord-res.zCoord)*f);
						}
					}
					for (AxisAlignedBB aabb : aabbs) {
						Vec3d res = calculateIntercept(aabb, point.origPos, point.pos);
						if(res != null) {
							if(point.origPos.yCoord == 85 && res.yCoord < 85) {
								point.pos = calculateIntercept(aabb, point.origPos, point.pos);
							}
							point.pos = res;
						}
					}
				}
				point.prevPos = point.pos;
			}
		}
        
        for (Link link : hardLinks) {
        	Vec3d posDiff = link.a.pos.subtract(link.b.pos);
    		double d = posDiff.lengthVector();
    		
    		double difference = d-link.distance;
			if(difference > link.distance)
				link.resolve();
		}
	}
	
    public Vec3d calculateIntercept(AxisAlignedBB aabb, Vec3d vecA, Vec3d vecB)
    {
    	Vec3d vecX = null, vecY = null, vecZ = null;
    	
    	double f = 0.8;
    	double m = 0.0;
    	 
    	if(vecA.yCoord > vecB.yCoord) {
            vecY = collideWithYPlane(aabb, aabb.maxY, vecA.add(new Vec3d(0, m, 0)), vecB);
    	}
    	
    	if(vecA.yCoord < vecB.yCoord) {
            vecY = collideWithYPlane(aabb, aabb.minY, vecA.add(new Vec3d(0, -m, 0)), vecB);
    	}
    	
    	if(vecY != null) {
    		return new Vec3d(vecB.xCoord - (vecB.xCoord-vecY.xCoord)*f, vecY.yCoord, vecB.zCoord - (vecB.zCoord-vecY.zCoord)*f);
    	}
    	
    	if(vecA.xCoord > vecB.xCoord) {
            vecX = collideWithXPlane(aabb, aabb.maxX, vecA.add(new Vec3d(m, 0, 0)), vecB);
    	}
    	
    	if(vecA.xCoord < vecB.xCoord) {
            vecX = collideWithXPlane(aabb, aabb.minX, vecA.add(new Vec3d(-m, 0, 0)), vecB);
    	}
    	
    	if(vecX != null) {
            return new Vec3d(vecX.xCoord, vecB.yCoord - (vecB.yCoord-vecX.yCoord)*f, vecB.zCoord - (vecB.zCoord-vecX.zCoord)*f);
    	}
    	
    	if(vecA.zCoord > vecB.zCoord) {
            vecZ = collideWithZPlane(aabb, aabb.maxZ, vecA.add(new Vec3d(0, 0, m)), vecB);
    	}
    	
    	if(vecA.zCoord < vecB.zCoord) {
            vecZ = collideWithZPlane(aabb, aabb.minZ, vecA.add(new Vec3d(0, 0, -m)), vecB);
    	}
    	
    	if(vecZ != null) {
    		return new Vec3d(vecB.xCoord - (vecB.xCoord-vecZ.xCoord)*f, vecB.yCoord - (vecB.yCoord-vecZ.yCoord)*f, vecZ.zCoord);
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
    	double m = -0.001;
        return vec.yCoord > aabb.minY+m && vec.yCoord < aabb.maxY-m && vec.zCoord > aabb.minZ+m && vec.zCoord < aabb.maxZ-m;
    }

    @VisibleForTesting
    public boolean intersectsWithXZ(AxisAlignedBB aabb, Vec3d vec)
    {
    	double m = -0.001;
        return vec.xCoord > aabb.minX+m && vec.xCoord < aabb.maxX-m && vec.zCoord > aabb.minZ+m && vec.zCoord < aabb.maxZ-m;
    }

    @VisibleForTesting
    public boolean intersectsWithXY(AxisAlignedBB aabb, Vec3d vec)
    {
        return vec.xCoord > aabb.minX && vec.xCoord < aabb.maxX && vec.yCoord > aabb.minY && vec.yCoord < aabb.maxY;
    }
    
}
