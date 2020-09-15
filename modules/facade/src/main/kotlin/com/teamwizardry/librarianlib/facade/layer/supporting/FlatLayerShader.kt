package com.teamwizardry.librarianlib.facade.layer.supporting

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.core.rendering.BlendMode
import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.util.ResourceLocation

internal object FlatLayerShader: Shader("flat_layer", null, ResourceLocation("librarianlib:facade/shaders/flat_layer.frag")) {
    val layerImage = GLSL.sampler2D()
    val maskImage = GLSL.sampler2D()
    val displaySize = GLSL.vec2()
    val alphaMultiply = GLSL.glFloat()
    val maskMode = GLSL.glInt()
    val renderMode = GLSL.glInt()

    /**
     * This has to be applied here, because MC's state will overwrite it
     */
    var blendMode: BlendMode = BlendMode.NORMAL

    override fun setupState() {
        displaySize.set(Client.window.framebufferWidth.toFloat(), Client.window.framebufferHeight.toFloat())
        RenderSystem.enableBlend()
        blendMode.glApply()
    }

    override fun teardownState() {
        blendMode.reset()
        RenderSystem.disableBlend()
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
