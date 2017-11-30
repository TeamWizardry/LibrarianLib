package com.teamwizardry.librarianlib.features.tesr

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

class TESRMod(val constructor: (TileMod) -> TileRenderHandler<TileMod>, val fast: Boolean) : TileEntitySpecialRenderer<TileMod>() {
    override fun render(te: TileMod?, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        if(fast) {
            fastTESRRender(te, x, y, z, partialTicks, destroyStage, alpha)
            return
        }
        if(te == null) return
        var renderer = te.renderHandler
        if(renderer == null) {
            renderer = constructor(te)
            te.renderHandler = renderer
        }

        RenderHelper.disableStandardItemLighting()

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.enableBlend()

        if (Minecraft.isAmbientOcclusionEnabled())
        {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        }
        else
        {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)

        renderer.render(partialTicks, destroyStage, alpha)

        GlStateManager.popMatrix()
        RenderHelper.enableStandardItemLighting()
    }

    override fun renderTileEntityFast(te: TileMod?, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float, buffer: BufferBuilder) {
        if(te == null) return
        var renderer = te.renderHandler
        if(renderer == null) {
            renderer = constructor(te)
            te.renderHandler = renderer
        }

        buffer.setTranslation(x, y, z)
        renderer.renderFast(buffer, x, y, z, partialTicks, destroyStage, alpha)
    }

    private fun fastTESRRender(te: TileMod?, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        RenderHelper.disableStandardItemLighting()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.enableBlend()
        GlStateManager.disableCull()

        if (Minecraft.isAmbientOcclusionEnabled())
        {
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
        }
        else
        {
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)

        renderTileEntityFast(te, x, y, z, partialTicks, destroyStage, alpha, buffer)
        buffer.setTranslation(0.0, 0.0, 0.0)

        tessellator.draw()

        RenderHelper.enableStandardItemLighting()
    }
}
