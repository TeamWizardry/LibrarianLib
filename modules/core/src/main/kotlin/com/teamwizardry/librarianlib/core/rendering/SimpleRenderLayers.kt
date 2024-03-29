package com.teamwizardry.librarianlib.core.rendering

import com.teamwizardry.librarianlib.core.bridge.IMutableRenderLayerPhaseParameters
import com.teamwizardry.librarianlib.core.util.mixinCast
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.VertexFormat
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11
import java.util.function.Consumer

public object SimpleRenderLayers {
    private val flatColorCache = mutableMapOf<Int, RenderLayer>()
    private val flatTextureCache = mutableMapOf<Pair<Identifier, Int>, RenderLayer>()

    /**
     * Simple flat polygons using the [POSITION_COLOR_TEX][VertexFormats.POSITION_COLOR_TEX] format.
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
        texture: Identifier,
        glMode: Int = GL11.GL_QUADS
    ): RenderLayer {
        TODO("Rendering changes")
    }

    private fun makeFlat(
        texture: Identifier,
        glMode: Int = GL11.GL_QUADS,
    ): RenderLayer {
        TODO("Rendering changes")
    }

    /**
     * Simple flat polygons using the [POSITION_COLOR][VertexFormats.POSITION_COLOR] format.
     *
     * This value should be reused, not generated every frame.
     *
     * @param glMode the OpenGL draw mode (e.g. [GL_QUADS][GL11.GL_QUADS], [GL_TRIANGLES][GL11.GL_TRIANGLES], etc.).
     * Defaults to [GL_QUADS][GL11.GL_QUADS]
     * @param configure additional configuration for the render state
     */
    @JvmStatic
    public fun flat(glMode: Int): RenderLayer {
        TODO("Rendering changes")
    }

    private fun makeFlat(glMode: Int): RenderLayer {
        TODO("Rendering changes")
    }

    /**
     * [RenderLayer.of] returns a package-private class [RenderLayer.MultiPhase] because... Mojang?
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
    ): RenderLayer {
        TODO("Rendering changes")
    }

    /**
     * Flat colored quads
     */
    @JvmStatic
    public val flatQuads: RenderLayer = flat(GL11.GL_QUADS)

    /**
     * Flat colored lines
     */
    @JvmStatic
    public val flatLines: RenderLayer = flat(GL11.GL_LINES)

    /**
     * Flat colored line strip
     */
    @JvmStatic
    public val flatLineStrip: RenderLayer = flat(GL11.GL_LINE_STRIP)

    /**
     * Note: We add a custom identity-hashed/equals render state because MC's render type caching is idiotic.
     *
     * Minecraft doesn't even consider the vertex format or GL draw mode when pulling render types from the cache, only
     * the GL *state*. To work around this we have to add the missing equality checks to a custom NOP "GL state" object
     * so they will get included in the necessary locations.
     *
     * `RenderLayer.Type.TYPES` (cache uses custom equality strategy):
     * - `hashCode()` is based on: RenderLayer.Type hashCode
     * - `equals()` is based on: `RenderPhase`
     *
     * `RenderLayer.Type`:
     * - `hashCode()` is based on: `name` and `RenderPhase`
     * - `equals()` is based on: identity
     *
     * `RenderLayer.MultiPhaseParameters`:
     * - `hashCode()` is based on: outline state and `RenderPhases` list
     * - `equals()` is based on: outline state and `RenderPhases` list
     */
    private data class CacheFixerRenderPhase(
        val vertexFormatIn: VertexFormat,
        val glMode: Int,
        val bufferSizeIn: Int,
        val useDelegate: Boolean,
        val needsSorting: Boolean
    ): RenderPhase("cache_fixer", {}, {})
}