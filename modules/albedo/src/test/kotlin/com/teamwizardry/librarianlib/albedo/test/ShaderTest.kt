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

abstract class ShaderTest<T: Shader>(
    val minX: Int = 0,
    val minY: Int = 0,
    val maxX: Int = 128,
    val maxY: Int = 128
) {
    private var initialized = false

    protected abstract fun doDraw(stack: MatrixStack, matrix: Matrix4d)

    private var _shader: Shader? = null
    @Suppress("UNCHECKED_CAST")
    protected var shader: T
        get() = _shader!! as T
        set(value) { _shader = value }

    protected fun drawUnitQuad(
        matrix: Matrix4d,
        minX: Int = this.minX, minY: Int = this.minY, maxX: Int = this.maxX, maxY: Int = this.maxY,
        minU: Float = 0f, minV: Float = 0f, maxU: Float = 1f, maxV: Float = 1f
    ) {
        val vb = Tessellator.getInstance().buffer

        vb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE)
        vb.vertex2d(matrix, minX, maxY).color(1f, 1f, 1f, 1f).texture(minU, maxV).next()
        vb.vertex2d(matrix, maxX, maxY).color(1f, 1f, 1f, 1f).texture(maxU, maxV).next()
        vb.vertex2d(matrix, maxX, minY).color(1f, 1f, 1f, 1f).texture(maxU, minV).next()
        vb.vertex2d(matrix, minX, minY).color(1f, 1f, 1f, 1f).texture(minU, minV).next()

        shader.use()
        BufferRenderer.draw(vb)
//        shader.unbind()
    }

    private val shaderConstructor: ConstructorMirror by lazy {
        Mirror.reflectClass(this.javaClass)
            .findSuperclass(ShaderTest::class.java)!!
            .typeParameters[0].asClassMirror()
            .getDeclaredConstructor()
    }

    fun draw(matrixStack: MatrixStack) {
        if(!initialized) {
            initialize()
            initialized = true
        }
        doDraw(matrixStack, Matrix4d(matrixStack))
    }

    protected open fun initialize() {
        _shader = shaderConstructor()
    }

    protected open fun delete() {
        _shader?.delete()
        _shader = null
    }

    fun destroy() {
        delete()
        initialized = false
    }
}