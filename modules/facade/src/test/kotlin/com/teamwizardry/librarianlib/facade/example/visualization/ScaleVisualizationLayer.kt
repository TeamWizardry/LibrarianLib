package com.teamwizardry.librarianlib.facade.example.visualization

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderLayers
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
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
            vb.vertex2d(context.transform, x, 0).color(color).next()
            vb.vertex2d(context.transform, x, height).color(color).next()
            x += gridSize
        }

        var y = gridSize
        while(y < height) {
            vb.vertex2d(context.transform, 0, y).color(color).next()
            vb.vertex2d(context.transform, width, y).color(color).next()
            y += gridSize
        }

        buffer.draw()
        RenderSystem.lineWidth(1f)
    }
}