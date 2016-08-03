package com.teamwizardry.librarianlib.ragdoll.line;

import com.teamwizardry.librarianlib.math.Vec2d;

/**
 * Created by Saad on 12/7/2016.
 */
public class Link2D {

    public PointMass2D a, b;
    public float restingDistance;
    public float strength;
    public boolean pinA, pinB;
    
    public Link2D(PointMass2D a, PointMass2D b, float strength) {
        this.a = a;
        this.b = b;
        this.strength = strength;
        this.restingDistance = (float) a.pos.sub(b.pos).length();
    }

    public Link2D(PointMass2D a, PointMass2D b, float restingDistance, float strength) {
        this.a = a;
        this.b = b;
        this.strength = strength;
        this.restingDistance = restingDistance;
    }

    public Link2D pinA() { pinA = true; return this; }
    public Link2D pinB() { pinB = true; return this; }
    
    public void resolve() {
        if (a.pin && b.pin) return;

        Vec2d posDiff = a.pos.sub(b.pos);
        double d = posDiff.length();

        double difference = (restingDistance - d) / d;
        difference *= strength;
        if (!( a.pin || pinA ) && !( b.pin || pinB )) // neither are pinned
            difference = difference / 2.0;

        Vec2d translate = posDiff.mul(difference);

        if (!( a.pin || pinA )) a.pos = a.pos.add(translate);

        if (!( b.pin || pinB )) b.pos = b.pos.sub(translate);
    }
}
