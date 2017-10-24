package com.teamwizardry.librarianlib.features.math

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.*
import net.minecraft.client.renderer.GlStateManager

/**
 * A simple 2d transformation consisting of a translation, rotation, and scale, in that order.
 */
class Transform2d {
    /**
     * The translation component of the transform
     */
    var translate = vec(0, 0)

    /**
     * The rotation component of the transform
     */
    var rotate = 0.0

    /**
     * The scale component of the transform
     */
    var scale2d = vec(1, 1)

    /**
     * Get and set the uniform scale of the transformation.
     *
     * If scale2d's components are not equal, this property returns the average of both components.
     */
    var scale: Double
        get() = (scale2d.x + scale2d.y)/2
        set(value) { scale2d = vec(value, value) }

    /**
     * Create a [Matrix3] containing this transform
     */
    fun matrix(): Matrix3 {
        return apply(Matrix3())
    }

    /**
     * Create a [Matrix3] containing the inverse of this transform
     */
    fun inverseMatrix(): Matrix3 {
        return applyInverse(Matrix3())
    }

    /**
     * Applies this transform to the passed matrix and returns the new matrix
     */
    fun apply(other: Matrix3): Matrix3 {
        return other.translate(translate).rotate(rotate).scale(scale2d)
    }

    /**
     * Applies the inverse of this transform to the passed matrix and returns the new matrix
     */
    fun applyInverse(other: Matrix3): Matrix3 {
        return other.scale(vec(1/scale2d.x, 1/scale2d.y)).rotate(-rotate).translate(-translate)
    }

    /**
     * Applies this transform to the passed vector and returns the new vector
     */
    fun apply(other: Vec2d): Vec2d {
        return (other + translate).rotate(rotate) * scale2d
    }

    /**
     * Applies the inverse of this transform to the passed vector and returns the new vector
     */
    fun applyInverse(other: Vec2d): Vec2d {
        return (other / scale2d).rotate(-rotate) - translate
    }

    /**
     * Applies this transform to the current GL state
     */
    fun glApply() {
        GlStateManager.translate(translate.x, translate.y, 0.0)
        GlStateManager.rotate(rotate.toFloat(), 0f, 0f, 1f)
        GlStateManager.scale(scale2d.x, scale2d.y, 1.0)
    }

    /**
     * Applies the inverse of this transform to the current GL state
     */
    fun glApplyInverse() {
        GlStateManager.scale(1/scale2d.x, 1/scale2d.y, 1.0)
        GlStateManager.rotate(-rotate.toFloat(), 0f, 0f, 1f)
        GlStateManager.translate(-translate.x, -translate.y, 0.0)
    }
}
