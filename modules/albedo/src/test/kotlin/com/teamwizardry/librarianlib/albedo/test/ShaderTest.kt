package com.teamwizardry.librarianlib.albedo.test

import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack

abstract class ShaderTest(
    val minX: Int = 0,
    val minY: Int = 0,
    val maxX: Int = 128,
    val maxY: Int = 128
) {
    private var initialized = false

    protected abstract fun doDraw(stack: MatrixStack, matrix: Matrix4d)

    fun draw(matrixStack: MatrixStack) {
        if(!initialized) {
            initialize()
            initialized = true
        }
        doDraw(matrixStack, Matrix4d(matrixStack))
    }

    protected open fun initialize() {}

    protected open fun delete() {}

    fun destroy() {
        delete()
        initialized = false
    }
}