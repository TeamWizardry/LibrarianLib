package com.teamwizardry.librarianlib.glitter.modules

import com.teamwizardry.librarianlib.albedo.buffer.RenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.StandardUniforms
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.shader.uniform.*
import net.minecraft.util.Identifier

public class SpriteRenderBuffer(vbo: VertexBuffer) : RenderBuffer(vbo) {
    protected val modelViewMatrix: Mat4x4Uniform = +Uniform.mat4.create("ModelViewMatrix")
    protected val projectionMatrix: Mat4x4Uniform = +Uniform.mat4.create("ProjectionMatrix")
    protected val fogColor: FloatVec4Uniform = +Uniform.vec4.create("FogColor")
    protected val fogStart: FloatUniform = +Uniform.float.create("FogStart")
    protected val fogEnd: FloatUniform = +Uniform.float.create("FogEnd")
    protected val light0Direction: FloatVec3Uniform = +Uniform.vec3.create("Light0_Direction")
    protected val light1Direction: FloatVec3Uniform = +Uniform.vec3.create("Light1_Direction")
    protected val lightmap: SamplerUniform = +Uniform.sampler2D.create("Lightmap")

    public val worldMatrix: Mat4x4Uniform = +Uniform.mat4.create("WorldMatrix")
    public val normalMatrix: Mat3x3Uniform = +Uniform.mat3.create("NormalMatrix")
    public val texture: SamplerUniform = +Uniform.sampler2D.create("Texture")
    public val upDominant: BoolUniform = +Uniform.bool.create("UpDominant")
    public val enableDiffuseLighting: BoolUniform = +Uniform.bool.create("EnableDiffuseLighting")
    public val enableDiffuseBackface: BoolUniform = +Uniform.bool.create("EnableDiffuseBackface")
    public val enableLightmap: BoolUniform = +Uniform.bool.create("EnableLightmap")

    private val position: VertexLayoutElement =
        +VertexLayoutElement("Position", VertexLayoutElement.FloatFormat.FLOAT, 3, false)

    private val up: VertexLayoutElement =
        +VertexLayoutElement("Up", VertexLayoutElement.FloatFormat.FLOAT, 3, false)
    private val facing: VertexLayoutElement =
        +VertexLayoutElement("Facing", VertexLayoutElement.FloatFormat.FLOAT, 3, false)
    private val size: VertexLayoutElement =
        +VertexLayoutElement("Size", VertexLayoutElement.FloatFormat.FLOAT, 2, false)

    private val color: VertexLayoutElement =
        +VertexLayoutElement("Color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)
    private val texCoords: VertexLayoutElement =
        +VertexLayoutElement("TexCoords", VertexLayoutElement.FloatFormat.FLOAT, 4, false)
    private val light: VertexLayoutElement =
        +VertexLayoutElement("Light", VertexLayoutElement.IntFormat.UNSIGNED_SHORT, 2)

    init {
        val features = arrayOf<String>()
        this.bind(
            Shader.build("glitter_sprite")
                .vertex(Identifier("liblib-glitter:sprite.vert"), *features)
                .geometry(Identifier("liblib-glitter:sprite.geom"), *features)
                .fragment(Identifier("liblib-glitter:sprite.frag"), *features)
                .build()
        )
    }

    override fun setupState() {
        super.setupState()
        StandardUniforms.setModelViewMatrix(modelViewMatrix)
        StandardUniforms.setProjectionMatrix(projectionMatrix)
        StandardUniforms.setFogParameters(fogStart, fogEnd, fogColor)
        StandardUniforms.setLights(light0Direction, light1Direction)
        StandardUniforms.setLightmap(lightmap)
    }

    public fun position(x: Double, y: Double, z: Double): SpriteRenderBuffer {
        start(position)
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

    public fun facing(x: Double, y: Double, z: Double): SpriteRenderBuffer {
        start(facing)
        putFloat(x.toFloat())
        putFloat(y.toFloat())
        putFloat(z.toFloat())
        return this
    }

    public fun size(x: Double, y: Double): SpriteRenderBuffer {
        start(size)
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

    public fun tex(minU: Float, minV: Float, maxU: Float, maxV: Float): SpriteRenderBuffer {
        start(texCoords)
        putFloat(minU)
        putFloat(minV)
        putFloat(maxU)
        putFloat(maxV)
        return this
    }

    public fun light(light: Int): SpriteRenderBuffer {
        start(this.light)
        putShort((light and 0xffff).toShort())
        putShort((light shr 16 and 0xffff).toShort())
        return this
    }

    public companion object {
        public val SHARED: SpriteRenderBuffer = SpriteRenderBuffer(VertexBuffer.SHARED)

    }
}