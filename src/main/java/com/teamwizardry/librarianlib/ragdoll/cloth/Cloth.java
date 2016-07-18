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
	public float stretch = 1, shear = 1, flex = 1f, air = 0.2f;
	
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
			}
		}
		
		for (int x = 0; x < masses.length; x++) {
			for (int z = 0; z < masses[x].length; z++) {
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
				
				if(x+2 < masses.length && z+2 < masses[x].length) {
					float dist = (float)(
							masses[x  ][z  ].pos.subtract(masses[x+1][z+1].pos).lengthVector() +
							masses[x+1][z+1].pos.subtract(masses[x+2][z+2].pos).lengthVector()
						); // even if initialized bent, try to keep flat.
					links.add(new Link(masses[x][z], masses[x+2][z+2], dist, flex));
				}
				if(x+2 < masses.length && z-2 > 0) {
					float dist = (float)(
							masses[x  ][z  ].pos.subtract(masses[x+1][z-1].pos).lengthVector() +
							masses[x+1][z-1].pos.subtract(masses[x+2][z-2].pos).lengthVector()
						); // even if initialized bent, try to keep flat.
					links.add(new Link(masses[x][z], masses[x+2][z-2], dist, flex));
				}
			}
		}
	}
	
	public void tick(List<AxisAlignedBB> aabbs) {
		air = 3.5f;
		double friction = 0.2;
		
		for (int i = 0; i < aabbs.size(); i++) {
		}
		Vec3d gravity = new Vec3d(0, -0.01, 0);

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
				point.applyMotion(lastMotion); // existing motion
				point.applyForce(gravity); // gravity
				
				Vec3d wind = lastMotion.add(new Vec3d(0.0, 0.0, 1.0/20.0));
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
				
				double angle = Math.acos(wind.normalize().dotProduct(normal));
				if(angle > Math.PI/2)
					normal = normal.scale(-1);
				
				// https://books.google.com/books?id=x5cLAQAAIAAJ&pg=PA5&lpg=PA5&dq=wind+pressure+on+a+flat+angled+surface&source=bl&ots=g090hiOfxv&sig=MqZQhLMozsMNndJtkA1R_bk5KiA&hl=en&sa=X&ved=0ahUKEwiozMW2z_vNAhUD7yYKHeqvBVcQ6AEILjAC#v=onepage&q&f=false
				// page 5-6. I'm using formula (5)
				// wind vector length squared is flat pressure. All the other terms can be changed in the air coefficent.
				Vec3d force = normal.scale(( Math.pow(wind.lengthVector(), 2) * angle )/(Math.PI/4));
				
				point.applyForce(force.scale(air));
					
				point.friction = null;
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
                	point.friction = null;
                	for (AxisAlignedBB aabb : aabbs) {
						Vec3d res = calculateIntercept(aabb, point, true);
						if(res != null) {
							point.pos = res;
						}
					}
					for (AxisAlignedBB aabb : aabbs) {
						Vec3d res = calculateIntercept(aabb, point, false);
						if(res != null) {
							point.pos = res;
						}
					}
					point.applyMotion(point.friction == null ? Vec3d.ZERO : point.friction.scale(-friction));
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
	
    public Vec3d calculateIntercept(AxisAlignedBB aabb, PointMass3D point, boolean yOnly)
    {
    	Vec3d vecA = point.origPos, vecB = point.pos;
    	
    	Vec3d vecX = null, vecY = null, vecZ = null;
    	
    	if(vecA.yCoord > vecB.yCoord) {
            vecY = collideWithYPlane(aabb, aabb.maxY, vecA, vecB);
    	}
    	
    	if(vecA.yCoord < vecB.yCoord) {
            vecY = collideWithYPlane(aabb, aabb.minY, vecA, vecB);
    	}
    	
    	if(vecY != null) {
			point.friction = new Vec3d(vecB.xCoord-vecY.xCoord, 0, vecB.zCoord-vecY.zCoord);
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
			point.friction = new Vec3d(0, vecB.yCoord-vecX.yCoord, vecB.zCoord-vecX.zCoord);
            return new Vec3d(vecX.xCoord, vecB.yCoord, vecB.zCoord);
    	}
    	
    	if(vecA.zCoord > vecB.zCoord) {
            vecZ = collideWithZPlane(aabb, aabb.maxZ, vecA, vecB);
    	}
    	
    	if(vecA.zCoord < vecB.zCoord) {
            vecZ = collideWithZPlane(aabb, aabb.minZ, vecA, vecB);
    	}
    	
    	if(vecZ != null) {
			point.friction = new Vec3d(vecB.xCoord-vecZ.xCoord, vecB.yCoord-vecZ.yCoord, 0);
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
