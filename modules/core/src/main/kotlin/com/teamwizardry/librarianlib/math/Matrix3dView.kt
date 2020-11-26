package com.teamwizardry.librarianlib.math

/**
 * A read-only view into a potentially mutable matrix
 */
public class Matrix3dView(public var target: Matrix3d): Matrix3d() {
    override val m00: Double get() = target.m00
    override val m01: Double get() = target.m01
    override val m02: Double get() = target.m02

    override val m10: Double get() = target.m10
    override val m11: Double get() = target.m11
    override val m12: Double get() = target.m12

    override val m20: Double get() = target.m20
    override val m21: Double get() = target.m21
    override val m22: Double get() = target.m22

    override fun toImmutable(): Matrix3d {
        return Matrix3d(this)
    }
}