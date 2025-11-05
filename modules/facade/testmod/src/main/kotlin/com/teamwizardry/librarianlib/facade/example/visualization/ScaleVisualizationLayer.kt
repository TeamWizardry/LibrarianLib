package com.teamwizardry.librarianlib.facade.example.visualization

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.IMValueDouble
import java.awt.Color

class ScaleVisualizationLayer(gridSize: Double): GuiLayer() {
    val gridSize_im: IMValueDouble = imDouble(gridSize)
    var gridSize: Double by gridSize_im

    var color: Color = DistinctColors.black

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

        val gridSize = this.gridSize
        if(gridSize < 0) return

        var x = gridSize
        while(x < width) {
            buffer.pos(context.transform, x, 0, 0).color(color).endVertex()
            buffer.pos(context.transform, x, height, 0).color(color).endVertex()
            x += gridSize
        }

        var y = gridSize
        while(y < height) {
            buffer.pos(context.transform, 0, y, 0).color(color).endVertex()
            buffer.pos(context.transform, width, y, 0).color(color).endVertex()
            y += gridSize
        }

        buffer.draw(Primitive.LINES)
        RenderSystem.lineWidth(1f)
    }
}