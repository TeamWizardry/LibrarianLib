package com.teamwizardry.librarianlib.facade.layer

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix3dStack
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.MutableMatrix4d

public class GuiDrawContext(
    public val matrix: Matrix3dStack,
    showDebugBoundingBox: Boolean,
    isInMask: Boolean
) {
    public var showDebugBoundingBox: Boolean = showDebugBoundingBox
        @JvmSynthetic
        internal set
    public var isInMask: Boolean = isInMask
        @JvmSynthetic
        internal set

    private var glMatrix = false

    /**
     * Pushes the current matrix to the GL transform. This matrix can be popped using [popGlMatrix] or, if it isn't, it
     * will be popped after the component is drawn. Calling this multiple times will not push the matrix multiple times.
     */
    @Suppress("CAST_NEVER_SUCCEEDS")
    public fun pushGlMatrix() {
        if (glMatrix) return
        glMatrix = true
        RenderSystem.pushMatrix()
        RenderSystem.multMatrix(create3dTransform(matrix).toMatrix4f())
    }

    /**
     * Pops the matrix pushed by [pushGlMatrix], if it has been pushed.
     */
    public fun popGlMatrix() {
        if (!glMatrix) return
        glMatrix = false
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
