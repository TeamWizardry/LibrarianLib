package com.teamwizardry.librarianlib.math.shapes

import net.minecraft.util.math.Vec3d
import java.util.*

/**
 * Created by Saad on 2/7/2016.
 */
interface IShape3D {

    /**
     * Will return a list of points in order that define every point of the shape

     * @return Will return the list of points required
     */
    val points: ArrayList<Vec3d>
}
