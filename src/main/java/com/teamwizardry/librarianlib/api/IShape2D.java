package com.teamwizardry.librarianlib.api;

import com.teamwizardry.librarianlib.math.Vec2;

import java.util.ArrayList;

/**
 * Created by Saad on 2/7/2016.
 */
public interface IShape2D {

    /**
     * Will return a list of points in order that define every point of the helix
     *
     * @return Will return the list of points required
     */
    ArrayList<Vec2> getPoints();
}
