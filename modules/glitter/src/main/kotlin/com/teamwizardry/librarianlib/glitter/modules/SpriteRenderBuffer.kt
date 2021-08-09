package com.teamwizardry.librarianlib.glitter.modules

import com.teamwizardry.librarianlib.albedo.buffer.RenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.StandardUniforms
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.shader.uniform.*
import net.minecraft.util.Identifier

private val glitterShader = Shader.build("glitter_sprite")
    .vertex(Identifier("liblib-glitter:sprite.vert"))
    .fragment(Identifier("liblib-glitter:sprite.frag"))
    .build()

public class SpriteRenderBuffer(vbo: VertexBuffer) : RenderBuffer(vbo) {
    protected val modelViewMatrix: Mat4x4Uniform = +Uniform.mat4.create("ModelViewMatrix")
    protected val projectionMatrix: Mat4x4Uniform = +Uniform.mat4.create("ProjectionMatrix")
    protected val fogColor: FloatVec4Uniform = +Uniform.vec4.create("FogColor")
    protected val fogStart: FloatUniform = +Uniform.float.create("FogStart")
    protected val fogEnd: FloatUniform = +Uniform.float.create("FogEnd")

    public val worldMatrix: Mat4x4Uniform = +Uniform.mat4.create("WorldMatrix")
    public val texture: SamplerUniform = +Uniform.sampler2D.create("Texture")

    private val point: VertexLayoutElement =
        +VertexLayoutElement("Point", VertexLayoutElement.FloatFormat.FLOAT, 3, false)
    private val up: VertexLayoutElement =
        +VertexLayoutElement("Up", VertexLayoutElement.FloatFormat.FLOAT, 3, false)
    private val right: VertexLayoutElement =
        +VertexLayoutElement("Right", VertexLayoutElement.FloatFormat.FLOAT, 3, false)
    private val offset: VertexLayoutElement =
        +VertexLayoutElement("Offset", VertexLayoutElement.FloatFormat.FLOAT, 2, false)

    private val color: VertexLayoutElement =
        +VertexLayoutElement("Color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)
    private val texCoord: VertexLayoutElement =
        +VertexLayoutElement("TexCoord", VertexLayoutElement.FloatFormat.FLOAT, 2, false)

    init {
        this.bind(glitterShader)
    }

    override fun setupState() {
        super.setupState()
        StandardUniforms.setModelViewMatrix(modelViewMatrix)
        StandardUniforms.setProjectionMatrix(projectionMatrix)
        StandardUniforms.setFogParameters(fogStart, fogEnd, fogColor)
    }

    public fun point(x: Double, y: Double, z: Double): SpriteRenderBuffer {
        start(point)
        putFloat(x.toFloat())
        putFloat(y.toFloat())
        putFloat(z.toFloat())
        return this
    }

    public fun up(x: Double, y: Double, z: Double): SpriteRenderBuffer {
        start(up)
        putFloat(x.toFloat())
        putFloat(y.toFloat())
        putFloat(z.toFloat())
        return this
    }

    public fun right(x: Double, y: Double, z: Double): SpriteRenderBuffer {
        start(right)
        putFloat(x.toFloat())
        putFloat(y.toFloat())
        putFloat(z.toFloat())
        return this
    }

    public fun offset(x: Double, y: Double): SpriteRenderBuffer {
        start(offset)
        putFloat(x.toFloat())
        putFloat(y.toFloat())
        return this
    }

    public fun color(r: Float, g: Float, b: Float, a: Float): SpriteRenderBuffer {
        start(color)
        putByte((r * 255).toInt())
        putByte((g * 255).toInt())
        putByte((b * 255).toInt())
        putByte((a * 255).toInt())
        return this
    }

    public fun tex(u: Float, v: Float): SpriteRenderBuffer {
        start(texCoord)
        putFloat(u)
        putFloat(v)
        return this
    }

    public companion object {
        public val SHARED: SpriteRenderBuffer = SpriteRenderBuffer(VertexBuffer.SHARED)

    }
}