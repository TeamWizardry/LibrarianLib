package com.teamwizardry.librarianlib.math;

import net.minecraft.util.math.Vec3d;

public class Tri {

	private static final double SMALL_NUM = 0.00000001f;
	
	Vec3d v1, v2, v3;
	
	public Tri(Vec3d v1, Vec3d v2, Vec3d v3) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
	}
	
	/**
	 * Traces this tri and returns a new end position.
	 * 
	 * Returns {@link end} if there wasn't a hit
	 */
	public Vec3d trace(Vec3d start, Vec3d end) {
		Vec3d v1 = this.v1;
		Vec3d v2 = this.v2;
		Vec3d v3 = this.v3;
		
		Vec3d intersect;
	    Vec3d    u, v, n;              // triangle vectors
	    Vec3d    dir, w0, w;           // ray vectors
	    double     r, a, b;              // params to calc ray-plane intersect

	    // get triangle edge vectors and plane normal
	    u = v2.subtract(v1);
	    v = v3.subtract(v1);
	    n = u.crossProduct(v);              // cross product
	    if (n.equals( Vec3d.ZERO ))             // triangle is degenerate
	        return end;                   // do not deal with this case

	    dir = end.subtract(start);             // ray direction vector
	    start.subtract(dir.normalize().scale(0.25));
	    dir = end.subtract(start);             // ray direction vector
	    w0 = start;
	    w0 = w0.subtract(v1);
	    a = -n.dotProduct(w0);
	    b =  n.dotProduct(dir);
	    if (Math.abs(b) < SMALL_NUM) {     // ray is  parallel to triangle plane
	        if (a == 0)                 // ray lies in triangle plane
	            return end;
	        else
	        	return end;              // ray disjoint from plane
	    }

	    // get intersect point of ray with triangle plane
	    r = a / b;
	    if (r < 0.0)                    // ray goes away from triangle
	        return end;                   // => no intersect
	    if (r > 1.0)                    // ray doesn't reach triangle
	    	return end;                   // => no intersect

	    intersect = start.add(dir.scale(r));            // intersect point of ray and plane
	    
	    float angles = 0;

        v1 = intersect.subtract(this.v1).normalize();
        v2 = intersect.subtract(this.v2).normalize();
        v3 = intersect.subtract(this.v3).normalize();

        angles += Math.acos(v1.dotProduct(v2));
        angles += Math.acos(v2.dotProduct(v3));
        angles += Math.acos(v3.dotProduct(v1));

        if(Math.abs(angles - 2*Math.PI) > 0.005)
        	return end;
                
        return intersect;
	}
	
	@Override
	public Tri clone() {
		return new Tri(v1, v2, v3);
	}
	
	public void translate(Vec3d vec) {
		v1 = v1.add(vec);
		v2 = v2.add(vec);
		v3 = v3.add(vec);
	}
	
	public void apply(Matrix4 matrix) {
		v1 = matrix.apply(v1);
		v2 = matrix.apply(v2);
		v3 = matrix.apply(v3);
	}
	
}
