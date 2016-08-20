package com.teamwizardry.librarianlib.bloat.ragdoll

import net.minecraft.util.math.Vec3d

class Sphere(internal var pos:

             Vec3d, internal var radius: Double) {

    fun trace(start: Vec3d, end: Vec3d): Vec3d {

        val u = end.subtract(start)
        val v = pos.subtract(start)
        val b = 2 * v.dotProduct(u)
        val c = v.dotProduct(v) - radius * radius
        val discriminant = b * b - 4 * c

        if (discriminant < 0) return end

        val tMinus = (-b - Math.sqrt(discriminant)) / 2
        val tPlus = (-b + Math.sqrt(discriminant)) / 2

        if (tMinus < 0 && tPlus < 0) {
            // sphere is behind the ray
            return end
        }

        val tValue: Double
        val normal: Vec3d
        val intersection: Vec3d
        val incoming: Boolean
        if (tMinus < 0 && tPlus > 0) {
            // ray origin lies inside the sphere. take tPlus
            tValue = tPlus
            //			return null;
            intersection = start.add(u.scale(tValue))
            normal = pos.subtract(intersection)
            incoming = false

            return end // ignore outgoing intersections
        } else {
            // both roots positive. take tMinus
            tValue = tMinus
            intersection = start.add(u.scale(tValue))
            normal = intersection.subtract(pos)
            incoming = true
        }

        return intersection
    }

    fun fix(vec: Vec3d): Vec3d {
        val dist = vec.subtract(pos)
        if (dist.lengthVector() < radius) {
            return pos.add(dist.normalize().scale(radius + 0.05))
        }
        return vec
    }

}
