package com.teamwizardry.librarianlib.ragdoll.line;

import com.teamwizardry.librarianlib.math.Vec2;

/**
 * Created by Saad on 12/7/2016.
 */
public class PointMass2D {

    public Vec2 pos, prevPos, nextPos;
    public float mass;
    public boolean pin = false;

    public PointMass2D(Vec2 pos, float mass) {
        this.prevPos = this.pos = pos;
        this.mass = mass;
    }

    public void updatePhysics() {
        Vec2 velocity = pos.sub(prevPos);

        nextPos = pos.add(velocity);
        prevPos = pos;
        pos = nextPos;


        // dampen velocity
        velocity.mul(0.99);


        // calculate the next position using Verlet Integration
        Vec2 nextPos = pos.add(velocity).add(0.5, 0.5);

        // reset variables
        prevPos = pos;
        pos = nextPos;
    }
}
