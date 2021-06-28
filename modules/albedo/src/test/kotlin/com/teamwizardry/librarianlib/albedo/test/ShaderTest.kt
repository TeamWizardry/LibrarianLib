package com.teamwizardry.librarianlib.albedo.test

import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import com.teamwizardry.librarianlib.math.Matrix4d
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.ConstructorMirror
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
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

    protected abstract fun initialize()

    protected abstract fun delete()

    fun destroy() {
        delete()
        initialized = false
    }
}