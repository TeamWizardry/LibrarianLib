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
        if(glMatrix) return
        glMatrix = true
        RenderSystem.pushMatrix()
        RenderSystem.multMatrix(create3dTransform(matrix).toMatrix4f())
    }

    /**
     * Pops the matrix pushed by [pushGlMatrix], if it has been pushed.
     */
    public fun popGlMatrix() {
        if(!glMatrix) return
        glMatrix = false
        RenderSystem.popMatrix()
    }

    private fun create3dTransform(m: Matrix3d): Matrix4d {
        val m4d = MutableMatrix4d()
        m4d[0, 0] = m[0, 0]
        m4d[1, 0] = m[1, 0]
        m4d[0, 1] = m[0, 1]
        m4d[1, 1] = m[1, 1]
        m4d[0, 3] = m[0, 2]
        m4d[1, 3] = m[1, 2]
        return m4d
    }
}
