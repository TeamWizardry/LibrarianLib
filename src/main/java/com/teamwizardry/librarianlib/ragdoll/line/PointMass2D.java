package com.teamwizardry.librarianlib.ragdoll.line;

import com.teamwizardry.librarianlib.math.Vec2d;

/**
 * Created by Saad on 12/7/2016.
 */
public class PointMass2D {

    public Vec2d pos, prevPos, nextPos;
    public float mass;
    public boolean pin = false;

    public PointMass2D(Vec2d pos, float mass) {
        this.prevPos = this.pos = pos;
        this.mass = mass;
    }
}
