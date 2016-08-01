package com.teamwizardry.librarianlib.math.shapes;

import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Saad on 2/7/2016.
 */
public class Helix implements IShape3D {

    /**
     * Amount of strands
     */
    private int strands = 1;

    /**
     * Points per strand
     */
    private int points = 30;

    /**
     * Radius of helix
     */
    private float radius = 5;

    /**
     * Max height a strand will reach
     */
    private float height = 5;

    /**
     * Factor for the curves. Negative values reverse rotation.
     */
    private float curve = 10;

    /**
     * Rotation of the helix (Fraction of PI)
     */
    private double rotation = Math.PI / 4;

    /**
     * Will reverse the y axis of the helix
     */
    private boolean reverse = false;

    /**
     * Will reduce the radius incrementally
     */
    private boolean shrink = true;

    /**
     * Center location of the helix
     */
    private Vec3d center;

    /**
     * Use this for a helix that shrinks/expands
     *
     * @param center  Center point of the helix
     * @param points  Amount of points to use per strand
     * @param radius  The maximum radius of the helix. starts from 0 because it shrinks/expands
     * @param height  Defines the maximum height for the helix to reach
     * @param strands The number of strands that the helix will have
     * @param curve   The stiffness of the curvature per strand
     * @param reverse Will draw the helix upside down starting from a fat bottom to a peak and vice versa
     */
    public Helix(Vec3d center, int points, float radius, float height, int strands, float curve, boolean reverse) {
        this.center = center;
        this.points = points;
        this.radius = radius;
        this.strands = strands;
        this.height = height;
        this.curve = curve;
        this.reverse = reverse;
        this.shrink = true;
    }

    /**
     * Use this for a helix that does not shrink nor expand. More like a candy cane pipe
     * @param center Center point of the helix
     * @param points Amount of points to use per strand
     * @param radius The maximum radius of the helix. starts from 0 because it shrinks/expands
     * @param height Defines the maximum height for the helix to reach
     * @param strands The number of strands that the helix will have
     * @param rotation Will define the amount of rotations the helix will assume. Higher values will increase the amount of spins
     */
    public Helix(Vec3d center, int points, float radius, float height, int strands, double rotation) {
        this.center = center;
        this.points = points;
        this.radius = radius;
        this.strands = strands;
        this.height = height;
        this.rotation = rotation;
        this.shrink = false;
    }

    @Override
    public ArrayList<Vec3d> getPoints() {
        ArrayList<Vec3d> locs = new ArrayList<>();
        for (int strand = 1; strand <= strands; strand++) {
            if (shrink) {
                double y = 0;
                if (reverse) y = height;
                for (int point = 1; point <= points; point++) {
                    float ratio = (float) point / points;
                    double angle = curve * ratio * 2 * Math.PI / strands + (2 * Math.PI * strand / strands) + rotation;
                    double x = Math.cos(angle) * ratio * radius;
                    double z = Math.sin(angle) * ratio * radius;
                    if (reverse) y -= (center.yCoord - center.yCoord + height) / points;
                    else y += (center.yCoord - center.yCoord + height) / points;
                    locs.add(center.add(new Vec3d(x, y, z)));
                    //locs.add(center.subtract(new Vec3d(x, y, z)));
                }
            } else {
                for (double y = 0; y < height; y += (center.yCoord - center.yCoord + height) / points) {
                    double x = radius * Math.cos(y * rotation);
                    double z = radius * Math.sin(y * rotation);
                    locs.add(center.add(new Vec3d(x, y, z)));
                    //locs.add(center.subtract(new Vec3d(x, y, z)));
                }
            }
        }
        if (reverse) Collections.reverse(locs);
        return locs;
    }

    public ArrayList<ArrayList<Vec3d>> getMultipleStrandPoints() {
        ArrayList<ArrayList<Vec3d>> locs = new ArrayList<>();
        for (int strand = 1; strand <= strands; strand++) {
            ArrayList<Vec3d> strandPoints = new ArrayList<>();
            if (shrink) {
                double y = 0;
                if (reverse) y = height;
                for (int point = 1; point <= points; point++) {
                    float ratio = (float) point / points;
                    double angle = curve * ratio * 2 * Math.PI / strands + (2 * Math.PI * strand / strands) + rotation;
                    double x = Math.cos(angle) * ratio * radius;
                    double z = Math.sin(angle) * ratio * radius;
                    if (reverse) y -= (center.yCoord - center.yCoord + height) / points;
                    else y += (center.yCoord - center.yCoord + height) / points;
                    strandPoints.add(center.add(new Vec3d(x, y, z)));
                }
            } else {
                for (double y = 0; y < height; y += (center.yCoord - center.yCoord + height) / points) {
                    double x = radius * Math.cos(y * rotation);
                    double z = radius * Math.sin(y * rotation);
                    strandPoints.add(center.add(new Vec3d(x, y, z)));
                }
            }
            locs.add(strandPoints);
        }
        if (reverse) Collections.reverse(locs);
        return locs;
    }
}
