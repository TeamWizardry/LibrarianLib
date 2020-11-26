package com.teamwizardry.librarianlib.math

/**
 * A read-only view into a potentially mutable matrix
 */
public class Matrix4dView(public var target: Matrix4d): Matrix4d() {
    override val m00: Double get() = target.m00
    override val m01: Double get() = target.m01
    override val m02: Double get() = target.m02
    override val m03: Double get() = target.m03

    override val m10: Double get() = target.m10
    override val m11: Double get() = target.m11
    override val m12: Double get() = target.m12
    override val m13: Double get() = target.m13

    override val m20: Double get() = target.m20
    override val m21: Double get() = target.m21
    override val m22: Double get() = target.m22
    override val m23: Double get() = target.m23

    override val m30: Double get() = target.m30
    override val m31: Double get() = target.m31
    override val m32: Double get() = target.m32
    override val m33: Double get() = target.m33

    override fun toImmutable(): Matrix4d = Matrix4d(this)
}