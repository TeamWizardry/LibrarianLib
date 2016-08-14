package com.teamwizardry.librarianlib.math

import net.minecraft.util.math.Vec3d

object Geometry {
    fun getNormal(a: Vec3d, b: Vec3d, c: Vec3d): Vec3d {
        val edge1 = a.subtract(b)
        val edge2 = b.subtract(c)
        val cross = edge1.crossProduct(edge2)
        val normal = cross.normalize()
        return normal
    }
}
