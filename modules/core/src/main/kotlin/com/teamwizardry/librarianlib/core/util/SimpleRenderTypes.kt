package com.teamwizardry.librarianlib.core.util

import net.minecraft.client.renderer.RenderState
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.util.UUID

// Note: Render type names have UUIDs affixed to them because MC caching is idiotic:
//
// `RenderType`:
// - `hashCode()` is based on: `name` (but not `vertexFormat`, `drawMode`, `bufferSize`, `useDelegate`, or `needsSorting`)
// - `equals()` is based on: `name` (`getClass` must also be equal, so subclasses are not accepted)
//
// `RenderType.Type`:
// - `hashCode()` is based on: `name` and `renderState`
// - `equals()` is based on: identity
//
// `RenderType.Type.TYPES` (cache uses custom equality strategy):
// - `hashCode()` is based on: `name` and `renderState`
// - `equals()` is based on: `renderState`
//
@Suppress("INACCESSIBLE_TYPE")
object SimpleRenderTypes {
    /**
     * Simple flat polygons using the [POSITION_COLOR_TEX][DefaultVertexFormats.POSITION_COLOR_TEX] format.
     *
     * This value should be reused if possible, not generated every frame.
     *
     * @param glMode the OpenGL draw mode (e.g. [GL_QUADS][GL11.GL_QUADS], [GL_TRIANGLES][GL11.GL_TRIANGLES], etc.).
     * Defaults to [GL_QUADS][GL11.GL_QUADS]
     */
    @JvmOverloads
    fun flat(texture: ResourceLocation, glMode: Int = GL11.GL_QUADS): RenderType {
        val renderState = RenderType.State.getBuilder()
            .texture(RenderState.TextureState(texture, false, false))
            .alpha(DefaultRenderStates.DEFAULT_ALPHA)
            .depthTest(DefaultRenderStates.DEPTH_LEQUAL)
            .transparency(DefaultRenderStates.TRANSLUCENT_TRANSPARENCY)

        return makeType("flat_texture",
            DefaultVertexFormats.POSITION_COLOR_TEX, glMode, 256, false, false, renderState.build(true)
        )
    }

    /**
     * Simple flat polygons using the [POSITION_COLOR][DefaultVertexFormats.POSITION_COLOR] format.
     *
     * This value should be reused if possible, not generated every frame.
     *
     * @param glMode the OpenGL draw mode (e.g. [GL_QUADS][GL11.GL_QUADS], [GL_TRIANGLES][GL11.GL_TRIANGLES], etc.).
     * Defaults to [GL_QUADS][GL11.GL_QUADS]
     */
    @JvmOverloads
    fun flat(glMode: Int = GL11.GL_QUADS): RenderType {
        val renderState = RenderType.State.getBuilder()
            .texture(RenderState.TextureState())
            .alpha(DefaultRenderStates.DEFAULT_ALPHA)
            .depthTest(DefaultRenderStates.DEPTH_LEQUAL)
            .transparency(DefaultRenderStates.TRANSLUCENT_TRANSPARENCY)

        return makeType("flat_color",
            DefaultVertexFormats.POSITION_COLOR, glMode, 256, false, false, renderState.build(true)
        )
    }

    /**
     * [RenderType.makeType] returns a package-private class [RenderType.Type] because... Mojang?
     */
    fun makeType(name: String,
        vertexFormatIn: VertexFormat, glMode: Int,
        bufferSizeIn: Int, p_228633_4_: Boolean, p_228633_5_: Boolean,
        p_228633_6_: RenderType.State
    ): RenderType {
        return RenderType.makeType("${name}_${UUID.randomUUID()}", vertexFormatIn, glMode, bufferSizeIn, p_228633_4_, p_228633_5_, p_228633_6_)
    }
}