package com.teamwizardry.librarianlib.gui.component.supporting

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.gui.value.RMValue
import com.teamwizardry.librarianlib.gui.value.RMValueDouble
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.Quaternion
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec

/**
 * A container for all of a component's transformation options.
 *
 * The transforms are applied in the following order:
 *
 * - translate & translateZ
 * - anchor
 * - rotate
 * - scale
 * - anchor
 * - postTranslate
 */
class ComponentTransform {
    val translate_rm: RMValue<Vec2d> = RMValue(vec(0, 0))
    /**
     * The translation component of the transform
     */
    var translate by translate_rm

    var translateZ_rm: RMValueDouble = RMValueDouble(0.0)
    /**
     * The Z translation component of the transform
     */
    var translateZ by translateZ_rm

    var rotate_rm: RMValueDouble = RMValueDouble(0.0)
    /**
     * The rotation component of the transform in radians
     */
    var rotate: Double by rotate_rm

    val scale2D_rm: RMValue<Vec2d> = RMValue(vec(1, 1))
    /**
     * The scale component of the transform
     */
    var scale2D: Vec2d by scale2D_rm

    /**
     * Get and set the uniform scale of the transformation.
     *
     * If scale2D's components are not equal, this property returns the average of its X and Y components.
     */
    var scale: Double
        get() = (scale2D.x + scale2D.y) / 2
        set(value) {
            scale2D = vec(value, value)
        }

    val anchor_rm: RMValue<Vec2d> = RMValue(vec(0, 0))
    /**
     * The point to rotate and scale about. Applied after translation
     */
    var anchor: Vec2d by anchor_rm

    internal val size_rm: RMValue<Vec2d> = RMValue(vec(0, 0))
    internal var size: Vec2d by size_rm

    /**
     * Create a [Matrix4d] containing this transform
     */
    fun matrix(): Matrix4d {
        val mat = Matrix4d()
        apply(mat)
        return mat
    }

    /**
     * Applies this transform to the passed matrix
     */
    fun apply(other: Matrix4d) {
        other.translate(vec(translate.x, translate.y, translateZ))
        other.rotate(Quaternion.fromAngleRadAxis(rotate, vec(0, 0, 1))) // todo other #1

        other.scale(scale2D.x, scale2D.y, 1.0, 1.0) // todo other #1
        other.translate(vec(-anchor.x * size.x, -anchor.y * size.y, 0))
    }

    /**
     * Applies this transform to the passed vector and returns the new vector
     */
    fun apply(other: Vec2d): Vec2d {
        var vec = other
        vec += translate
        vec *= scale2D
        vec = vec.rotate(rotate.toFloat())
        vec -= anchor * size
        return vec
    }

    /**
     * Applies the inverse of this transform to the passed vector and returns the new vector
     */
    fun applyInverse(other: Vec2d): Vec2d {
        var vec = other
        vec -= translate
        vec += anchor * size
        vec = vec.rotate((-rotate).toFloat())
        vec /= scale2D
        return vec
    }

    /**
     * Applies this transform to the current GL state
     */
    fun glApply() {
        GlStateManager.translated(translate.x, translate.y, translateZ)
        GlStateManager.rotated(Math.toDegrees(rotate), 0.0, 0.0, 1.0)
        GlStateManager.scaled(scale2D.x, scale2D.y, 1.0)
        GlStateManager.translated(-anchor.x * size.x, -anchor.y * size.y, 0.0)
    }
}
