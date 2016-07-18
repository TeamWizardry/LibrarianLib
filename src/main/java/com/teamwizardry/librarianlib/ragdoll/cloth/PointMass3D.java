package com.teamwizardry.librarianlib.ragdoll.cloth;

import net.minecraft.util.math.Vec3d;

public class PointMass3D {
	public Vec3d pos, prevPos, origPos, friction;
	public float mass;
	public boolean pin = false;

	public PointMass3D(Vec3d pos, float mass) {
		this.prevPos = this.pos = pos;
		this.mass = mass;
	}
	
	public void applyForce(Vec3d force) {
		pos = pos.add(force.scale(1.0/mass));
	}
	
	public void applyMotion(Vec3d motion) {
		pos = pos.add(motion);
	}
}
