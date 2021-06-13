package com.teamwizardry.librarianlib.albedo

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.uniform.AbstractUniform
import com.teamwizardry.librarianlib.albedo.uniform.SamplerArrayUniform
import com.teamwizardry.librarianlib.albedo.uniform.SamplerUniform
import com.teamwizardry.librarianlib.albedo.uniform.Uniform
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
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

    private var linked: Shader? = null
    private val boundTextureUnits = mutableMapOf<Pair<Int, Int>, Int>()
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

    public fun link(shader: Shader) {
        linked = shader
        linkedUniforms = ShaderBinder.bindUniforms(uniforms, shader)
        ShaderBinder.bindAttributes(attributes, shader)
    }

    public fun useProgram() {
        val linked = linked ?: return
        GL20.glUseProgram(linked.glProgram)
    }

    public fun pushUniforms() {
        val linked = linked ?: return
        useProgram()

        var currentUnit = 0
        boundTextureUnits.clear()
        linkedUniforms?.forEach {
            when (it) {
                is SamplerUniform -> {
                    it.textureUnit = boundTextureUnits.getOrPut(it.get() to it.textureTarget) { currentUnit++ }
                }
                is SamplerArrayUniform -> {
                    for (i in 0 until min(it.length, it.trueLength)) {
                        it.textureUnits[i] = boundTextureUnits.getOrPut(it[i] to it.textureTarget) { currentUnit++ }
                    }
                }
                else -> {
                }
            }
        }
        for ((tex, unit) in boundTextureUnits) {
            bindTexture(tex.first, tex.second, unit)
        }
        RenderSystem.activeTexture(GL13.GL_TEXTURE0)

        linkedUniforms?.forEach {
            it.push()
        }
    }

    /**
     * Unbinds this shader program and cleans up some GL texture state.
     */
    public fun unbind() {
        GL20.glUseProgram(0)
        boundTextureUnits.forEach { (tex, unit) ->
            unbindTexture(tex.second, unit)
        }
    }

    private fun bindTexture(texture: Int, target: Int, unit: Int) {
        if (unit < GlStateManager.TEXTURE_COUNT && target == GL11.GL_TEXTURE_2D) { // GlStateManager only tracks the first few GL_TEXTURE_2D units
            RenderSystem.activeTexture(GL13.GL_TEXTURE0 + unit)
            RenderSystem.enableTexture()
            RenderSystem.bindTexture(texture)
            RenderSystem.activeTexture(GL13.GL_TEXTURE0)
        } else {
            if (unit < GlStateManager.TEXTURE_COUNT) {
                // GlStateManager tracks the first few texture units, and it only changes the texture when the value
                // changes from its perspective. This becomes an issue if we change the texture without it knowing,
                // since it may think that a texture doesn't need to be re-bound. To alleviate this we set it to a
                // dummy value, making sure it will always try to re-bind next time someone binds a texture.
                RenderSystem.activeTexture(GL13.GL_TEXTURE0 + unit)
                RenderSystem.enableTexture()
                Client.textureManager.bindTexture(Identifier("librarianlib:albedo/textures/dummy.png"))
            }
            RenderSystem.activeTexture(GL13.GL_TEXTURE0) // get GlStateManager into a known state
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit)
            GL11.glEnable(target)
            GL11.glBindTexture(target, texture)
            GL13.glActiveTexture(GL13.GL_TEXTURE0) // make sure we return to that state
        }
    }

    private fun unbindTexture(target: Int, unit: Int) {
        if (unit < GlStateManager.TEXTURE_COUNT && target == GL11.GL_TEXTURE_2D) { // GlStateManager only tracks the first few GL_TEXTURE_2D units
            RenderSystem.activeTexture(GL13.GL_TEXTURE0 + unit)
            RenderSystem.bindTexture(0)
            RenderSystem.disableTexture()
            RenderSystem.activeTexture(GL13.GL_TEXTURE0)
        } else {
            if (unit < GlStateManager.TEXTURE_COUNT) {
                // GlStateManager tracks the first few texture units, and it only changes the texture when the value
                // changes from its perspective. This becomes an issue if we change the texture without it knowing,
                // since it may think that a texture doesn't need to be re-bound. To alleviate this we set it to a
                // dummy value, making sure it will always try to re-bind next time someone binds a texture.
                RenderSystem.activeTexture(GL13.GL_TEXTURE0 + unit)
                Client.textureManager.bindTexture(Identifier("librarianlib:albedo/textures/dummy.png"))
                RenderSystem.bindTexture(0)
                RenderSystem.disableTexture()
            }
            RenderSystem.activeTexture(GL13.GL_TEXTURE0) // get GlStateManager into a known state
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit)

            GL11.glEnable(target)
            GL11.glBindTexture(target, 0)
            GL11.glBindTexture(GL13.GL_TEXTURE_2D, 0)
            GL11.glDisable(target)
            GL13.glActiveTexture(GL13.GL_TEXTURE0) // make sure we return to that state
        }
    }
}