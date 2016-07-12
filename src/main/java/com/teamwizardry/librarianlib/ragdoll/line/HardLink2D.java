package com.teamwizardry.librarianlib.ragdoll.line;

import com.teamwizardry.librarianlib.math.Vec2;

public class HardLink2D extends Link2D {

    public HardLink2D(PointMass2D a, PointMass2D b, float strength) {
        super(a, b, strength);
    }

    public void resolve() {
        if (b.pin) return;

        Vec2 posDiff = a.pos.sub(b.pos);
        double d = posDiff.length();

        double difference = (distance - d) / d;

        Vec2 translate = posDiff.mul(difference);

        b.pos = b.pos.sub(translate);
    }
}
