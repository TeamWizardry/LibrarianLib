package com.teamwizardry.librarianlib.facade.example.visualization

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import net.minecraft.client.renderer.IRenderTypeBuffer
import java.awt.Color

class CrosshairsLayer(val color: Color = DistinctColors.blue, val length: Double = 1000.0): GuiLayer() {
    override fun draw(context: GuiDrawContext) {
        RenderSystem.lineWidth(2f)
        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(SimpleRenderTypes.flatLines)
        vb.pos2d(context.transform, -length, 0).color(color).endVertex()
        vb.pos2d(context.transform, length, 0).color(color).endVertex()
        vb.pos2d(context.transform, 0, -length).color(color).endVertex()
        vb.pos2d(context.transform, 0, length).color(color).endVertex()
        buffer.finish()
    }
}