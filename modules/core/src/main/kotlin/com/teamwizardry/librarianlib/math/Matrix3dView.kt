package com.teamwizardry.librarianlib.math

/**
 * A read-only view into a potentially mutable matrix
 */
class Matrix3dView(var target: Matrix3d): Matrix3d() {
    override var m00: Double
        get() = target.m00
        set(value) {}
    override var m01: Double
        get() = target.m01
        set(value) {}
    override var m02: Double
        get() = target.m02
        set(value) {}
    override var m10: Double
        get() = target.m10
        set(value) {}
    override var m11: Double
        get() = target.m11
        set(value) {}
    override var m12: Double
        get() = target.m12
        set(value) {}
    override var m20: Double
        get() = target.m20
        set(value) {}
    override var m21: Double
        get() = target.m21
        set(value) {}
    override var m22: Double
        get() = target.m22
        set(value) {}

    override fun toImmutable(): Matrix3d {
        return Matrix3d(this)
    }
}