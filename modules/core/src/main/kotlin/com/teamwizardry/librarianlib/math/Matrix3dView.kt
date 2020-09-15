package com.teamwizardry.librarianlib.math

/**
 * A read-only view into a potentially mutable matrix
 */
public class Matrix3dView(public var target: Matrix3d): Matrix3d() {
    override var m00: Double
        get() = target.m00
        set(_) {}
    override var m01: Double
        get() = target.m01
        set(_) {}
    override var m02: Double
        get() = target.m02
        set(_) {}
    override var m10: Double
        get() = target.m10
        set(_) {}
    override var m11: Double
        get() = target.m11
        set(_) {}
    override var m12: Double
        get() = target.m12
        set(_) {}
    override var m20: Double
        get() = target.m20
        set(_) {}
    override var m21: Double
        get() = target.m21
        set(_) {}
    override var m22: Double
        get() = target.m22
        set(_) {}

    override fun toImmutable(): Matrix3d {
        return Matrix3d(this)
    }
}