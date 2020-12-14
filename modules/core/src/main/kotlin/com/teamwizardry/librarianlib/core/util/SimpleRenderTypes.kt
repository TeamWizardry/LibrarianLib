package com.teamwizardry.librarianlib.core.util

import com.teamwizardry.librarianlib.core.bridge.IMutableRenderTypeState
import com.teamwizardry.librarianlib.core.util.kotlin.mixinCast
import net.minecraft.client.renderer.RenderState
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.util.function.Consumer

public object SimpleRenderTypes {
    private val flatColorCache = mutableMapOf<Int, RenderType>()
    private val flatTextureCache = mutableMapOf<Pair<ResourceLocation, Int>, RenderType>()

    /**
     * Simple flat polygons using the [POSITION_COLOR_TEX][DefaultVertexFormats.POSITION_COLOR_TEX] format.
     *
     * This value should be reused, not generated every frame.
     *
     * @param texture the texture to bind when drawing with this type
     * @param glMode the OpenGL draw mode (e.g. [GL_QUADS][GL11.GL_QUADS], [GL_TRIANGLES][GL11.GL_TRIANGLES], etc.).
     * Defaults to [GL_QUADS][GL11.GL_QUADS]
     * @param configure additional configuration for the render state
     */
    @JvmOverloads
    @JvmStatic
    public fun flat(
        texture: ResourceLocation,
        glMode: Int = GL11.GL_QUADS,
        configure: Consumer<RenderType.State.Builder>? = null
    ): RenderType {
        if (configure != null)
            return makeFlat(texture, glMode, configure)
        return flatTextureCache.getOrPut(texture to glMode) {
            makeFlat(texture, glMode, configure)
        }
    }

    private fun makeFlat(
        texture: ResourceLocation,
        glMode: Int = GL11.GL_QUADS,
        configure: Consumer<RenderType.State.Builder>? = null
    ): RenderType {
        val renderState = RenderType.State.getBuilder()
            .texture(RenderState.TextureState(texture, false, false))
            .alpha(DefaultRenderStates.DEFAULT_ALPHA)
            .depthTest(DefaultRenderStates.DEPTH_LEQUAL)
            .transparency(DefaultRenderStates.TRANSLUCENT_TRANSPARENCY)
        configure?.accept(renderState)

        return makeType(
            "flat_texture",
            DefaultVertexFormats.POSITION_COLOR_TEX, glMode, 256, false, false, renderState.build(true)
        )
    }

    /**
     * Simple flat polygons using the [POSITION_COLOR][DefaultVertexFormats.POSITION_COLOR] format.
     *
     * This value should be reused, not generated every frame.
     *
     * @param glMode the OpenGL draw mode (e.g. [GL_QUADS][GL11.GL_QUADS], [GL_TRIANGLES][GL11.GL_TRIANGLES], etc.).
     * Defaults to [GL_QUADS][GL11.GL_QUADS]
     * @param configure additional configuration for the render state
     */
    @JvmOverloads
    @JvmStatic
    public fun flat(glMode: Int, configure: Consumer<RenderType.State.Builder>? = null): RenderType {
        if (configure != null)
            return makeFlat(glMode, configure)
        return flatColorCache.getOrPut(glMode) {
            makeFlat(glMode, configure)
        }
    }

    private fun makeFlat(glMode: Int, configure: Consumer<RenderType.State.Builder>? = null): RenderType {
        val renderState = RenderType.State.getBuilder()
            .texture(RenderState.TextureState())
            .alpha(DefaultRenderStates.DEFAULT_ALPHA)
            .depthTest(DefaultRenderStates.DEPTH_LEQUAL)
            .transparency(DefaultRenderStates.TRANSLUCENT_TRANSPARENCY)
        configure?.accept(renderState)

        return makeType(
            "flat_color",
            DefaultVertexFormats.POSITION_COLOR, glMode, 256, false, false, renderState.build(true)
        )
    }

    /**
     * [RenderType.makeType] returns a package-private class [RenderType.Type] because... Mojang?
     *
     * Also their caching is garbage that sometimes will give you back some other random render type with the wrong
     * primitive type or vertex format. To deal with this, this method works around the cache by adding a custom NOP
     * render state that implements the missing equality operations.
     */
    @JvmStatic
    public fun makeType(
        name: String,
        vertexFormatIn: VertexFormat, glMode: Int,
        bufferSizeIn: Int, useDelegate: Boolean, needsSorting: Boolean,
        renderState: RenderType.State
    ): RenderType {
        mixinCast<IMutableRenderTypeState>(renderState).addState(
            CacheFixerRenderState(
                vertexFormatIn, glMode, bufferSizeIn, useDelegate, needsSorting
            )
        )
        @Suppress("INACCESSIBLE_TYPE")
        return RenderType.makeType(
            name,
            vertexFormatIn,
            glMode,
            bufferSizeIn,
            useDelegate,
            needsSorting,
            renderState
        )
    }

    /**
     * Flat colored quads
     */
    @JvmStatic
    public val flatQuads: RenderType = flat(GL11.GL_QUADS)

    /**
     * Flat colored lines
     */
    @JvmStatic
    public val flatLines: RenderType = flat(GL11.GL_LINES)

    /**
     * Flat colored line strip
     */
    @JvmStatic
    public val flatLineStrip: RenderType = flat(GL11.GL_LINE_STRIP)

    /**
     * Note: We add a custom identity-hashed/equals render state because MC's render type caching is idiotic.
     *
     * Minecraft doesn't even consider the vertex format or GL draw mode when pulling render types from the cache, only
     * the GL *state*. To work around this we have to add the missing equality checks to a custom NOP "GL state" object
     * so they will get included in the necessary locations.
     *
     * `RenderType.Type.TYPES` (cache uses custom equality strategy):
     * - `hashCode()` is based on: RenderType.Type hashCode
     * - `equals()` is based on: `renderState`
     *
     * `RenderType.Type`:
     * - `hashCode()` is based on: `name` and `renderState`
     * - `equals()` is based on: identity
     *
     * `RenderType.State`:
     * - `hashCode()` is based on: outline state and `renderStates` list
     * - `equals()` is based on: outline state and `renderStates` list
     */
    private data class CacheFixerRenderState(
        val vertexFormatIn: VertexFormat,
        val glMode: Int,
        val bufferSizeIn: Int,
        val useDelegate: Boolean,
        val needsSorting: Boolean
    ): RenderState("cache_fixer", {}, {})
}