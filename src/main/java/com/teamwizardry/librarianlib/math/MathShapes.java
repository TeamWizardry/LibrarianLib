package com.teamwizardry.librarianlib.math;

import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

/**
 * Created by Saad on 7/1/2016.
 */
public class MathShapes {

    public static ArrayList<Vec3d> createHelix(Vec3d location, int height, boolean reverse) {
        ArrayList<Vec3d> locs = new ArrayList<>();
        double radius = 0;

        for (double y = 0; y <= height; y += 0.01) {
            double x = radius * Math.cos(y * height);
            double z = radius * Math.sin(y * height);
            Vec3d newLoc = new Vec3d(location.xCoord + x, location.yCoord + y, location.zCoord + z);
            locs.add(newLoc);
            radius += 0.005;
        }
        return locs;
    }

    public static ArrayList<Vec3d> createReverseHelix(Vec3d location, int height) {
        ArrayList<Vec3d> locs = new ArrayList<>();
        double radius = 0;

        for (double y = 0; y <= height; y += 0.01) {
            double x = radius * Math.cos(y * 1 / height);
            double z = radius * Math.sin(y * 1 / height);
            Vec3d newLoc = new Vec3d(location.xCoord + x, location.yCoord + y, location.zCoord + z);
            locs.add(newLoc);
            radius += 0.005;
        }
        return locs;
    }

    public static ArrayList<Vec3d> createCircle(Vec3d center, double radius, int amount) {
        double increment = (2 * Math.PI) / amount;

        ArrayList<Vec3d> locations = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.xCoord + (radius * Math.cos(angle));
            double z = center.zCoord + (radius * Math.sin(angle));
            locations.add(new Vec3d(x, center.yCoord, z));
        }
        return locations;
    }
}
