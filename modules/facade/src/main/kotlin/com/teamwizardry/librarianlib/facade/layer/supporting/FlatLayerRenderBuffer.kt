package com.teamwizardry.librarianlib.facade.layer.supporting

import com.teamwizardry.librarianlib.albedo.base.buffer.BaseRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.shader.uniform.Uniform
import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.util.Identifier

internal class FlatLayerRenderBuffer(vbo: VertexBuffer) : BaseRenderBuffer<FlatLayerRenderBuffer>(vbo) {
    val layerImage = +Uniform.sampler2D.create("LayerImage")
    val maskImage = +Uniform.sampler2D.create("MaskImage")
    val displaySize = +Uniform.vec2.create("DisplaySize")
    val alphaMultiply = +Uniform.float.create("AlphaMultiply")
    val maskMode = +Uniform.int.create("MaskMode")
    val renderMode = +Uniform.int.create("RenderMode")
    private val texCoordAttribute = +VertexLayoutElement("TexCoord", VertexLayoutElement.FloatFormat.FLOAT, 2, false)

    init {
        bind(shader)
    }

    fun tex(u: Float, v: Float): FlatLayerRenderBuffer {
        start(texCoordAttribute)
        putFloat(u)
        putFloat(v)
        return this
    }

    override fun setupState() {
        displaySize.set(Client.window.framebufferWidth.toFloat(), Client.window.framebufferHeight.toFloat())
    }

    companion object {
        val shader = Shader.build("framebuffer_clear")
            .vertex(Identifier("liblib-facade:shaders/flat_layer.vert"))
            .fragment(Identifier("liblib-facade:shaders/flat_layer.frag"))
            .build()
        val SHARED = FlatLayerRenderBuffer(VertexBuffer.SHARED)
    }
}

public enum class MaskMode {
    /**
     * No masking will occur
     */
    NONE,

    /**
     * The layer's alpha is multiplied by the mask's alpha. i.e. transparent mask = transparent layer.
     */
    ALPHA,

    /**
     * The layer's alpha is multiplied by the mask's luma (brightness), blended with a white background.
     * i.e. dark mask = transparent layer, transparent mask = white background = opaque layer.
     */
    LUMA_ON_WHITE,

    /**
     * The layer's alpha is multiplied by the mask's luma (brightness), blended with a black background.
     * i.e. dark mask = transparent layer, transparent mask = black background = transparent layer.
     */
    LUMA_ON_BLACK,

    /**
     * The inverse of [ALPHA]. i.e. transparent mask = opaque layer.
     */
    INV_ALPHA,

    /**
     * The inverse of [LUMA_ON_WHITE]. i.e. dark mask = opaque layer,
     * transparent mask = white background = transparent layer.
     */
    INV_LUMA_ON_WHITE,

    /**
     * The inverse of [LUMA_ON_BLACK]. i.e. dark mask = opaque layer,
     * transparent mask = black background = opaque layer.
     */
    INV_LUMA_ON_BLACK;
}

public enum class RenderMode {
    /**
     * The default, this renders directly to the current FBO
     */
    DIRECT,

    /**
     * The default when rendering to a texture, this renders onto an FBO which is then rendered onto the screen. This
     * mode uses a technique that avoids issues of lost resolution due to scale.
     */
    RENDER_TO_FBO,

    /**
     * Draws the layer to a texture at a native resolution multiple (one unit = N texture pixels) and draws that to a
     * quad. This mode can lead to lost resolution, however sometimes this is the desired effect.
     */
    RENDER_TO_QUAD
}
