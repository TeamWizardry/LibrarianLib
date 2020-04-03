package com.teamwizardry.librarianlib.particles.modules

import net.minecraft.client.renderer.RenderState
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

internal object SpriteRenderType: RenderState("", Runnable {}, Runnable {}) {

    @Suppress("INACCESSIBLE_TYPE")
    fun spriteRenderType(sprite: ResourceLocation): RenderType {
        val renderState = RenderType.State.getBuilder()
            .texture(TextureState(sprite, false, false))
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .depthTest(DEPTH_LEQUAL)
            .build(true)
        return RenderType.makeType(
            "particle_sprite", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, false, false, renderState
        )
    }
}