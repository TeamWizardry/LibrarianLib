package com.teamwizardry.librarianlib.glitter.modules

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.glitter.BlendMode
import net.minecraft.client.renderer.RenderState
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

internal object SpriteRenderType: RenderState("", Runnable {}, Runnable {}) {

    @Suppress("INACCESSIBLE_TYPE")
    fun spriteRenderType(sprite: ResourceLocation, blendMode: BlendMode?, writeDepth: Boolean, depthSort: Boolean): RenderType {
        val renderState = RenderType.State.getBuilder()
            .texture(TextureState(sprite, false, false))
            .cull(CULL_DISABLED)
            .alpha(DEFAULT_ALPHA)
            .depthTest(DEPTH_LEQUAL)

        if(blendMode != null) {
            renderState.transparency(TransparencyState("particle_transparency", Runnable {
                RenderSystem.enableBlend()
                blendMode.glApply()
            }, Runnable {
                RenderSystem.disableBlend()
                RenderSystem.defaultBlendFunc()
            }))
        }

        if(!writeDepth) {
            renderState.writeMask(COLOR_WRITE)
        }


        return RenderType.makeType(
            "particle_type", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, false, false, renderState.build(true)
        )
    }
}