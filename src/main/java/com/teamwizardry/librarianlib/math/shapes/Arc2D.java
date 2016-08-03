package com.teamwizardry.librarianlib.math.shapes;

import com.teamwizardry.librarianlib.math.Vec2d;

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
    private Vec2d origin, target;

    public Arc2D(Vec2d origin, Vec2d target, float height, int particleCount) {
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
    public ArrayList<Vec2d> getPoints() {
        ArrayList<Vec2d> locs = new ArrayList<>();
        Vec2d link = target.sub(origin);
        float length = (float) link.length();
        float pitch = (float) (4 * height / Math.pow(length, 2));
        for (int i = 0; i < particles; i++) {
            Vec2d tmp = new Vec2d(link.x, link.y).normalize();
            Vec2d v = new Vec2d(tmp.x * length * i / particles, tmp.y * length * i / particles);
            float x = ((float) i / particles) * length - length / 2;
            float y = (float) (-pitch * Math.pow(x, 2) + height);
            locs.add(origin.add(v).add(new Vec2d(0, y)));
        }
        return locs;
    }
}