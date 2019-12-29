package com.teamwizardry.librarianlib.particles.testmod.entity

import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.entity.item.TNTEntity
import net.minecraft.util.ResourceLocation
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.block.Blocks
import net.minecraft.util.math.MathHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockRendererDispatcher
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraft.command.arguments.EntityArgument.entity
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.TippedArrowRenderer

@OnlyIn(Dist.CLIENT)
class ParticleSpawnerEntityRenderer(renderManagerIn: EntityRendererManager): EntityRenderer<ParticleSpawnerEntity>(renderManagerIn) {
    /**
     * Renders the desired `T` type Entity.
     */
    override fun doRender(entity: ParticleSpawnerEntity, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        this.bindEntityTexture(entity)
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.pushMatrix()
        GlStateManager.disableLighting()
        GlStateManager.translatef(x.toFloat(), y.toFloat(), z.toFloat())
        GlStateManager.rotatef(MathHelper.lerp(partialTicks, entity.prevRotationYaw, entity.rotationYaw) - 90.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotatef(MathHelper.lerp(partialTicks, entity.prevRotationPitch, entity.rotationPitch), 0.0f, 0.0f, 1.0f)
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        GlStateManager.enableRescaleNormal()

        GlStateManager.rotatef(45.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.scalef(0.05625f, 0.05625f, 0.05625f)
        GlStateManager.translatef(-4.0f, 0.0f, 0.0f)
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial()
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity))
        }

        GlStateManager.normal3f(0.05625f, 0.0f, 0.0f)
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(-7.0, -2.0, -2.0).tex(0.0, 0.15625).endVertex()
        bufferbuilder.pos(-7.0, -2.0, 2.0).tex(0.15625, 0.15625).endVertex()
        bufferbuilder.pos(-7.0, 2.0, 2.0).tex(0.15625, 0.3125).endVertex()
        bufferbuilder.pos(-7.0, 2.0, -2.0).tex(0.0, 0.3125).endVertex()
        tessellator.draw()
        GlStateManager.normal3f(-0.05625f, 0.0f, 0.0f)
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(-7.0, 2.0, -2.0).tex(0.0, 0.15625).endVertex()
        bufferbuilder.pos(-7.0, 2.0, 2.0).tex(0.15625, 0.15625).endVertex()
        bufferbuilder.pos(-7.0, -2.0, 2.0).tex(0.15625, 0.3125).endVertex()
        bufferbuilder.pos(-7.0, -2.0, -2.0).tex(0.0, 0.3125).endVertex()
        tessellator.draw()

        for (j in 0..3) {
            GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f)
            GlStateManager.normal3f(0.0f, 0.0f, 0.05625f)
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
            bufferbuilder.pos(-8.0, -2.0, 0.0).tex(0.0, 0.0).endVertex()
            bufferbuilder.pos(8.0, -2.0, 0.0).tex(0.5, 0.0).endVertex()
            bufferbuilder.pos(8.0, 2.0, 0.0).tex(0.5, 0.15625).endVertex()
            bufferbuilder.pos(-8.0, 2.0, 0.0).tex(0.0, 0.15625).endVertex()
            tessellator.draw()
        }

        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine()
            GlStateManager.disableColorMaterial()
        }

        GlStateManager.disableRescaleNormal()
        GlStateManager.enableLighting()
        GlStateManager.popMatrix()
        super.doRender(entity, x, y, z, entityYaw, partialTicks)
//        val blockrendererdispatcher = Minecraft.getInstance().blockRendererDispatcher
//        GlStateManager.pushMatrix()
//        GlStateManager.translatef(x.toFloat(), y.toFloat() + 0.5f, z.toFloat())
//
//        val scale = 0.25f
//        GlStateManager.scalef(scale, scale, scale)
//
//        this.bindEntityTexture(entity)
//        GlStateManager.rotatef(-90.0f, 0.0f, 1.0f, 0.0f)
//        GlStateManager.translatef(-0.5f, -0.5f, 0.5f)
//        blockrendererdispatcher.renderBlockBrightness(Blocks.COMMAND_BLOCK.defaultState, entity.brightness)
//        GlStateManager.translatef(0.0f, 0.0f, 1.0f)
//        if (this.renderOutlines) {
//            GlStateManager.enableColorMaterial()
//            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity))
//            blockrendererdispatcher.renderBlockBrightness(Blocks.COMMAND_BLOCK.defaultState, 1.0f)
//            GlStateManager.tearDownSolidRenderingTextureCombine()
//            GlStateManager.disableColorMaterial()
//        }
//
//        GlStateManager.popMatrix()
//        super.doRender(entity, x, y, z, entityYaw, partialTicks)
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    override fun getEntityTexture(entity: ParticleSpawnerEntity): ResourceLocation? {
//        public static final ResourceLocation RES_ARROW = new ResourceLocation("textures/entity/projectiles/arrow.png");
        return TippedArrowRenderer.RES_ARROW
    }
}
