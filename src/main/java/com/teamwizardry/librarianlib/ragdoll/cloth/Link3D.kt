package com.teamwizardry.librarianlib.ragdoll.cloth

import net.minecraft.util.math.Vec3d

open class Link3D {
    var a: PointMass3D
    var b: PointMass3D
    var distance: Float = 0.toFloat()
    var strength: Float = 0.toFloat()

    constructor(a: PointMass3D, b: PointMass3D, strength: Float) {
        this.a = a
        this.b = b
        this.strength = strength
        this.distance = a.pos.subtract(b.pos).lengthVector().toFloat()
    }

    constructor(a: PointMass3D, b: PointMass3D, distance: Float, strength: Float) {
        this.a = a
        this.b = b
        this.strength = strength
        this.distance = distance
    }

    open fun resolve() {
        if (a.pin && b.pin)
            return

        val posDiff = a.pos.subtract(b.pos)
        val d = posDiff.lengthVector()

        var difference = (distance - d) / d
        difference *= strength.toDouble()
        if (!a.pin && !b.pin)
        // neither are pinned
            difference = difference / 2.0

        val translate = posDiff.scale(difference)

        if (!a.pin) {
            a.pos = a.pos.add(translate)
        }

        if (!b.pin) {
            b.pos = b.pos.subtract(translate)
        }
    }
}
