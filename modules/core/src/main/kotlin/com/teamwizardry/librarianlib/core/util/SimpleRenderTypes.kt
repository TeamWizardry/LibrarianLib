package com.teamwizardry.librarianlib.core.util

import net.minecraft.client.renderer.RenderState
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

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

        return RenderType.makeType("flat_texture",
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

        return RenderType.makeType("flat_color",
            DefaultVertexFormats.POSITION_COLOR, glMode, 256, false, false, renderState.build(true)
        )
    }
}