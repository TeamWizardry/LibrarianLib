package com.teamwizardry.librarianlib.features.facade.component.supporting

import com.teamwizardry.librarianlib.features.kotlin.Client
import com.teamwizardry.librarianlib.features.shader.Shader
import com.teamwizardry.librarianlib.features.shader.ShaderHelper
import com.teamwizardry.librarianlib.features.shader.uniforms.FloatTypes
import com.teamwizardry.librarianlib.features.shader.uniforms.IntTypes
import com.teamwizardry.librarianlib.features.shader.uniforms.Uniform
import net.minecraft.util.ResourceLocation

object LayerToTextureShader : Shader(null, ResourceLocation("librarianlib:shaders/guilayer.frag")) {
    var maskMode: MaskMode = MaskMode.NONE
    var renderMode: RenderMode = RenderMode.RENDER_TO_FBO
    var alphaMultiply: Float = 1f

    private var displaySize: FloatTypes.FloatVec2Uniform? = null
    private var alphaMultiplyUniform: FloatTypes.FloatUniform? = null
    private var maskModeUniform: IntTypes.IntUniform? = null
    private var renderModeUniform: IntTypes.IntUniform? = null
    var layerImage: Uniform? = null
    var maskImage: Uniform? = null

    override fun initUniforms() {
        super.initUniforms()
        displaySize = getUniform("displaySize")
        alphaMultiplyUniform = getUniform("alphaMultiply")
        maskModeUniform = getUniform("maskMode")
        renderModeUniform = getUniform("renderMode")
        layerImage = getUniform("layerImage")
        maskImage = getUniform("maskImage")
    }

    override fun uniformDefaults() {
        super.uniformDefaults()
        displaySize?.set(
            Client.minecraft.displayWidth.toFloat(),
            Client.minecraft.displayHeight.toFloat()
        )
        alphaMultiplyUniform?.set(alphaMultiply)
        maskModeUniform?.set(maskMode.ordinal)
        renderModeUniform?.set(renderMode.ordinal)
    }

    init {
        ShaderHelper.addShader(this)
    }
}

enum class MaskMode {
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

enum class RenderMode {
    /**
     * The default, this renders onto the current FBO
     */
    DIRECT,
    /**
     * The default when rendering to a texture, this renders onto an FBO which is then rendered onto the screen. This
     * mode uses a technique that renders to an FBO the same resolution as the window and avoids issues of lost
     * resolution due to scale.
     */
    RENDER_TO_FBO,
    /**
     * Draws the layer to a texture at a native resolution multiple (one unit = N texture pixels) and draws that to a
     * quad. This mode can lead to lost resolution, however it is also the only one that supports antialiasing.
     */
    RENDER_TO_QUAD
}
