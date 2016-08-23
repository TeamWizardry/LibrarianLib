package com.teamwizardry.librarianlib.bloat.ragdoll.line

import com.teamwizardry.librarianlib.common.util.math.Vec2d
import java.util.*

class Rope(var middles: Int, length: Double, stretch: Float, bend: Float, begin: Vec2d, end: Vec2d) {

    var start: PointMass2D
    var end: PointMass2D
    var points: Array<PointMass2D>
    var stretchLinks: MutableList<Link2D>
    var bendLinks: MutableList<Link2D>
    var solvePasses = 3

    init {
        val mass = 0.1f
        val offsetPer = end.sub(begin).mul(1.0 / (middles + 1))
        var currentPos = begin
        points = Array<PointMass2D>(middles + 2) { i ->
            var point = PointMass2D(currentPos, mass)
            currentPos = currentPos.add(offsetPer)
            point
        }

        this.start = points[0]
        this.end = points[points.size - 1]

        stretchLinks = ArrayList<Link2D>()
        bendLinks = ArrayList<Link2D>()
        for (i in points.indices) {
            if (i + 1 < points.size) {
                val link = Link2D(points[i], points[i + 1], 1.0f - stretch)
                //				link.pinA = i < (points.length/2-1);
                //				link.pinB = i > points.length/2;;
                stretchLinks.add(link)
            }

            if (i + 2 < points.size)
                bendLinks.add(Link2D(points[i], points[i + 2], 1.0f - bend))
        }
    }

    fun tick() {
        val gravity = Vec2d(0.0, 1.0)

        for (point in points) {
            if (point.pin)
                continue
            point.pos = point.pos.add(gravity).add(point.pos.sub(point.prevPos))
        }

        for (i in 0..solvePasses - 1) {
            for (link in stretchLinks) {
                link.resolve()
            }
            for (link in bendLinks) {
                link.resolve()
            }
        }

        for (point in points) {
            point.prevPos = point.pos
        }
    }

}
