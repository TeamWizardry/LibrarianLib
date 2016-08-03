package com.teamwizardry.librarianlib.math.shapes;

import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

/**
 * Created by Saad on 13/7/2016.
 */
public class BezierCurve3D implements IShape3D {

    /**
     * Point 1 is the origin, point 2 is the target position
     */
    private Vec3d point1, point2;

    /**
     * These will do the actual curves
     */
    private Vec3d controlPoint1, controlPoint2;

    /**
     * Numer of points on the line
     */
    private int pointCount = 50;

    private ArrayList<Vec3d> points;

    public BezierCurve3D(Vec3d point1, Vec3d point2, int pointCount) {
        this.point1 = point1;
        this.point2 = point2;
        this.pointCount = pointCount;
        points = new ArrayList<>();

        Vec3d midpoint = point1.subtract(point2).scale(1.0 / 2.0);

        controlPoint1 = point1.subtract(midpoint.xCoord, 0, midpoint.zCoord);
        controlPoint2 = point2.add(new Vec3d(midpoint.xCoord, 0, midpoint.zCoord));

        // FORMULA: B(t) = (1-t)**3 p0 + 3(1 - t)**2 t P1 + 3(1-t)t**2 P2 + t**3 P3
        for (float i = 0; i < 1; i += 1 / pointCount) {
            double x = (1 - i) * (1 - i) * (1 - i) * point1.xCoord + 3 * (1 - i) * (1 - i) * i * controlPoint1.xCoord + 3 * (1 - i) * i * i * controlPoint2.xCoord + i * i * i * point2.xCoord;
            double y = (1 - i) * (1 - i) * (1 - i) * point1.yCoord + 3 * (1 - i) * (1 - i) * i * controlPoint1.yCoord + 3 * (1 - i) * i * i * controlPoint2.yCoord + i * i * i * point2.yCoord;
            double z = (1 - i) * (1 - i) * (1 - i) * point1.zCoord + 3 * (1 - i) * (1 - i) * i * controlPoint1.zCoord + 3 * (1 - i) * i * i * controlPoint2.zCoord + i * i * i * point2.zCoord;
            points.add(new Vec3d(x, y, z));
        }
    }

    /**
     * Will return a list of points in order that define every point of the shape
     *
     * @return Will return the list of points required
     */
    @Override
    public ArrayList<Vec3d> getPoints() {
        return points;
    }
}
