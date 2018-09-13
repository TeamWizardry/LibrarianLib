package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.value.RMValue
import com.teamwizardry.librarianlib.features.gui.value.RMValueDouble
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.kotlin.unaryMinus
import com.teamwizardry.librarianlib.features.math.Matrix4
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.GlStateManager

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
    /**
     * The translation component of the transform
     */
    var translate by RMValue(vec(0, 0))

    /**
     * The Z translation component of the transform
     */
    var translateZ by RMValueDouble(0.0)

    /**
     * The rotation component of the transform in radians
     */
    var rotate by RMValueDouble(0.0)

    /**
     * The scale component of the transform
     */
    var scale2D by RMValue(vec(1, 1))

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

    /**
     * The point to rotate and scale about. Applied after translation
     */
    var anchor by RMValue(vec(0, 0))

    internal var size by RMValue(vec(0, 0))

    /**
     * Create a [Matrix4] containing this transform
     */
    fun matrix(): Matrix4 {
        val mat = Matrix4()
        apply(mat)
        return mat
    }

    /**
     * Applies this transform to the passed matrix
     */
    fun apply(other: Matrix4) {
        other.translate(vec(translate.x, translate.y, translateZ))
        other.rotate(rotate, vec(0, 0, 1))

        other.scale(vec(scale2D.x, scale2D.y, 1))
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
        GlStateManager.translate(translate.x, translate.y, translateZ)
        GlStateManager.rotate(Math.toDegrees(rotate).toFloat(), 0f, 0f, 1f)
        GlStateManager.scale(scale2D.x, scale2D.y, 1.0)
        GlStateManager.translate(-anchor.x * size.x, -anchor.y * size.y, 0.0)
    }
}
