package com.teamwizardry.librarianlib.facade.example.visualization

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.core.util.vec
import java.awt.Color

class BoundsVisualizationLayer: GuiLayer() {
    var color: Color = DistinctColors.orange

    init {
        this.zIndex = 100.0
    }

    override fun update() {
        val parent = parent ?: return
        pos = vec(0, 0)
        size = parent.size
        scale = 1.0
        rotation = 0.0
        anchor = vec(0, 0)
    }

    override fun draw(context: GuiDrawContext) {
        RenderSystem.lineWidth(2f)

        val buffer = FlatColorRenderBuffer.SHARED

        getBoundingBoxPoints().forEach {
            buffer.pos(context.transform, it.x, it.y, 0).color(color).endVertex()
        }

        buffer.draw(Primitive.LINE_LOOP)
        RenderSystem.lineWidth(1f)
    }
}