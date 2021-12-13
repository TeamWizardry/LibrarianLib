package com.teamwizardry.librarianlib.albedo.test

import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.util.math.MatrixStack

abstract class ShaderTest(
    val width: Int = 128,
    val height: Int = 128,
) {
    private var initialized = false

    val minX: Int = 0
    val minY: Int = 0
    val maxX: Int = width
    val maxY: Int = height

    protected abstract fun doDraw(stack: MatrixStack, matrix: Matrix4d, mousePos: Vec2d)

    fun draw(matrixStack: MatrixStack, mousePos: Vec2d) {
        if(!initialized) {
            initialize()
            initialized = true
        }
        doDraw(matrixStack, Matrix4d(matrixStack), mousePos)
    }

    protected open fun initialize() {}

    protected open fun delete() {}

    fun destroy() {
        delete()
        initialized = false
    }
}