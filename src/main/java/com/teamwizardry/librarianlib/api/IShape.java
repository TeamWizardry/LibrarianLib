package com.teamwizardry.librarianlib.api;

import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

/**
 * Created by Saad on 2/7/2016.
 */
public interface IShape {

    /**
     * Will return a list of points in order that define every point of the helix
     *
     * @return Will return the list of points required
     */
    ArrayList<Vec3d> getPoints();
}
