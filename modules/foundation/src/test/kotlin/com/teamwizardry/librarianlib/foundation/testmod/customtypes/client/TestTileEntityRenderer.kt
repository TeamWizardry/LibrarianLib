package com.teamwizardry.librarianlib.foundation.testmod.customtypes.client

import com.mojang.blaze3d.matrix.MatrixStack
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.normal
import com.teamwizardry.librarianlib.core.util.kotlin.pos
import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestTileEntity
import net.minecraft.client.renderer.Atlases
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.model.Material
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.util.ResourceLocation

class TestTileEntityRenderer(rendererDispatcherIn: TileEntityRendererDispatcher): TileEntityRenderer<TestTileEntity>(rendererDispatcherIn) {

    override fun render(tileEntityIn: TestTileEntity, partialTicks: Float, matrixStackIn: MatrixStack,
        bufferIn: IRenderTypeBuffer, combinedLightIn: Int, combinedOverlayIn: Int) {
        val texture = Client.getBlockAtlasSprite("block/dirt".toRl())
        val builder = texture.wrapBuffer(bufferIn.getBuffer(RenderType.getCutout()))
        builder
            .pos(matrixStackIn.last.matrix, 0, tileEntityIn.lastFallDistance + 1, 0).color(1f, 1f, 1f, 1f)
        builder.tex(0f, 0f)
            .lightmap(combinedLightIn).normal(0f, 1f, 0f).endVertex()
        builder
            .pos(matrixStackIn.last.matrix, 1, tileEntityIn.lastFallDistance + 1, 0).color(1f, 1f, 1f, 1f)
        builder.tex(1f, 0f)
            .lightmap(combinedLightIn).normal(0f, 1f, 0f).endVertex()
        builder
            .pos(matrixStackIn.last.matrix, 1, tileEntityIn.lastFallDistance + 1, 1).color(1f, 1f, 1f, 1f)
        builder.tex(1f, 1f)
            .lightmap(combinedLightIn).normal(0f, 1f, 0f).endVertex()
        builder
            .pos(matrixStackIn.last.matrix, 0, tileEntityIn.lastFallDistance + 1, 1).color(1f, 1f, 1f, 1f)
        builder.tex(0f, 1f)
            .lightmap(combinedLightIn).normal(0f, 1f, 0f).endVertex()

        builder
            .pos(matrixStackIn.last.matrix, 0, tileEntityIn.lastFallDistance + 1, 1).color(1f, 1f, 1f, 1f)
        builder.tex(0f, 1f)
            .lightmap(combinedLightIn).normal(0f, -1f, 0f).endVertex()
        builder
            .pos(matrixStackIn.last.matrix, 1, tileEntityIn.lastFallDistance + 1, 1).color(1f, 1f, 1f, 1f)
        builder.tex(1f, 1f)
            .lightmap(combinedLightIn).normal(0f, -1f, 0f).endVertex()
        builder
            .pos(matrixStackIn.last.matrix, 1, tileEntityIn.lastFallDistance + 1, 0).color(1f, 1f, 1f, 1f)
        builder.tex(1f, 0f)
            .lightmap(combinedLightIn).normal(0f, -1f, 0f).endVertex()
        builder
            .pos(matrixStackIn.last.matrix, 0, tileEntityIn.lastFallDistance + 1, 0).color(1f, 1f, 1f, 1f)
        builder.tex(0f, 0f)
            .lightmap(combinedLightIn).normal(0f, -1f, 0f).endVertex()
    }
}