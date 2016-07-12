package com.teamwizardry.librarianlib.math.shapes;

import com.teamwizardry.librarianlib.api.IShape2D;
import com.teamwizardry.librarianlib.math.Vec2;

import java.util.ArrayList;

/**
 * Created by Saad on 12/7/2016.
 */
public class Arc2D implements IShape2D {

    /**
     * Height of the arc in blocks
     */
    private float height = 2;

    /**
     * Particles per arc
     */
    private int particles = 100;

    /**
     * The two points the arc will connect from and to
     */
    private Vec2 origin, target;

    public Arc2D(Vec2 origin, Vec2 target, float height, int particleCount) {
        this.height = height;
        this.particles = particleCount;
        this.origin = origin;
        this.target = target;
    }

    /**
     * Will return a list of points in order that define every point of the arc
     *
     * @return Will return the list of points required
     */
    @Override
    public ArrayList<Vec2> getPoints() {
        ArrayList<Vec2> locs = new ArrayList<>();
        Vec2 link = target.sub(origin);
        float length = (float) link.length();
        float pitch = (float) (4 * height / Math.pow(length, 2));
        for (int i = 0; i < particles; i++) {
            Vec2 tmp = new Vec2(link.x, link.y).normalize();
            Vec2 v = new Vec2(tmp.x * length * i / particles, tmp.y * length * i / particles);
            float x = ((float) i / particles) * length - length / 2;
            float y = (float) (-pitch * Math.pow(x, 2) + height);
            locs.add(origin.add(v).add(new Vec2(0, y)));
        }
        return locs;
    }
}