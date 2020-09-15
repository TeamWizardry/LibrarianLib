package com.teamwizardry.librarianlib.math

/**
 * A read-only view into a potentially mutable matrix
 */
public class Matrix4dView(public var target: Matrix4d): Matrix4d() {
    override var m00: Double
        get() = target.m00
        set(_) {}
    override var m01: Double
        get() = target.m01
        set(_) {}
    override var m02: Double
        get() = target.m02
        set(_) {}
    override var m03: Double
        get() = target.m03
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
    override var m13: Double
        get() = target.m13
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
    override var m23: Double
        get() = target.m23
        set(_) {}

    override var m30: Double
        get() = target.m30
        set(_) {}
    override var m31: Double
        get() = target.m31
        set(_) {}
    override var m32: Double
        get() = target.m32
        set(_) {}
    override var m33: Double
        get() = target.m33
        set(_) {}

    override fun toImmutable(): Matrix4d = Matrix4d(this)
}