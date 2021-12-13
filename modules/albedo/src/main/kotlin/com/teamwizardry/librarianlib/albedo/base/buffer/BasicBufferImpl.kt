package com.teamwizardry.librarianlib.albedo.base.buffer

import com.teamwizardry.librarianlib.albedo.buffer.RenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.StandardUniforms
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.shader.uniform.*
import net.minecraft.util.Identifier

public class BasicBufferImpl<T : Any>(
    public val enableTexture: Boolean,
    public val enableNormal: Boolean,
    public val enableLightmap: Boolean,
    public val enableFog: Boolean,
) : PositionBuffer<T>, ColorBuffer<T>, TexBuffer<T>, NormalBuffer<T>, LightmapBuffer<T> {

    private val modelViewMatrix: Mat4x4Uniform = Uniform.mat4.create("ModelViewMatrix")
    private val projectionMatrix: Mat4x4Uniform = Uniform.mat4.create("ProjectionMatrix")
    private val light0Direction: FloatVec3Uniform = Uniform.vec3.create("Light0_Direction")
    private val light1Direction: FloatVec3Uniform = Uniform.vec3.create("Light1_Direction")
    private val lightmap: SamplerUniform = Uniform.sampler2D.create("Lightmap")
    private val fogColor: FloatVec4Uniform = Uniform.vec4.create("FogColor")
    private val fogStart: FloatUniform = Uniform.float.create("FogStart")
    private val fogEnd: FloatUniform = Uniform.float.create("FogEnd")

    public override val texture: SamplerUniform = Uniform.sampler2D.create("Texture")

    public val position: VertexLayoutElement =
        VertexLayoutElement("Position", VertexLayoutElement.FloatFormat.FLOAT, 3, false)
    public val color: VertexLayoutElement =
        VertexLayoutElement("Color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)
    public val texCoord: VertexLayoutElement =
        VertexLayoutElement("TexCoord", VertexLayoutElement.FloatFormat.FLOAT, 4, false)
    public val normal: VertexLayoutElement =
        VertexLayoutElement("Normal", VertexLayoutElement.FloatFormat.FLOAT, 3, false)
    public val light: VertexLayoutElement =
        VertexLayoutElement("Light", VertexLayoutElement.IntFormat.UNSIGNED_SHORT, 2)

    private lateinit var instance: T
    private lateinit var access: RenderBuffer.InternalAccess

    public fun initialize(instance: T, access: RenderBuffer.InternalAccess) {
        this.instance = instance
        this.access = access

        val flags = mutableListOf<String>()

        access.add(modelViewMatrix)
        access.add(projectionMatrix)
        access.add(position)
        access.add(color)
        if(enableTexture) {
            flags.add("ENABLE_TEXTURE")
            access.add(texture)
            access.add(texCoord)
        }
        if(enableNormal) {
            flags.add("ENABLE_NORMAL")
            access.add(light0Direction)
            access.add(light1Direction)
            access.add(normal)
        }
        if(enableLightmap) {
            flags.add("ENABLE_LIGHTMAP")
            access.add(lightmap)
            access.add(light)
        }
        if(enableFog) {
            flags.add("ENABLE_FOG")
            access.add(fogColor)
            access.add(fogStart)
            access.add(fogEnd)
        }

        access.bind(getShader(flags))
    }

    public fun setupState() {
        StandardUniforms.setModelViewMatrix(modelViewMatrix)
        StandardUniforms.setProjectionMatrix(projectionMatrix)

        if(enableNormal) {
            StandardUniforms.setLights(light0Direction, light1Direction)
        }
        if(enableLightmap) {
            StandardUniforms.setLightmap(lightmap)
        }
        if(enableFog) {
            StandardUniforms.setFogParameters(fogStart, fogEnd, fogColor)
        }
    }

    override fun pos(x: Double, y: Double, z: Double): T {
        access.start(position)
        access.putFloat(x.toFloat())
        access.putFloat(y.toFloat())
        access.putFloat(z.toFloat())
        return instance
    }

    override fun color(r: Int, g: Int, b: Int, a: Int): T {
        access.start(color)
        access.putByte(r)
        access.putByte(g)
        access.putByte(b)
        access.putByte(a)
        return instance
    }

    override fun tex(u: Float, v: Float): T {
        access.start(texCoord)
        access.putFloat(u)
        access.putFloat(v)
        return instance
    }

    override fun normal(x: Double, y: Double, z: Double): T {
        access.start(normal)
        access.putFloat(x.toFloat())
        access.putFloat(y.toFloat())
        access.putFloat(z.toFloat())
        return instance
    }

    override fun light(lightmap: Int): T {
        access.start(light)
        access.putShort((lightmap and 0xffff).toShort())
        access.putShort((lightmap shr 16 and 0xffff).toShort())
        return instance
    }

    public companion object {
        private val shaders = mutableMapOf<List<String>, Shader>()

        private fun getShader(features: List<String>): Shader {
            return shaders.getOrPut(features) {
                val flags = features.toTypedArray()
                val name = "basic_buffer_" + features.joinToString("_") { it.removePrefix("ENABLE_").lowercase() }
                Shader.build(name)
                    .vertex(Identifier("liblib-albedo:builtin/basic.vert"), *flags)
                    .fragment(Identifier("liblib-albedo:builtin/basic.frag"), *flags)
                    .build()
            }
        }
    }
}

public abstract class BasicRenderBuffer(vbo: VertexBuffer, private val impl: BasicBufferImpl<*>): RenderBuffer(vbo) {
    init {
        @Suppress("UNCHECKED_CAST")
        (impl as BasicBufferImpl<Any>).initialize(this, internalAccess)
    }

    override fun setupState(): Unit = impl.setupState()
}
