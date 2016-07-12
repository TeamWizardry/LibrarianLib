package com.teamwizardry.librarianlib.ragdoll.line;

import com.teamwizardry.librarianlib.math.Vec2;

/**
 * Created by Saad on 12/7/2016.
 */
public class Link2D {

    public PointMass2D a, b;
    public float distance;
    public float strength;

    public Link2D(PointMass2D a, PointMass2D b, float strength) {
        this.a = a;
        this.b = b;
        this.strength = strength;
        this.distance = (float) a.pos.sub(b.pos).length();
    }

    public Link2D(PointMass2D a, PointMass2D b, float distance, float strength) {
        this.a = a;
        this.b = b;
        this.strength = strength;
        this.distance = distance;
    }

    public void resolve() {
        if (a.pin && b.pin) return;

        Vec2 posDiff = a.pos.sub(b.pos);
        double d = posDiff.length();

        double difference = (distance - d) / d;

        if (!a.pin && !b.pin) difference = difference / 2.0;

        Vec2 translate = posDiff.mul(difference);

        if (!a.pin) a.pos = a.pos.add(translate);
        if (!b.pin) b.pos = b.pos.sub(translate);

    }
}
