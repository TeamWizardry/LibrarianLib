package com.teamwizardry.librarianlib.facade.example.visualization

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderLayers
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import net.minecraft.client.render.VertexConsumerProvider
import java.awt.Color

class CrosshairsLayer(val color: Color = DistinctColors.blue, val length: Double = 1000.0): GuiLayer() {
    override fun draw(context: GuiDrawContext) {
        RenderSystem.lineWidth(2f)
        val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
        val vb = buffer.getBuffer(SimpleRenderLayers.flatLines)
        vb.vertex2d(context.transform, -length, 0).color(color).next()
        vb.vertex2d(context.transform, length, 0).color(color).next()
        vb.vertex2d(context.transform, 0, -length).color(color).next()
        vb.vertex2d(context.transform, 0, length).color(color).next()
        buffer.draw()
    }
}