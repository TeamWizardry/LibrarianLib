package com.teamwizardry.librarianlib.ragdoll.line;

import com.teamwizardry.librarianlib.math.Vec2;

/**
 * Created by Saad on 12/7/2016.
 */
public class Link2D {

    public PointMass2D a, b;
    public float restingDistance;
    public float stiffness;

    public Link2D(PointMass2D a, PointMass2D b, float stiffness) {
        this.a = a;
        this.b = b;
        this.stiffness = stiffness;
        this.restingDistance = (float) a.pos.sub(b.pos).length();
    }

    public Link2D(PointMass2D a, PointMass2D b, float restingDistance, float stiffness) {
        this.a = a;
        this.b = b;
        this.stiffness = stiffness;
        this.restingDistance = restingDistance;
    }

    public void resolve() {
        if (a.pin && b.pin) return;

        Vec2 posDiff = a.pos.sub(b.pos);
        double d = posDiff.length();

        double difference = (restingDistance - d) / d;

        if (!a.pin && !b.pin) // neither are pinned
            difference = difference / 2.0;

        Vec2 translate = posDiff.mul(difference);

        if (!a.pin) a.pos = a.pos.add(translate);

        if (!b.pin) b.pos = b.pos.sub(translate);
    }
}
