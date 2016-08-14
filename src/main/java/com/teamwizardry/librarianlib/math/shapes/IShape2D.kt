package com.teamwizardry.librarianlib.math.shapes

import com.teamwizardry.librarianlib.math.Vec2d

import java.util.ArrayList

/**
 * Created by Saad on 2/7/2016.
 */
interface IShape2D {

    /**
     * Will return a list of points in order that define every point of the helix

     * @return Will return the list of points required
     */
    val points: ArrayList<Vec2d>
}
