package com.teamwizardry.librarianlib.math.shapes;

import com.teamwizardry.librarianlib.api.IShape;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

/**
 * Created by Saad on 5/7/2016.
 */
public class Arc3D implements IShape {

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
    private Vec3d origin, target;

    public Arc3D(Vec3d origin, Vec3d target, float height, int particleCount) {
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
    public ArrayList<Vec3d> getPoints() {
        ArrayList<Vec3d> locs = new ArrayList<>();
        Vec3d link = target.subtract(origin);
        float length = (float) link.lengthVector();
        float pitch = (float) (4 * height / Math.pow(length, 2));
        for (int i = 0; i < particles; i++) {
            Vec3d tmp = new Vec3d(link.xCoord, link.yCoord, link.zCoord).normalize();
            Vec3d v = new Vec3d(tmp.xCoord * length * i / particles, tmp.yCoord * length * i / particles, tmp.zCoord * length * i / particles);
            float x = ((float) i / particles) * length - length / 2;
            float y = (float) (-pitch * Math.pow(x, 2) + height);
            locs.add(origin.add(v).add(new Vec3d(0, y, 0)));
        }
        return locs;
    }
}
