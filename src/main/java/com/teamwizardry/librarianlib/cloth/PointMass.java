package com.teamwizardry.librarianlib.cloth;

import net.minecraft.util.math.Vec3d;

public class PointMass {
	public Vec3d pos, prevPos, origPos;
	public float mass;
	public boolean pin = false;
	
	public PointMass(Vec3d pos, float mass) {
		this.prevPos = this.pos = pos;
		this.mass = mass;
	}
}
