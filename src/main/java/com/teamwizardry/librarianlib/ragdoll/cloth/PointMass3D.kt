package com.teamwizardry.librarianlib.ragdoll.cloth

import net.minecraft.util.math.Vec3d

class PointMass3D(pos: Vec3d, var mass: Float) {
    var pos: Vec3d
    var prevPos: Vec3d
    var origPos: Vec3d? = null
    var friction: Vec3d? = null
    var pin = false

    init {
        this.prevPos = this.pos = pos
    }

    fun applyForce(force: Vec3d) {
        pos = pos.add(force.scale(1.0 / mass))
    }

    fun applyMotion(motion: Vec3d) {
        pos = pos.add(motion)
    }

    override fun toString(): String {
        return (if (pin) "[P]" else "") + pos.toString()
    }
}
