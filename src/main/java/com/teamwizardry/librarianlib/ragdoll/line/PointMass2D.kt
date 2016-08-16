package com.teamwizardry.librarianlib.ragdoll.line

import com.teamwizardry.librarianlib.math.Vec2d

/**
 * Created by Saad on 12/7/2016.
 */
class PointMass2D(pos: Vec2d, var mass: Float) {

    var pos: Vec2d = pos
    var prevPos: Vec2d = pos
    var nextPos: Vec2d? = null
    var pin = false
}
