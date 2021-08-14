package com.teamwizardry.librarianlib.facade.example.visualization

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import java.awt.Color

class CrosshairsLayer(val color: Color = DistinctColors.blue, val length: Double = 1000.0): GuiLayer() {
    override fun draw(context: GuiDrawContext) {
        RenderSystem.lineWidth(2f)
        val buffer = FlatColorRenderBuffer.SHARED
        buffer.pos(context.transform, -length, 0, 0).color(color).endVertex()
        buffer.pos(context.transform, length, 0, 0).color(color).endVertex()
        buffer.pos(context.transform, 0, -length, 0).color(color).endVertex()
        buffer.pos(context.transform, 0, length, 0).color(color).endVertex()
        buffer.draw(Primitive.LINES)
    }
}