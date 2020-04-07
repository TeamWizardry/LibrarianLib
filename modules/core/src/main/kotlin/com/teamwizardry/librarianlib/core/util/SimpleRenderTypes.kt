package com.teamwizardry.librarianlib.core.util

import net.minecraft.client.renderer.RenderState
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

@Suppress("INACCESSIBLE_TYPE")
object SimpleRenderTypes {
    fun flat(texture: ResourceLocation): RenderType {
        val renderState = RenderType.State.getBuilder()
            .texture(RenderState.TextureState(texture, false, false))
            .alpha(DefaultRenderStates.DEFAULT_ALPHA)
            .depthTest(DefaultRenderStates.DEPTH_LEQUAL)
            .transparency(DefaultRenderStates.TRANSLUCENT_TRANSPARENCY)

        return RenderType.makeType("sprite_type",
            DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, false, false, renderState.build(true)
        )
    }
}