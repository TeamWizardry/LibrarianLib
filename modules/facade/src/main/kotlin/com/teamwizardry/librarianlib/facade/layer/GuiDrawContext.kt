package com.teamwizardry.librarianlib.facade.layer

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.mixinCast
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix3dStack
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.MutableMatrix4d
import net.minecraft.client.util.math.MatrixStack

public class GuiDrawContext(
    rootStack: MatrixStack,
    public val matrix: Matrix3dStack,
    public val debugOptions: FacadeDebugOptions,
    isInMask: Boolean
) {
    public var isInMask: Boolean = isInMask
        @JvmSynthetic
        internal set

    private val rootTransform = Matrix4d(rootStack.peek().model)
    private val combinedTransform = MutableMatrix4d()
    private val normal = Matrix3d(rootStack.peek().normal) // this won't change, since our transforms are 2d
    private val managedStack = MatrixStack()
    private var lastMatrixVersion = -1

    /**
     * The rendering transform matrix. A combination of the root GUI transform and the [matrix] transform.
     *
     * **Note:**
     * This property reuses the matrix instance, so its value is only stable until the next time [transform] is
     * accessed.
     */
    public val transform: Matrix4d
        get() {
            if(matrix.mutationCount != lastMatrixVersion) {
                combinedTransform.set(rootTransform)
                combinedTransform.mul(
                    matrix.m00, matrix.m01, 0.0, matrix.m02,
                    matrix.m10, matrix.m11, 0.0, matrix.m12,
                    0.0, 0.0, 1.0, 0.0,
                    0.0, 0.0, 0.0, 1.0
                )
                lastMatrixVersion = matrix.mutationCount
            }

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
            transform.copyToMatrix4f(managedStack.peek().model)
            // at the moment we only do 3d transforms, so the normal is always the same
            normal.copyToMatrix3f(managedStack.peek().normal)
            return managedStack
        }

    private var glMatrixPushed = false

    /**
     * Pushes the current matrix to RenderSystem model-view matrix. This matrix can be popped using
     * [popModelViewMatrix] or, if it isn't, it will be popped after the layer is drawn. Calling this multiple times
     * will update the pushed matrix, not push the matrix multiple times.
     *
     * This doesn't update the OpenGL transform matrix. To do that, call [RenderSystem.applyModelViewMatrix]
     */
    @Suppress("CAST_NEVER_SUCCEEDS")
    public fun pushModelViewMatrix() {
        if (glMatrixPushed) {
            RenderSystem.getModelViewStack().pop()
        }
        RenderSystem.getModelViewStack().push()
        RenderSystem.getModelViewStack().peek().model.multiply(transformStack.peek().model)
        RenderSystem.applyModelViewMatrix()
        glMatrixPushed = true
    }

    /**
     * Pops the matrix pushed by [pushModelViewMatrix], if it has been pushed.
     */
    public fun popModelViewMatrix() {
        if (!glMatrixPushed) return
        glMatrixPushed = false
        RenderSystem.getModelViewStack().pop()
        RenderSystem.applyModelViewMatrix()
    }
}
