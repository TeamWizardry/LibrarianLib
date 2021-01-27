package com.teamwizardry.librarianlib.facade.layer

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix3dStack
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.MutableMatrix4d

public class GuiDrawContext(
    rootStack: MatrixStack,
    public val matrix: Matrix3dStack,
    public val debugOptions: FacadeDebugOptions,
    isInMask: Boolean
) {
    public var isInMask: Boolean = isInMask
        @JvmSynthetic
        internal set

    private val rootTransform = Matrix4d(rootStack.last.matrix)
    private val combinedTransform = MutableMatrix4d()
    private val normal = Matrix3d(rootStack.last.normal) // this won't change, since our transforms are 2d
    private val managedStack = MatrixStack()

    /**
     * The rendering transform matrix. A combination of the root GUI transform and the [matrix] transform.
     *
     * **Note:**
     * This property reuses the matrix instance, so its value is only stable until the next time [transform] is
     * accessed. If this property is going to be used repeatedly it should be saved in a local variable to avoid
     * recomputing the matrix every time.
     */
    public val transform: Matrix4d
        get() {
            combinedTransform.set(rootTransform)
            combinedTransform.mul(
                matrix.m00, matrix.m01, 0.0, matrix.m02,
                matrix.m10, matrix.m11, 0.0, matrix.m12,
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 1.0
            )

            return combinedTransform
        }

    /**
     * A [MatrixStack] for use in Minecraft rendering APIs.
     *
     * **Note:**
     * The returned matrix will be overwritten each time this property is accessed, so this property requires the same
     * precautions as [transform].
     */
    public val transformStack: MatrixStack
        get() {
            transform.copyToMatrix4f(managedStack.last.matrix)
            // at the moment we only do 3d transforms, so the normal is always the same
            normal.copyToMatrix3f(managedStack.last.normal)
            return managedStack
        }

    private var glMatrixPushed = false

    /**
     * Pushes the current matrix to the GL transform. This matrix can be popped using [popGlMatrix] or, if it isn't, it
     * will be popped after the layer is drawn. Calling this multiple times will not push the matrix multiple times.
     */
    @Suppress("CAST_NEVER_SUCCEEDS")
    public fun pushGlMatrix() {
        if (glMatrixPushed) return
        glMatrixPushed = true
        RenderSystem.pushMatrix()
        RenderSystem.multMatrix(create3dTransform(matrix).toMatrix4f())
    }

    /**
     * Pops the matrix pushed by [pushGlMatrix], if it has been pushed.
     */
    public fun popGlMatrix() {
        if (!glMatrixPushed) return
        glMatrixPushed = false
        RenderSystem.popMatrix()
    }

    private fun create3dTransform(m: Matrix3d): Matrix4d {
        val m4d = MutableMatrix4d()
        m4d.m00 = m.m00
        m4d.m10 = m.m10
        m4d.m01 = m.m01
        m4d.m11 = m.m11
        m4d.m03 = m.m02
        m4d.m13 = m.m12
        return m4d
    }
}
