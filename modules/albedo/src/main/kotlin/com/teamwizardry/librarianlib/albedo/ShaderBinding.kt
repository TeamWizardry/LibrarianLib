package com.teamwizardry.librarianlib.albedo

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.bridge.GlStateManagerExtensions
import com.teamwizardry.librarianlib.albedo.uniform.AbstractUniform
import com.teamwizardry.librarianlib.albedo.uniform.SamplerArrayUniform
import com.teamwizardry.librarianlib.albedo.uniform.SamplerUniform
import com.teamwizardry.librarianlib.albedo.uniform.Uniform
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import kotlin.math.min

/**
 * An abstract class that reflectively links the contained
 */
public abstract class ShaderBinding {
    private val _attributes = mutableListOf<VertexLayoutElement>()
    public val attributes: List<VertexLayoutElement> = _attributes.unmodifiableView()
    private val _uniforms = mutableListOf<AbstractUniform>()
    public val uniforms: List<AbstractUniform> = _uniforms.unmodifiableView()
    private var linkedUniforms: List<Uniform>? = null

    private var shader: Shader? = null
    private val boundTextureUnits = Long2IntOpenHashMap(32)
    public var stride: Int = 0
        private set

    protected fun <T : AbstractUniform> add(uniform: T): T {
        _uniforms.add(uniform)
        return uniform
    }

    protected fun add(attribute: VertexLayoutElement): VertexLayoutElement {
        attribute.offset = stride
        stride += attribute.width
        _attributes.add(attribute)
        return attribute
    }

    @JvmSynthetic
    protected operator fun <T : AbstractUniform> T.unaryPlus(): T {
        add(this)
        return this
    }

    @JvmSynthetic
    protected operator fun VertexLayoutElement.unaryPlus(): VertexLayoutElement {
        add(this)
        return this
    }

    public fun bind(shader: Shader) {
        this.shader = shader
        linkedUniforms = ShaderBinder.bindUniforms(uniforms, shader)
        ShaderBinder.bindAttributes(attributes, shader)
    }

    public fun useProgram() {
        val shader = this.shader ?: return
        GL20.glUseProgram(shader.glProgram)
    }

    public fun pushUniforms() {
        useProgram()

        var nextUnit = 0
        boundTextureUnits.clear()
        linkedUniforms?.forEach {
            when (it) {
                is SamplerUniform -> {
                    val packed = packTextureBinding(it.textureTarget, it.get())
                    val unit = boundTextureUnits.putIfAbsent(packed, nextUnit)
                    if(unit == nextUnit)
                        nextUnit++
                    it.textureUnit = unit
                }
                is SamplerArrayUniform -> {
                    for (i in 0 until min(it.length, it.trueLength)) {
                        val packed = packTextureBinding(it.textureTarget, it[i])
                        val unit = boundTextureUnits.putIfAbsent(packed, nextUnit)
                        if(unit == nextUnit)
                            nextUnit++
                        it.textureUnits[i] = unit
                    }
                }
                else -> {
                }
            }
        }
        boundTextureUnits.long2IntEntrySet().fastForEach { entry ->
            bindTexture(unpackTexture(entry.longKey), unpackTarget(entry.longKey), entry.intValue)
        }
        RenderSystem.activeTexture(GL13.GL_TEXTURE0)

        linkedUniforms?.forEach {
            it.push()
        }
    }

    /**
     * Unbinds the shader program
     */
    public fun cleanup() {
        GL20.glUseProgram(0)
    }

    private fun packTextureBinding(target: Int, texture: Int): Long {
        return ((target.toULong() shl 32) or texture.toULong()).toLong()
    }

    private fun unpackTexture(packed: Long): Int {
        return packed.toInt()
    }
    private fun unpackTarget(packed: Long): Int {
        return (packed ushr 32).toInt()
    }

    private fun bindTexture(texture: Int, target: Int, unit: Int) {
        val glUnit = GL13.GL_TEXTURE0 + unit
        if(unit < GlStateManager.TEXTURE_COUNT) {
            GlStateManager._activeTexture(glUnit)
            GlStateManagerExtensions.bindTexture(target, texture)
        } else {
            GL13.glActiveTexture(glUnit)
            GL11.glBindTexture(target, texture)
        }
        GlStateManager._activeTexture(GL13.GL_TEXTURE0)
    }
}