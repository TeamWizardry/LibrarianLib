package com.teamwizardry.librarianlib.facade.layer.supporting

import com.teamwizardry.librarianlib.albedo.base.buffer.BaseRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.shader.uniform.SamplerUniform
import com.teamwizardry.librarianlib.albedo.shader.uniform.Uniform
import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.util.Identifier

internal class FlatLayerRenderBuffer(vbo: VertexBuffer) : BaseRenderBuffer<FlatLayerRenderBuffer>(vbo) {
    val layerImage = +Uniform.sampler2D.create("LayerImage")
    val maskImage = +Uniform.sampler2D.create("MaskImage")
    val alphaMultiply = +Uniform.float.create("AlphaMultiply")
    val maskMode = +Uniform.int.create("MaskMode")
    val renderMode = +Uniform.int.create("RenderMode")

    private val texelCoordAttribute = +VertexLayoutElement("TexelCoord", VertexLayoutElement.FloatFormat.FLOAT, 2, false)

    init {
        bind(shader)
    }

    fun texel(x: Float, y: Float): FlatLayerRenderBuffer {
        start(texelCoordAttribute)
        putFloat(x)
        putFloat(y)
        return this
    }

    override fun setupState() {
        super.setupState()
    }

    companion object {
        val shader = Shader.build("flat_layer")
            .vertex(Identifier.of("liblib_facade:flat_layer.vert"))
            .fragment(Identifier.of("liblib_facade:flat_layer.frag"))
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
     *
     * | Mask Color  | Layer Opacity |
     * |------------:|:--------------|
     * |       white | opaque        |
     * | transparent | opaque        |
     * |       black | transparent   |
     */
    LUMA_ON_WHITE,

    /**
     * The layer's alpha is multiplied by the mask's luma (brightness), blended with a black background.
     *
     * | Mask Color  | Layer Opacity |
     * |------------:|:--------------|
     * |       white | opaque        |
     * | transparent | transparent   |
     * |       black | transparent   |
     */
    LUMA_ON_BLACK,

    /**
     * The inverse of [ALPHA]. i.e. transparent mask = opaque layer.
     */
    INV_ALPHA,

    /**
     * The inverse of [LUMA_ON_WHITE].
     *
     * | Mask Color  | Layer Opacity |
     * |------------:|:--------------|
     * |       white | transparent   |
     * | transparent | transparent   |
     * |       black | opaque        |
     */
    INV_LUMA_ON_WHITE,

    /**
     * The inverse of [LUMA_ON_BLACK].
     *
     * | Mask Color  | Layer Opacity |
     * |------------:|:--------------|
     * |       white | transparent   |
     * | transparent | opaque        |
     * |       black | opaque        |
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
     * mode uses a technique that avoids issues of lost resolution due to layer scaling.
     */
    RENDER_TO_FBO,

    /**
     * Draws the layer to a texture at a native resolution multiple (one unit = N texture pixels) and draws that to a
     * quad. This mode can lead to lost resolution or aliasing, however sometimes this is the desired effect.
     */
    RENDER_TO_QUAD
}
