package com.teamwizardry.librarianlib.facade.example.visualization

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.IMValueDouble
import com.teamwizardry.librarianlib.core.util.vec
import java.awt.Color

class AnchorVisualizationLayer(val target: GuiLayer): GuiLayer() {
    val anchorSize_im: IMValueDouble = imDouble(3.0)
    var anchorSize: Double by anchorSize_im

    var color: Color = DistinctColors.red

    init {
        this.zIndex = 103.0
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
        val parent = parent ?: return

        RenderSystem.lineWidth(3f)

        val buffer = FlatColorRenderBuffer.SHARED

        // get into parent's space
        context.matrix *= inverseTransform
        // get into their parent space
        context.matrix *= parent.inverseTransform

        buffer.pos(context.transform, target.pos.x - anchorSize, target.pos.y - anchorSize, 0).color(color).endVertex()
        buffer.pos(context.transform, target.pos.x + anchorSize, target.pos.y + anchorSize, 0).color(color).endVertex()
        buffer.pos(context.transform, target.pos.x + anchorSize, target.pos.y - anchorSize, 0).color(color).endVertex()
        buffer.pos(context.transform, target.pos.x - anchorSize, target.pos.y + anchorSize, 0).color(color).endVertex()

        buffer.draw(Primitive.LINES)
        RenderSystem.lineWidth(1f)
    }
}