package com.teamwizardry.librarianlib.facade.example.visualization

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderTypes
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.core.util.vec
import net.minecraft.client.renderer.IRenderTypeBuffer
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

        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(SimpleRenderTypes.flatLineStrip)

        getBoundingBoxPoints().forEach {
            vb.pos2d(context.transform, it.x, it.y).color(color).endVertex()
        }

        buffer.finish()
        RenderSystem.lineWidth(1f)
    }
}