package com.teamwizardry.librarianlib.cloth;

import net.minecraft.util.math.Vec3d;

public class HardLink extends Link {

	public HardLink(PointMass a, PointMass b, float strength) {
		super(a, b, strength);
	}
	
	public void resolve() {
		if(b.pin)
			return;
		
		Vec3d posDiff = a.pos.subtract(b.pos);
		double d = posDiff.lengthVector();
		
		double difference = (distance - d)/d;
		
		Vec3d translate = posDiff.scale(difference);
		
		b.pos = b.pos.subtract(translate);
	}

}
