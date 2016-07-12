package com.teamwizardry.librarianlib.ragdoll.cloth;

import net.minecraft.util.math.Vec3d;

public class PointMass3D {
	public Vec3d pos, prevPos, origPos;
	public float mass;
	public boolean pin = false;

	public PointMass3D(Vec3d pos, float mass) {
		this.prevPos = this.pos = pos;
		this.mass = mass;
	}
}
