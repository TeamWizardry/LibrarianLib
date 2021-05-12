package com.teamwizardry.librarianlib.facade.example.visualization

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderLayers
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderTypes
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.IMValueDouble
import com.teamwizardry.librarianlib.core.util.vec
import net.minecraft.client.render.VertexConsumerProvider
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

        val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
        val vb = buffer.getBuffer(SimpleRenderLayers.flatLines)

        val gridSize = this.gridSize
        if(gridSize < 0) return

        var x = gridSize
        while(x < width) {
            vb.pos2d(context.transform, x, 0).color(color).endVertex()
            vb.pos2d(context.transform, x, height).color(color).endVertex()
            x += gridSize
        }

        var y = gridSize
        while(y < height) {
            vb.pos2d(context.transform, 0, y).color(color).endVertex()
            vb.pos2d(context.transform, width, y).color(color).endVertex()
            y += gridSize
        }

        buffer.finish()
        RenderSystem.lineWidth(1f)
    }
}