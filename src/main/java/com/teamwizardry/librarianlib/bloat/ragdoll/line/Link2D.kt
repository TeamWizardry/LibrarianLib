package com.teamwizardry.librarianlib.bloat.ragdoll.line

/**
 * Created by Saad on 12/7/2016.
 */
class Link2D {

    var a: PointMass2D
    var b: PointMass2D
    var restingDistance: Float = 0.toFloat()
    var strength: Float = 0.toFloat()
    var pinA: Boolean = false
    var pinB: Boolean = false

    constructor(a: PointMass2D, b: PointMass2D, strength: Float) {
        this.a = a
        this.b = b
        this.strength = strength
        this.restingDistance = a.pos.sub(b.pos).length().toFloat()
    }

    constructor(a: PointMass2D, b: PointMass2D, restingDistance: Float, strength: Float) {
        this.a = a
        this.b = b
        this.strength = strength
        this.restingDistance = restingDistance
    }

    fun pinA(): Link2D {
        pinA = true
        return this
    }

    fun pinB(): Link2D {
        pinB = true
        return this
    }

    fun resolve() {
        if (a.pin && b.pin) return

        val posDiff = a.pos.sub(b.pos)
        val d = posDiff.length()

        var difference = (restingDistance - d) / d
        difference *= strength.toDouble()
        if (!(a.pin || pinA) && !(b.pin || pinB))
        // neither are pinned
            difference = difference / 2.0

        val translate = posDiff.mul(difference)

        if (!(a.pin || pinA)) a.pos = a.pos.add(translate)

        if (!(b.pin || pinB)) b.pos = b.pos.sub(translate)
    }
}
