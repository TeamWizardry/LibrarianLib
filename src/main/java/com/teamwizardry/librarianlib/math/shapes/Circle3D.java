package com.teamwizardry.librarianlib.math.shapes;

import com.teamwizardry.librarianlib.math.Matrix4;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

/**
 * Created by Saad on 16/7/2016.
 */
public class Circle3D implements IShape3D {

    /**
     * Particles per arc
     */
    private int particles = 100;

    /**
     * The two points the arc will connect from and to
     */
    private Vec3d origin;

    /**
     * The radius of the circle
     */
    private double radius;

    /**
     * Orientation of the circle
     */
    private float pitch, yaw;

    private double theta = 0;

    public Circle3D(Vec3d origin, double radius, int particleCount) {
        this.particles = particleCount;
        this.origin = origin;
        this.radius = radius;
        pitch = 0;
        yaw = 0;
    }

    public Circle3D(Vec3d origin, double radius, int particleCount, float pitch, float yaw) {
        this.particles = particleCount;
        this.origin = origin;
        this.radius = radius;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    /**
     * Will return a list of points in order that define every point of the shape
     *
     * @return Will return the list of points required
     */
    @Override
    public ArrayList<Vec3d> getPoints() {
        ArrayList<Vec3d> points = new ArrayList<>();
        for (int i = 0; i <= particles; i++) {
            double tempTheta = i * Math.toRadians(360.0 / particles);
            Matrix4 matrix = new Matrix4();
            matrix.rotate(pitch, new Vec3d(0, -1, 0));
            matrix.rotate(yaw, new Vec3d(1, 0, 0));
            double x = origin.xCoord + radius * Math.sin(tempTheta);
            double y = origin.yCoord;
            double z = origin.zCoord + radius * Math.cos(tempTheta);
            matrix.apply(new Vec3d(x, y, z));
            points.add(new Vec3d(x, y, z));
        }
        return points;
    }
}
