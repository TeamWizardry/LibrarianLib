package com.teamwizardry.librarianlib.common.util.math

import net.minecraft.util.math.Vec3d

class Tri(internal var v1:

          Vec3d, internal var v2: Vec3d, internal var v3: Vec3d) {

    /**
     * Traces this tri and returns a new end position.

     * Returns [end] if there wasn't a hit
     */
    fun trace(start: Vec3d, end: Vec3d): Vec3d {
        var v1 = this.v1
        var v2 = this.v2
        var v3 = this.v3

        val intersect: Vec3d
        val u: Vec3d
        val v: Vec3d
        val n: Vec3d              // triangle vectors
        var dir: Vec3d
        var w0: Vec3d
        val w: Vec3d           // ray vectors
        val r: Double
        val a: Double
        val b: Double              // params to calc ray-plane intersect

        // get triangle edge vectors and plane normal
        u = v2.subtract(v1)
        v = v3.subtract(v1)
        n = u.crossProduct(v)              // cross product
        if (n == Vec3d.ZERO)
        // triangle is degenerate
            return end                   // do not deal with this case

        dir = end.subtract(start)             // ray direction vector
        start.subtract(dir.normalize().scale(0.25))
        dir = end.subtract(start)             // ray direction vector
        w0 = start
        w0 = w0.subtract(v1)
        a = -n.dotProduct(w0)
        b = n.dotProduct(dir)
        if (Math.abs(b) < SMALL_NUM) {     // ray is  parallel to triangle plane
            if (a == 0.0)
            // ray lies in triangle plane
                return end
            else
                return end              // ray disjoint from plane
        }

        // get intersect point of ray with triangle plane
        r = a / b
        if (r < 0.0)
        // ray goes away from triangle
            return end                   // => no intersect
        if (r > 1.0)
        // ray doesn't reach triangle
            return end                   // => no intersect

        intersect = start.add(dir.scale(r))            // intersect point of ray and plane

        var angles = 0f

        v1 = intersect.subtract(this.v1).normalize()
        v2 = intersect.subtract(this.v2).normalize()
        v3 = intersect.subtract(this.v3).normalize()

        angles += Math.acos(v1.dotProduct(v2)).toFloat()
        angles += Math.acos(v2.dotProduct(v3)).toFloat()
        angles += Math.acos(v3.dotProduct(v1)).toFloat()

        if (Math.abs(angles - 2 * Math.PI) > 0.005)
            return end

        return intersect
    }

    fun clone(): Tri {
        return Tri(v1, v2, v3)
    }

    fun translate(vec: Vec3d) {
        v1 = v1.add(vec)
        v2 = v2.add(vec)
        v3 = v3.add(vec)
    }

    fun apply(matrix: Matrix4) {
        v1 = matrix.apply(v1)
        v2 = matrix.apply(v2)
        v3 = matrix.apply(v3)
    }

    companion object {

        private val SMALL_NUM = 0.00000001
    }

}
