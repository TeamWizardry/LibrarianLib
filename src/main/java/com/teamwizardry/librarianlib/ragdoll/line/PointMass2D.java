package com.teamwizardry.librarianlib.ragdoll.line;

import com.teamwizardry.librarianlib.math.Vec2;

/**
 * Created by Saad on 12/7/2016.
 */
public class PointMass2D {

    public Vec2 pos, prevPos, origPos;
    public float mass;
    public boolean pin = false;

    public PointMass2D(Vec2 pos, float mass) {
        this.prevPos = this.pos = pos;
        this.mass = mass;
    }
}
